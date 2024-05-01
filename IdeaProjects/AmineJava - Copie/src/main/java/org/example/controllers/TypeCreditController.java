package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.models.Credit;
import org.example.models.TypeCredit;
import org.example.services.TypeCreditservice;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
public class TypeCreditController {
    @FXML
    public Button CreateButton;
    @FXML
    public Button UpdateButton;
    @FXML
    public Button DeleteButton,sortByNameButton,refreshButton;
    @FXML
    private TableView<TypeCredit> TableType;
    @FXML
    private TableColumn<TypeCredit, Integer> IdCol;

    @FXML
    private TableColumn<TypeCredit, String> NomCol;

    @FXML
    private TableColumn<TypeCredit, Float> TauxCol;
    @FXML
    private TextField Nomid, Tauxid,typeSearchField;
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur
    private final TypeCreditservice typeCreditservice = new TypeCreditservice();
    @FXML
    void createType(){
        //Recuperer
        String nom = Nomid.getText().trim();
        String taux =Tauxid.getText().trim();
        //verifier
        if(nom.isEmpty() || taux.isEmpty()){
            showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
            return;
        }
        if(!nom.matches("[a-zA-Z]+")){
            showAlertWithError("Erreur de saisie", "Le type doit contenir uniquement des caractères alphabétiques.");
            return;
        }
        Float t;
        try {
            t = Float.parseFloat(taux);
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de saisie", "Le paiement doit être un nombre réel.");
            return;
        }
        try{
            TypeCredit typeCredit = new TypeCredit(nom,t);
            typeCreditservice.create(typeCredit);
            System.out.println("Nouveau type ajouté avec succès !");
            Nomid.clear();
            Tauxid.clear();
            refreshTableView();

        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Vous avez une erreur dans la saisie de vos données!");
            alert.show();
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Vous avez une erreur dans la saisie de vos données!");
            alert.show();
        }

    }
    @FXML
    void deleteType(ActionEvent event){
        TypeCredit selectedCredit = TableType.getSelectionModel().getSelectedItem();
        if (selectedCredit != null) {
            try {
                typeCreditservice.delete(selectedCredit.getId());
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
    void updateType() {
        // Récupérer les valeurs depuis l'interface utilisateur
        // Assurez-vous d'avoir une manière de récupérer l'identifiant unique du crédit à mettre à jour
        TypeCredit selectedTypeCredit = TableType.getSelectionModel().getSelectedItem();
        int typecreditId = 0; // Déclarer et initialiser creditId
        String nom = ""; // Déclarer les variables à l'extérieur du bloc if
        String taux = "";
        if (selectedTypeCredit != null) {
            typecreditId = selectedTypeCredit.getId();
            nom = Nomid.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
            taux = Tauxid.getText().trim();

        }else {
            System.out.println("Sélectionnez un type à mettre à jour.");
        }
        // Effectuer les mêmes validations que dans la méthode createCredit()

        try {
            // Récupérer le crédit à mettre à jour depuis la base de données
            TypeCredit existingTypeCredit = typeCreditservice.findById1(typecreditId);
            if (existingTypeCredit != null) {
                // Mettre à jour les champs modifiables
                existingTypeCredit.setNom(nom);
                existingTypeCredit.setTaux(Float.parseFloat(taux));





                // Mettre à jour le crédit dans la base de données
                typeCreditservice.update(existingTypeCredit);
                System.out.println("type mis à jour avec succès !");
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
    private void populateFields (TypeCredit typeCredit){
        Nomid.setText(typeCredit.getNom());
        Tauxid.setText(String.format("%.2f",typeCredit.getTaux()));


    }
    @FXML
    void initialize () {
        this.TableType.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                this.populateFields(newSelection);
            } else {
                clearFields();
            }
        });
        configureTableView();
        refreshTableView();
    }
    @FXML
    void searchByType() {
        String typeToSearch = typeSearchField.getText().trim();
        if (!typeToSearch.isEmpty()) {
            try {
                List<TypeCredit> typeCredits = typeCreditservice.findByType(typeToSearch);
                if (typeCredits.isEmpty()) {
                    showAlertWithError("Résultats non trouvés", "Aucun crédit trouvé pour le type : " + typeToSearch);
                } else {
                    ObservableList<TypeCredit> creditObservableList = FXCollections.observableArrayList(typeCredits);
                    TableType.setItems(creditObservableList);
                }
            } catch (SQLException e) {
                showAlertWithError("Erreur SQL", "Erreur lors de la recherche par type : " + e.getMessage());
            }
        } else {
            showAlertWithError("Erreur de saisie", "Veuillez saisir un type pour rechercher.");
        }
    }
    @FXML
    void sortCreditsByName() throws SQLException {
        List<TypeCredit> typeCredits = typeCreditservice.read();
        typeCredits.sort(Comparator.comparing(TypeCredit::getNom));
        TableType.setItems(FXCollections.observableArrayList(typeCredits));
    }
    private void configureTableView () {


        this.IdCol.setCellValueFactory(new PropertyValueFactory<TypeCredit, Integer>("id"));
        this.NomCol.setCellValueFactory(new PropertyValueFactory<TypeCredit, String>("nom"));
        this.TauxCol.setCellValueFactory(new PropertyValueFactory<TypeCredit, Float>("taux"));

    }
    private void clearFields () {
        Nomid.clear();
        Tauxid.clear();
    }
    @FXML
    private void refreshTableView () {
        try {
            List<TypeCredit> credits = typeCreditservice.read();
            ObservableList<TypeCredit> creditObservableList = FXCollections.observableArrayList(credits);
            TableType.setItems(creditObservableList);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de l'actualisation des types: " + e.getMessage());
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
