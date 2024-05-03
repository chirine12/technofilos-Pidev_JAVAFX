package com.example.projetpi.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le nouveau fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/newcontrat.fxml"));

        // Créer la scène
        Scene scene = new Scene(root);

        // Configurer la scène et afficher la fenêtre
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();

    }
}
