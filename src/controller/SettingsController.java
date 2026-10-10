package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

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

        private final Properties props = new Properties();

        @FXML
        public void initialize() {
                try {
                        props.load(new FileInputStream("config.properties"));
                        ipTf.setText(props.getProperty("server", ""));
                        portTf.setText(props.getProperty("port", "3306"));
                        dbTf.setText(props.getProperty("database", ""));
                        usernameTf.setText(props.getProperty("username", ""));
                        passwordPf.setText(props.getProperty("password", ""));
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
                        props.setProperty("server", ipTf.getText());
                        props.setProperty("port", portTf.getText());
                        props.setProperty("database", dbTf.getText());
                        props.setProperty("username", usernameTf.getText());
                        props.setProperty("password", passwordPf.getText());
                        props.store(new FileOutputStream("config.properties"), "ELP Deliveries Settings");
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