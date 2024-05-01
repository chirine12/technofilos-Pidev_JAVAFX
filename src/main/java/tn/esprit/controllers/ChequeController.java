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

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import javafx.scene.image.Image;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javafx.scene.image.Image;
import java.util.Map;
import javafx.scene.image.ImageView;

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

    @FXML
    private Button generateQRButton;

    @FXML
    private ImageView qrCodeImageView;

    @FXML
    private  Button readQRButton;

    @FXML
    private  TextField txtQR;
    @FXML
    private TextField searchField;



    private final ChequeService chequeService = new ChequeService();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @FXML
    void createCheque() {
        // Récupérer les valeurs depuis l'interface utilisateur
        String Num = txtNum.getText().trim(); // Assurez-vous d'avoir un TextField pour le motif
        String NumCom = txtNumCom.getText().trim();
        String Mon = txtMon.getText().replace(",",".");

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
        Float Montant=Float.parseFloat(Mon);

        try {
            Montant = Float.parseFloat(Mon);
        } catch (NumberFormatException e) {
            showAlertWithError("Erreur de Validation", "Le format du montant est invalide.");
            return;
        }
        if (Montant <= 0) {
            showAlertWithError("Erreur de Validation", "Le montant doit être positif.");
            return;
        }

        if (!NumCom.matches("\\d{8}")) {
            showAlertWithError("Erreur de Validation", "RIB doit être composée de 8 chiffres exactement.");
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
    void DeleteCheque(ActionEvent event) {
        Cheque selectedCheque = TableCheque.getSelectionModel().getSelectedItem();
        if (selectedCheque != null) {
            // Afficher une boîte de dialogue de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Suppression de Cheque");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer ce Cheque?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // L'utilisateur a appuyé sur le bouton OK, procéder à la suppression
                try {
                    chequeService.delete(selectedCheque.getId());
                    refreshTableView();
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la suppression du cheque: " + e.getMessage());
                    e.printStackTrace();
                }
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
            return; // Ajout d'un return pour sortir de la fonction si aucun cheque n'est sélectionné
        }

        // Afficher une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de modification");
        alert.setHeaderText("Modification de Cheque");
        alert.setContentText("Êtes-vous sûr de vouloir modifier ce Cheque?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // L'utilisateur a appuyé sur le bouton OK, procéder à la modification
            // Suite du code de mise à jour du cheque ici...

            if (Numero.isEmpty() || NumCompte.isEmpty() || Montant.isEmpty()) {
                showAlertWithError("Erreur de saisie", "Veuillez remplir tous les champs.");
                return;
            }

            // Vérifier que montant et duree contiennent uniquement des chiffres
            if (!Numero.matches("\\d+")  || !NumCompte.matches("\\d+")) {
                showAlertWithError("Erreur de saisie", "Le Numéro de cheque , le montant et le RIB doivent contenir uniquement des chiffres.");
                return;
            }

            // Convertir les valeurs en types appropriés après la validation
            Integer Num = Integer.parseInt(Numero);
            Integer NumCom = Integer.parseInt(NumCompte);
            Float Mon=Float.parseFloat(Montant);

            try {
                Mon = Float.parseFloat(Montant);
            } catch (NumberFormatException e) {
                showAlertWithError("Erreur de Validation", "Le format du montant est invalide.");
                return;
            }
            if (Mon <= 0) {
                showAlertWithError("Erreur de Validation", "Le montant doit être positif.");
                return;
            }

            if (!NumCompte.matches("\\d{8}")) {
                showAlertWithError("Erreur de Validation", "RIB doit être composée de 8 chiffres exactement.");
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
                // Récupérer le crédit à mettre à jour depuis la base de données
                Cheque existingCheque = chequeService.findById(chequeId);
                if (existingCheque != null) {
                    // Mettre à jour les champs modifiables
                    existingCheque.setNum(Integer.parseInt(Numero));
                    existingCheque.setNumcompte(Integer.parseInt(NumCompte));
                    existingCheque.setMontant(Float.parseFloat(Montant));

                    // Mettre à jour la date de début si elle a été modifiée
                    LocalDate datedebValue1 = txtDate.getValue();
                    if (datedebValue1 != null) {
                        existingCheque.setDate(Date.valueOf(datedebValue));
                    }

                    // Mettre à jour le crédit dans la base de données
                    chequeService.update(existingCheque);
                    System.out.println("Cheque mis à jour avec succès !");
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Mettre à jour le TableView avec les résultats de recherche
            filterChequeByRib(newValue);
        });
    }
    @FXML
    void filterChequeByRib(ActionEvent event) {
        String rib = searchField.getText().trim();
        filterChequeByRib(rib);
    }

    private void filterChequeByRib(String rib) {
        try {
            // Lire tous les cheques
            List<Cheque> cheques = chequeService.read();

            // Filtrer les cheques en fonction du RIB
            ObservableList<Cheque> filteredCheques = FXCollections.observableArrayList();
            for (Cheque cheque : cheques) {
                // Convertir le numéro de compte en String avant de l'utiliser
                String numCompteStr = String.valueOf(cheque.getNumcompte());
                if (numCompteStr.contains(rib)) {
                    filteredCheques.add(cheque);
                }
            }

            // Mettre à jour le TableView avec les résultats de recherche
            TableCheque.setItems(filteredCheques);
        } catch (SQLException e) {
            errorMessage.setText("Erreur lors de la recherche des cheques: " + e.getMessage());
            e.printStackTrace();
        }
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

    @FXML
    private void generateQRCode() {
        String chequeNumber = txtNum.getText().trim();
        String rib = txtNumCom.getText().trim();

        // Concatenate the cheque details into a single string
        String chequeDetails = "Cheque Number: " + chequeNumber + "\nRIB: " + rib;

        // Set the width and height of the QR code
        int width = 350;
        int height = 350;

        // Generate the QR code image based on the cheque details
        Image qrImage = generateQRCodeImage(chequeDetails, width, height);

        // Set the generated QR code image to the ImageView
        qrCodeImageView.setImage(qrImage);
    }

    private Image generateQRCodeImage(String text, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Adjust margin as needed

        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            return new Image(new ByteArrayInputStream(outputStream.toByteArray()));
        } catch (WriterException | IOException e) {
            e.printStackTrace(); // Handle error gracefully in your application
            return null;
        }
    }
    @FXML
    void readQRCode(ActionEvent event) {
        try {
            // Get the path to the Python script
            InputStream inputStream = getClass().getResourceAsStream("/QR_codeReader.py");
            File tempFile = File.createTempFile("QR_codeReader.py", ".py");
            tempFile.deleteOnExit();

            // Copy the Python script from resources to a temporary file
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Command to execute Python script
            String command = "python " + tempFile.getAbsolutePath();

            // Start the process
            Process process = new ProcessBuilder(command.split(" ")).start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to finish
            int exitCode = process.waitFor();

            // Print the output
            System.out.println("Output: " + output);

            // Handle exit code if needed
            if (exitCode != 0) {
                System.err.println("Error: Process exited with code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
