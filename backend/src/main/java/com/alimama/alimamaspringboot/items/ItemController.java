package com.alimama.alimamaspringboot.items;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemsService itemsService;
    String collectionName = "items";

    @Autowired
    public ItemController(ItemsService itemsService) {this.itemsService = itemsService;}

    @GetMapping("/retrieve")
    public ResponseEntity<List<Document>> retrieveItems(@RequestBody Document filter) {
        List<Document> items = itemsService.retrieveItemsFromMongo(collectionName, filter);
        if (items != null)
            return ResponseEntity.ok(items);
        else
            return ResponseEntity.status(500).body(null);
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertItem(@RequestBody Document newItem) {
        boolean success = itemsService.insertItemToMongo(collectionName, newItem);
        if (success)
            return ResponseEntity.ok("Item inserted successfully,");
        else
            return ResponseEntity.status(500).body("Failed to insert item.");
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modifyItem(@RequestBody Document filter, @RequestBody Document updatedItem) {
        boolean success = itemsService.modifyItemInMongo(collectionName, filter, updatedItem);
        if (success)
            return ResponseEntity.ok("Item modified successfully.");
        else
            return ResponseEntity.status(500).body("Failed to modify item.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestBody Document filter) {
        boolean success = itemsService.deleteItemFromMongo(collectionName, filter);
        if (success)
            return ResponseEntity.ok("Item deleted successfully.");
        else
            return ResponseEntity.status(500).body("Failed to delete item.");
    }
}
