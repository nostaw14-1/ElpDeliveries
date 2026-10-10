import java.io.IOException;

import controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ElpDeliveries extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private void openSettings() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Settings.fxml"));
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Initial Setings");
        try {
            settingsStage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingsStage.showAndWait();
    }

    public void start(Stage primaryStage) throws Exception {
        if (!MainController.configExists()) {
            openSettings();
        }
        if (!MainController.configExists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Configuration Missing");
            alert.setContentText("A Database Configuration must be created");
            alert.showAndWait();
            Platform.exit();
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("ELP Deliveries");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}