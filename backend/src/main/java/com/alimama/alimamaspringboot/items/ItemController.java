package com.alimama.alimamaspringboot.items;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> retrieveItems(@RequestParam(required = false) String filterField,
                                           @RequestParam(required = false) String filterValue,
                                           @RequestParam(required = false) Integer vendorId,
                                           @RequestParam(required = false) String id) {
        Document filter = new Document();

        // Filter by item ID (_id)
        if (id != null) {
            try {
                ObjectId objectId = new ObjectId(id); // MongoDB ObjectId conversion
                Document item = itemsService.retrieveItemById(objectId);
                if (item != null) {
                    return ResponseEntity.ok(item); // Return the single item
                }
                else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Item not found.");
                }
            }
            catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ObjectId format.");
            }
        }

        // Filter by vendorId
        if (vendorId != null) {
            filter.append("vendorId", vendorId);
        }

        // Additional filtering by a custom field
        if (filterField != null && filterValue != null)
            filter.append(filterField, filterValue);

        List<Document> items = itemsService.retrieveItemsFromMongo(filter);

        if (items != null && !items.isEmpty())
            return ResponseEntity.ok(items); // Return list of items
        else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No items found.");
    }

    @PostMapping("/insert")
    public ResponseEntity<String> insertItem(@RequestBody ItemRequest itemRequest) {

        int vendorId = itemRequest.getVendorId();
        String productName = itemRequest.getProductName();
        int numInStock = itemRequest.getNumInStock();
        double price = itemRequest.getPrice();
        List<String> tags = itemRequest.getTags();
        double ratingAvgTotal = itemRequest.getRatingAvgTotal();

        boolean success = itemsService.insertItemToMongo(vendorId, productName, numInStock, price, null, tags, ratingAvgTotal);

        if (success)
            return ResponseEntity.ok("Item inserted successfully.");
        else
            return ResponseEntity.status(500).body("Failed to insert item.");
    }


    @PutMapping("/modify/{id}")
    public ResponseEntity<String> modifyItem(@PathVariable String id, @RequestBody Map<String, Object> updateRequest) {
        if (!itemsService.isValidObjectId(id)) {
            return ResponseEntity.badRequest().body("Invalid item ID.");
        }

        Document filter = new Document("_id", new ObjectId(id));
        Document updatedFields = new Document(updateRequest);

        // Log
        System.out.println("Filter: " + filter.toJson());
        System.out.println("Update Request: " + updateRequest.toString());

        // Validate the update document
        System.out.println("Update Fields: " + updatedFields.toJson());

        boolean success = itemsService.modifyItemInMongo(filter, updatedFields);
        if (success) {
            return ResponseEntity.ok("Item modified successfully.");
        }
        else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to modify item.");
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteItem(@PathVariable String id) {
        // Validate ObjectId format using the instance method of ItemService
        if (!itemsService.isValidObjectId(id)) {
            return ResponseEntity.badRequest().body("Invalid ID format.");
        }

        // Attempt to delete the item
        boolean success = itemsService.deleteItemFromMongo(id);
        if (success) {
            return ResponseEntity.ok("Item deleted successfully.");
        }
        else {
            return ResponseEntity.status(404).body("Failed to delete item.");
        }
    }
}
