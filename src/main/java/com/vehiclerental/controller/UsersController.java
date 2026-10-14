package com.vehiclerental.controller;

import com.vehiclerental.dao.UserDAO;
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

public class UsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> fullNameCol;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TextField searchField;
    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    
    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button usersBtn;
    @FXML private Button aboutBtn;

    private User loggedInUser;
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        loadUsers(userDAO.getAllUsers());

        setupButtonHover(dashboardBtn, false);
        setupButtonHover(vehiclesBtn, false);
        setupButtonHover(customersBtn, false);
        setupButtonHover(rentalsBtn, false);
        setupButtonHover(usersBtn, true);
        setupButtonHover(aboutBtn, false);
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

    private void loadUsers(List<User> users) {
        ObservableList<User> data = FXCollections.observableArrayList(users);
        usersTable.setItems(data);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadUsers(userDAO.getAllUsers());
        } else {
            List<User> all = userDAO.getAllUsers();
            List<User> filtered = all.stream().filter(u ->
                    u.getFullName().toLowerCase().contains(keyword.toLowerCase()) ||
                    u.getUserName().toLowerCase().contains(keyword.toLowerCase())
            ).toList();
            loadUsers(filtered);
        }
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        loadUsers(userDAO.getAllUsers());
    }

    @FXML
    private void handleAddUser() {
        showUserForm(null);
    }

    @FXML
    private void handleEditUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user to edit.");
            alert.showAndWait();
            return;
        }
        showUserForm(selected);
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
            return;
        }

        if (selected.getUserName().equals("admin")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Restricted Action");
            alert.setHeaderText("Action Blocked");
            alert.setContentText("The master system admin account cannot be deleted.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete " + selected.getFullName() + "?");
        confirm.setContentText("Remove this account and cancel their desktop database login?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            userDAO.deleteUser(selected.getId());
            loadUsers(userDAO.getAllUsers());
        }
    }

    private void showUserForm(User user) {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle(user == null ? "Register User" : "Edit User");

        VBox form = new VBox();
        form.setSpacing(12);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white;");
        form.setPrefWidth(420);

        Label title = new Label(user == null ? "Register Desktop User" : "Modify User Details");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        TextField nameField = createField("Full Name", user != null ? user.getFullName() : "");
        TextField userField = createField("Username", user != null ? user.getUserName() : "");
        
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter new password");
        passField.setPrefWidth(360);
        passField.setPrefHeight(40);
        passField.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-font-size: 13px; -fx-padding: 0 12px;");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.setItems(FXCollections.observableArrayList("Admin", "Staff"));
        roleBox.setValue(user != null ? user.getRole() : "Staff");
        roleBox.setPrefWidth(360);
        roleBox.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #CBD5E1; -fx-border-width: 1.5px; -fx-border-radius: 8px; -fx-background-radius: 8px;");

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        Button saveBtn = new Button(user == null ? "Create User" : "Save Changes");
        saveBtn.setPrefWidth(360);
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            String role = roleBox.getValue();

            if (name.isEmpty() || username.isEmpty() || (user == null && password.isEmpty())) {
                errorLabel.setText("Required parameters are missing.");
                return;
            }

            User u = user != null ? user : new User();
            u.setFullName(name);
            u.setUserName(username);
            u.setRole(role);
            if (!password.isEmpty()) {
                u.setPassword(password);
            }

            if (user == null) {
                userDAO.addUser(u);
            } else {
                userDAO.updateUser(u);
            }

            formStage.close();
            loadUsers(userDAO.getAllUsers());
        });

        form.getChildren().addAll(
                title,
                new Label("Full Name:"), nameField,
                new Label("Username:"), userField,
                new Label("Password:"), passField,
                new Label("System Role:"), roleBox,
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
            Stage stage = (Stage) usersTable.getScene().getWindow();
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
    @FXML private void handleUsers()     { loadUsers(userDAO.getAllUsers());
        if (loggedInUser.getRole().equals("Staff")) return; // Staff can't reach this screen at all
        navigateTo("users.fxml");
    }
    @FXML private void handleAbout()     { navigateTo("about.fxml"); }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vehiclerental/welcome.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1060, 660);
            scene.getStylesheets().add(getClass().getResource("/com/vehiclerental/styles/auriva.css").toExternalForm());
            Stage stage = (Stage) usersTable.getScene().getWindow();
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