package com.vehiclerental.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vehiclerental.entities.Customer;
import com.vehiclerental.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Direct link to the "customers" collection in MongoDB
    private MongoCollection<Document> collection;

    // Constructor - connects to MongoDB customers collection
    public CustomerDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        collection = database.getCollection("customers");
    }

    // SAVE a new customer to MongoDB
    public void addCustomer(Customer customer) {
        Document doc = new Document()
                .append("fullName", customer.getFullName())
                .append("phone", customer.getPhone())
                .append("email", customer.getEmail())
                .append("licenseNumber", customer.getLicenseNumber());
        collection.insertOne(doc);
    }

    // GET all customers from MongoDB
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (Document doc : collection.find()) {
            Customer c = new Customer();
            c.setId(doc.getObjectId("_id"));
            c.setFullName(doc.getString("fullName"));
            c.setPhone(doc.getString("phone"));
            c.setEmail(doc.getString("email"));
            c.setLicenseNumber(doc.getString("licenseNumber"));
            customers.add(c);
        }
        return customers;
    }

    // SEARCH customers by name
    // Used for the search bar on the customer screen
    public List<Customer> searchCustomers(String name) {
        List<Customer> customers = new ArrayList<>();
        // regex makes the search case insensitive
        // so "john" finds "John" and "JOHN" too
        Document filter = new Document("fullName",
                new Document("$regex", name).append("$options", "i"));
        for (Document doc : collection.find(filter)) {
            Customer c = new Customer();
            c.setId(doc.getObjectId("_id"));
            c.setFullName(doc.getString("fullName"));
            c.setPhone(doc.getString("phone"));
            c.setEmail(doc.getString("email"));
            c.setLicenseNumber(doc.getString("licenseNumber"));
            customers.add(c);
        }
        return customers;
    }

    // UPDATE customer details
    public void updateCustomer(Customer customer) {
        collection.updateOne(
                new Document("_id", customer.getId()),
                new Document("$set", new Document()
                        .append("fullName", customer.getFullName())
                        .append("phone", customer.getPhone())
                        .append("email", customer.getEmail())
                        .append("licenseNumber", customer.getLicenseNumber())
                )
        );
    }

    // DELETE a customer permanently
    public void deleteCustomer(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }
}