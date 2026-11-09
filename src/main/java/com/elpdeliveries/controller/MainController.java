package com.elpdeliveries.controller;

import com.elpdeliveries.model.*;
import com.elpdeliveries.util.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.elpdeliveries.model.DeliveryView.TODAY;

public class MainController {
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
    @FXML
    private Tab todayTab;
    @FXML
    private Tab futureTab;
    @FXML
    private Tab allTab;
    @FXML
    private Tab routedTab;
    @FXML
    private TableView<RouteDto> routesTable;
    @FXML
    private TableColumn<RouteDto, Integer> routeIdCol;
    @FXML
    private TableColumn<RouteDto, String> routeDriverNameCol;
    @FXML
    private TableColumn<RouteDto, Integer> routeDeliveryCountCol;
    @FXML
    private TableColumn<RouteDto, LocalDateTime> routeCreatedCol;
    @FXML
    private TableColumn<RouteDto, String> routeStatusCol;
    @FXML
    private TableColumn<RouteDto, Void> deleteCol;
    @FXML
    private Label deliveriesForRouteLabel;
    private DeliveryView currentView = TODAY;
    private Delivery selectedDelivery;

    @FXML
    public void initialize() {
        if (!ConfigManager.exists()) {
            ErrorHandler.showError("Please configure a database!", new RuntimeException());
            openSettings();
        }
        todayTab.setUserData(DeliveryView.TODAY);
        futureTab.setUserData(DeliveryView.FUTURE);
        allTab.setUserData(DeliveryView.ALL);
        routedTab.setUserData(DeliveryView.ALL);

        routesTable.setVisible(false);
        routesTable.setManaged(false);
        deliveriesForRouteLabel.setVisible(false);
        deliveriesForRouteLabel.setManaged(false);

        deliveryTable.setPlaceholder(new Text("No deliveries to show"));
        routesTable.setPlaceholder(new Text("No routes to show"));
        loadDeliveries(currentView);

        tabPane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
            currentView = (DeliveryView) newTab.getUserData();
            boolean routesSelected = newTab == routedTab;
            routesTable.setVisible(routesSelected);
            routesTable.setManaged(routesSelected);
            deliveriesForRouteLabel.setVisible(routesSelected);
            deliveriesForRouteLabel.setManaged(routesSelected);
            if (routesSelected) {
                deliverCol.setVisible(false);
                rteCol.setVisible(true);
                loadRoutes();
                if (!routesTable.getItems().isEmpty()) {
                    routesTable.getSelectionModel().selectFirst();
                } else {
                    deliveryTable.setItems(null);
                }
            } else {
                rteCol.setVisible(false);
                deliverCol.setVisible(true);
                performSearch();
            }

        });

        idCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.05));

        fnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        lnameCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.12));
        addrCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.35));
        dfdCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.16));
        completedCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.095));
        deliverCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.10));
        rteCol.prefWidthProperty().bind(deliveryTable.widthProperty().multiply(0.10));


        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        rteCol.setCellValueFactory(new PropertyValueFactory<>("routeOrder"));
        fnameCol.setCellValueFactory(new PropertyValueFactory<>("fname"));
        lnameCol.setCellValueFactory(new PropertyValueFactory<>("lname"));
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        dfdCol.setCellValueFactory(new PropertyValueFactory<>("dlvDate"));
        completedCol.setCellValueFactory(cellData -> cellData.getValue().completedProperty());
        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));
        deliverCol.setCellValueFactory(cellData -> cellData.getValue().includedInRouteProperty());
        deliverCol.setCellFactory(_ -> new CheckBoxTableCell<>());

        deliveryTable.getSelectionModel().selectedItemProperty()
                .addListener((_, _, newSelection) -> showPreview(newSelection));
        deliveryTable.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                Delivery selectedItem = deliveryTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    handleEdit();
                    event.consume();
                }
            }
        });
        deliveryTable.setRowFactory(_ -> {
            TableRow<Delivery> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    handleEdit();
                    event.consume();
                }
            });
            return row;
        });

        routeIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        routeDriverNameCol.setCellValueFactory(new PropertyValueFactory<>("staffName"));
        routeCreatedCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        routeStatusCol.setCellValueFactory(new PropertyValueFactory<>("routeStatus"));
        routeDeliveryCountCol.setCellValueFactory(new PropertyValueFactory<>("stopCount"));

        deleteCol.setCellFactory(_ -> new TableCell<>() {
            private final Button btn = new Button("Delete");

            {
                btn.setOnAction(_ -> {
                    RouteDto selectedRoute = getTableView().getItems().get(getIndex());
                    handleDeleteRoute(selectedRoute.getId());
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


        routesTable.getSelectionModel().selectedItemProperty().addListener((_, _, newRoute) -> {
            if (newRoute == null) {
                deliveryTable.setItems(null);
                return;
            }
            loadRouteDeliveries(newRoute.getId());
        });

    }

    private void handleDeleteRoute(Long id) {
        try {
            ApiClient.getInstance().deleteRoute(id);
            loadRoutes();
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
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
            List<Delivery> deliveries = deliveryTable.getItems();
            List<Integer> ids = deliveries.stream().map(Delivery::getId).toList();
            StaffDto driver = showStaffSelection();
            RouteResponse routeInfo = ApiClient.getInstance().routeDeliveries(new RouteRequest(ids, driver.id()));
            performSearch();
            showDeliveryConfirmation(routeInfo);
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private void performSearch() {
        LocalDate date = null;
        String receiptNum = null;
        if (!receiptField.getText().trim().isEmpty()) {
            receiptNum = receiptField.getText();
        }
        if (searchDatePicker.getValue() != null) {
            date = searchDatePicker.getValue();
            tabPane.getSelectionModel().selectLast();
        }
        if (tabPane.getSelectionModel().getSelectedItem() == routedTab) {
            loadRouteDeliveries(
                    routesTable.getSelectionModel().getSelectedItem().getId(),
                    date,
                    fnameField.getText(),
                    lnameField.getText(),
                    receiptNum,
                    uncompletedCB.isSelected());
        } else {
            loadDeliveries(currentView, date, fnameField.getText(), lnameField.getText(), receiptNum,
                    uncompletedCB.isSelected());
        }


    }

    private void loadDeliveries(DeliveryView view, LocalDate date, String fname, String lname, String receiptNum,
                                Boolean uncompleted) {
        try {
            DeliveryStats stats = ApiClient.getInstance().getStats();
            List<Delivery> deliveries = ApiClient.getInstance()
                    .getDeliveries(view, date, fname, lname, receiptNum, uncompleted);
            for (Delivery delivery : deliveries) {
                delivery.includedInRouteProperty()
                        .addListener((_, _, newVal) -> {
                            try {
                                ApiClient.getInstance().updateIncludedInRoute(delivery.getId(), newVal);
                            } catch (Exception e) {
                                ErrorHandler.showError("Unable to save route preference", e);
                            }
                        });
            }
            deliveryTable.setItems(FXCollections
                    .observableArrayList(deliveries));
            todayLabel.setText("Deliveries for Today: " + stats.todayCount());
            uncompletedLabel.setText("Uncompleted Deliveries: " + stats.uncompletedCount());
            countLabel.setText("Showing " + deliveries.size() + " Deliveries");
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private void loadDeliveries(DeliveryView view) {
        loadDeliveries(view, null, null, null, null, null);
    }

    private void loadRoutes() {
        try {
            List<RouteDto> routes = ApiClient.getInstance().getRoutes();
            routesTable.setItems(FXCollections.observableArrayList(routes));
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private void loadRouteDeliveries(Long routeId, LocalDate date, String fname, String lname, String receiptNum,
                                     Boolean uncompleted) {
        try {
            List<Delivery> deliveries = ApiClient.getInstance().getRouteDeliveries(
                    routeId,
                    date,
                    fname,
                    lname,
                    receiptNum,
                    uncompleted);
            deliveryTable.setItems(FXCollections.observableArrayList(deliveries));
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private void loadRouteDeliveries(Long routeId) {
        loadRouteDeliveries(routeId, null, null, null, null, null);
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
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    public void openSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elpdeliveries/ApiSettings.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private void showPreview(Delivery d) {
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
        dlvNotesTa.setText(d.getNotes() == null ? "" : d.getNotes());
    }

    private StaffDto showStaffSelection() throws Exception {
        List<StaffDto> staff = ApiClient.getInstance().getStaff();
        ChoiceDialog<StaffDto> dialog = new ChoiceDialog<>(staff.getFirst(), staff);
        dialog.setTitle("Route driver");
        dialog.setHeaderText("Who will complete this route?");
        dialog.setContentText("Staff:");
        return dialog.showAndWait().orElse(null);
    }

    private void showDeliveryConfirmation(RouteResponse response) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Created new route: " + response.getRouteId());
        alert.setContentText("New Route:\nDriver: " + response.getDriverName() +
                "\nDeliveries: " + response.getDeliveryCount());
        alert.showAndWait();
    }


}
