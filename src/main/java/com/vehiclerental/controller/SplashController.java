package com.vehiclerental.controller;

import com.vehiclerental.dao.UserDAO;
import com.vehiclerental.entities.User;
import com.vehiclerental.util.MongoDBConnection;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashController {

    @FXML private VBox splashBox;
    @FXML private ProgressBar loadingBar;
    @FXML private Label loadingLabel;

    @FXML
    public void initialize() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(1200), splashBox);
        // Fade duration extended to 1.2 seconds for a slower, more elegant entrance
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(loadingBar.progressProperty(), 0)),
                // At 0ms: bar starts at 0%

                new KeyFrame(Duration.millis(1500),
                        e -> loadingLabel.setText("Initializing system..."),
                        new KeyValue(loadingBar.progressProperty(), 0.1)),
                // At 1.5s: first status message, bar moves to 10%

                new KeyFrame(Duration.millis(3000),
                        e -> loadingLabel.setText("Connecting to database..."),
                        new KeyValue(loadingBar.progressProperty(), 0.3)),
                // At 3s: database message, bar at 30%

                new KeyFrame(Duration.millis(5000),
                        e -> loadingLabel.setText("Loading vehicle records..."),
                        new KeyValue(loadingBar.progressProperty(), 0.5)),
                // At 5s: vehicle records message, bar at 50%

                new KeyFrame(Duration.millis(7000),
                        e -> loadingLabel.setText("Setting up your workspace..."),
                        new KeyValue(loadingBar.progressProperty(), 0.7)),
                // At 7s: workspace message, bar at 70%

                new KeyFrame(Duration.millis(9000),
                        e -> loadingLabel.setText("Almost ready..."),
                        new KeyValue(loadingBar.progressProperty(), 0.88)),
                // At 9s: almost ready message, bar at 88%

                new KeyFrame(Duration.millis(11000),
                        e -> {
                            loadingLabel.setText("Welcome to Auriva!");
                            loadingBar.setProgress(1.0);
                            // At 11s: final message shown, bar forced to 100%

                            new Thread(() -> {
                                // Background thread handles DB work so the UI stays responsive
                                try {
                                    MongoDBConnection.getDatabase();
                                    // Opens the MongoDB connection

                                    UserDAO userDAO = new UserDAO();
                                    if (!userDAO.adminExists()) {
                                        // Only creates the admin account if one doesn't already exist
                                        User admin = new User(
                                                "Administrator",
                                                "admin",
                                                "admin123",
                                                "Admin"
                                        );
                                        userDAO.addUser(admin);
                                    }
                                } catch (Exception e2) {
                                    System.out.println("Setup error: " + e2.getMessage());
                                }

                                Platform.runLater(() -> openWelcomeScreen());
                                // Hands control back to the JavaFX thread before touching the UI
                            }).start();
                        },
                        new KeyValue(loadingBar.progressProperty(), 1.0))
        );
        timeline.play();
    }

    private void openWelcomeScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/welcome.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 1060, 660);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) splashBox.getScene().getWindow();
            // Retrieves the current window by going up from splashBox to its Scene, then to the Stage

            FadeTransition fadeOut = new FadeTransition(Duration.millis(700), splashBox);
            // 700ms fade-out before switching scenes
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                stage.setScene(scene);
                stage.setWidth(1060);
                stage.setHeight(660);
                stage.centerOnScreen();
                stage.show();
                // Scene swap only happens after the fade fully completes
            });
            fadeOut.play();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}