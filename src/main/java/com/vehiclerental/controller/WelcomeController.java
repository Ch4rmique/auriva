package com.vehiclerental.controller;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WelcomeController {

    @FXML private VBox rootVBox;
    @FXML private Button loginBtn;

    @FXML
    public void initialize() {
        if (rootVBox != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(1500), rootVBox);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
    }

    @FXML
    private void handleGetStarted() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/login.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) rootVBox.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            FadeTransition fade = new FadeTransition(Duration.millis(800), root);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}