package com.elpdeliveries.controller;

import com.elpdeliveries.model.GeocodeResult;
import com.elpdeliveries.model.SettingsDto;
import com.elpdeliveries.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    private TextField ipTf;
    @FXML
    private TextField portTf;
    @FXML
    private TextField bucketTf;
    @FXML
    private TextField akTf;
    @FXML
    private TextField skTf;
    @FXML
    private TextField sqlServerTf;
    @FXML
    private TextField sqlUserTf;
    @FXML
    private PasswordField sqlPwdPf;
    @FXML
    private TextField vroomTf;
    @FXML
    private TextField depotLatTf;
    @FXML
    private TextField depotLongTf;
    @FXML
    private TextField addrTf;

    @FXML
    public void initialize() {
        try {
            if (ConfigManager.exists()) {
                ipTf.setText(ConfigManager.getServer());
                portTf.setText(ConfigManager.getPort());
                SettingsDto settings = ApiClient.getInstance().getSettings();
                if (settings != null) {
                    sqlServerTf.setText(settings.getSqlServer());
                    sqlUserTf.setText(settings.getSqlUsername());
                    sqlPwdPf.setText(settings.getSqlPassword());
                    bucketTf.setText(settings.getAwsBucket());
                    akTf.setText(settings.getAwsAccessKey());
                    skTf.setText(settings.getAwsSecretKey());
                    vroomTf.setText(settings.getVroomIp());
                    depotLatTf.setText(settings.getDepotLat().toString());
                    depotLongTf.setText(settings.getDepotLong().toString());
                }
            }

        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            ConfigManager.save(ipTf.getText(), portTf.getText());
            if (sqlServerTf.getText().isBlank()) {
                Stage stage = (Stage) ipTf.getScene().getWindow();
                stage.close();
                return;
            }
            SettingsDto settings = new SettingsDto();
            settings.setSqlServer(sqlServerTf.getText());
            settings.setSqlUsername(sqlUserTf.getText());
            settings.setSqlPassword(sqlPwdPf.getText());
            settings.setVroomIp(vroomTf.getText());
            settings.setDepotLat(Double.parseDouble(depotLatTf.getText().trim()));
            settings.setDepotLong(Double.parseDouble(depotLongTf.getText().trim()));
            settings.setAwsBucket(bucketTf.getText());
            settings.setAwsAccessKey(akTf.getText());
            settings.setAwsSecretKey(skTf.getText());
            ApiClient.getInstance().updateSettings(settings);

            Stage stage = (Stage) ipTf.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    @FXML
    private void handleGeocode() {
        if (addrTf.getText().isBlank()) {
            return;
        }
        try {
            GeocodeResult result = ApiClient.getInstance().geocode(addrTf.getText());
            if (result == null) {
                throw new RuntimeException("Could not find address!");
            }
            depotLatTf.setText(result.latitude() + "");
            depotLongTf.setText(result.longitude() + "");
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }

    }
}