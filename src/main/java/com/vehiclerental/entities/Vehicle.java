package com.vehiclerental.entities;

import org.bson.types.ObjectId;

public class Vehicle {

    private ObjectId id;
    private String plateNumber;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String type;
    private double dailyRate;
    private String status;
    private int mileage;
    private String description;

    public Vehicle() {}

    public Vehicle(String plateNumber, String brand, String model,
                   int year, String color, String type,
                   double dailyRate, int mileage, String description) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.type = type;
        this.dailyRate = dailyRate;
        this.mileage = mileage;
        this.description = description;
        this.status = "Available";
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getMileage() { return mileage; }
    public void setMileage(int mileage) { this.mileage = mileage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return brand + " " + model + " (" + plateNumber + ")";
    }
}