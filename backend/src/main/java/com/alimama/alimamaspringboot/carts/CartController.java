package com.alimama.alimamaspringboot.carts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:8080")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addItemToCart(@RequestBody CartItemDTO cartItemDTO, @RequestParam String userId) {
        cartService.addItemToCart(userId, cartItemDTO);
        return ResponseEntity.ok("Item added to cart successfully");
    }

    // Get user's cart by userId
    @GetMapping("/get/{userId}")
    public ResponseEntity<CartDTO> getCartByUserId(@PathVariable String userId) {
        CartDTO cartDTO = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateCart(@RequestBody CartDTO cartDTO) {
        cartService.updateCart(cartDTO.getUserId(), cartDTO.getItems());
        return ResponseEntity.ok("Cart updated successfully");
    }

    // Empty the cart
    @PutMapping("/empty")
    public ResponseEntity<String> emptyCart(@RequestBody CartDTO cartDTO) {
        cartService.emptyCart(cartDTO.getUserId());
        return ResponseEntity.ok("Cart emptied successfully");
    }
}