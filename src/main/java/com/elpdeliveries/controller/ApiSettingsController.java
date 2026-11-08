package com.elpdeliveries.controller;

import com.elpdeliveries.util.AppLogger;
import com.elpdeliveries.util.ConfigManager;
import com.elpdeliveries.util.ErrorHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiSettingsController {
    @FXML
    private TextField serverTf;
    @FXML
    private TextField portTf;

    @FXML
    public void initialize() {
        try {
            if (ConfigManager.exists()) {
                serverTf.setText(ConfigManager.getServer());
                portTf.setText(ConfigManager.getPort());
            }
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            if (testConnection(serverTf.getText(), portTf.getText())) {
                ConfigManager.save(serverTf.getText(), portTf.getText());
                Stage stage = (Stage) portTf.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    @FXML
    private void handleMoreSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elpdeliveries/Settings.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            ErrorHandler.showError(e.getMessage(), e);
        }
    }

    public static Boolean testConnection(String server, String port) throws Exception {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + server + ":" + port + "/api/deliveries"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        AppLogger.info("Response code: " + response.statusCode());
        if (response.statusCode() == 200) {
            return true;
        } else {
            ErrorHandler.showError("Reponse code: " + response.statusCode() +
                    "\nMaybe try a different address/port?", new HttpRetryException("", response.statusCode()));
            return false;
        }
    }
}
