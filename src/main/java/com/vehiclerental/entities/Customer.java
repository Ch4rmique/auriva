package com.vehiclerental.entities;



import org.bson.types.ObjectId;

public class Customer {

    private ObjectId id;
    private String fullName;
    private String phone;
    private String email;
    private String licenseNumber;

    public Customer() {}

    public Customer(String fullName, String phone, String email, String licenseNumber) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.licenseNumber = licenseNumber;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    @Override
    public String toString() {
        return fullName + " (" + licenseNumber + ")";
    }
}
