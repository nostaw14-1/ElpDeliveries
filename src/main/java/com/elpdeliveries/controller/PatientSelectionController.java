package com.elpdeliveries.controller;

import java.util.List;

import com.elpdeliveries.model.Patient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class PatientSelectionController {

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> firstNameCol;

    @FXML
    private TableColumn<Patient, String> surnameCol;

    @FXML
    private TableColumn<Patient, String> phoneCol;

    @FXML
    private TableColumn<Patient, String> addressCol;

    private Patient selectedPatient;

    @FXML
    public void initialize() {

        firstNameCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("firstName"));

        surnameCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("surname"));

        phoneCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("mobileNumber"));

        addressCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("homeAddress"));
    }

    public void setPatients(List<Patient> patients) {
        patientTable.setItems(
                FXCollections.observableArrayList(patients));
    }

    @FXML
    private void handleSelect() {

        selectedPatient = patientTable.getSelectionModel()
                .getSelectedItem();

        if (selectedPatient != null) {
            ((Stage) patientTable.getScene()
                    .getWindow())
                    .close();
        }
    }

    public Patient getSelectedPatient() {
        return selectedPatient;
    }

    @FXML
    private void handleUseSelected() {
        selectedPatient = patientTable.getSelectionModel()
                .getSelectedItem();

        if (selectedPatient == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setHeaderText("No Patient Selected");
            alert.setContentText("Please select a patient.");

            alert.showAndWait();

            return;
        }

        Stage stage = (Stage) patientTable
                .getScene()
                .getWindow();

        stage.close();
    }
}