package com.alimama.alimamaspringboot.carts;

public class CartItemDTO {

    private String itemId;
    private String productName;
    private double unitPrice;
    private int quantity;

    public CartItemDTO() {}

    public CartItemDTO(String itemId, String productName, double unitPrice, int quantity) {
        this.itemId = itemId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getItemId() {return itemId;}
    public void setItemId(String itemId) {this.itemId = itemId;}

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public double getUnitPrice() {return unitPrice;}
    public void setUnitPrice(double unitPrice) {this.unitPrice = unitPrice;}

    public int getQuantity() {return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}
}
