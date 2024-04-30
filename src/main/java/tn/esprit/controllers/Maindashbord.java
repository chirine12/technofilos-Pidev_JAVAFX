package tn.esprit.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

public class Maindashbord {
    @FXML
    private AnchorPane contenu;

    public void changeContent(String fxmlFile) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlFile));

            contenu.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getContenu() {
        return contenu;
    }

    public void setContenu(Node contenu) {
        this.contenu.getChildren().setAll(contenu);
    }
}
