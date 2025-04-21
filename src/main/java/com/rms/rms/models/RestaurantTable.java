package com.rms.rms.models;

public class RestaurantTable {
    private final int tableNumber;
    private final String status;

    public RestaurantTable(int tableNumber, String status) {
        this.tableNumber = tableNumber;
        this.status = status;
    }

    // Getters
    public int getTableNumber() { return tableNumber; }
    public String getStatus() { return status; }
}
