package com.vehiclerental.controller;

import com.vehiclerental.dao.CustomerDAO;
import com.vehiclerental.dao.RentalDAO;
import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.entities.Rental;
import com.vehiclerental.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label roleBadge;
    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    @FXML private Label totalVehiclesLabel;
    @FXML private Label availableVehiclesLabel;
    @FXML private Label activeRentalsLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private Label overdueLabel;
    @FXML private VBox overdueAlert;
    @FXML private TableView<Rental> recentRentalsTable;
    @FXML private TableColumn<Rental, String> rentalCustomerCol;
    @FXML private TableColumn<Rental, String> rentalVehicleCol;
    @FXML private TableColumn<Rental, String> rentalStartCol;
    @FXML private TableColumn<Rental, String> rentalReturnCol;
    @FXML private TableColumn<Rental, String> rentalStatusCol;
    @FXML private TableColumn<Rental, Double> rentalCostCol;
    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button usersBtn;
    @FXML private Button aboutBtn;

    private User loggedInUser;
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private RentalDAO rentalDAO = new RentalDAO();

    @FXML
    public void initialize() {
        setupButtonHover(dashboardBtn, true);
        setupButtonHover(vehiclesBtn, false);
        setupButtonHover(customersBtn, false);
        setupButtonHover(rentalsBtn, false);
        setupButtonHover(usersBtn, false);
        setupButtonHover(aboutBtn, false);
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        loadDashboard();
    }

    private void loadDashboard() {
        // Set welcome message here
        welcomeLabel.setText("Welcome back, " + loggedInUser.getFullName() + "!");
        dateLabel.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, MMMM d yyyy")
        ));
        roleBadge.setText(loggedInUser.getRole());
        loggedInName.setText(loggedInUser.getFullName());
        loggedInRole.setText(loggedInUser.getRole());

        // Hide users button if Staff is the one logged in
        if (loggedInUser.getRole().equals("Staff")) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
        }

        // Load stats
        loadStats();

        // Load recent rentals table
        loadRecentRentals();

        // Check overdue
        checkOverdue();
    }

    private void loadStats() {
        try {
            int totalVehicles = vehicleDAO.getAllVehicles().size();
            int availableVehicles = vehicleDAO.getAvailableVehicles().size();
            int activeRentals = rentalDAO.getActiveRentals().size();
            int totalCustomers = customerDAO.getAllCustomers().size();

            totalVehiclesLabel.setText(String.valueOf(totalVehicles));
            availableVehiclesLabel.setText(String.valueOf(availableVehicles));
            activeRentalsLabel.setText(String.valueOf(activeRentals));
            totalCustomersLabel.setText(String.valueOf(totalCustomers));
        } catch (Exception e) {
            System.out.println("Error loading stats: " + e.getMessage());
        }
    }

    private void loadRecentRentals() {
        try {
            rentalCustomerCol.setCellValueFactory(
                    new PropertyValueFactory<>("customerName"));
            rentalVehicleCol.setCellValueFactory(
                    new PropertyValueFactory<>("vehiclePlate"));
            rentalStartCol.setCellValueFactory(
                    new PropertyValueFactory<>("startDate"));
            rentalReturnCol.setCellValueFactory(
                    new PropertyValueFactory<>("expectedReturnDate"));
            rentalStatusCol.setCellValueFactory(
                    new PropertyValueFactory<>("status"));
            rentalCostCol.setCellValueFactory(
                    new PropertyValueFactory<>("totalCost"));

            List<Rental> rentals = rentalDAO.getAllRentals();
            ObservableList<Rental> data = FXCollections.observableArrayList(rentals);
            recentRentalsTable.setItems(data);
        } catch (Exception e) {
            System.out.println("Error loading rentals: " + e.getMessage());
        }
    }

    private void checkOverdue() {
        try {
            List<Rental> overdue = rentalDAO.getOverdueRentals();
            if (!overdue.isEmpty()) {
                overdueAlert.setVisible(true);
                overdueAlert.setManaged(true);
                overdueLabel.setText(overdue.size() +
                        "rental(s) are past their return date. Please follow up.");
            }
        } catch (Exception e) {
            System.out.println("Error checking overdue: " + e.getMessage());
        }
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/" + fxmlFile)
            );
            Parent root = loader.load();

            // Pass user to next controller
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
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Navigation error: " + e.getMessage());
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
    @FXML private void handleVehicles() { navigateTo("vehicle.fxml"); }
    @FXML private void handleCustomers() { navigateTo("customer.fxml"); }
    @FXML private void handleRentals() { navigateTo("rental.fxml"); }
    @FXML private void handleUsers() { navigateTo("users.fxml"); }
    @FXML private void handleAbout() { navigateTo("about.fxml"); }

    @FXML
    private void handleLogout() {
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
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
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