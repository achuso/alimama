package com.alimama.alimamaspringboot.items;

import java.util.List;

public class ItemRequest {
    private int vendorId;
    private String productName;
    private int numInStock;
    private double price;
    private List<String> tags;
    private double ratingAvgTotal;

    public int getVendorId() {return vendorId;}
    public void setVendorId(int vendorId) {this.vendorId = vendorId;}

    public String getProductName() {return productName;}
    public void setProductName(String productName) {this.productName = productName;}

    public int getNumInStock() {return numInStock;}
    public void setNumInStock(int numInStock) {this.numInStock = numInStock;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public List<String> getTags() {return tags;}
    public void setTags(List<String> tags) {this.tags = tags;}

    public double getRatingAvgTotal() {return ratingAvgTotal;}
    public void setRatingAvgTotal(double ratingAvgTotal) {this.ratingAvgTotal = ratingAvgTotal;}
}
