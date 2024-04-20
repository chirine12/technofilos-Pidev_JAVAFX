package com.example.test.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.test.model.Beneficiaire;
import com.example.test.service.BeneficiaireService;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

public class BeneficiaireController {

    @FXML
    private Button btncreateV, btnmodifV, btnsuppV;
    @FXML
    private TableView<Beneficiaire> tableBeneficiaire;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<Beneficiaire, String> nomCol, prenomCol;
    @FXML
    private TableColumn<Beneficiaire, Long> ribCol;

    @FXML
    private TextField tNom, tPrenom, tRib;

    @FXML
    private Label errorMessage;

    private BeneficiaireService beneficiaireService = new BeneficiaireService();

    @FXML
    void createBeneficiaire(ActionEvent event) {
        String nom = tNom.getText().trim();
        String prenom = tPrenom.getText().trim();
        String ribStr = tRib.getText().trim();

        // Vérification que le nom et le prénom ne sont pas vides
        if (nom.isEmpty() || prenom.isEmpty()) {
            showAlert("Erreur de Validation", "Le nom et le prénom sont obligatoires.", Alert.AlertType.ERROR);
            return;
        }

        // Vérification que le RIB est composé de 11 chiffres exactement
        if (!ribStr.matches("\\d{11}")) {
            showAlert("Erreur de Validation", "Le RIB doit être composé de 11 chiffres.", Alert.AlertType.ERROR);
            return;
        }

        long rib;
        try {
            rib = Long.parseLong(ribStr);
        } catch (NumberFormatException e) {
            showAlert("Erreur de Validation", "Le format du RIB est invalide.", Alert.AlertType.ERROR);
            return;
        }

        try {
            Beneficiaire beneficiaire = new Beneficiaire(nom, prenom, rib);
            beneficiaireService.create(beneficiaire,1);
            clearFields();
            refreshTableView();
            showAlert("Succès", "Bénéficiaire ajouté avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                showAlert("Erreur de Duplication", "Ce RIB est déjà attribué à un autre bénéficiaire.", Alert.AlertType.ERROR);
            } else {
                showAlert("Erreur SQL", "Une erreur est survenue lors de l'ajout du bénéficiaire : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }





    @FXML
    void deleteBeneficiaire(ActionEvent event) {
        Beneficiaire selected = tableBeneficiaire.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                beneficiaireService.delete(selected.getId());
                refreshTableView();
                showAlert("Succès", "Bénéficiaire supprimé avec succès.", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Une erreur est survenue lors de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Erreur", "Veuillez sélectionner un bénéficiaire à supprimer.", Alert.AlertType.WARNING);
        }
        clearFields();
    }

    @FXML
    void updateBeneficiaire(ActionEvent event) {
        Beneficiaire selected = tableBeneficiaire.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Erreur", "Veuillez sélectionner un bénéficiaire à mettre à jour.", Alert.AlertType.WARNING);
            return;
        }

        String nom = tNom.getText().trim();
        String prenom = tPrenom.getText().trim();
        String ribStr = tRib.getText().trim();

        if (nom.isEmpty() || prenom.isEmpty() || ribStr.isEmpty()) {
            showAlert("Erreur", "Les champs ne peuvent pas être vides.", Alert.AlertType.ERROR);
            return;
        }

        long rib;
        try {
            rib = Long.parseLong(ribStr);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le RIB doit être un nombre.", Alert.AlertType.ERROR);
            return;
        }

        selected.setNom(nom);
        selected.setPrenom(prenom);
        selected.setRib(rib);

        try {
            beneficiaireService.update(selected);
            refreshTableView();
            clearFields();
            showAlert("Succès", "Bénéficiaire mis à jour avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Une erreur est survenue lors de la mise à jour : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void initialize() {
        configureTableView();
        refreshTableView();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                filterTable(newValue);
            } catch (SQLException e) {
                showAlert("Erreur SQL", "Une erreur est survenue lors de la recherche : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
    private ObservableList<Beneficiaire> searchBeneficiaireByName(String name) throws SQLException {
        List<Beneficiaire> allBeneficiaires = beneficiaireService.read(1);  // Supposant que cela récupère tous les bénéficiaires
        List<Beneficiaire> filtered = allBeneficiaires.stream()
                .filter(b -> b.getNom().toLowerCase().contains(name))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(filtered);
    }

    private void filterTable(String searchText) throws SQLException {
        searchText = searchText.trim().toLowerCase();
        if (searchText.isEmpty()) {
            refreshTableView();
        } else {
            ObservableList<Beneficiaire> filteredList = searchBeneficiaireByName(searchText);
            tableBeneficiaire.setItems(filteredList);
        }
    }

    private void configureTableView() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        ribCol.setCellValueFactory(new PropertyValueFactory<>("rib"));

        tableBeneficiaire.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tNom.setText(newSelection.getNom());
                tPrenom.setText(newSelection.getPrenom());
                tRib.setText(Long.toString(newSelection.getRib()));
            }
        });
    }

    private void refreshTableView() {
        try {
            List<Beneficiaire> beneficiaires = beneficiaireService.read(1);
            ObservableList<Beneficiaire> beneficiaireObservableList = FXCollections.observableArrayList(beneficiaires);
            tableBeneficiaire.setItems(beneficiaireObservableList);
        } catch (SQLException e) {
            showAlert("Erreur SQL", "Une erreur est survenue lors de la récupération des données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void clearFields() {
        tNom.clear();
        tPrenom.clear();
        tRib.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
