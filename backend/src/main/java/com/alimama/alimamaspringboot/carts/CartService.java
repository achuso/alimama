package com.alimama.alimamaspringboot.carts;

import com.alimama.alimamaspringboot.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private MongoDBConnection mongoDBConnection;

    private final String cartCollectionName = "carts";

    // Constructor ensures MongoDB connection
    public CartService(MongoDBConnection mongoDBConnection) {
        this.mongoDBConnection = mongoDBConnection;
        this.mongoDBConnection.connectMongoDB();
    }

    // Convert CartItemDTO to Document for MongoDB
    private Document cartItemDtoToDocument(CartItemDTO cartItemDTO) {
        return new Document("item_id", cartItemDTO.getItemId())
                .append("product_name", cartItemDTO.getProductName())
                .append("unit_price", cartItemDTO.getUnitPrice())
                .append("quantity", cartItemDTO.getQuantity());
    }

    // Add an item to the cart
    public void addItemToCart(String userId, CartItemDTO cartItemDTO) {
        // Find the user's cart
        Document query = new Document("_id", userId);

        // Retrieve user's cart
        List<Document> cartDocuments = mongoDBConnection.queryReadMongoDB(cartCollectionName, query);

        Document cart;
        String operationType;

        if (cartDocuments.isEmpty()) {
            // Create a new cart if it doesn't exist
            cart = new Document("_id", userId)
                    .append("items", new ArrayList<>())
                    .append("total_amount", 0.0)
                    .append("created_at", new java.util.Date());
            operationType = "insert";  // Use insert if the cart is new
            System.out.println("Creating a new cart for user: " + userId);
        }
        else {
            // Get the user's existing cart
            cart = cartDocuments.get(0);
            operationType = "update";  // Use update if the cart already exists
            System.out.println("Updating existing cart for user: " + userId);
        }

        List<Document> items = safelyCastToListOfDocuments(cart.get("items"));

        // Check if the item already exists in the cart
        Document existingItem = items.stream()
                .filter(item -> item.getString("item_id").equals(cartItemDTO.getItemId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // If the item exists, update the quantity
            int newQuantity = existingItem.getInteger("quantity") + cartItemDTO.getQuantity();
            existingItem.put("quantity", newQuantity);
        } else {
            // Add the new item to the cart
            items.add(cartItemDtoToDocument(cartItemDTO));
        }

        // Update the total amount
        double totalAmount = items.stream()
                .mapToDouble(item -> item.getDouble("unit_price") * item.getInteger("quantity"))
                .sum();

        // Update the cart document
        cart.put("items", items);
        cart.put("total_amount", totalAmount);

        // Insert or update the cart in MongoDB depending on the operation type
        boolean success = mongoDBConnection.queryExecuteMongoDB(operationType, cartCollectionName, query, new Document("$set", cart), cart);

        if (success) {
            System.out.println("Cart " + operationType + " successful for user: " + userId);
        } else {
            System.err.println("Cart " + operationType + " failed for user: " + userId);
        }
    }

    public CartDTO getCartByUserId(String userId) {
        if (mongoDBConnection.connectMongoDB()) {
            Document filter;

            // Check if the userId is a valid ObjectId
            if (ObjectId.isValid(userId))
                filter = new Document("_id", new ObjectId(userId));
            else
                filter = new Document("_id", userId);

            System.out.println("Querying cart with filter: " + filter.toJson());

            List<Document> cartDocuments = mongoDBConnection.queryReadMongoDB(cartCollectionName, filter);

            if (cartDocuments != null && !cartDocuments.isEmpty()) {
                Document cart = cartDocuments.get(0);
                List<Document> itemDocuments = safelyCastToListOfDocuments(cart.get("items"));
                List<CartItemDTO> cartItems = new ArrayList<>();

                for (Document itemDocument : itemDocuments) {
                    cartItems.add(documentToCartItemDTO(itemDocument));
                }

                return new CartDTO(userId, cartItems, cart.getDouble("total_amount"));
            }
        }

        System.out.println("No cart found for user: " + userId);
        return new CartDTO(userId, new ArrayList<>(), 0.0);
    }

    // Update the cart with new items and recalculate total
    public void updateCart(String userId, List<CartItemDTO> updatedItems) {
        Document query = new Document("_id", userId);

        // Convert CartItemDTO to Document
        List<Document> updatedItemDocuments = new ArrayList<>();
        for (CartItemDTO itemDTO : updatedItems) {
            updatedItemDocuments.add(cartItemDtoToDocument(itemDTO));
        }

        // Calculate the new total amount
        double totalAmount = updatedItemDocuments.stream()
                .mapToDouble(item -> item.getDouble("unit_price") * item.getInteger("quantity"))
                .sum();

        // Update the cart with the new items and total amount
        Document update = new Document("$set", new Document("items", updatedItemDocuments)
                .append("total_amount", totalAmount));

        // Update the cart in Mongo
        mongoDBConnection.queryExecuteMongoDB("update", cartCollectionName, query, update, null);
    }

    // Empty the user's cart
    public void emptyCart(String userId) {
        Document query = new Document("_id", userId);

        Document update = new Document("$set", new Document("items", new ArrayList<>())
                .append("total_amount", 0.0));

        mongoDBConnection.queryExecuteMongoDB("update", cartCollectionName, query, update, null);
    }

    // Helper method for safely casting list of documents
    private List<Document> safelyCastToListOfDocuments(Object obj) {
        if (obj instanceof List<?>) {
            return (List<Document>) obj;
        }
        return new ArrayList<>();
    }

    // Convert Document to CartItemDTO (assume proper implementation)
    private CartItemDTO documentToCartItemDTO(Document itemDocument) {
        return new CartItemDTO(
                itemDocument.getString("item_id"),
                itemDocument.getString("product_name"),
                itemDocument.getDouble("unit_price"),
                itemDocument.getInteger("quantity")
        );
    }
}
