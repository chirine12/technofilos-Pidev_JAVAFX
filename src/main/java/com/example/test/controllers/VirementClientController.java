package com.example.test.controllers;

import com.itextpdf.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.test.model.Virement;
import com.example.test.service.VirementClientService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.time.LocalDate;

import java.util.Date;
import java.util.List;

import javafx.scene.control.ComboBox;


public class VirementClientController {


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
    private ComboBox<Long> combobenef;

    @FXML
    private TextField  tmontant, tmotif, tsource;
    @FXML
    private DatePicker tdate; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur


    private final VirementClientService virementService = new VirementClientService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @FXML
    void createVirement() {
        String motif = tmotif.getText().trim();
        if (motif.length() > 20) {
            showAlertWithError("Erreur de Validation", "Le motif ne peut pas dépasser 20 caractères.");
            return;
        }
        String montantStr = tmontant.getText().replace(",", ".");
        // Utilisez comboBox pour obtenir le destinataire sélectionné
        Long destinataire = combobenef.getValue();

        // Vérifiez que tous les champs nécessaires sont remplis
        if (motif.isEmpty() || montantStr.isEmpty() || destinataire == null || tsource.getText().isEmpty()) {
            showAlertWithError("Erreur de Validation", "Tous les champs doivent être remplis.");
            return;
        }

        // Validation du montant pour qu'il ne soit pas négatif et qu'il ne dépasse pas 6 chiffres
        float montant;
        try {
            montant = Float.parseFloat(montantStr);
            if (montant < 0 || montant >= 1000000) {
                showAlertWithError("Erreur de Montant", "Le montant doit être positif et inférieur à 1 000 000.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de Montant", "Le montant doit être un nombre valide.");
            return;
        }

        // Validation de la source pour qu'elle contienne exactement 11 chiffres
        String sourceStr = tsource.getText();
        if (sourceStr.length() != 11 || !sourceStr.matches("\\d+")) {
            showAlertWithError("Erreur de Source", "Le numéro de la source doit contenir exactement 11 chiffres.");
            return;
        }
        long source = Long.parseLong(sourceStr);

        try {
            LocalDate date = LocalDate.now(); // Capture today's date
            int compteCourantId = virementService.getCompteCourantIdByClientId(1); // Assuming client ID is managed elsewhere

            Virement virement = new Virement(source, destinataire, montant, motif, date);
            virementService.create(virement, 1); // Assuming client ID is static here as 1

            clearFields();
            refreshTableView();
            showAlertWithSuccess("Succès", "Virement ajouté avec succès.");
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
    void updateVirement(ActionEvent event) {
        Virement selectedVirement = tablevirement.getSelectionModel().getSelectedItem();
        if (selectedVirement != null) {
            try {
                String motif = tmotif.getText().trim();
                if (motif.length() > 20) {
                    showAlertWithError("Erreur de Validation", "Le motif ne peut pas dépasser 20 caractères.");
                    return;
                }

                String montantStr = tmontant.getText().replace(",", ".");
                if (motif.isEmpty() || montantStr.isEmpty() || combobenef.getValue() == null || tsource.getText().isEmpty()) {
                    showAlertWithError("Erreur de Validation", "Tous les champs doivent être remplis.");
                    return;
                }

                Long destinataire = combobenef.getValue();
                float montant = Float.parseFloat(montantStr);
                if (montant < 0 || montant >= 1000000) {
                    showAlertWithError("Erreur de Montant", "Le montant doit être positif et inférieur à 1 000 000.");
                    return;
                }

                String sourceStr = tsource.getText();
                if (sourceStr.length() != 11 || !sourceStr.matches("\\d+")) {
                    showAlertWithError("Erreur de Source", "Le numéro de la source doit contenir exactement 11 chiffres.");
                    return;
                }
                long source = Long.parseLong(sourceStr);

                // Set the updated values in the selected Virement
                selectedVirement.setSource(source);
                selectedVirement.setDestinataire(destinataire);
                selectedVirement.setMontant(montant);
                selectedVirement.setMotif(motif);

                // Call the virementService to update the Virement in the database
                virementService.update(selectedVirement);

                refreshTableView(); // Refresh the display
                clearFields(); // Clear the form fields
                System.out.println("Virement updated successfully.");

            } catch (NumberFormatException e) {
                showAlertWithError("Numeric Error", "Please enter valid numbers.");
            } catch (SQLException e) {
                showAlertWithError("SQL Error", "Error updating virement: " + e.getMessage());
            } catch (Exception e) {
                showAlertWithError("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
            }
        } else {
            System.out.println("Select a virement to update.");
        }
    }


    private void populateFields(Virement virement) {
        tsource.setText(String.valueOf(virement.getSource()));

        // Find and select the destinataire in the ComboBox
        combobenef.getSelectionModel().select(virement.getDestinataire());

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
        loadDestinataireRIBs();
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

        //this.idCol.setCellValueFactory(new PropertyValueFactory<Virement, Integer>("id"));
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
        combobenef.getSelectionModel().clearSelection();  // This will clear the selection in the ComboBox
        tmontant.clear();
        tmotif.clear();
    }
    private void showAlertWithSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);  // Use INFORMATION alert type for success messages
        alert.setTitle(title);
        alert.setHeaderText(null);  // Optional: remove the header
        alert.setContentText(content);
        alert.showAndWait();
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
    @FXML
    private void pagebeneficiaire(ActionEvent event) {
        try {
            // Charge la nouvelle vue FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/test/beneficiaire.fxml"));
            Parent root = loader.load();

            // Obtient la scène actuelle et définit le nouveau contenu de la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }  catch (IOException e) {
        System.err.println("Erreur lors du chargement du FXML : " + e.getMessage());
        e.printStackTrace();
    } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void loadDestinataireRIBs() {
        try {
            List<Long> ribList = virementService.getRibs();
            ObservableList<Long> observableRibList = FXCollections.observableArrayList(ribList);
            combobenef.getItems().setAll(observableRibList);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des RIBs : " + e.getMessage());
            e.printStackTrace();
        }
    }


}