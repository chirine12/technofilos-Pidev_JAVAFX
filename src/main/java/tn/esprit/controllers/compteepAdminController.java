package tn.esprit.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.model.Compteep;
import tn.esprit.service.CompteepService;
import tn.esprit.utils.SQLConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Calendar;
import java.sql.SQLException;
import java.util.ArrayList;

import tn.esprit.service.TypetauxService;

public class compteepAdminController implements Initializable {
    @FXML
    private TableColumn<Compteep, Integer> idColumn;

    @FXML
    private TableColumn<Compteep, Long> ribColumn;

    @FXML
    private TableColumn<Compteep, Double> soldeColumn;

    @FXML
    private TableColumn<Compteep, String> typeColumn;

    @FXML
    private TableColumn<Compteep, Date> dateouvColumn;

    @FXML
    private TableColumn<Compteep, String> descriptionColumn;

    @FXML
    private TableColumn<Compteep, Boolean> etatColumn;


    @FXML
    private TextField tDescription;
    @FXML
    private ComboBox<String> cmbType;
    @FXML
    private TableView<Compteep> table;

    private CompteepService compteepService;
    private ObservableList<Compteep> compteepsData = FXCollections.observableArrayList();



    @FXML
    private Button btndemandes;


    @FXML
    private Button btntypetaux;
    private Maindashbord mainController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        compteepService = new CompteepService();
        initializeColumns();
        loadData();

        // Initially disable the description field
        tDescription.setDisable(true);

        // Add listener to the table's selected item property
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Check if a new row is selected
            if (newSelection != null) {
                // Set the text from the selected item's description
                tDescription.setText(newSelection.getDescription());
                // Enable the description field for editing
                tDescription.setDisable(false);
            } else {
                // Clear the text and disable the field if no row is selected
                tDescription.setText("");
                tDescription.setDisable(true);
            }
        });
    }



    private void initializeColumns() {
        // Initialise les colonnes de la table en associant chaque colonne à une propriété du modèle Compteep
        ribColumn.setCellValueFactory(new PropertyValueFactory<>("rib"));
        soldeColumn.setCellValueFactory(new PropertyValueFactory<>("solde"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateouvColumn.setCellValueFactory(new PropertyValueFactory<>("dateouv"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));

    }

    private void loadData() {
        try {
            // Charge les données à partir du service CompteepService
            List<Compteep> compteepList = compteepService.read();
            compteepsData.addAll(compteepList);
            table.setItems(compteepsData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins, par exemple, afficher un message d'erreur
        }

    }






    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void modifyCompteep(ActionEvent event) {
        // Récupérer l'élément sélectionné dans la table
        Compteep selectedCompteep = table.getSelectionModel().getSelectedItem();
        if (selectedCompteep != null) {
            // Valider la description en utilisant la méthode de validation du service
            String newDescription = tDescription.getText();
            if (!compteepService.validateDescription(newDescription)) {
                showAlertWithWarning("Invalid Description", "Description must be maximum 10 characters long and must not contain digits.");
                return; // Arrêter la modification du compte si la description est invalide
            }

            // Mettre à jour la description de l'élément sélectionné
            selectedCompteep.setDescription(newDescription);

            try {
                // Mettre à jour l'élément dans la base de données
                 compteepService.update(selectedCompteep);
                // Rafraîchir la table avec les données mises à jour
                table.refresh();
            } catch (SQLException e) {
                showAlertWithError("Error updating account", e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlertWithWarning("No account selected", "Please select an account to modify.");
        }
    }

    private void showAlertWithWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML

    void desactiverCompteep(ActionEvent event) {
        // Récupérer l'élément sélectionné dans la table
        Compteep selectedCompteep = table.getSelectionModel().getSelectedItem();
        if (selectedCompteep != null) {
            try {
                // Mettre à jour l'état du compte dans la base de données en utilisant son ID
                selectedCompteep.setEtat(false); // Mettre à jour l'état dans l'objet Compteep
                compteepService.update(selectedCompteep); // Mettre à jour l'état dans la base de données

                // Rafraîchir la vue de la table
                table.refresh();
            } catch (SQLException e) {
                showAlertWithError("Error disabling account", e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlertWithWarning("No account selected", "Please select an account to disable.");
        }
    }
    @FXML

    void activerCompteep(ActionEvent event) {
        // Récupérer l'élément sélectionné dans la table
        Compteep selectedCompteep = table.getSelectionModel().getSelectedItem();
        if (selectedCompteep != null) {
            try {
                // Mettre à jour l'état du compte dans la base de données en utilisant son ID
                selectedCompteep.setEtat(true); // Mettre à jour l'état dans l'objet Compteep
                compteepService.update(selectedCompteep); // Mettre à jour l'état dans la base de données

                // Rafraîchir la vue de la table
                table.refresh();
            } catch (SQLException e) {
                showAlertWithError("Error disabling account", e.getMessage());
                e.printStackTrace();
            }
        } else {
            showAlertWithWarning("No account selected", "Please select an account to disable.");
        }
    }

    @FXML
    private void voirstat(ActionEvent event) {
        try {
            // Charge le FXML pour la page des demandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/statcompteep.fxml"));
            Parent statPage = loader.load();

            // Obtient le contrôleur de la page des demandes
           StatCompteepController statController= loader.getController();

            // Assurez-vous que demandeController a une méthode setMainController ou similaire
            statController.setMainController(mainController);

            // Change le contenu principal à la nouvelle page des demandes
            mainController.setContenu(statPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pagedemandes() {
        try {
            // Charge le FXML pour la page des demandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/acceptdemande.fxml"));
            Parent demandePage = loader.load();

            // Obtient le contrôleur de la page des demandes
            demandesController demandeController = loader.getController();

            // Assurez-vous que demandeController a une méthode setMainController ou similaire
            demandeController.setMainController(mainController);

            // Change le contenu principal à la nouvelle page des demandes
            mainController.setContenu(demandePage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pagetypetaux() {
        try {
            // Charge le FXML pour la page des demandes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/Typetaux.fxml"));
            Parent typetauxPage = loader.load();

            // Obtient le contrôleur de la page des demandes
            typetaux typetaux= loader.getController();

            // Assurez-vous que demandeController a une méthode setMainController ou similaire
            typetaux.setMainController(mainController);

            // Change le contenu principal à la nouvelle page des demandes
            mainController.setContenu(typetauxPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainController(Maindashbord mainController) {
        this.mainController = mainController;
    }

}