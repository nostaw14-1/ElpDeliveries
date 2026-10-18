package com.elpdeliveries.controller;

import java.util.List;

import com.elpdeliveries.model.Patient;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class PatientSelectionController extends Controller<List<Patient>> {

    @FXML
    private TableView<Patient> patientTable;

    @FXML
    private TableColumn<Patient, String> firstNameCol;

    @FXML
    private TableColumn<Patient, String> surnameCol;

    @FXML
    private TableColumn<Patient, String> mobilePhoneCol;

    @FXML
    private TableColumn<Patient, String> homePhoneCol;

    @FXML
    private TableColumn<Patient, String> addressCol;

    private Patient selectedPatient;

    @FXML
    public void initialize() {

        firstNameCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("firstName"));

        surnameCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("surname"));

        mobilePhoneCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("mobileNumber"));

        homePhoneCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("phoneNumber"));

        addressCol.setCellValueFactory(
                new javafx.scene.control.cell.PropertyValueFactory<>("homeAddress"));
    }

    public void setPatients(List<Patient> patients) {
        patientTable.setItems(
                FXCollections.observableArrayList(patients));
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
