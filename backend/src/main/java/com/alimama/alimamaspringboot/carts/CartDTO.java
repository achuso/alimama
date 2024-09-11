package com.alimama.alimamaspringboot.carts;

import java.util.List;

public class CartDTO {

    private String userId;
    private List<CartItemDTO> items;
    private double totalAmount;

    public CartDTO() {}

    public CartDTO(String userId, List<CartItemDTO> items, double totalAmount) {
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
    }

    public String getUserId() {return userId;}
    public void setUserId(String userId) {this.userId = userId;}

    public List<CartItemDTO> getItems() {return items;}
    public void setItems(List<CartItemDTO> items) {this.items = items;}

    public double getTotalAmount() {return totalAmount;}
    public void setTotalAmount(double totalAmount) {this.totalAmount = totalAmount;}
}
