package com.alimama.alimamaspringboot;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

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

    public ResultSet querySelectPostgres(String query) {
        ResultSet results = null;
        PreparedStatement stmt = null;

        if (this.postgresqlClient != null) {
            try {
                stmt = this.postgresqlClient.prepareStatement(query); // sanitizes string <3
                results = stmt.executeQuery();
            }
            catch (SQLException e) {
                System.err.println("SQL select error: " + e.getMessage());
            }
            finally {
                try {
                    if (stmt != null) stmt.close();
                }
                catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return results;
    }

    public boolean queryUpdatePostgres(String query) {
        boolean success = false;
        PreparedStatement stmt = null;

        if (this.postgresqlClient != null) {
            try {
                // Transact start
                this.postgresqlClient.setAutoCommit(false);
                stmt = this.postgresqlClient.prepareStatement(query);
                stmt.executeUpdate();
                this.postgresqlClient.commit();
                success = true;
            }
            catch (SQLException e) {
                System.err.println("SQL update error: " + e.getMessage());
                try {
                    if (this.postgresqlClient != null) {
                        this.postgresqlClient.rollback();
                        System.err.println("Rollback successful");
                    }
                }
                catch (SQLException rollback_e) {
                    System.err.println("Rollback error: " + rollback_e.getMessage());
                }
            }
            finally {
                try { // transact end
                    if (this.postgresqlClient != null)
                        this.postgresqlClient.setAutoCommit(true);
                    if (stmt != null) stmt.close();
                }
                catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return success;
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

    public List<Document> queryReadMongoDB(String collectionName, Document query) {
        List<Document> results = new ArrayList<>();

        if (this.mongoClient != null) {
            MongoDatabase database = mongoClient.getDatabase(env.get("mongo_dbname"));
            MongoCollection<Document> collection = database.getCollection(collectionName);

            for ( Document doc : collection.find(query) )
                results.add(doc);
        }
        return results;
    }

    // insert, update, or delete
    public boolean queryExecuteMongoDB(String operationType, String collectionName, Document filter, Document updateDoc, Document newDoc) {
        if (this.mongoClient == null) {
            System.err.println("MongoDB Client not connected.");
            return false;
        }

        MongoDatabase database = mongoClient.getDatabase(env.get("mongo_dbname"));
        MongoCollection<Document> collection = database.getCollection(collectionName);

        try {
            switch (operationType.toLowerCase()) {
            case "insert":
                if (newDoc != null) {
                    collection.insertOne(newDoc);
                    return true;
                }
                else {
                    System.err.println("Insert failed: no doc provided.");
                    return false;
                }
            case "update":
                if (filter != null && updateDoc != null) {
                    collection.updateOne(filter, new Document("$set", updateDoc));
                    return true;
                }
                else {
                    System.err.println("Update failed: no filter/new doc provided.");
                    return false;
                }

            case "delete":
                if (filter != null) {
                    collection.deleteOne(filter);
                    return true;
                } else {
                    System.err.println("Delete failed: no filter provided.");
                    return false;
                }

            default:
                System.err.println("Invalid operationType.");
                return false;
            }
        }
        catch (Exception e) {
            System.err.println("MongoDB operation error: " + e.getMessage());
            return false;
        }
    }


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