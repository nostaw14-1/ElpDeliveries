package com.elpdeliveries.controller;

import java.sql.Connection;
import java.sql.DriverManager;

import com.elpdeliveries.util.ConfigManager;

import com.elpdeliveries.util.ErrorHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController {

        @FXML
        private TextField ipTf;

        @FXML
        private TextField portTf;

        @FXML
        private TextField dbTf;

        @FXML
        private TextField usernameTf;

        @FXML
        private PasswordField passwordPf;

        @FXML
        private TextField bucketTf;

        @FXML
        private TextField akTf;

        @FXML
        private TextField skTf;

        @FXML private TextField sqlServerTf;
        @FXML private TextField sqlDbTf;
        @FXML private TextField sqlUserTf;
        @FXML private PasswordField sqlPwdPf;
        @FXML private TextField vroomTf;
        @FXML private TextField depotLatTf;
        @FXML private TextField depotLongTf;

        @FXML
        public void initialize() {
                try {
                        if (ConfigManager.exists()) {
                                ipTf.setText(ConfigManager.getServer());
                                portTf.setText(ConfigManager.getPort());
                                dbTf.setText(ConfigManager.getDatabase());
                                usernameTf.setText(ConfigManager.getUsername());
                                passwordPf.setText(ConfigManager.getPassword());
                                sqlServerTf.setText(ConfigManager.getSqlServer());
                                sqlDbTf.setText(ConfigManager.getSqlDatabase());
                                sqlUserTf.setText(ConfigManager.getSqlUsername());
                                sqlPwdPf.setText(ConfigManager.getSqlPassword());
                                bucketTf.setText(ConfigManager.getBucket());
                                akTf.setText(ConfigManager.getAwsAccessKey());
                                skTf.setText(ConfigManager.getAwsSecretKey());
                                vroomTf.setText(ConfigManager.getVroomIp());
                                depotLatTf.setText(ConfigManager.getDepotLat());
                                depotLongTf.setText(ConfigManager.getDepotLong());
                        }
                } catch (Exception e) {
                        ErrorHandler.showError(e.getMessage(), e);
                }
        }

        @FXML
        private boolean handleTest() {

                String server = ipTf.getText().trim();
                String port = portTf.getText().trim();
                String database = dbTf.getText().trim();
                String username = usernameTf.getText().trim();
                String password = passwordPf.getText().trim();

                String url = "jdbc:mysql://" + server + ":" + port + "/" + database;
                try (Connection _ = DriverManager.getConnection(url, username, password)) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Connection Successful");
                        alert.setContentText("Connected successfully.");
                        alert.showAndWait();
                        return true;
                } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Connection Failed");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                        return false;
                }
        }

        @FXML
        private void handleSubmit() {
                if (handleTest()) {
                        try {
                                ConfigManager.save(ipTf.getText(),
                                                portTf.getText(),
                                                dbTf.getText(),
                                                usernameTf.getText(),
                                                passwordPf.getText(),
                                                sqlServerTf.getText(),
                                                sqlDbTf.getText(),
                                                sqlUserTf.getText(),
                                                sqlPwdPf.getText(),
                                                akTf.getText(),
                                                skTf.getText(),
                                                bucketTf.getText(),
                                                vroomTf.getText(),
                                                depotLatTf.getText(),
                                                depotLongTf.getText());
                                Stage stage = (Stage) ipTf.getScene().getWindow();
                                stage.close();
                        } catch (Exception e) {
                                ErrorHandler.showError(e.getMessage(), e);
                        }
                }
        }
}