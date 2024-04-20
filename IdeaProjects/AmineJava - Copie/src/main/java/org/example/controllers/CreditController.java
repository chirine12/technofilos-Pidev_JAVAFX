package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.Credit;
import org.example.services.CreditService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
public class CreditController {
    @FXML
    public Button refreshButton;
    @FXML
    public Button CreateButton;
    @FXML
    public Button UpdateButton;
    @FXML
    public Button DeleteButton;

    @FXML
    private TableView<Credit> TableCredit;

    @FXML
    private TableColumn<Credit, Date> DatedebCol;

    @FXML
    private TableColumn<Credit, Integer> MontantCol;

    @FXML
    private TableColumn<Credit, Integer> idCol;

    @FXML
    private TableColumn<Credit, Integer> DureeCol;

    @FXML
    private TableColumn<Credit, Float> PayementCol;

    @FXML
    private TableColumn<Credit, String> TypeCol;

    @FXML
    private TableColumn<Credit, Date> DatefinCol;
    @FXML
    private TextField typeid, montantid, payementid, dureeid;
    @FXML
    private DatePicker datedebid,datefinid; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur


    private final CreditService creditService = new CreditService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createCredit() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String type = typeid.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
        String montantText = montantid.getText().trim();
        String payementText = payementid.getText().trim();
        String dureeText = dureeid.getText().trim();

        // Vérifier que les champs ne sont pas vides
        if (type.isEmpty() || montantText.isEmpty() || payementText.isEmpty() || dureeText.isEmpty()) {
            showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier que montant et duree contiennent uniquement des chiffres
        if (!montantText.matches("\\d+") || !dureeText.matches("\\d+")) {
            showAlertWithError("Erreur de saisie", "Montant et durée doivent contenir uniquement des chiffres.");
            return;
        }

        // Convertir les valeurs en types appropriés après la validation
        Integer montant = Integer.parseInt(montantText);
        Integer duree = Integer.parseInt(dureeText);

        // Vérifier que type contient uniquement des caractères
        if (!type.matches("[a-zA-Z]+")) {
            showAlertWithError("Erreur de saisie", "Le type doit contenir uniquement des caractères alphabétiques.");
            return;
        }

        // Vérifier que payement contient un nombre réel
        Float payement;
        try {
            payement = Float.parseFloat(payementText);
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Le paiement doit être un nombre réel.");
            return;
        }

        // Récupérer la date de début
        LocalDate datedebValue = datedebid.getValue();
        if (datedebValue == null) {
            showAlertWithError("Erreur de saisie", "Veuillez sélectionner une date de début.");
            return;
        }
        Date datedeb = Date.valueOf(datedebValue);

        // Calculer la date de fin en fonction de la durée
        Calendar c = Calendar.getInstance();
        c.setTime(datedeb);
        c.add(Calendar.YEAR, duree);
        Date datefin = new Date(c.getTimeInMillis());

        try {
            // Création de l'instance de Credit et ajout via le service
            Credit credit = new Credit(type, montant, payement, duree, datedeb, datefin);
            creditService.create(credit);
            System.out.println("Nouveau crédit ajouté avec succès !");
            // Réinitialiser les champs de l'interface utilisateur
            typeid.clear();
            montantid.clear();
            payementid.clear();
            dureeid.clear();
            datedebid.setValue(null); // Réinitialiser la date de début
            refreshTableView();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Vous avez une erreur dans la saisie de vos données!");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Vous avez une erreur dans la saisie de vos données!");
            alert.show();
        }
    }
    @FXML
    void deleteCredit (ActionEvent event){
        Credit selectedCredit = TableCredit.getSelectionModel().getSelectedItem();
        if (selectedCredit != null) {
            try {
                creditService.delete(selectedCredit.getId());
                refreshTableView();

            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression du credit: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sélectionnez un credit à supprimer.");
        }
    }
    @FXML
    void updateCredit() {
        // Récupérer les valeurs depuis l'interface utilisateur
        // Assurez-vous d'avoir une manière de récupérer l'identifiant unique du crédit à mettre à jour
        Credit selectedCredit = TableCredit.getSelectionModel().getSelectedItem();
        int creditId = 0; // Déclarer et initialiser creditId
        String type = ""; // Déclarer les variables à l'extérieur du bloc if
        String montantText = "";
        String payementText = "";
        String dureeText = "";
        if (selectedCredit != null) {
             creditId = selectedCredit.getId();
             type = typeid.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
             montantText = montantid.getText().trim();
             payementText = payementid.getText().trim();
             dureeText = dureeid.getText().trim();
        }else {
            System.out.println("Sélectionnez un crédit à mettre à jour.");
        }
        // Effectuer les mêmes validations que dans la méthode createCredit()

        try {
            // Récupérer le crédit à mettre à jour depuis la base de données
            Credit existingCredit = creditService.findById(creditId);
            if (existingCredit != null) {
                // Mettre à jour les champs modifiables
                existingCredit.setType(type);
                existingCredit.setMontant(Integer.parseInt(montantText));
                existingCredit.setPayement(Float.parseFloat(payementText));
                existingCredit.setDuree(Integer.parseInt(dureeText));

                // Mettre à jour la date de début si elle a été modifiée
                LocalDate datedebValue = datedebid.getValue();
                if (datedebValue != null) {
                    existingCredit.setDatedeb(Date.valueOf(datedebValue));
                }

                // Calculer la nouvelle date de fin si nécessaire
                if (existingCredit.getDatedeb() != null && existingCredit.getDuree() != 0) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(existingCredit.getDatedeb());
                    c.add(Calendar.YEAR, existingCredit.getDuree());
                    existingCredit.setDatefin(new Date(c.getTimeInMillis()));
                }

                // Mettre à jour le crédit dans la base de données
                creditService.update(existingCredit);
                System.out.println("Crédit mis à jour avec succès !");
                refreshTableView();
            } else {
                showAlertWithError("Erreur", "Le crédit à mettre à jour n'existe pas.");
            }
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur lors de la mise à jour du crédit : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Montant, payement et duree doivent être des nombres.");
        } catch (Exception e) {
            showAlertWithError("Erreur Inattendue", "Erreur inattendue : " + e.getMessage());
        }
    }
    private void populateFields (Credit credit){
        typeid.setText(credit.getType());
        montantid.setText(String.valueOf(credit.getMontant()));
        payementid.setText(String.format("%.2f",credit.getPayement()));
        dureeid.setText(String.valueOf(credit.getDuree()));

    }
    @FXML
    void initialize () {
        this.TableCredit.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
        

        this.idCol.setCellValueFactory(new PropertyValueFactory<Credit, Integer>("id"));
        this.TypeCol.setCellValueFactory(new PropertyValueFactory<Credit, String>("type"));
        this.MontantCol.setCellValueFactory(new PropertyValueFactory<Credit, Integer>("montant"));
        this.PayementCol.setCellValueFactory(new PropertyValueFactory<Credit, Float>("payement"));
        this.DureeCol.setCellValueFactory(new PropertyValueFactory<Credit, Integer>("duree"));
        this.DatedebCol.setCellValueFactory(new PropertyValueFactory<Credit, Date>("datedeb"));
        this.DatefinCol.setCellValueFactory(new PropertyValueFactory<Credit, Date>("datefin"));
    }
    private void clearFields () {
        typeid.clear();
        montantid.clear();
        payementid.clear();
        dureeid.clear();


    }
    @FXML
    private void refreshTableView () {
        try {
            List<Credit> credits = creditService.read();
            ObservableList<Credit> creditObservableList = FXCollections.observableArrayList(credits);
            TableCredit.setItems(creditObservableList);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de l'actualisation des virements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlertWithError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
