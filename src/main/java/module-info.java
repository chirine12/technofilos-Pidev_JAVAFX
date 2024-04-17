module com.example.test.projet_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.internal.le;
    requires twilio;
    // The controllers package must be opened to javafx.fxml for reflection.
    opens tn.esprit.controllers to javafx.fxml;
    // If you have FXML files in tn.esprit.test package, you should open it to javafx.fxml as well.
    opens tn.esprit.test to javafx.fxml;
    // You need to export the tn.esprit.test package to javafx.graphics to allow JavaFX to access your Main class.
    exports tn.esprit.test to javafx.graphics;
    opens tn.esprit.model to javafx.base;
    exports com.example.test.projet_java;

}

