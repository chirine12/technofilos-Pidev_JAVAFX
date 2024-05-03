module com.example.projetpi {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.xml;
    requires java.sql;

    opens com.example.projetpi to javafx.fxml;


    exports com.example.projetpi.Controllers;
    opens com.example.projetpi.Controllers to javafx.fxml;
    exports com.example.projetpi.Model;
    opens com.example.projetpi.Model to javafx.fxml;
    exports com.example.projetpi.test;
    opens com.example.projetpi.test to javafx.fxml;
    exports com.example.projetpi.utils;
    opens com.example.projetpi.utils to javafx.fxml;
}