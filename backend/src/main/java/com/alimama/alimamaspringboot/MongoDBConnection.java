package com.alimama.alimamaspringboot;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

import org.springframework.stereotype.Service;

@Service
public class MongoDBConnection {
    private MongoClient mongoClient;
    private final String mongoConnURI;
    private final String mongoDbName;

    public MongoDBConnection() {
        // MongoDB credentials
        String mongoHost = "localhost";
        String mongoPortStr = System.getenv("MONGO_PORT");
        this.mongoDbName = System.getenv("MONGO_DB_NAME");
        String mongoUser = System.getenv("MONGO_INITDB_ROOT_USERNAME");
        String mongoPassword = System.getenv("MONGO_INITDB_ROOT_PASSWORD");

        if (/* mongoHost == null || */ mongoPortStr == null || mongoDbName == null || mongoUser == null || mongoPassword == null)
            throw new IllegalArgumentException("Missing required MongoDB environment variables");

        int mongoPort;
        try {
            mongoPort = Integer.parseInt(mongoPortStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port number for MongoDB: " + mongoPortStr);
        }

        this.mongoConnURI = String.format("mongodb://%s:%s@%s:%d/%s", mongoUser, mongoPassword, mongoHost, mongoPort, mongoDbName);
    }

    public boolean connectMongoDB() {
        if (this.mongoClient == null) {
            try {
                this.mongoClient = MongoClients.create(this.mongoConnURI);
                MongoDatabase database = this.mongoClient.getDatabase(this.mongoDbName);
                database.runCommand(new Document("ping", 1));
                return true;
            } catch (Exception e) {
                System.out.println("Failed to connect to MongoDB: " + e.getMessage());
                return false;
            }
        }
        return true;
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
                    } else {
                        System.err.println("Insert failed: no doc provided.");
                        return false;
                    }
                case "update":
                    if (filter != null && updateDoc != null) {
                        collection.updateOne(filter, new Document("$set", updateDoc));
                        return true;
                    } else {
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
        } catch (Exception e) {
            System.err.println("MongoDB operation error: " + e.getMessage());
            return false;
        }
    }

    public boolean disconnectMongoDB() {
        if (this.mongoClient != null) {
            try {
                this.mongoClient.close();
                return true;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    public MongoClient getMongoClient() {
        return this.mongoClient;
    }
}
