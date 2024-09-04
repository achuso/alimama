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

    public boolean insertItemToMongo(int vendorId, String productName, int numInStock, double price, List<MultipartFile> pictures, List<String> tags, double ratingAvgTotal) {
        if (mongoDBConnection.connectMongoDB()) {
            Document newItem = new Document();

            // Store vendorId as a number, no ObjectId required
            newItem.append("vendorId", vendorId);
            newItem.append("productName", productName);
            newItem.append("numInStock", numInStock);
            newItem.append("price", price);

            // Handle pictures field, store filenames if available
            List<String> fileNames = new ArrayList<>();
            if (pictures != null && !pictures.isEmpty()) {
                for (MultipartFile picture : pictures) {
                    String fileName = picture.getOriginalFilename();
                    fileNames.add(fileName);
                }
            }
            newItem.append("pictures", fileNames.isEmpty() ? new ArrayList<>() : fileNames);

            // Handle tags, store them or set to an empty array if null
            newItem.append("tags", tags != null ? tags : new ArrayList<>());

            // Store the rating
            newItem.append("ratingAvgTotal", ratingAvgTotal);

            try {
                // Attempt to insert the document into MongoDB
                return mongoDBConnection.queryExecuteMongoDB("insert", "items", null, null, newItem);
            } catch (Exception e) {
                System.err.println("Error inserting item to MongoDB: " + e.getMessage());
                return false;
            }
        } else {
            System.err.println("Failed to connect to MongoDB");
            return false;
        }
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
