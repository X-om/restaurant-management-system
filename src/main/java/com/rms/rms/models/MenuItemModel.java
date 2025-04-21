package com.rms.rms.models;

public class MenuItemModel {
    private int id;
    private String name;
    private double price;

    public MenuItemModel(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    // Getters
    public String getName() { return name; }
    public double getPrice() { return price; }

    public int getId() {
        return id;
    }
}