package controller;

import java.sql.Connection;
import java.sql.DriverManager;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.ConfigManager;

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
        public void initialize() {
                try {
                        if (ConfigManager.exists()) {
                                ipTf.setText(ConfigManager.getServer());
                                portTf.setText(ConfigManager.getPort());
                                dbTf.setText(ConfigManager.getDatabase());
                                usernameTf.setText(ConfigManager.getUsername());
                                passwordPf.setText(ConfigManager.getPassword());
                        }
                } catch (Exception e) {
                }
        }

        @FXML
        private void handleTest() {

                String url = "jdbc:mysql://" + ipTf.getText() + ":" + portTf.getText() + "/" + dbTf.getText();
                try (
                                Connection conn = DriverManager.getConnection(url, usernameTf.getText(),
                                                passwordPf.getText())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Connection Successful");
                        alert.setContentText("Connected successfully.");
                        alert.showAndWait();
                } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Connection Failed");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                }
        }

        @FXML
        private void handleSubmit() {
                try {
                        ConfigManager.save(ipTf.getText(),
                                        portTf.getText(),
                                        dbTf.getText(),
                                        usernameTf.getText(),
                                        passwordPf.getText());
                        Stage stage = (Stage) ipTf.getScene().getWindow();
                        stage.close();
                } catch (Exception e) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Save Failed");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                }
        }
}