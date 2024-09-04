package com.alimama.alimamaspringboot.items;

import com.alimama.alimamaspringboot.DatabaseConnection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final DatabaseConnection databaseConnection;
    private final String collectionName = "items";

    @Autowired
    public ItemService(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    // Retrieve items from MongoDB with a filter
    public List<Document> retrieveItemsFromMongo(Document filter) {
        if (databaseConnection.connectMongodb())
            return databaseConnection.queryReadMongoDB(collectionName, filter);
        return null;
    }

    public boolean insertItemToMongo(String productName, int numInStock, double price, List<MultipartFile> pictures) {
        if (databaseConnection.connectMongodb()) {
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

            return databaseConnection.queryExecuteMongoDB("insert", collectionName, null, null, newItem);
        }
        return false;
    }

    public boolean modifyItemInMongo(Document filter, Document updatedFields) {
        if (databaseConnection.connectMongodb()) {
            Document updateDoc = new Document("$set", updatedFields);
            return databaseConnection.queryExecuteMongoDB("update", collectionName, filter, updateDoc, null);
        }
        return false;
    }

    public boolean deleteItemFromMongo(Document filter) {
        if (databaseConnection.connectMongodb()) {
            return databaseConnection.queryExecuteMongoDB("delete", collectionName, filter, null, null);
        }
        return false;
    }
}
