package com.vehiclerental;

import com.vehiclerental.dao.UserDAO;
import com.vehiclerental.entities.User;
import com.vehiclerental.util.MongoDBConnection;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        MongoDBConnection.getDatabase();
        createDefaultAdminIfNotExists();

        // Set window icon
        stage.getIcons().add(
                new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(
                                        "/com/vehiclerental/images/auriva-icon.png"
                                )
                        )
                )
        );

        stage.setTitle("Auriva Vehicle Rentals");

        // Load splash screen
        FXMLLoader splashLoader = new FXMLLoader(
                Objects.requireNonNull(
                        getClass().getResource("/com/vehiclerental/splash.fxml")
                )
        );

        Parent splashRoot = splashLoader.load();
        Scene splashScene = new Scene(splashRoot, 600, 400);
        splashScene.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/com/vehiclerental/styles/auriva.css")
                ).toExternalForm()
        );

        stage.setScene(splashScene);
        stage.centerOnScreen();
        stage.show();

        // Wait 3 seconds then fade to welcome screen(for transto purpose)
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            try {
                FXMLLoader welcomeLoader = new FXMLLoader(
                        Objects.requireNonNull(
                                getClass().getResource("/com/vehiclerental/welcome.fxml")
                        )
                );

                Parent welcomeRoot = welcomeLoader.load();
                Scene welcomeScene = new Scene(welcomeRoot, 1060, 660);
                welcomeScene.getStylesheets().add(
                        Objects.requireNonNull(
                                getClass().getResource("/com/vehiclerental/styles/auriva.css")
                        ).toExternalForm()
                );

                // Fade out splash
                FadeTransition fadeOut = new FadeTransition(
                        Duration.millis(500), splashRoot
                );
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    stage.setScene(welcomeScene);
                    stage.setWidth(1060);
                    stage.setHeight(660);
                    stage.centerOnScreen();
                });
                fadeOut.play();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }

    private void createDefaultAdminIfNotExists() {
        try {
            UserDAO userDAO = new UserDAO();
            if (!userDAO.adminExists()) {
                User admin = new User(
                        "Administrator",
                        "admin",
                        "admin123",
                        "Admin"
                );
                userDAO.addUser(admin);
                System.out.println("Default admin created!");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
            } else {
                System.out.println("Admin already exists!");
            }
        } catch (Exception e) {
            System.out.println("Could not create admin: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        System.setProperty("https.protocols", "TLSv1.2");
        System.setProperty("jdk.tls.disabledAlgorithms", "");
        launch(args);
    }
}