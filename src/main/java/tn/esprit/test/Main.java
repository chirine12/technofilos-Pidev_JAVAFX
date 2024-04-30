package tn.esprit.test;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    private Stage primaryStage; // Stage principal de l'application

    public SidebarController(Stage primaryStage) {
        this.primaryStage = primaryStage; // Injecter le Stage principal lors de la création du contrôleur
    }

    @FXML
    private void handleHome() {
        loadUI("/FXML/clientDashboard.fxml");
    }

    @FXML
    private void handleSettings() {
        loadUI("/FXML/adminDashboard.fxml");
    }

    private void loadUI(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root)); // Utiliser le Stage principal pour le changement de scène
        } catch (IOException e) {
            // Utiliser une logique plus robuste pour le traitement des erreurs
            System.out.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


