package com.elpdeliveries.controller;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import com.elpdeliveries.model.Delivery;
import com.elpdeliveries.model.Patient;
import com.elpdeliveries.util.DBConnection;
import com.elpdeliveries.util.PatientDBConnection;
import com.elpdeliveries.util.S3Uploader;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AddEditController {
    @FXML
    private TextField fnameTf;
    @FXML
    private TextField lnameTf;
    @FXML
    private TextField phoneTf;
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
    private TextArea storeNotesTa;
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
    private String photoPath;
    private ImageView imageView;
    private Delivery delivery;
    private DBConnection dao = new DBConnection();

    @FXML
    private void initialize() {

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
        receiptTf.setText(delivery.getReceiptNum() + "");
        addrTa.setText(delivery.getAddr());
        storeNotesTa.setText(delivery.getStoreNotes());
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
    }

    @FXML
    private void handleSubmit() {
        try {
            Date deliveryDate = null;
            Integer receiptNum = null;
            if (dfdDatePicker.getValue() != null) {
                deliveryDate = Date.valueOf(dfdDatePicker.getValue());
            }
            if (receiptTf.getText() != null) {
                receiptNum = Integer.parseInt(receiptTf.getText());
            }
            if (delivery == null) {
                Delivery newDelivery = new Delivery(
                        null,
                        deliveryDate,
                        fnameTf.getText(),
                        lnameTf.getText(),
                        phoneTf.getText(),
                        addrTa.getText(),
                        storeNotesTa.getText(),
                        driverNotesTa.getText(),
                        receiptNum,
                        photoPath,
                        completedCb.isSelected(),
                        completedByTf.getText(),
                        null,
                        handedCb.isSelected());
                dao.addDelivery(newDelivery);
            } else {
                delivery.setFname(fnameTf.getText());
                delivery.setLname(lnameTf.getText());
                delivery.setPhnum(phoneTf.getText());
                delivery.setAddr(addrTa.getText());
                delivery.setStoreNotes(storeNotesTa.getText());
                delivery.setDlvNotes(driverNotesTa.getText());
                delivery.setReceiptNum(Integer.parseInt(receiptTf.getText().trim()));
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
            e.printStackTrace();
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
    private void handleFillFromZ() {
        PatientDBConnection patientDao = new PatientDBConnection();
        try {
            if (fnameTf.getText().trim() == null && lnameTf.getText().trim() == null) {
                return;
            }

            List<Patient> foundPatients = patientDao.findPatients(fnameTf.getText(), lnameTf.getText());
            if (foundPatients.size() == 0) {
                showError("No Matches Found");
                return;
            }
            if (foundPatients.size() == 1) {
                Patient patient = foundPatients.getFirst();
                fnameTf.setText(patient.getFirstName());
                lnameTf.setText(patient.getSurname());
                phoneTf.setText(patient.getMobileNumber());
                addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
            }
            if (foundPatients.size() > 1) {
                Patient patient = showPatientSelection(foundPatients);
                fnameTf.setText(patient.getFirstName());
                lnameTf.setText(patient.getSurname());
                phoneTf.setText(patient.getMobileNumber());
                addrTa.setText(patient.getHomeAddress() + ", " + patient.getHomeSuburb());
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            showError(e.getMessage());
            return null;
        }
    }

}