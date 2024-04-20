package com.example.test.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.test.model.Virement;
import com.example.test.service.VirementService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class VirementController {

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


    private final VirementService virementService = new VirementService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createVirement() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String motif = tmotif.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
        String montantStr = tmontant.getText().replace(",", "."); // Pour le montant, supposant que vous avez un TextField
        String sourceStr = tsource.getText(); // Pour la source, supposant que vous avez un TextField
        String destinataireStr = tdestinataire.getText(); // Pour le destinataire

        // Validation de la saisie utilisateur
        if (motif.isEmpty()) {
            showAlertWithError("Erreur de Validation", "Le motif ne peut pas être vide.");
            return;
        }

        float montant;
        try {
            montant = Float.parseFloat(montantStr);
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de Validation", "Le format du montant est invalide.");
            return;
        }
        if (montant <= 0) {
            showAlertWithError("Erreur de Validation", "Le montant doit être positif.");
            return;
        }

        if (!sourceStr.matches("\\d{11}")) {
            showAlertWithError("Erreur de Validation", "La source doit être composée de 11 chiffres exactement.");
            return;
        }
        long source = Long.parseLong(sourceStr);

        if (!destinataireStr.matches("\\d{11}")) {
            showAlertWithError("Erreur de Validation", "Le destinataire doit être composé de 11 chiffres exactement.");
            return;
        }
        long destinataire = Long.parseLong(destinataireStr);

        // Utiliser la date actuelle pour la création
        LocalDate date = LocalDate.now();

        try {
            // Création de l'instance de Virement et ajout via le service
            Virement virement = new Virement(source, destinataire, montant, motif, date);
            virementService.create(virement);
            System.out.println("Nouveau virement ajouté avec succès !");
            // Réinitialiser les champs de l'interface utilisateur
            tmotif.setText("");
            tmontant.setText("");
            tsource.setText("");
            tdestinataire.setText("");
            refreshTableView();
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur SQL lors de la création du virement : " + e.getMessage());
        } catch (Exception e) {
            showAlertWithError("Erreur Inattendue", "Erreur inattendue : " + e.getMessage());
        }
    }



        /*try {
            Virement newVirement = new Virement();
            newVirement.setSource(Long.parseLong(tsource.getText()));
            newVirement.setDestinataire(Long.parseLong(tdestinataire.getText()));
            newVirement.setMontant(Float.parseFloat(tmontant.getText()));
            newVirement.setMotif(tmotif.getText());
            // Conversion de la date du DatePicker en java.util.Date puis en java.sql.Date
            if (tdate.getValue() != null) {
                Date date = dateFormat.parse(tdate.getValue().toString());
                newVirement.setDate(new java.sql.Date(date.getTime()));
            }
            virementService.create(newVirement);
            refreshTableView();
            clearFields();
            errorMessage.setText("Virement créé avec succès.");
        } catch (Exception e) {
            errorMessage.setText("Erreur lors de la création du virement: " + e.getMessage());
            e.printStackTrace();
        }*/


            @FXML
            void deleteVirement (ActionEvent event){
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
            void updateVirement (ActionEvent event) throws SQLException {
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


            private void populateFields (Virement virement){
                tsource.setText(String.valueOf(virement.getSource()));
                tdestinataire.setText(String.valueOf(virement.getDestinataire()));
                tmontant.setText(String.format("%.2f", virement.getMontant()));
                tmotif.setText(virement.getMotif());

            }

            @FXML
            void initialize () {
                this.tablevirement.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        this.populateFields(newSelection);
                    } else {
                        clearFields();
                    }
                });
                configureTableView();
                refreshTableView();
            }

            private void configureTableView () {
                // Assurez-vous que le type générique de TableColumn correspond à <Virement, TypeDeLaPropriété>
                // Par exemple, pour idCol qui pourrait être un Integer, la déclaration pourrait ressembler à TableColumn<Virement, Integer>

                this.idCol.setCellValueFactory(new PropertyValueFactory<Virement, Integer>("id"));
                this.srcCol.setCellValueFactory(new PropertyValueFactory<Virement, Long>("source"));
                this.destCol.setCellValueFactory(new PropertyValueFactory<Virement, Long>("destinataire"));
                this.montantCol.setCellValueFactory(new PropertyValueFactory<Virement, Float>("montant"));
                this.motifCol.setCellValueFactory(new PropertyValueFactory<Virement, String>("motif"));
                this.dateCol.setCellValueFactory(new PropertyValueFactory<Virement, Date>("date"));
            }


            private void refreshTableView () {
                try {
                    List<Virement> virements = virementService.read();
                    ObservableList<Virement> virementObservableList = FXCollections.observableArrayList(virements);
                    tablevirement.setItems(virementObservableList);
                } catch (SQLException e) {
                    errorMessage.setText("Erreur lors de l'actualisation des virements: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            private void clearFields () {
                tsource.clear();
                tdestinataire.clear();
                tmontant.clear();
                tmotif.clear();


            }
            private void showAlertWithError (String title, String content){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setContentText(content);
                alert.showAndWait();
            }
        }


