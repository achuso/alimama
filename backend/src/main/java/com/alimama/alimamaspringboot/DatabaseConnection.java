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

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

import org.springframework.stereotype.Service;

@Service
public class DatabaseConnection {
    private Connection postgresqlClient;
    private final String postgresConnURI;

    private MongoClient mongoClient;
    private final String mongoConnURI;

    // Store only some of the environmental variables as instance variables
    private final String pgUser;
    private final String pgPassword;
    private final String mongoDbName;

    public DatabaseConnection() {
        // PostgreSQL credentials
        String pgHost = System.getenv("POSTGRES_HOST");
        String pgPort = System.getenv("POSTGRES_PORT");
        String pgDbName = System.getenv("POSTGRES_DB_NAME");
        this.pgUser = System.getenv("POSTGRES_USER");
        this.pgPassword = System.getenv("POSTGRES_PASSWORD");

        if (pgHost == null || pgPort == null || pgDbName == null || pgUser == null || pgPassword == null)
            throw new IllegalArgumentException("Missing required PostgreSQL environment variables");

        this.postgresConnURI = String.format("jdbc:postgresql://%s:%s/%s", pgHost, pgPort, pgDbName);

        // MongoDB credentials
        String mongoHost = System.getenv("MONGO_HOST");
        String mongoPortStr = System.getenv("MONGO_PORT");
        this.mongoDbName = System.getenv("MONGO_DB_NAME");
        String mongoUser = System.getenv("MONGO_INITDB_ROOT_USERNAME");
        String mongoPassword = System.getenv("MONGO_INITDB_ROOT_PASSWORD");

        if (mongoHost == null || mongoPortStr == null || mongoDbName == null || mongoUser == null || mongoPassword == null)
            throw new IllegalArgumentException("Missing required MongoDB environment variables");

        int mongoPort;
        try {
            mongoPort = Integer.parseInt(mongoPortStr);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number for MongoDB: " + mongoPortStr);
        }

        this.mongoConnURI = String.format("mongodb://%s:%s@%s:%d/%s", mongoUser, mongoPassword, mongoHost, mongoPort, mongoDbName);
    }

    public boolean connectPostgres() {
        if (this.postgresqlClient == null) {
            try {
                this.postgresqlClient = DriverManager.getConnection(this.postgresConnURI, this.pgUser, this.pgPassword);
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
            } catch (SQLException e) {
                System.err.println("SQL update error: " + e.getMessage());
                try {
                    if (this.postgresqlClient != null) {
                        this.postgresqlClient.rollback();
                        System.err.println("Rollback successful");
                    }
                } catch (SQLException rollback_e) {
                    System.err.println("Rollback error: " + rollback_e.getMessage());
                }
            } finally {
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

    public boolean disconnectPostgres() {
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
                this.mongoClient = MongoClients.create(this.mongoConnURI);
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
            MongoDatabase database = mongoClient.getDatabase(this.mongoDbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            for (Document doc : collection.find(query))
                results.add(doc);
        }
        return results;
    }

    public boolean queryExecuteMongoDB(String operationType, String collectionName, Document filter, Document updateDoc, Document newDoc) {
        if (this.mongoClient == null) {
            System.err.println("MongoDB Client not connected.");
            return false;
        }

        MongoDatabase database = mongoClient.getDatabase(this.mongoDbName);
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
                }
                else {
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

    public Connection getPostgresqlClient() {
        return this.postgresqlClient;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

}
