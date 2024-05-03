package com.example.projetpi.Controllers;

import com.example.projetpi.Model.Contrat;
import com.example.projetpi.utils.DBconnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import jdk.internal.foreign.StringSupport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;

public class ContratController implements Initializable {
    @FXML
    private Label nombreC;
    @FXML
    private TextField ttyp;

    @FXML
    private Button bClear;

    @FXML
    private Button bDelete;

    @FXML
    private Button bSave;

    @FXML
    private Button bUpdate;

    @FXML
    private DatePicker tDatedebut;

    @FXML
    private DatePicker tDatefin;

    @FXML
    private ImageView tImage;

    @FXML
    private Button tImport;

    @FXML
    private TextField tNumero;

    @FXML
    private TableColumn<Contrat, String> tSignature;

    @FXML
    private TableColumn<Contrat, String> tType;

    @FXML
    private TableColumn<Contrat, LocalDate> tdatedebut;

    @FXML
    private TableColumn<Contrat, LocalDate> tdatefin;

    @FXML
    private TableColumn<Contrat, Integer> tnumero;

    @FXML
    private AnchorPane ttype;

    private Connection con;

    @FXML
    private TableView<Contrat> table;
    @FXML
    private TextField search_0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        con = DBconnexion.getCon();
        showContrats();
        getContrats();
        search_0.textProperty().addListener((observable, oldValue, newValue) -> {

        });

    }
    private ObservableList<Contrat> showContrats() {
        ObservableList<Contrat> contrats = FXCollections.observableArrayList(); // Utilisez une liste observable pour faciliter la liaison avec la TableView
        try {
            String query = "SELECT * FROM contrat";
            PreparedStatement st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                contrats.add(new Contrat(
                        rs.getInt("id"),
                        rs.getString("Signature"),
                        rs.getString("type"),
                        rs.getDate("datedebut").toLocalDate(),
                        rs.getDate("datefin").toLocalDate(),
                        rs.getInt("numero")


                ));

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return contrats; // Retournez la liste des contrats
    }

    public void getContrats() {
        ObservableList<Contrat> list = showContrats();
        table.setItems(list);
        tSignature.setCellValueFactory(new PropertyValueFactory<>("Signature"));
        tType.setCellValueFactory(new PropertyValueFactory<>("type"));
        tdatedebut.setCellValueFactory(new PropertyValueFactory<>("datedebut"));
        tdatefin.setCellValueFactory(new PropertyValueFactory<>("datefin"));
        tnumero.setCellValueFactory(new PropertyValueFactory<>("numero"));

    }

    @FXML
    public void createContrat(ActionEvent event) {
        LocalDate datedebut = tDatedebut.getValue();
        LocalDate datefin = tDatefin.getValue();
        String type = ttyp.getText();
        String numero = tNumero.getText();

        if (datedebut == null || datefin == null || type.isEmpty() || numero.isEmpty()) {
            showAlert("Erreur de saisie", "Champs obligatoires vides", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        if (!type.matches("cdi|cdd|cvpi")) {
            showAlert("Erreur de saisie", "Type invalide", "Le type doit être 'cdi', 'cdd' ou 'cvpi'.");
            return;
        }

        if (!numero.matches("\\d{8}")) {
            showAlert("Erreur de saisie", "Numéro invalide", "Le numéro doit être un entier de 8 caractères.");
            return;
        }

        if (!datedebut.isBefore(datefin)) {
            showAlert("Erreur de saisie", "Dates invalides", "La date de début doit être avant la date de fin.");
            return;
        }

        Image signatureImage = tImage.getImage();
        if (signatureImage == null) {
            showAlert("Erreur de saisie", "Image manquante", "Veuillez sélectionner une image.");
            return;
        }
        Image signatureImage = tImage.getImage();
        if (signatureImage == null) {
            showAlert("Erreur de saisie", "Image manquante", "Veuillez sélectionner une image.");
            return;
        }

        // Convertir l'image en base64
        String base64Image = convertImageToBase64(signatureImage);

        // Insérer le contrat avec la signature en base64 dans la base de données
        insertContrat(datedebut, datefin, type, Integer.parseInt(numero), base64Image);
    }

    private String convertImageToBase64(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null); // Convertir l'image JavaFX en BufferedImage
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            StringSupport ImageIO = null;
            ImageIO.write(bufferedImage, "png", outputStream); // Écrire l'image dans le flux de sortie avec le format PNG
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageBytes = outputStream.toByteArray(); // Convertir le flux de sortie en un tableau de bytes
        return Base64.getEncoder().encodeToString(imageBytes); // Encoder les bytes en base64 et les renvoyer sous forme de chaîne
    }





    private void insertContrat(LocalDate datedebut, LocalDate datefin, String type, int numero, String base64Image) {
        String insertQuery = "INSERT INTO contrat (datedebut, datefin, type, numero, Signature) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBconnexion.getCon();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

            preparedStatement.setDate(1, java.sql.Date.valueOf(datedebut));
            preparedStatement.setDate(2, java.sql.Date.valueOf(datefin));
            preparedStatement.setString(3, type);
            preparedStatement.setInt(4, numero);
            preparedStatement.setString(5, base64Image); // Utiliser la chaîne base64

            preparedStatement.executeUpdate();

            clearFields();
            showAlert("Succès", "Contrat ajouté", "Le contrat a été ajouté avec succès.");

            showContrats();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'insertion", "Une erreur est survenue lors de l'insertion du contrat.");
        }
    }




    @FXML
    public void importImage(ActionEvent event) {
        File signatureFile = openSignatureFileChooser();
        if (signatureFile != null) {
            if (!signatureFile.getName().toLowerCase().endsWith(".png")) {
                showAlert("Erreur de fichier", "Type de fichier invalide", "Le fichier doit être de type PNG.");
                return;
            }
            Image image = new Image(signatureFile.toURI().toString());
            tImage.setImage(image);


        }
    }




    private File openSignatureFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Image", "*.png", "*.jpg", "*.gif"),
                new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );
        return fileChooser.showOpenDialog(null);
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        tDatedebut.setValue(null);
        tDatefin.setValue(null);
        tNumero.clear();
        ttyp.clear();
        tImage.setImage(null);
    }

    @FXML
    void deleteContrat(ActionEvent event) {
        Contrat contrat = table.getSelectionModel().getSelectedItem();
        if (contrat == null) {
            showAlert("Erreur de sélection", "Aucun contrat sélectionné", "Veuillez sélectionner un contrat à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation de suppression");
        confirmationAlert.setHeaderText("Suppression de contrat");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce contrat ?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (((Optional<?>) result).isPresent() && result.get() == ButtonType.OK) {
            try {
                String deleteQuery = "DELETE FROM contrat WHERE id = ?";
                PreparedStatement preparedStatement = con.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, contrat.getId());
                preparedStatement.executeUpdate();

                // Rafraîchir la TableView après la suppression
                table.getItems().remove(contrat);
                table.refresh();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur de suppression", "Erreur lors de la suppression du contrat", "Une erreur est survenue lors de la suppression du contrat.");
            }
        }
    }

    @FXML
    void getData(MouseEvent event) {
        Contrat contrat = table.getSelectionModel().getSelectedItem();
        if (contrat != null) {
            // Récupérer les données du contrat sélectionné
            int id = contrat.getId();
            LocalDate datedebut = contrat.getDatedebut();
            LocalDate datefin = contrat.getDatefin();
            String type = contrat.getType();
            int numero = contrat.getNumero();

            // Afficher les données dans les champs correspondants
            tNumero.setText(String.valueOf(numero));
            tDatedebut.setValue(datedebut);
            tDatefin.setValue(datefin);
            ttyp.setText(type);
            tImage.setImage(new Image("file:temp.png")); // Mettez à jour l'image comme vous le souhaitez

            // Désactiver le bouton Save car nous sommes en mode affichage uniquement

        } else {
            showAlert("Erreur de sélection", "Aucun contrat sélectionné", "Veuillez sélectionner un contrat pour afficher ses données.");
        }
    }



    @FXML
    void updateContrat(ActionEvent event) {
        Contrat contrat = table.getSelectionModel().getSelectedItem();
        if (contrat != null) {
            // Logique pour mettre à jour le contrat sélectionné
            LocalDate newDatedebut = tDatedebut.getValue();
            LocalDate newDatefin = tDatefin.getValue();
            String newType = ttyp.getText();
            String newNumero = tNumero.getText();
            Image newSignatureImage = tImage.getImage();

            // Effectuez les vérifications nécessaires sur les nouvelles valeurs ici

            // Vérifier si le champ tNumero est vide
            if (newNumero.isEmpty()) {
                showAlert("Erreur de saisie", "Numéro manquant", "Veuillez saisir un numéro pour le contrat.");
                return;
            }

            // Vérifier si le numéro est un entier valide
            int numero;
            try {
                numero = Integer.parseInt(newNumero);
            } catch (NumberFormatException e) {
                showAlert("Erreur de saisie", "Numéro invalide", "Le numéro doit être un entier valide.");
                return;
            }

            // Mettez à jour les données du contrat
            contrat.setDatedebut(newDatedebut);
            contrat.setDatefin(newDatefin);
            contrat.setType(newType);
            contrat.setNumero(numero);

            // Rafraîchir la TableView pour refléter les modifications
            table.refresh();

            // Effacer les champs après la mise à jour
            clearFields();

            // Afficher un message de succès
            showAlert("Succès", "Contrat mis à jour", "Le contrat a été mis à jour avec succès.");
        } else {
            showAlert("Erreur de sélection", "Aucun contrat sélectionné", "Veuillez sélectionner un contrat à mettre à jour.");
        }
    }

    @FXML
    void clearField(ActionEvent event) {
        clearFields();
    }


}
