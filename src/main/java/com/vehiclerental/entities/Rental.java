package com.vehiclerental.entities;


import org.bson.types.ObjectId;
import java.time.LocalDate;

public class Rental {

    private ObjectId id;
    private String customerName;
    private String vehiclePlate;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private double totalCost;
    private String status;      // Active, Returned servced etc

    public Rental() {}

    public Rental(String customerName, String vehiclePlate, LocalDate startDate, LocalDate expectedReturnDate, double dailyRate) {
        this.customerName = customerName;
        this.vehiclePlate = vehiclePlate;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = "Active";
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, expectedReturnDate);
        this.totalCost = days * dailyRate;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }
    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}