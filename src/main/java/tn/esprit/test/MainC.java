package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainC extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCarte.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);

        // Charger le fichier CSS et l'ajouter à la scène
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style1.css")).toExternalForm());

        // Créer la barre de menu
        MenuBar menuBar = new MenuBar();
        // Ajoutez vos éléments de menu ici

        // Créer un conteneur BorderPane pour organiser la barre de menu et le contenu principal
        BorderPane root = new BorderPane();
        root.setTop(menuBar); // Ajouter la barre de menu en haut
        root.setCenter(parent); // Ajouter le contenu principal au centre

        // Créer une nouvelle scène avec le BorderPane en tant que racine
        Scene sceneWithMenuBar = new Scene(root, 1200, 700);

        // Appliquer la scène à la fenêtre principale
        stage.setScene(sceneWithMenuBar);
        stage.setTitle("Ajouter une carte");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
