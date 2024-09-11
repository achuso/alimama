package com.alimama.alimamaspringboot.carts;

import com.alimama.alimamaspringboot.MongoDBConnection;
import org.bson.Document;
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

    // Convert Document from MongoDB to CartItemDTO
    private CartItemDTO documentToCartItemDTO(Document itemDocument) {
        return new CartItemDTO(
                itemDocument.getString("item_id"),
                itemDocument.getString("product_name"),
                itemDocument.getDouble("unit_price"),
                itemDocument.getInteger("quantity")
        );
    }

    // Add an item to the cart
    public void addItemToCart(String userId, CartItemDTO cartItemDTO) {
        Document query = new Document("_id", userId); // Find user's cart

        // Retrieve user's cart
        List<Document> cartDocuments = mongoDBConnection.queryReadMongoDB(cartCollectionName, query);

        Document cart;
        if (cartDocuments.isEmpty()) {
            // If the cart doesn't exist, create a new cart
            cart = new Document("_id", userId)
                    .append("items", new ArrayList<>())
                    .append("total_amount", 0.0)
                    .append("created_at", new java.util.Date());
        }
        else {
            // If the cart exists, get the first result (since user carts are unique)
            cart = cartDocuments.get(0);
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
            // If the item doesn't exist, add it to the cart
            items.add(cartItemDtoToDocument(cartItemDTO));
        }

        // Update the total amount
        double totalAmount = items.stream()
                .mapToDouble(item -> item.getDouble("unit_price") * item.getInteger("quantity"))
                .sum();

        // Update the cart document
        cart.put("items", items);
        cart.put("total_amount", totalAmount);

        // Insert/update the cart
        mongoDBConnection.queryExecuteMongoDB("update", cartCollectionName, query, new Document("$set", cart), null);
    }

    public CartDTO getCartByUserId(String userId) {
        Document query = new Document("_id", userId);  // Query to find the cart by userId

        // Retrieve the cart for the user
        List<Document> cartDocuments = mongoDBConnection.queryReadMongoDB(cartCollectionName, query);

        if (cartDocuments.isEmpty()) {
            // Return an empty cart if the user doesn't have one
            return new CartDTO(userId, new ArrayList<>(), 0.0);
        }
        else {
            Document cartDocument = cartDocuments.get(0);

            // Safely cast the items list using the helper method
            List<Document> itemDocuments = safelyCastToListOfDocuments(cartDocument.get("items"));

            // Convert itemDocuments to CartItemDTOs
            List<CartItemDTO> cartItems = new ArrayList<>();
            for (Document itemDocument : itemDocuments) {
                cartItems.add(documentToCartItemDTO(itemDocument));
            }

            return new CartDTO(userId, cartItems, cartDocument.getDouble("total_amount"));
        }
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

    // Safely cast Object to List<Document>
    private List<Document> safelyCastToListOfDocuments(Object itemsObject) {
        List<Document> items = new ArrayList<>();

        if (itemsObject instanceof List<?>)
            for (Object item : (List<?>) itemsObject)
                if (item instanceof Document)
                    items.add((Document) item);

        return items;
    }
}
