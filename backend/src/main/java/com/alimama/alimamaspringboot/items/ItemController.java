package com.alimama.alimamaspringboot.items;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:8080")
public class ItemController {

    private final ItemService itemsService;

    @Autowired
    public ItemController(ItemService itemsService) {
        this.itemsService = itemsService;
    }

    @GetMapping("/retrieve")
    public ResponseEntity<List<Document>> retrieveItems(@RequestParam(required = false) String filterField, @RequestParam(required = false) String filterValue) {
        Document filter = new Document();
        if (filterField != null && filterValue != null) {
            filter.append(filterField, filterValue);
        }
        List<Document> items = itemsService.retrieveItemsFromMongo(filter);
        if (items != null)
            return ResponseEntity.ok(items);
        else
            return ResponseEntity.status(500).body(null);
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertItem(
            @RequestParam("productName") String productName,
            @RequestParam("numInStock") int numInStock,
            @RequestParam("price") double price
    ) {
        // Process the request with productName, numInStock, and price only
        boolean success = itemsService.insertItemToMongo(productName, numInStock, price, null);
        if (success) {
            return ResponseEntity.ok("Item inserted successfully.");
        }
        else {
            return ResponseEntity.status(500).body("Failed to insert item.");
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modifyItem(@RequestBody Document filter, @RequestBody Document updatedFields) {
        boolean success = itemsService.modifyItemInMongo(filter, updatedFields);
        if (success)
            return ResponseEntity.ok("Item modified successfully.");
        else
            return ResponseEntity.status(404).body("Failed to modify item.");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestBody Document filter) {
        boolean success = itemsService.deleteItemFromMongo(filter);
        if (success)
            return ResponseEntity.ok("Item deleted successfully.");
        else
            return ResponseEntity.status(404).body("Failed to delete item.");
    }
}
