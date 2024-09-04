package com.alimama.alimamaspringboot.items;

import com.alimama.alimamaspringboot.MongoDBConnection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final MongoDBConnection mongoDBConnection;
    private final String collectionName = "items";

    @Autowired
    public ItemService(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    // Retrieve items from MongoDB with a filter
    public List<Document> retrieveItemsFromMongo(Document filter) {
        if (mongoDBConnection.connectMongoDB())
            return mongoDBConnection.queryReadMongoDB(collectionName, filter);
        return null;
    }

    public boolean insertItemToMongo(String productName, int numInStock, double price, List<MultipartFile> pictures) {
        if (mongoDBConnection.connectMongoDB()) {
            Document newItem = new Document();
            newItem.append("productName", productName);
            newItem.append("numInStock", numInStock);
            newItem.append("price", price);

            List<String> fileNames = new ArrayList<>();
            if (pictures != null) {
                for (MultipartFile picture : pictures) {
                    String fileName = picture.getOriginalFilename();
                    fileNames.add(fileName);
                }
                newItem.append("pictures", fileNames);
            }

            return mongoDBConnection.queryExecuteMongoDB("insert", collectionName, null, null, null);
        }
        return false;
    }

    public boolean modifyItemInMongo(Document filter, Document updatedFields) {
        if (mongoDBConnection.connectMongoDB()) {
            Document updateDoc = new Document("$set", updatedFields);
            return mongoDBConnection.queryExecuteMongoDB("update", collectionName, filter, updateDoc, null);
        }
        return false;
    }

    public boolean deleteItemFromMongo(Document filter) {
        if (mongoDBConnection.connectMongoDB()) {
            return mongoDBConnection.queryExecuteMongoDB("delete", collectionName, filter, null, null);
        }
        return false;
    }
}
