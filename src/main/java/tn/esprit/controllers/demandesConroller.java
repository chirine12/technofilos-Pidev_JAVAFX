package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.model.TypeTaux;
import tn.esprit.model.demande_desac_ce;
import tn.esprit.service.SMSService;
import tn.esprit.service.TypetauxService;
import tn.esprit.service.demandesService;
import tn.esprit.utils.SQLConnector;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class demandesConroller implements Initializable {
    @FXML
    private TableView table;

    @FXML
    private TableColumn idCompteColumn;

    @FXML
    private TableColumn raison;

    @FXML
    private Button btnaccept;

    @FXML
    private Button btnrefuser;
    @FXML
    private  demandesService demandesService;
    private ObservableList<demande_desac_ce> demandesData = FXCollections.observableArrayList();
    private SMSService smsService = new SMSService();
    @FXML
    private void accept() {
        // Try to get the selected request from the table.
        demande_desac_ce selectedDemande = (demande_desac_ce) table.getSelectionModel().getSelectedItem();

        if (selectedDemande != null) {
            try {
                // Update the account state in the database.
                updateCompteState(selectedDemande.getCompteepId());

                // Delete the request from the database.
                demandesService.delete(selectedDemande.getId());

                // Remove the request from the observable list to update the UI.
                demandesData.remove(selectedDemande);

                // Refresh the table view to show the changes.
                table.refresh();

                // Send an SMS to notify about the request handling.
                try {
                    smsService.sendSms("+216 55 347 204", "+16204593244", "Votre demande de desactivation a été traitée.");
                } catch (com.twilio.exception.ApiException twilioEx) {
                    System.err.println("Twilio API error: " + twilioEx.getMessage());
                    // Here you can log the error or notify the user interface about the failure.
                }

            } catch (SQLException sqlEx) {
                sqlEx.printStackTrace();
                // Handle database errors, possibly re-throw or log, depending on your application's requirements.
            } catch (Exception ex) {
                System.err.println("An unexpected error occurred: " + ex.getMessage());
                // This captures any other unexpected exceptions.
            }
        } else {
            System.out.println("Aucune demande sélectionnée.");
            // Optionally, inform the user that no request has been selected.
        }
    }




    private void updateCompteState(long compteepId) throws SQLException {
        // Écrire la requête SQL pour mettre à jour l'état du compte associé à cet ID à false
        String sql = "UPDATE compteep SET etat = false WHERE id = ?";
        Connection cnx= SQLConnector.getInstance().getConnection();
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setLong(1, compteepId);
            statement.executeUpdate();
        }
    }


    @FXML
    private void refuser() {
        demande_desac_ce selectedDemande = (demande_desac_ce) table.getSelectionModel().getSelectedItem();
        if (selectedDemande != null) {
            try {


                // Supprimer la demande de la base de données
                demandesService.delete(selectedDemande.getId());

                // Supprimer l'élément correspondant de la liste demandesData
                demandesData.remove(selectedDemande);

                // Rafraîchir la TableView pour refléter les changements
                table.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
                // Gérer l'exception selon les besoins
            }
        } else {
            // Afficher un message à l'utilisateur indiquant qu'aucune demande n'a été sélectionnée
            System.out.println("Aucune demande sélectionnée.");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        demandesService = new demandesService();


        initializeColumns(); // Initialise les colonnes de la table
        loadData(); // Charge les données dans la table

    }
    private void initializeColumns() {
        // Initialise les colonnes de la table en associant chaque colonne à une propriété du modèle Compteep
        idCompteColumn.setCellValueFactory(new PropertyValueFactory<>("compteepId"));
        raison.setCellValueFactory(new PropertyValueFactory<>("raison"));


    }

    private void loadData() {
        try {
            // Charge les données à partir du service CompteepService
            List<demande_desac_ce> demandesList = demandesService.read();
            demandesData.addAll(demandesList);
            table.setItems(demandesData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins, par exemple, afficher un message d'erreur
        }
    }
}
