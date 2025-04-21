package com.rms.rms.models;

public class OrderDetails {

    private final String foodName;
    private final String foodSection;
    private final int quantity;
    private final double itemPrice;

    public OrderDetails(String foodName, String foodSection, int quantity, double itemPrice) {
        this.foodName = foodName;
        this.foodSection = foodSection;
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public String getFoodName() { return foodName; }
    public String getFoodSection() { return foodSection; }
    public int getQuantity() { return quantity; }
    public double getItemPrice() { return itemPrice; }
    public double getTotalPrice() { return quantity * itemPrice; }
}
