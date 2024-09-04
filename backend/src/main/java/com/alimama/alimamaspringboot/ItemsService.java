package com.alimama.alimamaspringboot;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsService {

    private final DatabaseConnection databaseConnection;

    @Autowired
    public ItemsService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<Document> retrieveItemsFromMongo(String collectionName, Document filter) {
        if (databaseConnection.connectMongodb())
            return databaseConnection.queryReadMongoDB(collectionName, filter);
        return null;
    }

    public boolean insertItemToMongo(String collectionName, Document newItem) {
        if (databaseConnection.connectMongodb())
            return databaseConnection.queryExecuteMongoDB("insert", collectionName, null, null, newItem);
        return false;
    }

    public boolean modifyItemInMongo(String collectionName, Document filter, Document updatedFields) {
        if (databaseConnection.connectMongodb()) {
            Document updateDoc = new Document("$set", updatedFields); // set modified fields
            return databaseConnection.queryExecuteMongoDB("update", collectionName, filter, updateDoc, null);
        }
        return false;
    }

    public boolean deleteItemFromMongo(String collectionName, Document filter) {
        if (databaseConnection.connectMongodb())
            return databaseConnection.queryExecuteMongoDB("delete", collectionName, filter, null, null);
        return false;
    }
}
