package com.rms.rms.models;

import java.time.LocalDateTime;

public class OrderHistory {
    private final int orderId;
    private final int tableNumber;
    private final double totalPrice;
    private final LocalDateTime orderDate;

    public OrderHistory(int orderId, int tableNumber, double totalPrice, LocalDateTime orderDate){
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public int getOrderId(){
        return orderId;
    }

    public int getTableNumber(){
        return tableNumber;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    public LocalDateTime getOrderDate(){
        return orderDate;
    }
}
