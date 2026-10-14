package com.vehiclerental.controller;

import com.vehiclerental.dao.UserDAO;
import com.vehiclerental.entities.User;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginController {

    @FXML private Label roleLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Button backBtn;
    @FXML private Label errorLabel;
    @FXML private VBox loginCard;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button showPasswordBtn;


    private String role;
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
if (loginCard != null) {
            TranslateTransition slide = new TranslateTransition(Duration.millis(600), loginCard);
            slide.setFromY(50);
            slide.setToY(0);
            slide.play();

            FadeTransition fade = new FadeTransition(Duration.millis(600), loginCard);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();
        }
        roleComboBox.getItems().addAll("Admin", "Staff");
        roleComboBox.setValue("Staff");

    }


    private void createDefaultAdmin() {
        User defaultAdmin = new User(
                "Administrator",
                "admin",
                "admin123",
                "Admin"
        );
        userDAO.addUser(defaultAdmin);
        System.out.println("Default admin created!");
        System.out.println("Username: admin | Password: admin123");
    }

    @FXML
    private void handleLogin() {
        role = roleComboBox.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        User user = userDAO.login(username, password);

        if (user == null) {
            showError("Invalid username or password");
            shakeCard();
            return;
        }

        if (!user.getRole().equals(role)) {
            showError("Invalid credentials for " + role + " login");
            shakeCard();
            return;
        }

        openDashboard(user);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        FadeTransition fade = new FadeTransition(Duration.millis(300), errorLabel);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void shakeCard() {
        TranslateTransition shake = new TranslateTransition(Duration.millis(80), loginCard);
        shake.setFromX(0);
        shake.setByX(15);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/welcome.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) backBtn.getScene().getWindow();

            FadeTransition fade = new FadeTransition(Duration.millis(500), root);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/dashboard.fxml")
            );
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUser(user);

            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );

            Stage stage = (Stage) loginBtn.getScene().getWindow();

            FadeTransition fade = new FadeTransition(Duration.millis(800), root);
            fade.setFromValue(0.0);
            fade.setToValue(1.0);
            fade.play();

            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TextField to show password as plain text and vice-versa
    private TextField visiblePasswordField;
    private boolean passwordVisible = false;

    @FXML
    private void handleShowPassword() {
        if (!passwordVisible) {
            // Show password
            visiblePasswordField = new TextField();
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setPrefWidth(passwordField.getPrefWidth());
            visiblePasswordField.setPrefHeight(passwordField.getPrefHeight());
            visiblePasswordField.setStyle(passwordField.getStyle());
            visiblePasswordField.setPromptText("Enter your password");

            // Replace passwordField with visiblePasswordField in the HBox
            javafx.scene.layout.HBox hbox =
                    (javafx.scene.layout.HBox) passwordField.getParent();
            int index = hbox.getChildren().indexOf(passwordField);
            hbox.getChildren().set(index, visiblePasswordField);

            showPasswordBtn.setText("off");
            passwordVisible = true;
        } else {
            // Hide password again
            passwordField.setText(visiblePasswordField.getText());

            javafx.scene.layout.HBox hbox =
                    (javafx.scene.layout.HBox) visiblePasswordField.getParent();
            int index = hbox.getChildren().indexOf(visiblePasswordField);
            hbox.getChildren().set(index, passwordField);

            showPasswordBtn.setText("on");
            passwordVisible = false;
        }
    }
}