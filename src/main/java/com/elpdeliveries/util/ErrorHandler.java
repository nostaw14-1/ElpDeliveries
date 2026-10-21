package com.elpdeliveries.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ErrorHandler {
    private ErrorHandler() {}

    public static void showError(String message, Throwable throwable) {
        AppLogger.error(message, throwable);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(throwable.getClass().getSimpleName());
        alert.setHeaderText(message);

        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        TextArea area = new TextArea(sw.toString());
        area.setEditable(false);
        alert.getDialogPane().setExpandableContent(area);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }
}
