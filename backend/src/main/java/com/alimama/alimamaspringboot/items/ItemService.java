package com.alimama.alimamaspringboot.items;

import com.alimama.alimamaspringboot.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ItemService {

    private final MongoDBConnection mongoDBConnection;
    private final String collectionName = "items";
    private static final Pattern OBJECT_ID_PATTERN = Pattern.compile("^[0-9a-fA-F]{24}$");

    @Autowired
    public ItemService(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
    }

    // Retrieve items from MongoDB with a filter
    public List<Document> retrieveItemsFromMongo(Document filter) {
        if (mongoDBConnection.connectMongoDB()) {
            List<Document> documents = mongoDBConnection.queryReadMongoDB(collectionName, filter);

            // Convert ObjectId to String
            for (Document doc : documents) {
                Object id = doc.get("_id");
                if (id instanceof org.bson.types.ObjectId) {
                    doc.put("_id", id.toString()); // Convert ObjectId to String
                }
            }
            return documents;
        }
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
                return mongoDBConnection.queryExecuteMongoDB("insert", collectionName, null, null, newItem);
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
            // Make sure that the _id is converted to ObjectId if it is not already an ObjectId
            if (filter.containsKey("_id") && !(filter.get("_id") instanceof ObjectId)) {
                filter.put("_id", new ObjectId(filter.getString("_id")));  // Convert the _id to ObjectId
            }

            Document updateDoc = new Document("$set", updatedFields);
            return mongoDBConnection.queryExecuteMongoDB("update", collectionName, filter, updateDoc, null);
        }
        return false;
    }

    public boolean isValidObjectId(String id) {
        if (id == null) {
            return false;
        }
        return OBJECT_ID_PATTERN.matcher(id).matches();
    }

    // Delete an item by its ID
    public boolean deleteItemFromMongo(String id) {
        if (!isValidObjectId(id)) {
            throw new IllegalArgumentException("Invalid ID format.");
        }

        if (mongoDBConnection.connectMongoDB()) {
            // Create a filter to find the item by _id
            Document filter = new Document("_id", new ObjectId(id));
            // Attempt to delete the item
            return mongoDBConnection.queryExecuteMongoDB("delete", collectionName, filter, null, null);
        }
        else {
            System.err.println("Failed to connect to MongoDB");
            return false;
        }
    }
}
