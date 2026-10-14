package com.vehiclerental.controller;

import com.vehiclerental.dao.CustomerDAO;
import com.vehiclerental.entities.Customer;
import com.vehiclerental.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class CustomerController {

    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> customerPhoneCol;
    @FXML private TableColumn<Customer, String> customerEmailCol;
    @FXML private TableColumn<Customer, String> customerLicenseCol;
    @FXML private TextField searchField;
    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    @FXML private Button usersBtn;

    
    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button aboutBtn;

    @FXML private Label customerCountLabel;
    private User loggedInUser;
    private final CustomerDAO customerDAO = new CustomerDAO();

    @FXML
    public void initialize() {
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        customerLicenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));

        loadCustomers(customerDAO.getAllCustomers());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadCustomers(customerDAO.getAllCustomers());
            } else {
                loadCustomers(customerDAO.searchCustomers(newValue.trim()));
            }
        });

        setupButtonHover(dashboardBtn, false);
        setupButtonHover(vehiclesBtn, false);
        setupButtonHover(customersBtn, true);
        setupButtonHover(rentalsBtn, false);
        setupButtonHover(usersBtn, false);
        setupButtonHover(aboutBtn, false);
    }

    //where users screen is removed from staff screen
    public void setUser(User user) {
        this.loggedInUser = user;
        loggedInName.setText(user.getFullName());
        loggedInRole.setText(user.getRole());
        if (user.getRole().equals("Staff")) {
            usersBtn.setVisible(false);
            usersBtn.setManaged(false);
        }
    }

        private void loadCustomers(List<Customer> customers) {
            ObservableList<Customer> data = FXCollections.observableArrayList(customers);
            customersTable.setItems(data);
            customerCountLabel.setText(customers.size() + " customers");
        }


    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadCustomers(customerDAO.getAllCustomers());
        } else {
            loadCustomers(customerDAO.searchCustomers(keyword));
        }
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        loadCustomers(customerDAO.getAllCustomers());
    }

    @FXML
    private void handleAddCustomer() {
        showCustomerForm(null);
    }

    @FXML
    private void handleEditCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer from the table to edit.");
            alert.showAndWait();
            return;
        }
        showCustomerForm(selected);
    }

    @FXML
    private void handleDeleteCustomer() {
        Customer selected = customersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer from the table to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete " + selected.getFullName() + "?");
        confirm.setContentText("Are you sure you want to permanently delete this customer records?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customerDAO.deleteCustomer(selected.getId());
            loadCustomers(customerDAO.getAllCustomers());
        }
    }

    private void showCustomerForm(Customer customer) {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle(customer == null ? "Add Customer" : "Edit Customer");

        VBox form = new VBox();
        form.setSpacing(12);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white;");
        form.setPrefWidth(420);

        Label title = new Label(customer == null ? "Add New Customer" : "Edit Customer Details");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        TextField nameField = createField("Full Name", customer != null ? customer.getFullName() : "");
        TextField phoneField = createField("Phone Number", customer != null ? customer.getPhone() : "");
        TextField emailField = createField("Email Address", customer != null ? customer.getEmail() : "");
        TextField licenseField = createField("Driving License Number", customer != null ? customer.getLicenseNumber() : "");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        Button saveBtn = new Button(customer == null ? "Register Customer" : "Save Changes");
        saveBtn.setPrefWidth(360);
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String license = licenseField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || license.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            Customer c = customer != null ? customer : new Customer();
            c.setFullName(name);
            c.setPhone(phone);
            c.setEmail(email);
            c.setLicenseNumber(license);

            if (customer == null) {
                customerDAO.addCustomer(c);
            } else {
                customerDAO.updateCustomer(c);
            }

            formStage.close();
            loadCustomers(customerDAO.getAllCustomers());
        });

        form.getChildren().addAll(
                title,
                new Label("Full Name:"), nameField,
                new Label("Phone Number:"), phoneField,
                new Label("Email:"), emailField,
                new Label("Driving License:"), licenseField,
                errorLabel, saveBtn
        );

        Scene scene = new Scene(form, 420, 500);
        formStage.setScene(scene);
        formStage.showAndWait();
    }

    private TextField createField(String prompt, String value) {
        TextField field = new TextField(value);
        field.setPromptText(prompt);
        field.setPrefWidth(360);
        field.setPrefHeight(40);
        field.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px; -fx-padding: 0 12px;");
        return field;
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
            Stage stage = (Stage) customersTable.getScene().getWindow();
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
    @FXML private void handleCustomers() { loadCustomers(customerDAO.getAllCustomers()); }
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
            Stage stage = (Stage) customersTable.getScene().getWindow();
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