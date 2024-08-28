package com.alimama.alimamaspringboot;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {
    private Connection postgresqlClient;
    private final String postgresConnURI;

    private MongoClient mongoClient;
    private final String connMongoURI;

    private final Map<String, String> env;

    DatabaseConnection() {
        env = new HashMap<>();
        // Postgresql credentials
        env.put("pg_username", System.getenv("POSTGRES_USER"));
        env.put("pg_password", System.getenv("POSTGRES_PASSWORD"));
        env.put("pg_dbname", System.getenv("POSTGRES_DB_NAME"));
        env.put("pg_hostname", System.getenv("POSTGRES_HOST"));
        env.put("pg_port", System.getenv("POSTGRES_PORT"));
        this.postgresConnURI = String.format(
                "jdbc:postgresql://%s:%s/%s",
                env.get("pg_hostname"), env.get("pg_port"), env.get("pg_dbname"));
        // MongoDB credentials
        env.put("mongo_dbname", System.getenv("MONGO_DB_NAME"));
        env.put("mongo_username", System.getenv("MONGO_INITDB_ROOT_USERNAME"));
        env.put("mongo_password", System.getenv("MONGO_INITDB_ROOT_PASSWORD"));
        env.put("mongo_port", System.getenv("MONGO_PORT"));
        env.put("mongo_host", System.getenv("MONGO_HOST"));
        this.connMongoURI = String.format(
                "mongodb://%s:%s@%s:%s/%s",
                env.get("mongo_username"), env.get("mongo_password"),
                env.get("mongo_host"), env.get("mongo_port"),
                env.get("mongo_dbname")
                );
    }

    public boolean connectPostgres() throws SQLException {
        if(this.postgresqlClient == null) {
            try {
                this.postgresqlClient = DriverManager.getConnection(this.postgresConnURI, env.get("pg_username"), env.get("pg_password"));
                return true;
            }
            catch (SQLException e) {
                return false;
            }
        }
        return false;
    }

    public ResultSet submitQueryPostgres(String query) {
        ResultSet results = null;
        PreparedStatement stmt = null;

        if (this.postgresqlClient != null) {
            try {
                // Start transaction
                this.postgresqlClient.setAutoCommit(false);

                stmt = this.postgresqlClient.prepareStatement(query); // handles sanitization <3
                results = stmt.executeQuery();
                this.postgresqlClient.commit();
                this.postgresqlClient.setAutoCommit(true);

                // Finish transaction
                return results;
            }
            catch (SQLException e) {
                // Rollback
                try {
                    if (this.postgresqlClient != null)
                        this.postgresqlClient.rollback();
                }
                catch (SQLException rollback_e) {
                    System.err.println(rollback_e.getMessage());
                }
                System.err.println(e.getMessage());
            }
            finally {
                try {
                    if (this.postgresqlClient != null)
                        this.postgresqlClient.setAutoCommit(true);
                    if (stmt != null)
                        stmt.close();
                }
                catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return null;
    }

    public boolean disconnectPostgres() throws SQLException {
        if (this.postgresqlClient != null) {
            try {
                this.postgresqlClient.close();
                return true;
            }
            catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean connectMongodb() {
        if (this.mongoClient == null) {
            try {
                this.mongoClient = MongoClients.create(this.connMongoURI);
                return true;
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    // function for submitting query
    /*
    =
    =
    =
    =
    =
    =
    =
    =
     */

    public boolean disconnectMongodb() {
        if (this.mongoClient != null) {
            try {
                this.mongoClient.close();
                return true;
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }
}
