package com.alimama.alimamaspringboot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import org.springframework.stereotype.Service;

@Service
public class PostgreSQLConnection {
    private Connection postgresqlClient;
    private final String postgresConnURI;

    // Store only some of the environmental variables as instance variables
    private final String pgUser;
    private final String pgPassword;

    public PostgreSQLConnection() {
        // PostgreSQL credentials
        // SET PGHOST TO THAT OF ENV WHEN ON DOCKER
        String pgHost = "localhost";
        String pgPort = System.getenv("POSTGRES_PORT");
        String pgDbName = System.getenv("POSTGRES_DB_NAME");
        this.pgUser = System.getenv("POSTGRES_USER");
        this.pgPassword = System.getenv("POSTGRES_PASSWORD");

        if (pgHost == null || pgPort == null || pgDbName == null || pgUser == null || pgPassword == null)
            throw new IllegalArgumentException("Missing required PostgreSQL environment variables");

        this.postgresConnURI = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDbName);
    }

    public boolean connectPostgres() {
        if (this.postgresqlClient == null) {
            try {
                this.postgresqlClient = DriverManager.getConnection(this.postgresConnURI, this.pgUser, this.pgPassword);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public ResultSet querySelectPostgres(String query) {
        ResultSet results = null;
        PreparedStatement stmt = null;

        if (this.postgresqlClient != null) {
            try {
                stmt = this.postgresqlClient.prepareStatement(query);
                results = stmt.executeQuery();
            } catch (SQLException e) {
                System.err.println("SQL select error: " + e.getMessage());
            }
        }
        return results;
    }

    public boolean queryUpdatePostgres(String query) {
        boolean success = false;
        PreparedStatement stmt = null;

        if (this.postgresqlClient != null) {
            try {
                this.postgresqlClient.setAutoCommit(false);
                stmt = this.postgresqlClient.prepareStatement(query);
                stmt.executeUpdate();
                this.postgresqlClient.commit();
                success = true;
            } catch (SQLException e) {
                System.err.println("SQL update error: " + e.getMessage());
                try {
                    this.postgresqlClient.rollback();
                    System.err.println("Rollback successful");
                } catch (SQLException rollback_e) {
                    System.err.println("Rollback error: " + rollback_e.getMessage());
                }
            } finally {
                try {
                    if (this.postgresqlClient != null)
                        this.postgresqlClient.setAutoCommit(true);
                    if (stmt != null) stmt.close();
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return success;
    }

    public boolean disconnectPostgres() {
        if (this.postgresqlClient != null) {
            try {
                this.postgresqlClient.close();
                return true;
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    public Connection getPostgresqlClient() {
        return this.postgresqlClient;
    }
}