package com.vehiclerental.controller;

import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.entities.User;
import com.vehiclerental.entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

public class VehicleController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private FlowPane vehicleCardsPane;
    @FXML private Label totalCountLabel;
    @FXML private Label availableCountLabel;
    @FXML private Label rentedCountLabel;
    @FXML private Label servicedCountLabel;
    @FXML private Label badCountLabel;
    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    @FXML private Button usersBtn;

    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button aboutBtn;

    private User loggedInUser;
    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        statusFilter.setItems(FXCollections.observableArrayList(
                "All", "Available", "Rented", "Serviced", "Bad"
        ));
        statusFilter.setValue("All");
        loadVehicles(vehicleDAO.getAllVehicles());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                loadVehicles(vehicleDAO.getAllVehicles());
            } else {
                loadVehicles(vehicleDAO.searchVehicles(newValue.trim()));
            }
        });

        setupButtonHover(dashboardBtn, false);
        setupButtonHover(vehiclesBtn, true);
        setupButtonHover(customersBtn, false);
        setupButtonHover(rentalsBtn, false);
        setupButtonHover(usersBtn, false);
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

    private void loadVehicles(List<Vehicle> vehicles) {
        vehicleCardsPane.getChildren().clear();

        long available = vehicles.stream()
                .filter(v -> v.getStatus().equals("Available")).count();
        long rented = vehicles.stream()
                .filter(v -> v.getStatus().equals("Rented")).count();
        long serviced = vehicles.stream()
                .filter(v -> v.getStatus().equals("Serviced")).count();
        long bad = vehicles.stream()
                .filter(v -> v.getStatus().equals("Bad")).count();

        totalCountLabel.setText("All Vehicles (" + vehicles.size() + ")");
        availableCountLabel.setText("Available (" + available + ")");
        rentedCountLabel.setText("Rented (" + rented + ")");
        servicedCountLabel.setText("Serviced (" + serviced + ")");
        badCountLabel.setText("Bad (" + bad + ")");

        for (Vehicle vehicle : vehicles) {
            vehicleCardsPane.getChildren().add(createVehicleCard(vehicle));
        }

        if (vehicles.isEmpty()) {
            Label empty = new Label("No vehicles found. Click Add Vehicle to get started.");
            empty.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px;");
            vehicleCardsPane.getChildren().add(empty);
        }
    }

    private VBox createVehicleCard(Vehicle vehicle) {
        VBox card = new VBox();
        card.setPrefWidth(250);
        card.setSpacing(12);
        card.setPadding(new Insets(0, 0, 16, 0));
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);"
        );

        //  Vehicle image based on type of vehicle
        String imagePath;
        switch (vehicle.getType() != null ? vehicle.getType() : "Car") {
            case "Truck" -> imagePath = "/com/vehiclerental/images/truck.jpg";
            case "Bike"  -> imagePath = "/com/vehiclerental/images/bike.jpg";
            case "Van"   -> imagePath = "/com/vehiclerental/images/van.jpg";
            case "SUV"   -> imagePath = "/com/vehiclerental/images/suv.jpg";
            default      -> imagePath = "/com/vehiclerental/images/car.jpg";
        }

        ImageView vehicleImage = new ImageView();
        try {
            var stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                vehicleImage.setImage(new Image(stream));
            }
        } catch (Exception e) {
            System.out.println("Image not found: " + imagePath);
        }
        vehicleImage.setFitWidth(250);
        vehicleImage.setFitHeight(140);
        vehicleImage.setPreserveRatio(false);
        vehicleImage.setSmooth(true);

        // Rounded top corners clip
        Rectangle clip = new Rectangle(250, 140);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        vehicleImage.setClip(clip);

        // ── Content padding wrapper ──
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(12, 16, 0, 16));

        // ── Status badge color ──
        String badgeColor;
        switch (vehicle.getStatus() != null ? vehicle.getStatus() : "") {
            case "Available" -> badgeColor =
                    "-fx-background-color: #ECFDF5; -fx-text-fill: #10B981;";
            case "Rented"    -> badgeColor =
                    "-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB;";
            case "Serviced"  -> badgeColor =
                    "-fx-background-color: #FFFBEB; -fx-text-fill: #F59E0B;";
            case "Bad"       -> badgeColor =
                    "-fx-background-color: #FEF2F2; -fx-text-fill: #EF4444;";
            default          -> badgeColor =
                    "-fx-background-color: #F1F5F9; -fx-text-fill: #64748B;";
        }

        // ── Top row: type tag + status badge ──
        HBox topRow = new HBox();
        topRow.setSpacing(8);
        topRow.setStyle("-fx-alignment: center-left;");

        Label typeLabel = new Label(vehicle.getType() != null ? vehicle.getType() : "Car");
        typeLabel.setStyle(
                "-fx-background-color: #EFF6FF;" +
                        "-fx-text-fill: #2563EB;" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 4px 10px;"
        );

        Label statusBadge = new Label(vehicle.getStatus());
        statusBadge.setStyle(
                badgeColor +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-padding: 4px 10px;"
        );

        topRow.getChildren().addAll(typeLabel, statusBadge);

        // ── Vehicle name ──
        Label nameLabel = new Label(vehicle.getBrand() + " " + vehicle.getModel());
        nameLabel.setStyle(
                "-fx-text-fill: #0F172A;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;"
        );

        // ── Year and color ──
        Label yearColor = new Label(vehicle.getYear() + "  |  " + vehicle.getColor());
        yearColor.setStyle("-fx-text-fill: #64748B; -fx-font-size: 12px;");

        // ── Plate number ──
        Label plateLabel = new Label("Plate: " + vehicle.getPlateNumber());
        plateLabel.setStyle(
                "-fx-text-fill: #475569;" +
                        "-fx-font-size: 13px;" +
                        "-fx-font-weight: bold;"
        );

        // ── Mileage ──
        Label mileageLabel = new Label("Mileage: " + vehicle.getMileage() + " km");
        mileageLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 12px;");

        // ── Daily rate ──
        Label rateLabel = new Label(
                "N" + String.format("%.0f", vehicle.getDailyRate()) + " / day"
        );
        rateLabel.setStyle(
                "-fx-text-fill: #2563EB;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        // ── Divider ──
        Label divider = new Label();
        divider.setPrefHeight(1);
        divider.setPrefWidth(218);
        divider.setStyle("-fx-background-color: #E2E8F0;");

        // ── Action buttons ──
        HBox buttons = new HBox();
        buttons.setSpacing(8);
        buttons.setPadding(new Insets(4, 16, 0, 16));

        Button editBtn = new Button("Edit");
        editBtn.setPrefWidth(105);
        editBtn.setStyle(
                "-fx-background-color: #EFF6FF;" +
                        "-fx-text-fill: #2563EB;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8px 0;"
        );
        editBtn.setOnAction(e -> handleEditVehicle(vehicle));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setPrefWidth(105);
        deleteBtn.setStyle(
                "-fx-background-color: #FEF2F2;" +
                        "-fx-text-fill: #EF4444;" +
                        "-fx-font-size: 12px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 8px 0;"
        );
        deleteBtn.setOnAction(e -> handleDeleteVehicle(vehicle));

        buttons.getChildren().addAll(editBtn, deleteBtn);

        content.getChildren().addAll(
                topRow, nameLabel, yearColor,
                plateLabel, mileageLabel, rateLabel,
                divider
        );

        card.getChildren().addAll(vehicleImage, content, buttons);

        // ── Hover effect ──
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.18), 20, 0, 0, 6);" +
                        "-fx-translate-y: -3;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);" +
                        "-fx-translate-y: 0;"
        ));

        return card;
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadVehicles(vehicleDAO.getAllVehicles());
        } else {
            loadVehicles(vehicleDAO.searchVehicles(keyword));
        }
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        statusFilter.setValue("All");
        loadVehicles(vehicleDAO.getAllVehicles());
    }

    @FXML
    private void handleAddVehicle() {
        showVehicleForm(null);
    }

    private void handleEditVehicle(Vehicle vehicle) {
        showVehicleForm(vehicle);
    }

    private void handleDeleteVehicle(Vehicle vehicle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Vehicle");
        alert.setHeaderText(
                "Delete " + vehicle.getBrand() + " " + vehicle.getModel() + "?"
        );
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            vehicleDAO.deleteVehicle(vehicle.getId());
            loadVehicles(vehicleDAO.getAllVehicles());
        }
    }

    private void showVehicleForm(Vehicle vehicle) {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle(vehicle == null ? "Add Vehicle" : "Edit Vehicle");

        VBox form = new VBox();
        form.setSpacing(12);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: white;");
        form.setPrefWidth(420);

        Label title = new Label(vehicle == null ? "Add New Vehicle" : "Edit Vehicle");
        title.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        TextField plateField   = createField("Plate Number",
                vehicle != null ? vehicle.getPlateNumber() : "");
        TextField brandField   = createField("Brand",
                vehicle != null ? vehicle.getBrand() : "");
        TextField modelField   = createField("Model",
                vehicle != null ? vehicle.getModel() : "");
        TextField yearField    = createField("Year",
                vehicle != null ? String.valueOf(vehicle.getYear()) : "");
        TextField colorField   = createField("Color",
                vehicle != null ? vehicle.getColor() : "");
        TextField rateField    = createField("Daily Rate (N)",
                vehicle != null ? String.valueOf(vehicle.getDailyRate()) : "");
        TextField mileageField = createField("Mileage (km)",
                vehicle != null ? String.valueOf(vehicle.getMileage()) : "");
        TextField descField    = createField("Description",
                vehicle != null ? vehicle.getDescription() : "");

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.setItems(FXCollections.observableArrayList(
                "Car", "SUV", "Truck", "Van", "Bike"
        ));
        typeBox.setValue(vehicle != null ? vehicle.getType() : "Car");
        typeBox.setPrefWidth(360);
        typeBox.setStyle(comboStyle());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.setItems(FXCollections.observableArrayList(
                "Available", "Rented", "Serviced", "Bad"
        ));
        statusBox.setValue(vehicle != null ? vehicle.getStatus() : "Available");
        statusBox.setPrefWidth(360);
        statusBox.setStyle(comboStyle());

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        Button saveBtn = new Button(vehicle == null ? "Add Vehicle" : "Save Changes");
        saveBtn.setPrefWidth(360);
        saveBtn.setPrefHeight(44);
        saveBtn.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
        );

        saveBtn.setOnAction(e -> {
            if (plateField.getText().isEmpty()
                    || brandField.getText().isEmpty()
                    || modelField.getText().isEmpty()) {
                errorLabel.setText("Plate, Brand and Model are required.");
                return;
            }
            try {
                Vehicle v = vehicle != null ? vehicle : new Vehicle();
                v.setPlateNumber(plateField.getText().trim());
                v.setBrand(brandField.getText().trim());
                v.setModel(modelField.getText().trim());
                v.setYear(yearField.getText().isEmpty() ? 0
                        : Integer.parseInt(yearField.getText().trim()));
                v.setColor(colorField.getText().trim());
                v.setType(typeBox.getValue());
                v.setDailyRate(rateField.getText().isEmpty() ? 0
                        : Double.parseDouble(rateField.getText().trim()));
                v.setMileage(mileageField.getText().isEmpty() ? 0
                        : Integer.parseInt(mileageField.getText().trim()));
                v.setDescription(descField.getText().trim());
                v.setStatus(statusBox.getValue());

                if (vehicle == null) {
                    vehicleDAO.addVehicle(v);
                } else {
                    vehicleDAO.updateVehicle(v);
                }

                formStage.close();
                loadVehicles(vehicleDAO.getAllVehicles());

            } catch (NumberFormatException ex) {
                errorLabel.setText("Year, Rate and Mileage must be numbers.");
            }
        });

        form.getChildren().addAll(
                title,
                new Label("Type:"),   typeBox,
                new Label("Status:"), statusBox,
                plateField, brandField, modelField,
                yearField, colorField, rateField,
                mileageField, descField,
                errorLabel, saveBtn
        );

        ScrollPane scroll = new ScrollPane(form);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(scroll, 420, 580);
        formStage.setScene(scene);
        formStage.showAndWait();
    }

    private String comboStyle() {
        return "-fx-background-color: #F8FAFC;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-width: 1.5px;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;";
    }

    private TextField createField(String prompt, String value) {
        TextField field = new TextField(value);
        field.setPromptText(prompt);
        field.setPrefWidth(360);
        field.setPrefHeight(40);
        field.setStyle(
                "-fx-background-color: #F8FAFC;" +
                        "-fx-border-color: #CBD5E1;" +
                        "-fx-border-width: 1.5px;" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-size: 13px;" +
                        "-fx-padding: 0 12px;"
        );
        return field;
    }

    private void navigateTo(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/vehiclerental/" + fxmlFile)
            );
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
            scene.getStylesheets().add(
                    getClass().getResource("/com/vehiclerental/styles/auriva.css")
                            .toExternalForm()
            );
            Stage stage = (Stage) searchField.getScene().getWindow();
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
    @FXML private void handleVehicles()  { loadVehicles(vehicleDAO.getAllVehicles()); }
    @FXML private void handleCustomers() { navigateTo("customer.fxml"); }
    @FXML private void handleRentals()   { navigateTo("rental.fxml"); }
    @FXML private void handleUsers()     { navigateTo("users.fxml"); }
    @FXML private void handleAbout()     { navigateTo("about.fxml"); }

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
            Stage stage = (Stage) searchField.getScene().getWindow();
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