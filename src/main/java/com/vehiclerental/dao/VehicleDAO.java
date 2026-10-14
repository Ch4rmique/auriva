package com.vehiclerental.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vehiclerental.entities.Vehicle;
import com.vehiclerental.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    private MongoCollection<Document> collection;

    public VehicleDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        collection = database.getCollection("vehicles");
    }

    // Convert MongoDB document to Vehicle object
    private Vehicle documentToVehicle(Document doc) {
        Vehicle v = new Vehicle();
        v.setId(doc.getObjectId("_id"));
        v.setPlateNumber(doc.getString("plateNumber"));
        v.setBrand(doc.getString("brand"));
        v.setModel(doc.getString("model"));
        v.setYear(doc.getInteger("year", 0));
        v.setColor(doc.getString("color"));
        v.setType(doc.getString("type"));
        v.setDailyRate(doc.getDouble("dailyRate"));
        v.setStatus(doc.getString("status"));
        v.setMileage(doc.getInteger("mileage", 0));
        v.setDescription(doc.getString("description"));
        return v;
    }

    // Save new vehicle
    public void addVehicle(Vehicle vehicle) {
        Document doc = new Document()
                .append("plateNumber", vehicle.getPlateNumber())
                .append("brand", vehicle.getBrand())
                .append("model", vehicle.getModel())
                .append("year", vehicle.getYear())
                .append("color", vehicle.getColor())
                .append("type", vehicle.getType())
                .append("dailyRate", vehicle.getDailyRate())
                .append("status", vehicle.getStatus())
                .append("mileage", vehicle.getMileage())
                .append("description", vehicle.getDescription());
        collection.insertOne(doc);
        System.out.println("Vehicle added: " + vehicle.getBrand());
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        for (Document doc : collection.find()) {
            vehicles.add(documentToVehicle(doc));
        }
        return vehicles;
    }

    // Get available vehicles only
    public List<Vehicle> getAvailableVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        for (Document doc : collection.find(
                new Document("status", "Available"))) {
            vehicles.add(documentToVehicle(doc));
        }
        return vehicles;
    }

    // Search vehicles by brand or model
    public List<Vehicle> searchVehicles(String keyword) {
        List<Vehicle> vehicles = new ArrayList<>();
        Document filter = new Document("$or", List.of(
                new Document("brand", new Document("$regex", keyword)
                        .append("$options", "i")),
                new Document("model", new Document("$regex", keyword)
                        .append("$options", "i")),
                new Document("plateNumber", new Document("$regex", keyword)
                        .append("$options", "i"))
        ));
        for (Document doc : collection.find(filter)) {
            vehicles.add(documentToVehicle(doc));
        }
        return vehicles;
    }

    // Filter vehicles by status
    public List<Vehicle> getVehiclesByStatus(String status) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (Document doc : collection.find(
                new Document("status", status))) {
            vehicles.add(documentToVehicle(doc));
        }
        return vehicles;
    }

    // Update vehicle status only
    public void updateVehicleStatus(ObjectId id, String status) {
        collection.updateOne(
                new Document("_id", id),
                new Document("$set", new Document("status", status))
        );
    }

    // Update full vehicle details
    public void updateVehicle(Vehicle vehicle) {
        collection.updateOne(
                new Document("_id", vehicle.getId()),
                new Document("$set", new Document()
                        .append("plateNumber", vehicle.getPlateNumber())
                        .append("brand", vehicle.getBrand())
                        .append("model", vehicle.getModel())
                        .append("year", vehicle.getYear())
                        .append("color", vehicle.getColor())
                        .append("type", vehicle.getType())
                        .append("dailyRate", vehicle.getDailyRate())
                        .append("status", vehicle.getStatus())
                        .append("mileage", vehicle.getMileage())
                        .append("description", vehicle.getDescription())
                )
        );
    }

    // Delete vehicle
    public void deleteVehicle(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
        System.out.println("Vehicle deleted.");
    }
}