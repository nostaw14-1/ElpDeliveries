package com.elpdeliveries.controller;

import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.model.Patient;
import com.elpdeliveries.model.ReceiptItem;
import com.elpdeliveries.model.ReceiptLookupResult;
import com.elpdeliveries.util.DBConnection;
import com.elpdeliveries.util.DeliveryGeocodingService;
import com.elpdeliveries.util.PatientDBConnection;
import com.elpdeliveries.util.S3Uploader;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class AddEditController {
    private final DBConnection dao = new DBConnection();
    @FXML
    private TextField fnameTf;
    @FXML
    private TextField lnameTf;
    @FXML
    private TextField phoneTf;
    @FXML
    private TextField homePhoneTf;
    @FXML
    private TextField receiptTf;
    @FXML
    private TextField createdAtTf;
    @FXML
    private TextField completedByTf;
    @FXML
    private TextField completedAtTf;
    @FXML
    private TextArea addrTa;
    @FXML
    private TextArea driverNotesTa;
    @FXML
    private DatePicker dfdDatePicker;
    @FXML
    private CheckBox handedCb;
    @FXML
    private CheckBox completedCb;
    @FXML
    private Button deleteButton;
    @FXML
    private BorderPane photoPane;
    @FXML
    private TableView<ReceiptItem> itemsTable;
    @FXML
    private TableColumn<ReceiptItem, Integer> qtyCol;
    @FXML
    private TableColumn<ReceiptItem, String> itemCol;
    @FXML
    private TableColumn<ReceiptItem, String> priceCol;
    private String photoPath;
    private ImageView imageView;
    private Delivery delivery;

    @FXML
    private void initialize() {
        qtyCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("quantity"));
        itemCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        priceCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("priceString"));
        itemsTable.setPlaceholder(new Text("Items not available"));
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery == null) {
            deleteButton.setVisible(false);
            return;
        }

        fnameTf.setText(delivery.getFname());
        lnameTf.setText(delivery.getLname());
        phoneTf.setText(delivery.getPhnum());
        homePhoneTf.setText(delivery.getHomePhone());
        receiptTf.setText(delivery.getReceiptId() + "");
        addrTa.setText(delivery.getAddr());
        driverNotesTa.setText(delivery.getDlvNotes());
        completedByTf.setText(delivery.getCompletedBy());
        handedCb.setSelected(delivery.isHandedToCustomer());
        completedCb.setSelected(delivery.isCompleted());
        if (delivery.getCreatedDate() != null) {
            createdAtTf.setText(delivery.getCreatedDate().toString());
        }
        if (delivery.getCompletedDate() != null) {
            completedAtTf.setText(delivery.getCompletedDate().toString());
        }
        if (delivery.getDlvDate() != null) {
            dfdDatePicker.setValue(delivery.getDlvDate().toLocalDate());
        }

        if (delivery.getPhoto() != null && !delivery.getPhoto().isEmpty()) {
            photoPath = delivery.getPhoto();
            showImage(photoPath);
        }
        try {
            ReceiptLookupResult items = new PatientDBConnection().findPatientsById(delivery.getReceiptId());
            itemsTable.setItems(FXCollections.observableArrayList(items.getItems()));
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            Date deliveryDate = null;
            int receiptId = 0;
            if (dfdDatePicker.getValue() != null) {
                deliveryDate = Date.valueOf(dfdDatePicker.getValue());
            }
            if (receiptTf.getText() != null) {
                receiptId = Integer.parseInt(receiptTf.getText());
            }
            if (delivery == null) {
                Delivery newDelivery = new Delivery(
                        null,
                        deliveryDate,
                        fnameTf.getText(),
                        lnameTf.getText(),
                        phoneTf.getText(),
                        homePhoneTf.getText(),
                        addrTa.getText(),
                        "",
                        driverNotesTa.getText(),
                        receiptId,
                        photoPath,
                        completedCb.isSelected(),
                        completedByTf.getText(),
                        null,
                        handedCb.isSelected(),
                        null,
                        null,
                        null);
                dao.addDelivery(newDelivery);
            } else {
                delivery.setFname(fnameTf.getText());
                delivery.setLname(lnameTf.getText());
                delivery.setPhnum(phoneTf.getText());
                delivery.setHomePhone(homePhoneTf.getText());
                delivery.setAddr(addrTa.getText());
                delivery.setStoreNotes("");
                delivery.setDlvNotes(driverNotesTa.getText());
                delivery.setReceiptId(Integer.parseInt(receiptTf.getText().trim()));
                delivery.setCompleted(completedCb.isSelected());
                delivery.setCompletedBy(completedByTf.getText());
                delivery.setHandedToCustomer(handedCb.isSelected());
                delivery.setDlvDate(deliveryDate);
                delivery.setPhoto(photoPath);
                dao.updateDelivery(delivery);
            }
            closeWindow();

        } catch (SQLException e) {
            showError(e.getMessage());
        } catch (NumberFormatException e) {
            showError("Receipt number must be numeric");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) fnameTf.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleUpload() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Delivery Photo");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Image Files",
                "*.png",
                "*.jpg",
                "*.jpeg",
                "*.gif"));

        File file = chooser.showOpenDialog(photoPane.getScene().getWindow());
        if (file == null) {
            return;
        }
        try {
            photoPath = S3Uploader.upload(file);
            showImage(photoPath);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Upload Failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDelete() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Confirm");
            alert.setContentText("Are you sure you want to delete?");
            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                if (delivery != null) {
                    dao.deleteDelivery(delivery.getId());
                    Stage stage = (Stage) completedCb.getScene().getWindow();
                    stage.close();
                }
            }

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void showImage(String imageUrl) {
        Image image = new Image(imageUrl);
        if (imageView == null) {
            imageView = new ImageView();
            imageView.setPreserveRatio(true);
            imageView.fitWidthProperty().bind(photoPane.widthProperty());
            imageView.fitHeightProperty().bind(photoPane.heightProperty());
            photoPane.setCenter(imageView);
            imageView.setImage(image);
            imageView.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    showFullImage(image);
                }
            });
            imageView.setStyle("-fx-cursor: hand");
            Tooltip.install(imageView, new Tooltip("Double-click to enlarge"));
        }
    }

    private void showFullImage(Image image) {
        ImageView fullImageView = new ImageView(image);
        fullImageView.setPreserveRatio(true);
        BorderPane root = new BorderPane(fullImageView);
        Scene scene = new Scene(root, 1000, 800);
        fullImageView.fitHeightProperty().bind(scene.heightProperty());
        fullImageView.fitWidthProperty().bind(scene.widthProperty());
        Stage stage = new Stage();
        stage.setTitle("Photo preview");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleFillFromId() {
        PatientDBConnection patientDao = new PatientDBConnection();
        try {
            if (receiptTf.getText().isBlank()) {
                return;
            }

            ReceiptLookupResult foundPatients = patientDao.findPatientsById(Integer.parseInt(receiptTf.getText().trim()));
            if (foundPatients.getItems().isEmpty()) {
                showError("No Matches Found");
                return;
            }
            Patient patient = foundPatients.getPatient();
            fnameTf.setText(patient.getFirstName());
            lnameTf.setText(patient.getSurname());
            phoneTf.setText(patient.getMobileNumber());
            homePhoneTf.setText(patient.getPhoneNumber());
            addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
            itemsTable.setItems(FXCollections.observableArrayList(foundPatients.getItems()));

        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleFillFromName() {
        itemsTable.getItems().clear();
        PatientDBConnection patientDao = new PatientDBConnection();
        try {
            if (fnameTf.getText().isBlank() && lnameTf.getText().isBlank()) {
                return;
            }

            List<Patient> foundPatients = patientDao.findPatientsByName(fnameTf.getText(), lnameTf.getText());
            if (foundPatients.isEmpty()) {
                showError("No Matches Found");
                return;
            }
            if (foundPatients.size() == 1) {
                Patient patient = foundPatients.getFirst();
                fnameTf.setText(patient.getFirstName());
                lnameTf.setText(patient.getSurname());
                phoneTf.setText(patient.getMobileNumber());
                homePhoneTf.setText(patient.getPhoneNumber());
                addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
            }
            if (foundPatients.size() > 1) {
                Patient patient = showPatientSelection(foundPatients);
                assert patient != null;
                fnameTf.setText(patient.getFirstName());
                lnameTf.setText(patient.getSurname());
                phoneTf.setText(patient.getMobileNumber());
                homePhoneTf.setText(patient.getPhoneNumber());
                addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private Patient showPatientSelection(
            List<Patient> patients) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/elpdeliveries/PatientSelection.fxml"));
            Scene scene = new Scene(loader.load());
            PatientSelectionController controller = loader.getController();
            controller.setPatients(patients);
            Stage stage = new Stage();
            stage.setTitle("Select Patient");
            stage.setScene(scene);
            stage.showAndWait();
            return controller.getSelectedPatient();
        } catch (Exception e) {
            showError(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleGeocodeDelivery() {
        DeliveryGeocodingService geocoder = new DeliveryGeocodingService();
        try {
            handleSubmit();
            geocoder.geocodeDelivery(delivery);
            dao.updateDelivery(delivery);
            setDelivery(delivery);
            driverNotesTa.setText(delivery.getLatitude() + "\n" + delivery.getLongitude());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}