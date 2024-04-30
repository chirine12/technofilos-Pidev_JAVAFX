module com.example.test {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires io;
    opens com.example.test.model;
    opens com.example.test.controllers to javafx.fxml;
    opens com.example.test.test to javafx.graphics;
    requires javafx.web;
    requires java.net.http;
    requires java.mail;
    opens com.example.test to javafx.fxml;

    exports com.example.test;
}