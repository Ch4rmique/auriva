package com.vehiclerental.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vehiclerental.entities.Rental;
import com.vehiclerental.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalDAO {

    // Direct link to the "rentals" collection in MongoDB
    private MongoCollection<Document> collection;

    // Constructor - connects to MongoDB rentals collection
    public RentalDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        collection = database.getCollection("rentals");
    }

    // SAVE a new rental to MongoDB
    // Dates are saved as Strings because MongoDB handles
    // Java LocalDate better as text
    public void addRental(Rental rental) {
        Document doc = new Document()
                .append("customerName", rental.getCustomerName())
                .append("vehiclePlate", rental.getVehiclePlate())
                .append("startDate", rental.getStartDate().toString())
                .append("expectedReturnDate", rental.getExpectedReturnDate().toString())
                .append("totalCost", rental.getTotalCost())
                .append("status", rental.getStatus());
        collection.insertOne(doc);
    }

    // GET all rentals from MongoDB
    public List<Rental> getAllRentals() {
        List<Rental> rentals = new ArrayList<>();
        for (Document doc : collection.find()) {
            Rental r = new Rental();
            r.setId(doc.getObjectId("_id"));
            r.setCustomerName(doc.getString("customerName"));
            r.setVehiclePlate(doc.getString("vehiclePlate"));
            // Parse converts the String back to a LocalDate
            r.setStartDate(LocalDate.parse(doc.getString("startDate")));
            r.setExpectedReturnDate(LocalDate.parse(doc.getString("expectedReturnDate")));
            r.setTotalCost(doc.getDouble("totalCost"));
            r.setStatus(doc.getString("status"));
            String actualReturn = doc.getString("actualReturnDate");
            // actualReturnDate only exists after vehicle is returned
            // so we check if it is null first
            if (actualReturn != null) {
                r.setActualReturnDate(LocalDate.parse(actualReturn));
            }
            rentals.add(r);
        }
        return rentals;
    }

    // GET only active rentals
    // Used for dashboard and overdue checking
    public List<Rental> getActiveRentals() {
        List<Rental> rentals = new ArrayList<>();
        for (Document doc : collection.find(new Document("status", "Active"))) {
            Rental r = new Rental();
            r.setId(doc.getObjectId("_id"));
            r.setCustomerName(doc.getString("customerName"));
            r.setVehiclePlate(doc.getString("vehiclePlate"));
            r.setStartDate(LocalDate.parse(doc.getString("startDate")));
            r.setExpectedReturnDate(LocalDate.parse(doc.getString("expectedReturnDate")));
            r.setTotalCost(doc.getDouble("totalCost"));
            r.setStatus(doc.getString("status"));
            rentals.add(r);
        }
        return rentals;
    }

    // GET overdue rentals
    // These are Active rentals where expected return date has passed
    public List<Rental> getOverdueRentals() {
        List<Rental> overdueRentals = new ArrayList<>();
        // Get all active rentals first
        List<Rental> activeRentals = getActiveRentals();
        // Then check which ones are past their return date
        for (Rental r : activeRentals) {
            if (r.getExpectedReturnDate().isBefore(LocalDate.now())) {
                overdueRentals.add(r);
            }
        }
        return overdueRentals;
    }

    // PROCESS a vehicle return
    // Sets status to Returned and records the actual return date
    public void returnVehicle(ObjectId rentalId, LocalDate actualReturnDate) {
        collection.updateOne(
                new Document("_id", rentalId),
                new Document("$set", new Document()
                        .append("status", "Returned")
                        .append("actualReturnDate", actualReturnDate.toString())
                )
        );
    }

    // DELETE a rental
    public void deleteRental(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }
}