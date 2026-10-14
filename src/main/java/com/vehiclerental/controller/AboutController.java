package com.vehiclerental.controller;

import com.vehiclerental.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutController {

    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    
    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button usersBtn;
    @FXML private Button aboutBtn;
    @FXML private Button logoutBtn;

    private User loggedInUser;

    @FXML
    public void initialize() {
        setupButtonHover(dashboardBtn, false);
        setupButtonHover(vehiclesBtn, false);
        setupButtonHover(customersBtn, false);
        setupButtonHover(rentalsBtn, false);
        setupButtonHover(usersBtn, false);
        setupButtonHover(aboutBtn, true);
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        loggedInName.setText(user.getFullName());
        loggedInRole.setText(user.getRole());
        
        if (user.getRole().equals("Staff")) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vehiclerental/" + fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController c) {
                c.setUser(loggedInUser);
            } else if (controller instanceof VehicleController c) {
                c.setUser(loggedInUser);
            } else if (controller instanceof CustomerController c) {
                c.setUser(loggedInUser);
            } else if (controller instanceof RentalController c) {
                c.setUser(loggedInUser);
            } else if (controller instanceof UsersController c) {
                c.setUser(loggedInUser);
            } else if (controller instanceof AboutController c) {
                c.setUser(loggedInUser);
            }

            javafx.animation.FadeTransition transition = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(350),
                    root
            );
            transition.setFromValue(0.0);
            transition.setToValue(1.0);
            transition.play();

            Scene scene = new Scene(root, 1100, 700);
            scene.getStylesheets().add(getClass().getResource("/com/vehiclerental/styles/auriva.css").toExternalForm());
            Stage stage = (Stage) loggedInName.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupButtonHover(Button button, boolean isActive) {
        if (button == null) return;
        String normalStyle = isActive 
            ? "-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-padding: 0 0 0 24px; -fx-cursor: hand; -fx-border-width: 0 0 0 4px; -fx-border-color: #D4AF37;"
            : "-fx-background-color: transparent; -fx-text-fill: #BFDBFE; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 0 0 0 24px; -fx-cursor: hand;";

        String hoverStyle = isActive 
            ? "-fx-background-color: #1D4ED8; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left; -fx-padding: 0 0 0 24px; -fx-cursor: hand; -fx-border-width: 0 0 0 4px; -fx-border-color: #D4AF37;"
            : "-fx-background-color: #1E3A8A; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: center-left; -fx-padding: 0 0 0 24px; -fx-cursor: hand;";

        button.setOnMouseEntered(e -> button.setStyle(hoverStyle));
        button.setOnMouseExited(e -> button.setStyle(normalStyle));
    }

    @FXML private void handleDashboard() { navigateTo("dashboard.fxml"); }
    @FXML private void handleVehicles()  { navigateTo("vehicle.fxml"); }
    @FXML private void handleCustomers() { navigateTo("customer.fxml"); }
    @FXML private void handleRentals()   { navigateTo("rental.fxml"); }
    @FXML private void handleUsers()     { navigateTo("users.fxml"); }
    @FXML private void handleAbout()     { navigateTo("about.fxml"); }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vehiclerental/welcome.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1060, 660);
            scene.getStylesheets().add(getClass().getResource("/com/vehiclerental/styles/auriva.css").toExternalForm());
            Stage stage = (Stage) loggedInName.getScene().getWindow();
            stage.setScene(scene);
            stage.setWidth(1060);
            stage.setHeight(660);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}