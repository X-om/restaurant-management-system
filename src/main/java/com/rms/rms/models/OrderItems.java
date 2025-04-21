package com.rms.rms.models;

public class OrderItems {
    private int menuItemId;
    private int quantity;
    private double price;

    public OrderItems(int menuItemId, int quantity, double price){
        this.menuItemId = menuItemId;
        this.quantity = quantity;
        this.price = price;
    }

    public double getPrice(){
        return  price;
    }

    public double getTotalPrice(){
        return (quantity * price);
    }

    public int getQuantity(){
        return quantity;
    }

    public int getMenuItemId(){
        return menuItemId;
    }

    public void setQuantity(int quantity) { this.quantity = quantity; }

}
