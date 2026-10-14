package com.vehiclerental.controller;

import com.vehiclerental.dao.CustomerDAO;
import com.vehiclerental.dao.RentalDAO;
import com.vehiclerental.dao.VehicleDAO;
import com.vehiclerental.entities.Customer;
import com.vehiclerental.entities.Rental;
import com.vehiclerental.entities.User;
import com.vehiclerental.entities.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;

import java.awt.Desktop;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class RentalController {

    @FXML private TableView<Rental> rentalsTable;
    @FXML private TableColumn<Rental, String> customerNameCol;
    @FXML private TableColumn<Rental, String> vehiclePlateCol;
    @FXML private TableColumn<Rental, LocalDate> startDateCol;
    @FXML private TableColumn<Rental, LocalDate> returnDateCol;
    @FXML private TableColumn<Rental, String> statusCol;
    @FXML private TableColumn<Rental, Double> costCol;
    @FXML private TextField searchField;
    @FXML private Label loggedInName;
    @FXML private Label loggedInRole;
    @FXML private Button usersBtn;

    @FXML private Button dashboardBtn;
    @FXML private Button vehiclesBtn;
    @FXML private Button customersBtn;
    @FXML private Button rentalsBtn;
    @FXML private Button aboutBtn;

    private User loggedInUser;
    private final RentalDAO rentalDAO = new RentalDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        vehiclePlateCol.setCellValueFactory(new PropertyValueFactory<>("vehiclePlate"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        returnDateCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        costCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        loadRentals(rentalDAO.getAllRentals());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue.trim();
            if (keyword.isEmpty()) {
                loadRentals(rentalDAO.getAllRentals());
            } else {
                List<Rental> all = rentalDAO.getAllRentals();
                List<Rental> filtered = all.stream().filter(r ->
                        r.getCustomerName().toLowerCase().contains(keyword.toLowerCase()) ||
                        r.getVehiclePlate().toLowerCase().contains(keyword.toLowerCase())
                ).toList();
                loadRentals(filtered);
            }
        });

        setupButtonHover(dashboardBtn, false);
        setupButtonHover(vehiclesBtn, false);
        setupButtonHover(customersBtn, false);
        setupButtonHover(rentalsBtn, true);
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

    private void loadRentals(List<Rental> rentals) {
        ObservableList<Rental> data = FXCollections.observableArrayList(rentals);
        rentalsTable.setItems(data);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadRentals(rentalDAO.getAllRentals());
        } else {
            List<Rental> all = rentalDAO.getAllRentals();
            List<Rental> filtered = all.stream().filter(r ->
                    r.getCustomerName().toLowerCase().contains(keyword.toLowerCase()) ||
                            r.getVehiclePlate().toLowerCase().contains(keyword.toLowerCase())
            ).toList();
            loadRentals(filtered);
        }
    }

    @FXML
    private void handleClear() {
        searchField.clear();
        loadRentals(rentalDAO.getAllRentals());
    }

    @FXML
    private void handleNewRental() {
        Stage formStage = new Stage();
        formStage.initModality(Modality.APPLICATION_MODAL);
        formStage.setTitle("New Rental Transaction");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        grid.setStyle("-fx-background-color: white;");

        Label title = new Label("Issue Rental Agreement");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        grid.add(title, 0, 0, 2, 1);

        ComboBox<Customer> customerBox = new ComboBox<>();
        customerBox.setItems(FXCollections.observableArrayList(customerDAO.getAllCustomers()));
        customerBox.setPrefWidth(250);
        grid.add(new Label("Select Customer:"), 0, 1);
        grid.add(customerBox, 1, 1);

        ComboBox<Vehicle> vehicleBox = new ComboBox<>();
        vehicleBox.setItems(FXCollections.observableArrayList(vehicleDAO.getAvailableVehicles()));
        vehicleBox.setPrefWidth(250);
        grid.add(new Label("Select Vehicle:"), 0, 2);
        grid.add(vehicleBox, 1, 2);

        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);

        DatePicker returnDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        grid.add(new Label("Expected Return:"), 0, 4);
        grid.add(returnDatePicker, 1, 4);

        Label priceLabel = new Label("Total Cost: N0.00");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        grid.add(priceLabel, 0, 5, 2, 1);

        CheckBox customerSign = new CheckBox("Customer Digitally Signed Terms");
        CheckBox staffSign = new CheckBox("Authorized Staff Sign-off");
        grid.add(customerSign, 0, 6, 2, 1);
        grid.add(staffSign, 0, 7, 2, 1);

        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");
        grid.add(errorLabel, 0, 8, 2, 1);

        Button submitBtn = new Button("Process & Generate PDF");
        submitBtn.setPrefWidth(350);
        submitBtn.setPrefHeight(40);
        submitBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-cursor: hand;");
        grid.add(submitBtn, 0, 9, 2, 1);

        Runnable updateCost = () -> {
            Vehicle v = vehicleBox.getValue();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = returnDatePicker.getValue();
            if (v != null && start != null && end != null) {
                long days = ChronoUnit.DAYS.between(start, end);
                if (days <= 0) days = 1;
                double cost = days * v.getDailyRate();
                priceLabel.setText("Total Cost: N" + String.format("%.2f", cost));
            }
        };

        vehicleBox.setOnAction(e -> updateCost.run());
        startDatePicker.setOnAction(e -> updateCost.run());
        returnDatePicker.setOnAction(e -> updateCost.run());

        submitBtn.setOnAction(e -> {
            Customer cust = customerBox.getValue();
            Vehicle veh = vehicleBox.getValue();
            LocalDate start = startDatePicker.getValue();
            LocalDate end = returnDatePicker.getValue();

            if (cust == null || veh == null || start == null || end == null) {
                errorLabel.setText("Please fill out all transaction fields.");
                return;
            }
            if (end.isBefore(start)) {
                errorLabel.setText("Return date cannot be before start date.");
                return;
            }
            if (!customerSign.isSelected() || !staffSign.isSelected()) {
                errorLabel.setText("Both customer and staff sign-offs are required.");
                return;
            }

            long days = ChronoUnit.DAYS.between(start, end);
            if (days <= 0) days = 1;

            Rental r = new Rental(cust.getFullName(), veh.getPlateNumber(), start, end, veh.getDailyRate());
            rentalDAO.addRental(r);
            vehicleDAO.updateVehicleStatus(veh.getId(), "Rented");

            createDummyPdfAndOpen(r);

            formStage.close();
            loadRentals(rentalDAO.getAllRentals());
        });

        Scene scene = new Scene(grid, 400, 480);
        formStage.setScene(scene);
        formStage.showAndWait();
    }

    private void createDummyPdfAndOpen(Rental rental) {
        try {
            File dir = new File("contracts");
            if (!dir.exists()) dir.mkdir();
            File file = new File(dir, "Contract_" + rental.getVehiclePlate() + "_" + LocalDate.now() + ".pdf");

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            Paragraph title = new Paragraph("AURIVA VEHICLE RENTALS")
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            doc.add(title);

            Paragraph tagline = new Paragraph("\"Move with Gold. Drive with Grace.\"")
                    .setFontSize(10)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            doc.add(tagline);

            Paragraph subtitle = new Paragraph("OFFICIAL RENTAL CONTRACT AGREEMENT")
                    .setFontSize(12)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            doc.add(subtitle);

            float[] columnWidths = {180F, 320F};
            Table table = new Table(columnWidths);

            table.addCell(new Cell().add(new Paragraph("Customer Name:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph(rental.getCustomerName()).setFontSize(11)));

            table.addCell(new Cell().add(new Paragraph("Vehicle Plate:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph(rental.getVehiclePlate()).setFontSize(11)));

            table.addCell(new Cell().add(new Paragraph("Start Date:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph(rental.getStartDate().toString()).setFontSize(11)));

            table.addCell(new Cell().add(new Paragraph("Expected Return:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph(rental.getExpectedReturnDate().toString()).setFontSize(11)));

            table.addCell(new Cell().add(new Paragraph("Total Rental Cost:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph("N" + String.format("%.2f", rental.getTotalCost())).setFontSize(11)));

            table.addCell(new Cell().add(new Paragraph("Contract Status:").setBold().setFontSize(11)));
            table.addCell(new Cell().add(new Paragraph(rental.getStatus()).setFontSize(11)));

            doc.add(table.setMarginBottom(30));

            Paragraph terms = new Paragraph("TERMS AND CONDITIONS:\n" +
                    "1. The Customer agrees to return the vehicle in its original condition on the specified Expected Return Date.\n" +
                    "2. Late returns are subject to a penalty charge per day.\n" +
                    "3. The Customer warrants that they possess a valid driver license and are authorized to operate the vehicle.\n" +
                    "4. The Customer agrees to pay for all damage or loss incurred during the rental period.\n" +
                    "5. Both parties confirm they have inspected the vehicle before departure and agree to the state recorded.")
                    .setFontSize(9)
                    .setMarginBottom(40);
            doc.add(terms);

            float[] sigWidths = {250F, 250F};
            Table sigTable = new Table(sigWidths);
            sigTable.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);

            Cell custCell = new Cell().add(new Paragraph("__________________________________\nCustomer Signature")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            custCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            sigTable.addCell(custCell);

            Cell staffCell = new Cell().add(new Paragraph("__________________________________\nAuthorized Staff Signature")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            staffCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER);
            sigTable.addCell(staffCell);

            doc.add(sigTable);

            doc.close();

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnVehicle() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Transaction Selected");
            alert.setContentText("Please select an active rental to process a return.");
            alert.showAndWait();
            return;
        }

        if (selected.getStatus().equals("Returned")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Returned");
            alert.setHeaderText("Already Returned");
            alert.setContentText("This vehicle has already been marked as returned.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Process Return");
        confirm.setHeaderText("Confirm Return for " + selected.getVehiclePlate());
        confirm.setContentText("Mark this vehicle as returned to the fleet?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            rentalDAO.returnVehicle(selected.getId(), LocalDate.now());

            List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
            for (Vehicle v : allVehicles) {
                if (v.getPlateNumber().equals(selected.getVehiclePlate())) {
                    vehicleDAO.updateVehicleStatus(v.getId(), "Available");
                    break;
                }
            }
            loadRentals(rentalDAO.getAllRentals());
        }
    }

    @FXML
    private void handleDeleteRental() {
        Rental selected = rentalsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Transaction Selected");
            alert.setContentText("Please select a transaction record to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Transaction Record?");
        confirm.setContentText("This will permanently remove the record from logs.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            rentalDAO.deleteRental(selected.getId());
            loadRentals(rentalDAO.getAllRentals());
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
            Stage stage = (Stage) rentalsTable.getScene().getWindow();
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
    @FXML private void handleRentals()   { loadRentals(rentalDAO.getAllRentals()); }
    @FXML private void handleUsers()     { navigateTo("users.fxml"); }
    @FXML private void handleAbout()     { navigateTo("about.fxml"); }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/vehiclerental/welcome.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1060, 660);
            scene.getStylesheets().add(getClass().getResource("/com/vehiclerental/styles/auriva.css").toExternalForm());
            Stage stage = (Stage) rentalsTable.getScene().getWindow();
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