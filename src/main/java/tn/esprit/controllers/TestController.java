package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.model.Carte;
import tn.esprit.model.Cheque;
import tn.esprit.service.CarteService;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
public class TestController {
    @FXML
    public Button CreateButton;
    @FXML
    public Button UpdateButton;
    @FXML
    public Button DeleteButton;

    @FXML
    private TableView<Carte> TableCarte;

    @FXML
    private TableColumn<Carte, Date> DateCol;

    @FXML
    private TableColumn<Carte, Integer> NumCol;

    @FXML
    private TableColumn<Carte, Integer> IdCol;

    @FXML
    private TableColumn<Carte, Integer> CVVcol;

    @FXML
    private TableColumn<Carte, String> NomCol;

    @FXML
    private TextField txtNum, txtNom, txtCVV;
    @FXML
    private DatePicker txtDate; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur
    @FXML
    private TextField searchFieldC;



    private final CarteService carteService = new CarteService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createCarte() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String Num = txtNum.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
        String Nom = txtNom.getText().trim();
        String CVV = txtCVV.getText().trim();

        // Vérifier que les champs ne sont pas vides
        if (Num.isEmpty() || Nom.isEmpty() || CVV.isEmpty()) {
            showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier que montant et duree contiennent uniquement des chiffres
        if (!Num.matches("\\d+") || !CVV.matches("\\d+")) {
            showAlertWithError("Erreur de saisie", "CVV et Numero doivent contenir uniquement des chiffres.");
            return;
        }

        // Convertir les valeurs en types appropriés après la validation
        Integer Numero = Integer.parseInt(Num);
        Integer C = Integer.parseInt(CVV);

        // Vérifier que type contient uniquement des caractères
        if (!Nom.matches("[a-zA-Z]+")) {
            showAlertWithError("Erreur de saisie", "Le Nom doit contenir uniquement des caractères alphabétiques.");
            return;
        }
        if (!CVV.matches("\\d{3}")) {
            showAlertWithError("Erreur de Validation", "Le CVV doit être composée de 3 chiffres exactement.");
            return;
        }
        if (!Num.matches("\\d{8}")) {
            showAlertWithError("Erreur de Validation", "Le Numéro doit être composée de 8 chiffres exactement.");
            return;
        }
        // Récupérer la date de début
        LocalDate datedebValue = txtDate.getValue();
        if (datedebValue == null) {
            showAlertWithError("Erreur de saisie", "Veuillez sélectionner une date.");
            return;
        }
        Date date = Date.valueOf(datedebValue);

        try {
            // Création de l'instance de Credit et ajout via le service
            Carte carte = new Carte(Numero, Nom, date, C);
            carteService.create(carte);
            System.out.println("Nouveau carte ajouté avec succès !");
            // Réinitialiser les champs de l'interface utilisateur
            txtNum.clear();
            txtNom.clear();
            txtCVV.clear();
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
    void deleteCarte (ActionEvent event){
        Carte selectedCarte = TableCarte.getSelectionModel().getSelectedItem();
        if (selectedCarte != null) {
            try {
                carteService.delete(selectedCarte.getId());
                refreshTableView();

            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression du carte: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sélectionnez une carte à supprimer.");
        }
    }
    @FXML
    void updateCarte() {
        // Récupérer les valeurs depuis l'interface utilisateur
        // Assurez-vous d'avoir une manière de récupérer l'identifiant unique du crédit à mettre à jour
        Carte selectedCarte = TableCarte.getSelectionModel().getSelectedItem();
        int carteId = 0; // Déclarer et initialiser creditId
        String  Numero = ""; // Déclarer les variables à l'extérieur du bloc if
        String  Nom = "";
        String  C = "";
        if (selectedCarte != null) {
            carteId = selectedCarte.getId();
            Numero = txtNum.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
            Nom = txtNom.getText().trim();
             C= txtCVV.getText().trim();
        }else {
            System.out.println("Sélectionnez une carte à mettre à jour.");
        }
        if (Numero.isEmpty() || Nom.isEmpty() || C.isEmpty()) {
            showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }

        // Vérifier que montant et duree contiennent uniquement des chiffres
        if (!Numero.matches("\\d+") || !C.matches("\\d+")) {
            showAlertWithError("Erreur de saisie", "CVV et NUMERO doivent contenir uniquement des chiffres.");
            return;
        }

        // Convertir les valeurs en types appropriés après la validation
        Integer Num = Integer.parseInt(Numero);
        Integer CVV = Integer.parseInt(C);

        // Vérifier que type contient uniquement des caractères
        if (!Nom.matches("[a-zA-Z]+")) {
            showAlertWithError("Erreur de saisie", "Le Nom doit contenir uniquement des caractères alphabétiques.");
            return;
        }
        if (!C.matches("\\d{3}")) {
            showAlertWithError("Erreur de Validation", "Le CVV doit être composée de 3 chiffres exactement.");
            return;
        }
        if (!Numero.matches("\\d{8}")) {
            showAlertWithError("Erreur de Validation", "Le Numéro doit être composée de 8 chiffres exactement.");
            return;
        }
        // Récupérer la date de début
        LocalDate datedebValue = txtDate.getValue();
        if (datedebValue == null) {
            showAlertWithError("Erreur de saisie", "Veuillez sélectionner une date.");
            return;
        }
        Date date = Date.valueOf(datedebValue);

        try {
            // Récupérer le crédit à mettre à jour depuis la base de données
            Carte existingCarte = carteService.findById(carteId);
            if (existingCarte != null) {
                // Mettre à jour les champs modifiables
                existingCarte.setNom(Nom);
                existingCarte.setNum(Integer.parseInt(Numero));
                existingCarte.setCvv(Integer.parseInt(C));

                // Mettre à jour la date de début si elle a été modifiée
                LocalDate datedebValue1 = txtDate.getValue();
                if (datedebValue1 != null) {
                    existingCarte.setDateexp(Date.valueOf(datedebValue));
                }

                // Mettre à jour le crédit dans la base de données
                carteService.update(existingCarte);
                System.out.println("Carte mis à jour avec succès !");
                refreshTableView();
            } else {
                showAlertWithError("Erreur", "Le carte à mettre à jour n'existe pas.");
            }
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur lors de la mise à jour du carte : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Numéro et CVV doivent être des nombres.");
        } catch (Exception e) {
            showAlertWithError("Erreur Inattendue", "Erreur inattendue : " + e.getMessage());
        }
    }
    private void populateFields (Carte carte){
        txtNom.setText(carte.getNom());
        txtNum.setText(String.valueOf(carte.getNum()));
        txtCVV.setText(String.valueOf(carte.getCvv()));

    }
    @FXML
    void initialize () {
        this.TableCarte.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.populateFields(newSelection);
            } else {
                clearFields();
            }
        });
        configureTableView();
        refreshTableView();
        searchFieldC.textProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le TableView avec les résultats de recherche
            filterCarteByNumero(newValue);
        });
    }
    @FXML
    void filterCarteByNumero(ActionEvent event) {
        String numero = searchFieldC.getText().trim();
        filterCarteByNumero(numero);
    }

    private void filterCarteByNumero(String numero) {
        try {
            // Créez une instance de CarteService
            CarteService carteService = new CarteService();

            // Lire tous les cartes
            List<Carte> cartes = carteService.read();

            // Filtrer les cartes en fonction du numéro de carte
            ObservableList<Carte> filteredCartes = FXCollections.observableArrayList();
            for (Carte carte : cartes) {
                // Convertir le numéro de carte en String avant de l'utiliser
                String numeroCarteStr = String.valueOf(carte.getNum());
                if (numeroCarteStr.contains(numero)) {
                    filteredCartes.add(carte);
                }
            }

            // Mettre à jour le TableView avec les résultats de recherche
            TableCarte.setItems(filteredCartes);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de la recherche des cartes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureTableView () {
        // Assurez-vous que le type générique de TableColumn correspond à <Virement, TypeDeLaPropriété>
        // Par exemple, pour idCol qui pourrait être un Integer, la déclaration pourrait ressembler à TableColumn<Virement, Integer>

        this.IdCol.setCellValueFactory(new PropertyValueFactory<Carte, Integer>("id"));
        this.NumCol.setCellValueFactory(new PropertyValueFactory<Carte, Integer>("Num"));
        this.NomCol.setCellValueFactory(new PropertyValueFactory<Carte, String>("Nom"));
        this.DateCol.setCellValueFactory(new PropertyValueFactory<Carte, Date>("dateexp"));
        this.CVVcol.setCellValueFactory(new PropertyValueFactory<Carte, Integer>("cvv"));
    }
    private void clearFields () {
        txtNum.clear();
        txtNom.clear();
        txtCVV.clear();


    }
    @FXML
    private void refreshTableView () {
        try {
            List<Carte> cartes = carteService.read();
            ObservableList<Carte> creditObservableList = FXCollections.observableArrayList(cartes);
            TableCarte.setItems(creditObservableList);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de l'actualisation des cartes: " + e.getMessage());
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
