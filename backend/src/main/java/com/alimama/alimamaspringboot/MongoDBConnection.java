package com.alimama.alimamaspringboot;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MongoDBConnection {
    private static final Logger logger = LoggerFactory.getLogger(MongoDBConnection.class);

    private MongoClient mongoClient;
    private final String mongoConnURI;
    private final String mongoDbName;

    public MongoDBConnection() {
        String mongoHost = getEnv("MONGO_HOST", "mongodb"); // Default to "mongodb" for Dockerized setup
        int mongoPort = Integer.parseInt(getEnv("MONGO_PORT", "27017"));
        this.mongoDbName = getEnv("MONGO_DB_NAME", "alimama_db");
        String mongoUser = getEnv("MONGO_INITDB_ROOT_USERNAME", "admin");
        String mongoPassword = getEnv("MONGO_INITDB_ROOT_PASSWORD", "password");

        this.mongoConnURI = String.format("mongodb://%s:%s@%s:%d/%s?authSource=admin",
                mongoUser, mongoPassword, mongoHost, mongoPort, mongoDbName);
        logger.info("MongoDB connection URI initialized.");
    }

    private String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isEmpty()) {
            if (defaultValue != null) {
                logger.warn("Environment variable '{}' not found. Using default: {}", key, defaultValue);
                return defaultValue;
            }
            throw new IllegalArgumentException("Missing required environment variable: " + key);
        }
        return value;
    }

    public boolean connectMongoDB() {
        if (this.mongoClient == null) {
            try {
                this.mongoClient = MongoClients.create(this.mongoConnURI);
                MongoDatabase database = this.mongoClient.getDatabase(this.mongoDbName);
                database.runCommand(new Document("ping", 1));
                logger.info("Successfully connected to MongoDB.");
                return true;
            } 
            catch (Exception e) {
                logger.error("Failed to connect to MongoDB: {}", e.getMessage(), e);
                return false;
            }
        }
        logger.info("MongoDB client already connected.");
        return true;
    }

    public List<Document> queryReadMongoDB(String collectionName, Document query) {
        List<Document> results = new ArrayList<>();
        if (this.mongoClient != null) {
            try {
                MongoDatabase database = mongoClient.getDatabase(this.mongoDbName);
                MongoCollection<Document> collection = database.getCollection(collectionName);
                logger.info("Executing query on collection: {}", collectionName);
                for (Document doc : collection.find(query)) {
                    results.add(doc);
                    logger.debug("Found document: {}", doc.toJson());
                }
            } 
            catch (Exception e) {
                logger.error("Error during query execution: {}", e.getMessage(), e);
            }
        } else {
            logger.error("MongoDB client is not connected.");
        }
        return results;
    }

    public boolean queryExecuteMongoDB(String operationType, String collectionName, Document filter, Document updateDoc, Document newDoc) {
        if (this.mongoClient == null) {
            logger.error("MongoDB client is not connected.");
            return false;
        }

        try {
            MongoDatabase database = mongoClient.getDatabase(this.mongoDbName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            switch (operationType.toLowerCase()) {
            case "insert":
                if (newDoc != null) {
                    collection.insertOne(newDoc);
                    logger.info("Successfully inserted document into collection: {}", collectionName);
                    return true;
                } else {
                    logger.warn("Insert operation failed: no document provided.");
                    return false;
                }
            case "update":
                if (filter != null && updateDoc != null) {
                    UpdateResult result = collection.updateOne(filter, updateDoc);
                    logger.info("Updated {} documents in collection: {}", result.getModifiedCount(), collectionName);
                    return result.getModifiedCount() > 0;
                } else {
                    logger.warn("Update operation failed: filter or update document is missing.");
                    return false;
                }
            case "delete":
                if (filter != null) {
                    DeleteResult result = collection.deleteOne(filter);
                    logger.info("Deleted {} documents from collection: {}", result.getDeletedCount(), collectionName);
                    return result.getDeletedCount() > 0;
                } else {
                    logger.warn("Delete operation failed: filter is missing.");
                    return false;
                }
            default:
                logger.error("Invalid operation type: {}", operationType);
                return false;
            }
        } 
        catch (Exception e) {
            logger.error("MongoDB operation error: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean disconnectMongoDB() {
        if (this.mongoClient != null) {
            try {
                this.mongoClient.close();
                logger.info("MongoDB connection closed successfully.");
                return true;
            } 
            catch (Exception e) {
                logger.error("Error while closing MongoDB connection: {}", e.getMessage(), e);
                return false;
            }
        }
        logger.warn("MongoDB client was not connected.");
        return false;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
}
