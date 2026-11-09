package com.elpdeliveries.controller;

import com.elpdeliveries.model.*;
import com.elpdeliveries.util.*;
import javafx.application.Platform;
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
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class AddEditController {
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
    private TableView<ReceiptItemDto> itemsTable;
    @FXML
    private TableColumn<ReceiptItemDto, Integer> qtyCol;
    @FXML
    private TableColumn<ReceiptItemDto, String> itemCol;
    @FXML
    private TableColumn<ReceiptItemDto, String> priceCol;
    private String photoPath;
    private ImageView imageView;
    private Delivery delivery;
    @Setter
    private Runnable onSave;
    @Setter
    private Consumer<LocalDate> onSaveAndContinue;

    @FXML
    private void initialize() {
        Platform.runLater(() -> receiptTf.requestFocus());
        qtyCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("qty"));
        itemCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
        priceCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("priceString"));
        itemsTable.setPlaceholder(new Text("Items not available"));
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        if (delivery == null) {
            deleteButton.setVisible(false);
            dfdDatePicker.setValue(LocalDate.now());
            return;
        }
        fnameTf.setText(delivery.getFname());
        lnameTf.setText(delivery.getLname());
        phoneTf.setText(delivery.getPhnum());
        homePhoneTf.setText(delivery.getHomePhone());
        receiptTf.setText(delivery.getReceiptId());
        addrTa.setText(delivery.getAddr());
        driverNotesTa.setText(delivery.getNotes());
        completedByTf.setText(delivery.getCompletedBy());
        handedCb.setSelected(delivery.getHandedToCustomer());
        completedCb.setSelected(delivery.getCompleted());
        if (delivery.getCreatedDate() != null) {
            createdAtTf.setText(delivery.getCreatedDate().toString());
        }
        if (delivery.getCompletedDate() != null) {
            completedAtTf.setText(delivery.getCompletedDate().toString());
        }
        if (delivery.getDlvDate() != null) {
            dfdDatePicker.setValue(delivery.getDlvDate());
        }

        if (delivery.getPhoto() != null && !delivery.getPhoto().isEmpty()) {
            photoPath = delivery.getPhoto();
            showImage(photoPath);
        }
        try {
            PatientLookupDto items = ApiClient.getInstance().findPatientsById(delivery.getReceiptId());
            itemsTable.setItems(FXCollections.observableArrayList(items.getItems()));
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    @FXML
    private boolean saveDelivery() {
        try {
            LocalDate deliveryDate = null;
            if (dfdDatePicker.getValue() != null) {
                deliveryDate = dfdDatePicker.getValue();
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
                        driverNotesTa.getText(),
                        receiptTf.getText(),
                        photoPath,
                        completedCb.isSelected(),
                        completedByTf.getText(),
                        null,
                        handedCb.isSelected(),
                        null,
                        null,
                        null,
                        true);
                ApiClient.getInstance().addDelivery(newDelivery);
                AppLogger.info("Added new delivery");

            } else {
                delivery.setFname(fnameTf.getText());
                delivery.setLname(lnameTf.getText());
                delivery.setPhnum(phoneTf.getText());
                delivery.setHomePhone(homePhoneTf.getText());
                delivery.setAddr(addrTa.getText());
                delivery.setNotes(driverNotesTa.getText());
                delivery.setReceiptId(receiptTf.getText().trim());
                delivery.setCompleted(completedCb.isSelected());
                delivery.setCompletedBy(completedByTf.getText());
                delivery.setHandedToCustomer(handedCb.isSelected());
                delivery.setDlvDate(deliveryDate);
                delivery.setPhoto(photoPath);
                AppLogger.info("Updated delivery: " + delivery.getId());
                ApiClient.getInstance().updateDelivery(delivery);
            }
        return true;
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
            return false;
        }
    }

    @FXML
    private void handleSubmit() {
        if (!saveDelivery()) {
            return;
        }
        if (onSave != null) {
            onSave.run();
        }
        closeWindow();
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
            AppLogger.info("Uploaded file: " + file);
            showImage(photoPath);
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
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
                    AppLogger.info("Deleting delivery: " + delivery.getId());
                    ApiClient.getInstance().deleteDelivery(delivery.getId());
                    Stage stage = (Stage) completedCb.getScene().getWindow();
                    stage.close();
                }
            }

        } catch (Exception e) {
            ErrorHandler.showError("Delete failed!", e);
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
    private boolean handleFillFromId() {
        try {
            if (receiptTf.getText().isBlank()) {
                return false;
            }
            PatientLookupDto foundPatient = ApiClient.getInstance().findPatientsById(receiptTf.getText().trim());
            if (foundPatient.getFirstName().isEmpty()) {
                ErrorHandler.showError("No matches found", new Exception("No matches found"));
                return false;
            }
            fnameTf.setText(foundPatient.getFirstName());
            lnameTf.setText(foundPatient.getSurname());
            phoneTf.setText(foundPatient.getMobileNumber());
            homePhoneTf.setText(foundPatient.getPhoneNumber());
            addrTa.setText(foundPatient.getHomeAddress() + ", " + foundPatient.getHomeSuburb());
            itemsTable.setItems(FXCollections.observableArrayList(foundPatient.getItems()));
            return true;
        }
        catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
            return false;
        }
    }

    @FXML
    private void handleFillFromName() {
        itemsTable.getItems().clear();

        try {
            if (fnameTf.getText().isBlank() && lnameTf.getText().isBlank()) {
                return;
            }

            List<Patient> foundPatients = ApiClient.getInstance().findPatientsByName(fnameTf.getText(), lnameTf.getText());
            if (foundPatients.isEmpty()) {
                ErrorHandler.showError("No matches found", new Exception("No matches found"));
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
                if (patient != null) {
                    fnameTf.setText(patient.getFirstName());
                    lnameTf.setText(patient.getSurname());
                    phoneTf.setText(patient.getMobileNumber());
                    homePhoneTf.setText(patient.getPhoneNumber());
                    addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
                }
            }
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    private Patient showPatientSelection(List<Patient> patients) {
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
            ErrorHandler.showError(e.getMessage(), e);
            return null;
        }
    }

    @FXML
    public void handleReceiptScanned() {
        try {
            if (!handleFillFromId()) return;
            if (!saveDelivery()) return;
            LocalDate continueDate = dfdDatePicker.getValue();
            closeWindow();
            Platform.runLater(() -> {
                if (onSaveAndContinue != null) {
                    onSaveAndContinue.accept(continueDate);
                }
            });

        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    public void setDate(LocalDate date) {
        Platform.runLater(() -> dfdDatePicker.setValue(date));
    }
}