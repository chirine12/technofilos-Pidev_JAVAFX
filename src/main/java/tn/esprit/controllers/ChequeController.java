package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.model.Cheque;
import tn.esprit.service.CarteService;
import tn.esprit.service.ChequeService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
public class ChequeController {
    @FXML
    public Button CreateButton;
    @FXML
    public Button UpdateButton;
    @FXML
    public Button DeleteButton;

    @FXML
    private TableView<Cheque> TableCheque;

    @FXML
    private TableColumn<Cheque, Date> DateCol;

    @FXML
    private TableColumn<Cheque, Integer> NumCol;

    @FXML
    private TableColumn<Cheque, Integer> IdCol;

    @FXML
    private TableColumn<Cheque, Integer> NumComCol;

    @FXML
    private TableColumn<Cheque, Float> MonCol;

    @FXML
    private TextField txtNum, txtNumCom, txtMon;
    @FXML
    private DatePicker txtDate; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur


    private final ChequeService chequeService = new ChequeService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createCheque() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String Num = txtNum.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
        String NumCom = txtNumCom.getText().trim();
        String Mon = txtMon.getText().trim();

        // Vérifier que les champs ne sont pas vides
        if (Num.isEmpty() || NumCom.isEmpty() || Mon.isEmpty()) {
            showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier que montant et duree contiennent uniquement des chiffres
        if (!Num.matches("\\d+")  || !NumCom.matches("\\d+")) {
            showAlertWithError("Erreur de saisie", "Le Numéro de cheque , le montant et le RIB doivent contenir uniquement des chiffres.");
            return;
        }

        // Convertir les valeurs en types appropriés après la validation
        Integer Numero = Integer.parseInt(Num);
        Integer NumCompte = Integer.parseInt(NumCom);
        Float Montant;
        try {
            Montant = Float.parseFloat(String.valueOf(txtMon));
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Le montant doit être un nombre réel.");
            return;
        }




        // Récupérer la date de début
        LocalDate datedebValue = txtDate.getValue();
        if (datedebValue == null) {
            showAlertWithError("Erreur de saisie", "Veuillez sélectionner une date de début.");
            return;
        }
        Date date = Date.valueOf(datedebValue);

        try {
            // Création de l'instance de Credit et ajout via le service
            Cheque cheque = new Cheque(Numero, NumCompte, Montant,date);
            chequeService.create(cheque);
            System.out.println("Nouveau cheque ajouté avec succès !");
            // Réinitialiser les champs de l'interface utilisateur
            txtNum.clear();
            txtNumCom.clear();
            txtMon.clear();
            txtDate.setValue(null); // Réinitialiser la date de début
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
    void DeleteCheque (ActionEvent event){
        Cheque selectedCheque = TableCheque.getSelectionModel().getSelectedItem();
        if (selectedCheque != null) {
            try {
                chequeService.delete(selectedCheque.getId());
                refreshTableView();

            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression du cheque: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sélectionnez un cheque à supprimer.");
        }
    }
    @FXML
    void UpdateCheque() {
        // Récupérer les valeurs depuis l'interface utilisateur
        // Assurez-vous d'avoir une manière de récupérer l'identifiant unique du crédit à mettre à jour
        Cheque selectedCheque = TableCheque.getSelectionModel().getSelectedItem();
        int chequeId = 0; // Déclarer et initialiser creditId
        String  Numero = ""; // Déclarer les variables à l'extérieur du bloc if
        String  NumCompte = "";
        String  Montant = "";
        if (selectedCheque != null) {
            chequeId = selectedCheque.getId();
            Numero = txtNum.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
            NumCompte = txtNumCom.getText().trim();
            Montant= txtMon.getText().trim();
        }else {
            System.out.println("Sélectionnez un cheque à mettre à jour.");
        }
        // Effectuer les mêmes validations que dans la méthode createCredit()

        try {
            // Récupérer le crédit à mettre à jour depuis la base de données
            Cheque existingCheque = chequeService.findById(chequeId);
            if (existingCheque != null) {
                // Mettre à jour les champs modifiables
                existingCheque.setNum(Integer.parseInt(Numero));
                existingCheque.setNumcompte(Integer.parseInt(NumCompte));
                existingCheque.setMontant(Float.parseFloat(Montant));

                // Mettre à jour la date de début si elle a été modifiée
                LocalDate datedebValue = txtDate.getValue();
                if (datedebValue != null) {
                    existingCheque.setDate(Date.valueOf(datedebValue));
                }

                // Mettre à jour le crédit dans la base de données
                chequeService.update(existingCheque);
                System.out.println("Carte mis à jour avec succès !");
                refreshTableView();
            } else {
                showAlertWithError("Erreur", "Le cheque à mettre à jour n'existe pas.");
            }
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur lors de la mise à jour du cheque : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Montant, RIB et Numéro doivent être des nombres.");
        } catch (Exception e) {
            showAlertWithError("Erreur Inattendue", "Erreur inattendue : " + e.getMessage());
        }
    }
    private void populateFields (Cheque cheque){
        txtNum.setText(String.valueOf(cheque.getNum()));
        txtNumCom.setText(String.valueOf(cheque.getNumcompte()));
        txtMon.setText(String.valueOf(cheque.getMontant()));

    }
    @FXML
    void initialize () {
        this.TableCheque.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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

        this.IdCol.setCellValueFactory(new PropertyValueFactory<Cheque, Integer>("id"));
        this.NumCol.setCellValueFactory(new PropertyValueFactory<Cheque, Integer>("Num"));
        this.NumComCol.setCellValueFactory(new PropertyValueFactory<Cheque, Integer>("numcompte"));
        this.MonCol.setCellValueFactory(new PropertyValueFactory<Cheque, Float>("montant"));
        this.DateCol.setCellValueFactory(new PropertyValueFactory<Cheque, Date>("date"));

    }
    private void clearFields () {
        txtNum.clear();
        txtNumCom.clear();
        txtMon.clear();


    }
    @FXML
    private void refreshTableView () {
        try {
            List<Cheque> cheques = chequeService.read();
            ObservableList<Cheque> chequeObservableList = FXCollections.observableArrayList(cheques);
            TableCheque.setItems(chequeObservableList);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de l'actualisation des cheques: " + e.getMessage());
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
