package tn.esprit.controllers;





import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import tn.esprit.model.Credit;
import tn.esprit.model.TypeCredit;
import tn.esprit.service.CreditService;
import tn.esprit.utils.EmailUtil;
import tn.esprit.service.TypeCreditservice;


import javafx.stage.FileChooser;
import java.io.File;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Comparator;



public class CreditController {
    @FXML
    public Button refreshButton;
    @FXML
    public Button CreateButton;
    @FXML
    public Button UpdateButton;
    @FXML
    public Button DeleteButton,sortByNameButton,exportToPdfButton;

    @FXML
    private TableView<Credit> TableCredit;

    @FXML
    private TableColumn<Credit, Date> DatedebCol;

    @FXML
    private TableColumn<Credit, Integer> MontantCol;


    @FXML
    private TableColumn<Credit, Integer> DureeCol;

    @FXML
    private TableColumn<Credit, Float> PayementCol;

    @FXML
    private TableColumn<Credit, String> TypeCol;

    @FXML
    private TableColumn<Credit, Date> DatefinCol;
    @FXML
    private ComboBox<String> typeid;
    @FXML
    private TextField  montantid, payementid, dureeid,typeSearchField;
    @FXML
    private DatePicker datedebid,datefinid; // Utilisation d'un DatePicker pour la sélection de date
    @FXML
    private Label errorMessage; // Un label pour afficher les messages d'erreur
    private Maindashbord mainController;


    private final CreditService creditService = new CreditService();
    private final TypeCreditservice typecreditService = new TypeCreditservice();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createCredit() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String type = typeid.getValue(); // Assurez-vous d'avoir un TextField pour le motif
        String montantText = montantid.getText().trim();
        String payementText = payementid.getText().trim();
        String dureeText = dureeid.getText().trim();
        // Vérifier que les champs ne sont pas vides
        if ( montantText.isEmpty() ||  dureeText.isEmpty()) {
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



        // Vérifier que payement contient un nombre réel


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
            int selectedTypeCredit = typecreditService.findTypeCreditByName(type);
            if (selectedTypeCredit != -1) {
                float taux = typecreditService.findTauxById(selectedTypeCredit);
                float paiement = calculateMonthlyPayment(montant, taux, duree);
                payementid.setText(String.format("%.2f", paiement));
                // Création de l'instance de Credit et ajout via le service
                Credit credit = new Credit(type, montant, paiement, duree, datedeb, datefin, selectedTypeCredit);
                creditService.create(credit);
                System.out.println("Nouveau crédit ajouté avec succès !");
                // Envoi d'un e-mail pour informer l'utilisateur
                String recipient = "medamine.benkhelifa@gmail.com"; // Remplacez par l'adresse e-mail de l'utilisateur
                String subject = "Nouveau crédit créé avec succès";

                String body = "Bonjour,\n\nVotre nouveau crédit a été créé avec succès.\n\n";

                // Ajouter les détails du crédit au corps du message
                body += "- Type : " + type + "\n" +
                        "- Montant : " + montant + "\n" +
                        "- Paiement mensuel : " + paiement + "\n" +
                        "- Durée : " + duree + " ans\n" +
                        "- Date de début : " + datedeb + "\n" +
                        "- Date de fin : " + datefin + "\n\n";

                body += "Cordialement,\nE-bank";

                // Envoyer l'e-mail avec le corps et le logo

                EmailUtil.sendEmail(recipient, subject, body);

                System.out.println("E-mail envoyé à l'utilisateur pour informer la création du crédit !");

                // Réinitialiser les champs de l'interface utilisateur
                typeid.setValue(null);
                montantid.clear();
                dureeid.clear();
                datedebid.setValue(null); // Réinitialiser la date de début
                refreshTableView();
            } else {
                // Afficher un message d'erreur à l'utilisateur
                System.out.println("Le type de crédit sélectionné n'existe pas. Veuillez sélectionner un autre type de crédit.");
                showAlertWithError("Type de crédit non trouvé", "Le type de crédit sélectionné n'existe pas. Veuillez sélectionner un autre type de crédit.");
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Erreur SQL lors de la création du crédit ");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setContentText("Une erreur inattendue s'est produite");
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
            type = typeid.getValue(); // Assurez-vous d'avoir un TextField pour le motif
            montantText = montantid.getText().trim();
            payementText = payementid.getText().trim();
            dureeText = dureeid.getText().trim();
        }else {
            System.out.println("Sélectionnez un crédit à mettre à jour.");
        }
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
        Credit initialSelection = TableCredit.getSelectionModel().getSelectedItem();
        showCreditDetails(initialSelection);

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
    @FXML
    void searchByType() {
        String typeToSearch = typeSearchField.getText().trim();
        if (!typeToSearch.isEmpty()) {
            try {
                List<Credit> credits = creditService.findByType(typeToSearch);
                if (credits.isEmpty()) {
                    showAlertWithError("Résultats non trouvés", "Aucun crédit trouvé pour le type : " + typeToSearch);
                } else {
                    ObservableList<Credit> creditObservableList = FXCollections.observableArrayList(credits);
                    TableCredit.setItems(creditObservableList);
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
        List<Credit> credits = creditService.read();
        credits.sort(Comparator.comparing(Credit::getType));
        TableCredit.setItems(FXCollections.observableArrayList(credits));
    }
    @FXML
    void exportCreditsToPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("credits.pdf");
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
            creditService.generateCreditReport(file.getAbsolutePath());
        }
    }

    private float calculateMonthlyPayment(int montant, float taux, int duree) {
        // Calculer le paiement mensuel en utilisant la formule de paiement
        float tauxMensuel = taux/ 12; // Convertir le taux annuel en taux mensuel
        int nbEcheances = duree * 12; // Convertir la durée en nombre d'échéances mensuelles
        float paiement = (float) (montant * tauxMensuel / (1 - Math.pow(1 + tauxMensuel, -nbEcheances)));
        return paiement;
    }


    private void populateFields (Credit credit){

        montantid.setText(String.valueOf(credit.getMontant()));
        payementid.setText(String.format("%.2f",credit.getPayement()));
        dureeid.setText(String.valueOf(credit.getDuree()));

    }
    @FXML
    void initialize () {
        typeid.setOnShowing(event -> {
            // Remplir la ComboBox avec les types de crédit disponibles
            populateTypeComboBox();
        });

        Credit initialSelection = TableCredit.getSelectionModel().getSelectedItem();
        showCreditDetails(initialSelection);

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
    private void showCreditDetails(Credit credit) {
        if (credit != null) {
            // Afficher les détails du crédit dans les champs du formulaire

            montantid.setText(String.valueOf(credit.getMontant()));
            payementid.setText(String.valueOf(credit.getPayement()));
            dureeid.setText(String.valueOf(credit.getDuree()));

            // Utilisez d'autres champs de l'objet Credit pour remplir les autres champs du formulaire
        } else {
            // Si aucun crédit n'est sélectionné, vider les champs du formulaire
            typeid.setValue(null);
            montantid.clear();
            payementid.clear();
            dureeid.clear();
            // Clear other form fields
        }
    }


    private void configureTableView () {
        this.TypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        this.MontantCol.setCellValueFactory(new PropertyValueFactory<>("montant"));
        this.PayementCol.setCellValueFactory(new PropertyValueFactory<>("payement"));
        this.DureeCol.setCellValueFactory(new PropertyValueFactory<>("duree"));
        this.DatedebCol.setCellValueFactory(new PropertyValueFactory<>("datedeb"));
        this.DatefinCol.setCellValueFactory(new PropertyValueFactory<>("datefin"));
    }
    private void clearFields () {
        typeid.setValue(null);
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
    private void populateTypeComboBox() {
        try {
            // Récupérer les types de crédit depuis la base de données
            List<TypeCredit> types = typecreditService.read();

            // Créer une liste de noms de types de crédit
            List<String> typeNames = new ArrayList<>();
            for (TypeCredit type : types) {
                typeNames.add(type.getNom());
            }

            // Ajouter les noms des types de crédit à la ComboBox
            ObservableList<String> options = FXCollections.observableArrayList(typeNames);
            typeid.setItems(options);
        } catch (SQLException e) {
            showAlertWithError("Erreur", "Erreur lors de la récupération des types de crédit : " + e.getMessage());
        }
    }



    public void setMainController(Maindashbord mainController) {
        this.mainController = mainController;
    }

}