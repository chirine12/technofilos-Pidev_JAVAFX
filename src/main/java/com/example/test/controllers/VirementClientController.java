package com.example.test.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.test.model.Virement;
import com.example.test.service.VirementClientService;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class VirementClientController {

    @FXML
    private Button btncreateV, btnmodifV, btnsuppV;
    @FXML
    private TableView<Virement> tablevirement;

    @FXML
    private TableColumn<Virement, Date> dateCol;

    @FXML
    private TableColumn<Virement, Long> destCol;

    @FXML
    private TableColumn<Virement, Integer> idCol;

    @FXML
    private TableColumn<Virement, Float> montantCol;

    @FXML
    private TableColumn<Virement, String> motifCol;

    @FXML
    private TableColumn<Virement, Long> srcCol;
    @FXML
    private TextField tdestinataire, tmontant, tmotif, tsource;
    @FXML
    private DatePicker tdate; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur


    private final VirementClientService virementService = new VirementClientService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @FXML

    void createVirement() {
        String motif = tmotif.getText().trim();
        String montantStr = tmontant.getText().replace(",", ".");
        String destinataireStr = tdestinataire.getText();

        if (motif.isEmpty() || montantStr.isEmpty() || destinataireStr.isEmpty() || tsource.getText().isEmpty()) {
            showAlertWithError("Erreur de Validation", "Tous les champs doivent être remplis.");
            return;
        }

        try {
            float montant = Float.parseFloat(montantStr);
            long source = Long.parseLong(tsource.getText());
            long destinataire = Long.parseLong(destinataireStr);
            LocalDate date = LocalDate.now(); // Capture today's date

            int compteCourantId = virementService.getCompteCourantIdByClientId(1); // Assuming client ID is managed elsewhere

            Virement virement = new Virement(source, destinataire, montant, motif, date);
            virementService.create(virement, 1); // Assuming client ID is static here as 1

            clearFields();
            refreshTableView();
            showAlertWithError("Succès", "Virement ajouté avec succès.");
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de Montant", "Le montant doit être un nombre valide.");
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur lors de la création du virement : " + e.getMessage());
        } catch (Exception e) {
            showAlertWithError("Erreur Inattendue", "Erreur inattendue : " + e.getMessage());
        }
    }

    @FXML
    void deleteVirement(ActionEvent event) {
        Virement selectedVirement = tablevirement.getSelectionModel().getSelectedItem();
        if (selectedVirement != null) {
            try {
                virementService.delete(selectedVirement.getId());
                refreshTableView();

            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression du virement: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sélectionnez un virement à supprimer.");
        }
    }

    @FXML
    void updateVirement(ActionEvent event) throws SQLException {
        Virement selectedVirement = tablevirement.getSelectionModel().getSelectedItem();
        if (selectedVirement != null) {

            // Essayez de parser les valeurs numériques et attrapez NumberFormatException si nécessaire
            selectedVirement.setSource(Long.parseLong(tsource.getText()));
            selectedVirement.setDestinataire(Long.parseLong(tdestinataire.getText()));
            // Remplacez les virgules par des points pour gérer les formats numériques avec virgules
            String montantStr = tmontant.getText().replace(",", ".");
            selectedVirement.setMontant(Float.parseFloat(montantStr));

            selectedVirement.setMotif(tmotif.getText());
            // Convertissez directement la valeur de DatePicker en LocalDate

            // Appel à virementService pour mettre à jour le virement dans la base de données
            virementService.update(selectedVirement);

            refreshTableView(); // Rafraîchir l'affichage
            clearFields(); // Effacer les champs du formulaire
            System.out.println("Virement mis à jour avec succès.");

        } else {
            System.out.println("Sélectionnez un virement à mettre à jour.");
        }
    }


    private void populateFields(Virement virement) {
        tsource.setText(String.valueOf(virement.getSource()));
        tdestinataire.setText(String.valueOf(virement.getDestinataire()));
        tmontant.setText(String.format("%.2f", virement.getMontant()));
        tmotif.setText(virement.getMotif());

    }

    @FXML
    void initialize() {
        this.tablevirement.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.populateFields(newSelection);
            } else {
                clearFields();
            }
        });
        configureTableView();
        refreshTableView();
        loadRibForSource();
    }

    private void loadRibForSource() {
        try {
            String rib = virementService.getRibByClientId(1);  // Suppose que l'ID client est 1
            if (rib != null) {
                tsource.setText(rib);  // Assurez-vous que tsource est bien le TextField où vous souhaitez afficher le RIB
            } else {
                tsource.setText("RIB non disponible");
            }
        } catch (Exception e) {
            tsource.setText("Erreur lors de la récupération du RIB");
            e.printStackTrace();
        }
    }

    private void configureTableView() {
        // Assurez-vous que le type générique de TableColumn correspond à <Virement, TypeDeLaPropriété>
        // Par exemple, pour idCol qui pourrait être un Integer, la déclaration pourrait ressembler à TableColumn<Virement, Integer>

        this.idCol.setCellValueFactory(new PropertyValueFactory<Virement, Integer>("id"));
        this.srcCol.setCellValueFactory(new PropertyValueFactory<Virement, Long>("source"));
        this.destCol.setCellValueFactory(new PropertyValueFactory<Virement, Long>("destinataire"));
        this.montantCol.setCellValueFactory(new PropertyValueFactory<Virement, Float>("montant"));
        this.motifCol.setCellValueFactory(new PropertyValueFactory<Virement, String>("motif"));
        this.dateCol.setCellValueFactory(new PropertyValueFactory<Virement, Date>("date"));
    }


    private void refreshTableView() {
        try {

            List<Virement> virements = virementService.read(1);
            ObservableList<Virement> virementObservableList = FXCollections.observableArrayList(virements);
            tablevirement.setItems(virementObservableList);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de l'actualisation des virements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        tsource.clear();
        tdestinataire.clear();
        tmontant.clear();
        tmotif.clear();


    }

    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void handleDownloadReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("VirementReport.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files", "*.pdf"));

        // Obtenez la référence de votre scène principale ou stage ici
        // Par exemple, si cette méthode est liée à un bouton, vous pouvez obtenir le stage comme suit:
        // Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Note: 'event' doit être passé en paramètre à la méthode handleDownloadReport si utilisé.
        // Pour simplifier sans contexte d'événement, nous utilisons null ou une autre référence appropriée.
        File file = fileChooser.showSaveDialog(null); // Remplacez null par la référence de Stage si disponible

        if (file != null) {
            // Utiliser l'ID client fixe 1 pour générer le rapport
            int clientId = 1; // Utilisation de l'ID client fixe
            virementService.generateVirementReport(clientId, file.getAbsolutePath());
        }
    }

}


