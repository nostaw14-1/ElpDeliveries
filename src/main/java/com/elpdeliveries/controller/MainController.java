package com.elpdeliveries.controller;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.util.ConfigManager;
import com.elpdeliveries.util.DBConnection;

public class MainController {
    @FXML
    private TableView<Delivery> deliveryTable;
    @FXML
    private Button addButton;
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
    private TableColumn<Delivery, String> phCol;
    @FXML
    private TableColumn<Delivery, Date> dfdCol;
    @FXML
    private TableColumn<Delivery, Void> addEditCol;
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
    private TextField createdAtTf;
    @FXML
    private TextField completedByTf;
    @FXML
    private TextArea dlvNotesTa;
    @FXML
    private TextArea storeNotesTa;
    private String currentView = "All Deliveries";
    private DBConnection dao;
    private Delivery selectedDelivery;

    @FXML
    public void initialize() {
        dao = new DBConnection();
        if (dao.getConnection() == null || !ConfigManager.exists()) {
            openSettings();
            dao = new DBConnection();
            if (dao.getConnection() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Database Connection Failed");
                alert.setContentText("Please configure a valid database connection");
                alert.showAndWait();
                return;
            }
        }
        deliveryTable.setPlaceholder(new Text("No deliveries to show"));
        loadDeliveries(currentView);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            currentView = newTab.getText();
            performSearch();
        });

        addEditCol.setCellFactory(col -> new TableCell<Delivery, Void>() {
            private Button btn = new Button("Edit");
            {
                btn.setOnAction(e -> {
                    Delivery selectDelivery = getTableView().getItems().get(getIndex());
                    openAddEdit(selectDelivery);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    btn.prefWidthProperty().bind(getTableColumn().widthProperty().subtract(10));
                    btn.prefHeightProperty().bind(getTableRow().heightProperty().subtract(10));
                    setGraphic(btn);
                }
            }
        });

        idCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.05));
        fnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        lnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        addrCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.25));
        phCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.10));
        dfdCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.16));
        addEditCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.10));
        completedCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.095));

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        phCol.setCellValueFactory(new PropertyValueFactory<>("phnum"));
        dfdCol.setCellValueFactory(new PropertyValueFactory<>("dlvDate"));
        completedCol.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleBooleanProperty(cellData.getValue().isCompleted()));

        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));

        deliveryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            showPreview(newSelection);
        });

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
    private void handleEdit() {
        if (selectedDelivery == null) {
            return;
        }
        openAddEdit(selectedDelivery);
    }

    private void performSearch() {
        Date date = null;
        Integer receiptNum = null;
        if (searchDatePicker.getValue() != null) {
            date = Date.valueOf(searchDatePicker.getValue());
        }
        if (!receiptField.getText().trim().isEmpty()) {
            receiptNum = Integer.parseInt(receiptField.getText());
        }
        loadDeliveries(currentView, date, fnameField.getText(), lnameField.getText(), receiptNum,
                uncompletedCB.isSelected());

    }

    private int loadDeliveries(String view, Date date, String fname, String lname, Integer receiptNum,
            Boolean uncompleted) {
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        try {
            deliveries = dao.getDeliveries(view, date, fname, lname, receiptNum, uncompleted);
            deliveryTable.setItems(FXCollections
                    .observableArrayList(deliveries));
            todayLabel.setText("Deliveries for Today: " + dao.getTodayCount());
            uncompletedLabel.setText("Uncompleted Deliveries: " + dao.getUncompletedCount());
            countLabel.setText("Showing " + deliveries.size() + " Deliveries");
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return deliveries.size();
    }

    private int loadDeliveries(String view) {
        ArrayList<Delivery> deliveries = new ArrayList<Delivery>();
        try {
            deliveries = dao.getDeliveries(view, null, null, null, null, null);
            deliveryTable
                    .setItems(FXCollections.observableArrayList(deliveries));
            todayLabel.setText("Deliveries for Today: " + dao.getTodayCount());
            uncompletedLabel.setText("Uncompleted Deliveries: " + dao.getUncompletedCount());
            countLabel.setText("Showing " + deliveries.size() + " Deliveries");
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        return deliveries.size();
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
            stage.setScene(scene);
            stage.showAndWait();
            performSearch();
        } catch (IOException e) {
            e.printStackTrace();
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showPreview(Delivery d) {
        selectedDelivery = d;
        if (d == null) {
            receiptTf.clear();
            createdAtTf.clear();
            completedByTf.clear();
            dlvNotesTa.clear();
            storeNotesTa.clear();
            return;
        }
        receiptTf.setText(String.valueOf(d.getReceiptNum()));
        createdAtTf.setText(d.getCreatedDate() == null ? "" : d.getCreatedDate() + "");
        completedByTf.setText(d.getCompletedBy() == null ? "" : d.getCompletedBy());
        dlvNotesTa.setText(d.getDlvNotes() == null ? "" : d.getDlvNotes());
        storeNotesTa.setText(d.getStoreNotes() == null ? "" : d.getStoreNotes());

    }
}
