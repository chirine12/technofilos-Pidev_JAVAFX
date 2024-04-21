package tn.esprit.controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.esprit.model.demande_desac_ce;
import tn.esprit.service.SMSService;
import tn.esprit.service.demandesService;
import tn.esprit.utils.SQLConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class afficherdemandeClientController implements Initializable {
    @FXML
    private TableView table;

    @FXML
    private TableColumn ribColumn;


    @FXML
    private TableColumn raison;


    @FXML
    private Button btnretour;
    @FXML
    private  demandesService demandesService;
    private ObservableList<demande_desac_ce> demandesData = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        demandesService = new demandesService();


        initializeColumns(); // Initialise les colonnes de la table
        loadData(); // Charge les données dans la table

    }
    private void initializeColumns() {
        // Initialise les colonnes de la table en associant chaque colonne à une propriété du modèle Compteep
        ribColumn.setCellValueFactory(new PropertyValueFactory<>("rib")); // Ajoutez ceci
        raison.setCellValueFactory(new PropertyValueFactory<>("raison"));


    }

    private void loadData() {
        try {
            // Charge les données à partir du service CompteepService
            List<demande_desac_ce> demandesList = demandesService.readSpecificClient();

            demandesData.addAll(demandesList);
            table.setItems(demandesData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins, par exemple, afficher un message d'erreur
        }
    }


    @FXML
    private void clientdashbordpage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientdashbord.fxml"));
            Parent root = loader.load();



            Stage stage = (Stage) btnretour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load the demands page.");
        }
    }
}
