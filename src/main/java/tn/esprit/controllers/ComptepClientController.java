package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.model.Compteep;
import tn.esprit.model.demande_desac_ce;
import tn.esprit.service.ClientCompteepService;
import tn.esprit.service.CompteepService;
import tn.esprit.service.TypetauxService;
import tn.esprit.utils.SQLConnector;

import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ComptepClientController  implements Initializable {



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
        private Button btnCreer;
        @FXML
        private Button btnModifier;
        @FXML
        private Button btnSupprimer;
        @FXML
        private TextField tDescription;
        @FXML
        private ComboBox<String> cmbType;
        @FXML
        private TableView<Compteep> table;
        private String originalDescription;
        private ClientCompteepService ClientCompteepService ;
        private ObservableList<Compteep> compteepsData = FXCollections.observableArrayList();
        TypetauxService typeTauxService = new TypetauxService();



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ClientCompteepService = new ClientCompteepService();

        try {
            Connection cnx = SQLConnector.getInstance().getConnection();
            // Exécution de la requête SQL pour récupérer les types depuis la table typetaux
            String sql = "SELECT DISTINCT type FROM typetaux";
            PreparedStatement statement = cnx.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            List<String> types = new ArrayList<>();
            while (resultSet.next()) {
                types.add(resultSet.getString("type"));
            }

            ObservableList<String> options = FXCollections.observableArrayList(types);
            cmbType.setItems(options);
        } catch (SQLException e) {
            e.printStackTrace();  // Gérer l'exception selon vos besoins
        }

        initializeColumns(); // Initialise les colonnes de la table, incluant la colonne ID non-visible
        loadData(); // Charge les données dans la table
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Afficher les données de l'élément sélectionné dans les champs de saisie
                tDescription.setText(newSelection.getDescription());
                cmbType.setValue(newSelection.getType());
                cmbType.setDisable(true); // Désactiver le champ pour empêcher la modification
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

        // Définir la colonne ID
        TableColumn<Compteep, Number> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));


        // Rendre la colonne ID invisible
        idColumn.setVisible(false);

        // Ajouter la colonne ID à la TableView
        table.getColumns().add(idColumn);
    }


    private void loadData() {
        List<Compteep> compteepList = new ArrayList<>();
        try {
            // Charge les données à partir du service CompteepService
            compteepList = ClientCompteepService.readC(2);
        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher un message d'erreur à l'utilisateur
            showAlert("Erreur", "Impossible de charger les données: " + e.getMessage());
        } finally {
            compteepsData.addAll(compteepList);
            table.setItems(compteepsData);
        }
    }



    private void showAlert(String title, String message) {
        // Créer une nouvelle alerte de type INFORMATION
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);  // Définir le titre de l'alerte
        alert.setHeaderText(null);  // Pas de texte d'en-tête
        alert.setContentText(message);  // Définir le message de l'alerte

        // Afficher l'alerte et attendre que l'utilisateur la ferme
        alert.showAndWait();
    }

    @FXML
        void createCompteep(ActionEvent event) {
            String selectedType = cmbType.getValue();
            String description = tDescription.getText();

            if (!ClientCompteepService .validateDescription(description)) {
                showAlertWithWarning("Invalid Description", "Description must be maximum 10 characters long and must not contain digits.");
                return;
            }

            try {
                int typeId = typeTauxService.findIdByType(selectedType); // Assurez-vous que cette méthode existe et fonctionne correctement
                Long rib = generateRandomRib();
                java.util.Date utilDate = Calendar.getInstance().getTime();
                java.sql.Date dateOuverture = new java.sql.Date(utilDate.getTime());

                // Utilisez un constructeur qui inclut typeTauxId
                Compteep newCompteep = new Compteep(rib, 0.0, selectedType, dateOuverture, description, true, typeId);
                ClientCompteepService .create(newCompteep);
                compteepsData.add(newCompteep);

                cmbType.getSelectionModel().clearSelection();
                tDescription.clear();
            } catch (SQLException e) {
                showAlertWithError("Error creating account", e.getMessage());
                e.printStackTrace();
            }
        }



        private Long generateRandomRib() {

            return (long) ((Math.random() * 90_000_000_000L) + 10_000_000_000L);
            // Utility method for showing error alerts
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
                if (!ClientCompteepService .validateDescription(newDescription)) {
                    showAlertWithWarning("Invalid Description", "Description must be maximum 10 characters long and must not contain digits.");
                    return; // Arrêter la modification du compte si la description est invalide
                }

                // Mettre à jour la description de l'élément sélectionné
                selectedCompteep.setDescription(newDescription);

                try {
                    // Mettre à jour l'élément dans la base de données
                    ClientCompteepService .update(selectedCompteep);
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
    private void onDisableAccountClicked() {
        Compteep selectedAccount = table.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Erreur", "Aucun compte sélectionné.");
            System.out.println("Aucun compte n'a été sélectionné pour la désactivation.");
            return;
        }

        long compteepId = selectedAccount.getId();
        System.out.println("Compte sélectionné pour la désactivation: " + selectedAccount);
        System.out.println("Compte ID utilisé pour la demande: " + compteepId);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Désactivation du Compte");
        dialog.setHeaderText("Désactivation du compte: " + selectedAccount.getRib());
        dialog.setContentText("Veuillez entrer la raison de la désactivation:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Raison de désactivation saisie par l'utilisateur: " + result.get());
        } else {
            System.out.println("Aucune raison de désactivation n'a été fournie par l'utilisateur.");
        }
        result.ifPresent(raison -> createDesactivationRequest(compteepId, raison));
    }




    private void createDesactivationRequest(long compteepId, String raison) {
        System.out.println("Création de la demande de désactivation pour le compte ID: " + compteepId + " avec la raison: " + raison);

        demande_desac_ce demande = new demande_desac_ce();
        demande.setCompteepId(compteepId);
        demande.setClientId(1);  // Assurez-vous que cela est approprié pour votre logique d'application
        demande.setRaison(raison);

        String sql = "INSERT INTO demande_desac_ce (client_id, compteep_id, raison) VALUES (?, ?, ?)";
        try (Connection conn = SQLConnector.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, demande.getClientId());
            pstmt.setLong(2, demande.getCompteepId());
            pstmt.setString(3, demande.getRaison());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert("Succès", "La demande de désactivation a été créée avec succès.");
                System.out.println("Demande de désactivation insérée avec succès pour le compte ID: " + compteepId);
            } else {
                showAlert("Erreur", "La création de la demande a échoué.");
                System.out.println("Échec de l'insertion de la demande de désactivation pour le compte ID: " + compteepId);
            }
        } catch (SQLException ex) {
            showAlert("Erreur de base de données", "Erreur lors de la création de la demande: " + ex.getMessage());
            ex.printStackTrace();
            System.out.println("Exception SQL lors de l'insertion de la demande de désactivation: " + ex.getMessage());
        }
    }





}





