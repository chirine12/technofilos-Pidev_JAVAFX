package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.model.Compteep;
import tn.esprit.model.TypeTaux;
import tn.esprit.service.CompteepService;
import tn.esprit.service.TypetauxService;
import tn.esprit.utils.SQLConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class typetaux implements Initializable {

    @FXML
    private TextField ttype;

    @FXML
    private TextField tTaux;

    @FXML
    private TableView<TypeTaux> table;

    @FXML
    private TableColumn<TypeTaux, String> typeColumn;

    @FXML
    private TableColumn<TypeTaux, Double> tauxColumn;

    private TypetauxService typetauxService;
    private ObservableList<TypeTaux> typetauxData = FXCollections.observableArrayList();
    @FXML
    private Button btnretour;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       typetauxService = new TypetauxService();


        initializeColumns(); // Initialise les colonnes de la table
        loadData(); // Charge les données dans la table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Afficher le type de l'élément sélectionné dans le champ de texte
                ttype.setText(newSelection.getType());
                // Récupérer le taux de l'élément sélectionné et le mettre dans un champ modifiable
                tTaux.setText(String.valueOf(newSelection.getTaux()));
                // Désactiver le champ de texte pour le type pour empêcher la modification
                ttype.setDisable(true);

            }
        });
    }
    private void initializeColumns() {
        // Initialise les colonnes de la table en associant chaque colonne à une propriété du modèle Compteep
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        tauxColumn.setCellValueFactory(new PropertyValueFactory<>("taux"));


    }

    private void loadData() {
        try {
            // Charge les données à partir du service CompteepService
            List<TypeTaux> typetauxList = typetauxService.read();
            typetauxData.addAll(typetauxList);
            table.setItems(typetauxData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins, par exemple, afficher un message d'erreur
        }
    }
    // Méthode pour créer un nouveau TypeTaux
    @FXML

    private void createTypetaux() {
        // Récupérer les valeurs depuis les champs de texte
        String type = ttype.getText();
        String tauxStr = tTaux.getText();

        // Valider le type
        if (!typetauxService.validateDescription(type)) {
            showAlertWithWarning("Invalid type", "Description must be maximum 10 characters long and must not contain digits.");
            return; // Arrêter la création du compte si la description est invalide
        }

        // Valider le taux
        if (!typetauxService.validateTaux(tauxStr)) {
            showAlertWithWarning("Invalid Taux", "Please enter a valid positive number for the Taux.");
            return;
        }

        double taux = Double.parseDouble(tauxStr);

        // Créer un nouvel objet TypeTaux
        TypeTaux newTypeTaux = new TypeTaux(type, taux);
        try {
            // Appeler la méthode de service pour créer le TypeTaux
            typetauxService.create(newTypeTaux);
            // Effacer toutes les données existantes du TableView
            typetauxData.clear();
            // Recharger les données dans le TableView
            loadData();
            // Effacer les champs de texte après la création réussie
            ttype.clear();
            tTaux.clear();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            // Gérer l'erreur, par exemple, afficher un message à l'utilisateur
        }
    }


    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showAlertWithWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    // Méthode pour modifier un TypeTaux sélectionné
    @FXML
    private void modifyTypetaux() {
        // Récupérer l'élément sélectionné dans la table
        TypeTaux selectedTypetaux = table.getSelectionModel().getSelectedItem();
        if (selectedTypetaux != null) {
            // Valider le taux
            String newTauxStr = tTaux.getText(); // Récupérer le nouveau taux
            if (!typetauxService.validateTaux(newTauxStr)) {
                showAlertWithWarning("Invalid Taux", "Please enter a valid positive number for the Taux.");
                return;
            }

            double newTaux = Double.parseDouble(newTauxStr);

            // Mettre à jour la propriété du taux du TypeTaux sélectionné avec la nouvelle valeur
            selectedTypetaux.setTaux(newTaux);

            try {
                // Mettre à jour l'élément dans la base de données
                typetauxService.update(selectedTypetaux);
                // Rafraîchir la table avec les données mises à jour
                typetauxData.clear();
                // Recharger les données dans le TableView
                loadData();
                // Effacer les champs de texte après la modification réussie
                tTaux.clear();
            } catch (SQLException e) {
                showAlertWithError("Error updating TypeTaux", e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlertWithWarning("No TypeTaux selected", "Please select a TypeTaux to modify.");
        }
    }


    // Méthode pour supprimer un TypeTaux sélectionné
    @FXML
    private void deleteTypetaux() {
        // Récupérer l'élément sélectionné dans la table
        TypeTaux selectedTypetaux = table.getSelectionModel().getSelectedItem();
        if (selectedTypetaux != null) {
            // Demander confirmation à l'utilisateur
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete TypeTaux");
            alert.setContentText("Are you sure you want to delete this TypeTaux?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    // Supprimer l'élément de la base de données
                    typetauxService.delete(selectedTypetaux.getId());
                    // Mettre à jour la liste observée
                    typetauxData.remove(selectedTypetaux);
                } catch (SQLException e) {
                    showAlertWithError("Error deleting TypeTaux", e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            showAlertWithWarning("No TypeTaux selected", "Please select a TypeTaux to delete.");
        }
    }

    @FXML
    private void admindashbordpage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/admindashbord.fxml"));
            Parent root = loader.load();

            // Get the current window or create a new stage if necessary
            Stage stage = (Stage) btnretour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the demands page.");
        }
    }

}
