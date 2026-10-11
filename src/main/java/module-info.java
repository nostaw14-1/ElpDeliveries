module com.elpdeliveries {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    exports com.elpdeliveries.controller;
    exports com.elpdeliveries.model;

    opens com.elpdeliveries.controller to javafx.fxml;
    exports com.elpdeliveries;
}
