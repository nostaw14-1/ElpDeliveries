package com.elpdeliveries.controller;

import com.elpdeliveries.model.Patient;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.util.List;

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

        selectedPatient =
                patientTable.getSelectionModel()
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
}