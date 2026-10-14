package com.vehiclerental.dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vehiclerental.entities.User;
import com.vehiclerental.util.MongoDBConnection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mindrot.jbcrypt.BCrypt;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Direct link to the "users" collection in MongoDB
    private MongoCollection<Document> collection;

    // Constructor - connects to MongoDB users collection
    public UserDAO() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        collection = database.getCollection("users");
    }

    // SAVE a new user to MongoDB
    // Password is hashed with BCrypt before saving
    // The real password is NEVER stored anywhere
    public void addUser(User user) {
        // BCrypt.hashpw scrambles the password
        // "10" is the strength level - higher = more secure but slower
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
        Document doc = new Document()
                .append("fullName", user.getFullName())
                .append("username", user.getUserName())
                .append("password", hashedPassword)
                .append("role", user.getRole());
        collection.insertOne(doc);
    }

    // LOGIN CHECK
    // Finds the user by username then checks password with BCrypt
    // Returns the User object if login is correct
    // Returns null if username not found or password is wrong
    public User login(String username, String password) {
        // Step 1 - find the user by username
        Document doc = collection.find(new Document("username", username)).first();

        // Step 2 - if no user found return null immediately
        if (doc == null) return null;

        // Step 3 - get the stored hash from MongoDB
        String storedHash = doc.getString("password");

        // Step 4 - BCrypt checks if typed password matches the hash
        // It scrambles what was typed and compares - never unscrambles
        if (!BCrypt.checkpw(password, storedHash)) return null;

        // Step 5 - password correct so build and return the User object
        User user = new User();
        user.setId(doc.getObjectId("_id"));
        user.setFullName(doc.getString("fullName"));
        user.setUserName(doc.getString("username"));
        user.setPassword(storedHash);
        user.setRole(doc.getString("role"));
        return user;
    }

    // CHECK if any admin account exists in the database
    // Used on first launch to create default admin if none exists
    public boolean adminExists() {
        Document doc = collection.find(new Document("role", "Admin")).first();
        return doc != null;
    }

    // GET all users - only Admin can see this
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            User u = new User();
            u.setId(doc.getObjectId("_id"));
            u.setFullName(doc.getString("fullName"));
            u.setUserName(doc.getString("username"));
            u.setPassword(doc.getString("password"));
            u.setRole(doc.getString("role"));
            users.add(u);
        }
        return users;
    }

    // DELETE a user - only Admin can do this
    public void deleteUser(ObjectId id) {
        collection.deleteOne(new Document("_id", id));
    }

    // UPDATE user details
    public void updateUser(User user) {
        collection.updateOne(
                new Document("_id", user.getId()),
                new Document("$set", new Document()
                        .append("fullName", user.getFullName())
                        .append("username", user.getUserName())
                        .append("role", user.getRole())
                )
        );
    }
}