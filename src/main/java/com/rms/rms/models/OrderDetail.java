package com.rms.rms.models;

public class OrderDetail {
    private final String foodName;
    private final String foodSection;
    private final int quantity;
    private final double unitPrice;

    public OrderDetail(String foodName, String foodSection, int quantity, double unitPrice) {
        this.foodName = foodName;
        this.foodSection = foodSection;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return quantity * unitPrice;
    }

    public double getUnitPrice(){
        return unitPrice;
    }

    public String getFoodName(){
        return foodName;
    }

    public String getFoodSection(){
        return foodSection;
    }

    public int getQuantity(){
        return quantity;
    }


}
