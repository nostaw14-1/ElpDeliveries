package com.elpdeliveries.controller;

import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private final RouteOptimizationService VROS = new VroomRouteOptimizationService();
    @FXML
    private TableView<Delivery> deliveryTable;
    @FXML
    private Text todayLabel;
    @FXML
    private Text uncompletedLabel;
    @FXML
    private TableColumn<Delivery, Integer> idCol;
    @FXML
    private TableColumn<Delivery, String> fnameCol;
    @FXML
    private TableColumn<Delivery, String> lnameCol;
    @FXML
    private TableColumn<Delivery, String> addrCol;
    @FXML
    private TableColumn<Delivery, String> rteCol;
    @FXML
    private TableColumn<Delivery, Date> dfdCol;
    @FXML
    private TableColumn<Delivery, Boolean> deliverCol;
    @FXML
    private TableColumn<Delivery, Boolean> completedCol;
    @FXML
    private CheckBox uncompletedCB;
    @FXML
    private DatePicker searchDatePicker;
    @FXML
    private TextField fnameField;
    @FXML
    private TextField lnameField;
    @FXML
    private TextField receiptField;
    @FXML
    private Label countLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextField receiptTf;
    @FXML
    private TextField phoneTf;
    @FXML
    private TextField homePhoneTf;
    @FXML
    private TextArea dlvNotesTa;
    private String currentView = "Deliveries for Today";
    private DBConnection dao;
    private Delivery selectedDelivery;

    @FXML
    public void initialize() {
        if (ConfigManager.exists()) {
            try {
                dao = new DBConnection();
            } catch (Exception e) {
                ErrorHandler.showError(e.getMessage(), e);
                openSettings();
            }
            if (dao.getConnection() == null) {
                openSettings();
                try {
                    dao = new DBConnection();
                } catch (Exception e) {
                    ErrorHandler.showError(e.getMessage(), e);
                    openSettings();
                }
            }
        } else {
            ErrorHandler.showError("Please configure a database!", new RuntimeException());
            openSettings();
        }

        deliveryTable.setPlaceholder(new Text("No deliveries to show"));
        loadDeliveries(currentView);

        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            currentView = newTab.getText();
            performSearch();
        });

        idCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.05));
        rteCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.05));
        fnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        lnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        addrCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.30));
        dfdCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.16));
        deliverCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.10));
        completedCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.095));

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        rteCol.setCellValueFactory(new PropertyValueFactory<>("routeOrder"));
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        dfdCol.setCellValueFactory(new PropertyValueFactory<>("dlvDate"));
        completedCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isCompleted()));

        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));
        deliverCol.setCellValueFactory(cellData -> cellData.getValue().includedInRouteProperty());
        deliverCol.setCellFactory(_ -> new CheckBoxTableCell<>());

        deliveryTable.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newSelection) -> showPreview(newSelection));

    }

    @FXML
    private void handleSearch() {
        performSearch();
    }

    @FXML
    private void handleAdd() {
        openAddEdit(null);
    }

    @FXML
    private void handleSettings() {
        openSettings();
    }

    @FXML
    private void handleClear() {
        searchDatePicker.setValue(null);
        fnameField.clear();
        lnameField.clear();
        receiptField.clear();
        performSearch();
    }

    @FXML
    private void handleEdit() {
        if (selectedDelivery == null) {
            return;
        }
        openAddEdit(selectedDelivery);
    }

    @FXML
    private void handleGenerateRoute() {
        try {
            List<Delivery> deliveries = deliveryTable.getItems().stream().filter(Delivery::isIncludedInRoute).toList();
            List<Integer> orderedIds = VROS.optimiseRoute(deliveries,
                    Double.parseDouble(ConfigManager.getDepotLat()),
                    Double.parseDouble(ConfigManager.getDepotLong()));

            dao.updateRouteOrders(orderedIds);
            performSearch();
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private List<Delivery> performSearch() {
        Date date = null;
        Integer receiptNum = null;
        if (searchDatePicker.getValue() != null) {
            date = Date.valueOf(searchDatePicker.getValue());
            tabPane.getSelectionModel().selectLast();
        }
        if (!receiptField.getText().trim().isEmpty()) {
            receiptNum = Integer.parseInt(receiptField.getText());
        }
        return loadDeliveries(currentView, date, fnameField.getText(), lnameField.getText(), receiptNum,
                uncompletedCB.isSelected());

    }

    private List<Delivery> loadDeliveries(String view, Date date, String fname, String lname, Integer receiptNum,
                                          Boolean uncompleted) {
        try {
            ArrayList<Delivery> deliveries = dao.getDeliveries(view, date, fname, lname, receiptNum, uncompleted);
            for (Delivery delivery : deliveries) {
                delivery.includedInRouteProperty()
                        .addListener((obs, oldVal, newVal) -> {
                            try {
                                dao.updateIncludeInRoute(delivery.getId(), newVal);
                            } catch (SQLException e) {
                                ErrorHandler.showError("Unable to save route preference", e);
                            }
                        });
            }
            deliveryTable.setItems(FXCollections
                    .observableArrayList(deliveries));
            todayLabel.setText("Deliveries for Today: " + dao.getTodayCount());
            uncompletedLabel.setText("Uncompleted Deliveries: " + dao.getUncompletedCount());
            countLabel.setText("Showing " + deliveries.size() + " Deliveries");
            return deliveries;
        } catch (SQLException e) {
            ErrorHandler.showError(e.getMessage(), e);
            return null;
        }
    }

    private void loadDeliveries(String view) {
        loadDeliveries(view, null, null, null, null, null);
    }

    private void openAddEdit(Delivery d) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elpdeliveries/AddEdit.fxml"));
            Scene scene = new Scene(loader.load());
            AddEditController controller = loader.getController();
            Stage stage = new Stage();
            if (d != null) {
                controller.setDelivery(d);
                stage.setTitle("Editing delivery " + d.getId());
            } else {
                controller.setDelivery(null);
                stage.setTitle("Adding new delivery");
            }
            controller.setOnSave(this::performSearch);
            controller.setOnSaveAndContinue(() -> {
                performSearch();
                openAddEdit(null);
            });
            stage.setScene(scene);
            stage.showAndWait();
            performSearch();
        } catch (IOException e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    public void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elpdeliveries/Settings.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.showAndWait();
            if (dao != null) {
                dao.close();
            }
            dao = new DBConnection();
            if (deliveryTable != null) {
                performSearch();
            }

        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    public void showPreview(Delivery d) {
        selectedDelivery = d;
        if (d == null) {
            receiptTf.clear();
            phoneTf.clear();
            homePhoneTf.clear();
            dlvNotesTa.clear();
            return;
        }
        receiptTf.setText(String.valueOf(d.getReceiptId()));
        phoneTf.setText(d.getPhnum() == null ? "" : d.getPhnum());
        homePhoneTf.setText(d.getHomePhone() == null ? "" : d.getHomePhone());
        dlvNotesTa.setText(d.getDlvNotes() == null ? "" : d.getDlvNotes());

    }


}
