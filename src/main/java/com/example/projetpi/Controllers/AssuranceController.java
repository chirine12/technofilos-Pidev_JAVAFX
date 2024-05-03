package com.example.projetpi.Controllers;

import com.example.projetpi.Model.Assurance;
import com.example.projetpi.utils.DBconnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class AssuranceController implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;
    @FXML
    private BarChart<String, Number> chartS;

    @FXML
    private CategoryAxis chartX;

    @FXML
    private NumberAxis chartY;

    @FXML
    private Label nombreA;


    @FXML
    private Button addBtn;

    @FXML
    private AnchorPane add_form;
    @FXML
    private AnchorPane home_form;

    @FXML
    private Button homebtn;

    @FXML
    private Button tContrat;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;
    @FXML
    private AnchorPane addForm;

    @FXML
    private TextField tMontant;

    @FXML
    private TextField tType;

    @FXML
    private TextField tdelais;

    @FXML
    private TableColumn<Assurance, String> coldelais;

    @FXML
    private TableColumn<Assurance, Integer> colmontant;

    @FXML
    private TableColumn<Assurance, String> coltype;

    @FXML
    private TableView<Assurance> table;
    private int id;
    @FXML
    private AnchorPane add2form;
    @FXML
    private TextField search_0;

    @FXML
    void switchForm(ActionEvent event) {
        if (event.getSource() == homebtn) {

            home_form.setVisible(true);
            addForm.setVisible(false);
        } else if (event.getSource() == addBtn) {
            home_form.setVisible(false);
            add2form.setVisible(true);
            addForm.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showAssurances();
        generateStatistics();
        homeAssuranceNumber();
        search_0.textProperty().addListener((observable, oldValue, newValue) -> {
            // Appeler la méthode de recherche avec le nouveau texte saisi
            search(newValue);
        });
    }

    public void homeAssuranceNumber() {
        String query = "SELECT COUNT(id) FROM assurance";
        con = DBconnexion.getCon();
        int countData = 0;
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                countData = rs.getInt("COUNT(id)");
            }
            nombreA.setText(String.valueOf(countData));
        } catch (SQLException e) {
            throw new RuntimeException(e);


        }
    }

    public ObservableList<Assurance> getAssurances() {
        ObservableList<Assurance> assurances = FXCollections.observableArrayList();
        String query = "SELECT * from assurance";
        con = DBconnexion.getCon();
        try {
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Assurance st = new Assurance();
                st.setId(rs.getInt("id"));
                st.setType(rs.getString("type"));
                st.setDelais(rs.getString("Delais"));
                st.setMontant(rs.getInt("montant"));
                assurances.add(st);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return assurances;
    }

    public void showAssurances() {
        ObservableList<Assurance> list = getAssurances();
        table.setItems(list);
        coldelais.setCellValueFactory(new PropertyValueFactory<>("delais"));
        colmontant.setCellValueFactory(new PropertyValueFactory<>("montant"));
        coltype.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    @FXML
    void clearField(ActionEvent event) {
        clear();
    }

    @FXML
    void createAssurance(ActionEvent event) {
        if (validateFields()) {
            String insert = " insert into assurance(Type,Delais,Montant) values (?,?,?)";
            con = DBconnexion.getCon();
            try {
                st = con.prepareStatement(insert);
                st.setString(1, tType.getText());
                st.setString(2, tdelais.getText());
                st.setString(3, tMontant.getText());
                st.executeUpdate();
                clear();
                showAssurances();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            homeAssuranceNumber();
            generateStatistics();
            // Après la création de l'assurance, affichez un message et redirigez l'utilisateur vers la page du contrat
            showAlertAndRedirect("Succès", "Assurance créée", "L'assurance a été créée avec succès. Cliquez pour voir le contrat.", "Fxml/contrat.fxml");
        }

    }


    @FXML
    void getData(MouseEvent event) {
        Assurance assurance = table.getSelectionModel().getSelectedItem();
        id = assurance.getId();
        tType.setText(assurance.getType());
        tMontant.setText(String.valueOf(assurance.getMontant()));
        tdelais.setText(assurance.getDelais());
        btnSave.setDisable(true);
    }

    void clear() {
        tdelais.setText(null);
        tMontant.setText(null);
        tType.setText(null);
        btnSave.setDisable(false);
    }

    @FXML
    void deleteAssurance(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Voulez-vous vraiment supprimer cette assurance  ?");

        // Personnaliser les boutons de l'alerte de confirmation
        ButtonType buttonYes = new ButtonType("Oui");
        ButtonType buttonCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonCancel);

        // Afficher l'alerte de confirmation et attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Si l'utilisateur clique sur le bouton "Oui", supprimer le contrat
        if (result.isPresent() && result.get() == buttonYes) {
            String delete = "DELETE FROM assurance WHERE id = ? ";
            con = DBconnexion.getCon();
            try {
                st = con.prepareStatement(delete);
                st.setInt(1, id);
                st.executeUpdate();
                showAssurances();
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        homeAssuranceNumber();
        generateStatistics();
    }

    @FXML
    void updateAssurance(ActionEvent event) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Voulez-vous vraiment modifier cette assurance ?");

        // Personnaliser les boutons de l'alerte de confirmation
        ButtonType buttonYes = new ButtonType("Oui");
        ButtonType buttonCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirmationAlert.getButtonTypes().setAll(buttonYes, buttonCancel);

        // Afficher l'alerte de confirmation et attendre la réponse de l'utilisateur
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        // Si l'utilisateur clique sur le bouton "Oui", supprimer le contrat
        if (result.isPresent() && result.get() == buttonYes) {
            if (validateFields()) {

            }
            String update = "update assurance set type =? , delais =?, montant  =? where id =? ";
            con = DBconnexion.getCon();
            try {
                st = con.prepareStatement(update);
                st.setString(1, tType.getText());
                st.setString(2, tdelais.getText());
                st.setInt(3, Integer.parseInt(tMontant.getText()));
                st.setInt(4, id);
                st.executeUpdate();
                showAssurances();
                clear();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            generateStatistics();
        }
        // Après la mise à jour de l'assurance, affichez un message et redirigez l'utilisateur vers la page du contrat
        showAlertAndRedirect("Succès", "Assurance mise à jour", "L'assurance a été mise à jour avec succès. Cliquez pour voir le contrat.", "chemin/vers/page/contrat.fxml");

    }


    private void showAlertAndRedirect(String title, String header, String content, String fxmlPath) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();

        // Si l'utilisateur clique sur le bouton "OK", chargez et affichez le fichier FXML du contrat
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Charger le fichier FXML du contrat
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                // Créer une nouvelle fenêtre
                Stage stage = new Stage();

                // Définir la scène avec le contenu du fichier FXML chargé
                Scene scene = new Scene(root);
                stage.setScene(scene);

                // Afficher la nouvelle fenêtre
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean validateFields() {
        if (tType.getText().isEmpty() || tdelais.getText().isEmpty() || tMontant.getText().isEmpty()) {
            showErrorAlert("Champs vides", "Veuillez remplir tous les champs.");
            return false;
        }

        // Validation du type
        String type = tType.getText();
        if (!Pattern.matches("^(vie|habitation|automobile|vie )$", type)) {
            showErrorAlert("Type invalide", "Le type doit être 'vie', 'habitation', 'automobile' ou 'automobile '.");
            return false;
        }

        // Validation du délais
        String delais = tdelais.getText();
        if (!Pattern.matches("^\\d+\\s*mois$", delais)) {
            showErrorAlert("Délais invalide", "Le délais doit être un nombre suivi de 'mois', par exemple '12 mois'.");
            return false;
        }

        // Validation du montant
        String montant = tMontant.getText();
        if (!Pattern.matches("^\\d+$", montant)) {
            showErrorAlert("Montant invalide", "Le montant doit être un nombre entier positif.");
            return false;
        }

        return true;
    }

    @FXML
    void switchTocontrat(MouseEvent event) {
        try {
            // Charger le fichier FXML de la nouvelle page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Fxml/contrat.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec le contenu du fichier FXML chargé
            Scene scene = new Scene(root);

            // Obtenir la scène actuelle à partir de l'élément source de l'événement
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();

            // Définir la nouvelle scène sur la fenêtre principale (stage) et l'afficher
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Gérer les erreurs liées au chargement du fichier FXML
        }
    }




    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void search(String search_0) {
        // Effacer la liste actuelle d'assurances
        table.getItems().clear();

        // Vérifier si le champ de recherche est vide
        if (search_0 == null || search_0.isEmpty()) {
            // Si le champ de recherche est vide, afficher toutes les assurances
            showAssurances();
            return;
        }

        // Interroger la base de données pour récupérer les assurances correspondant au texte de recherche
        String query = "SELECT * FROM assurance WHERE Type LIKE ? OR Delais LIKE ? OR Montant LIKE ?";
        con = DBconnexion.getCon();
        try {
            st = con.prepareStatement(query);
            st.setString(1, "%" + search_0 + "%");
            st.setString(2, "%" + search_0 + "%");
            st.setString(3, "%" + search_0 + "%");
            rs = st.executeQuery();
            while (rs.next()) {
                Assurance assurance = new Assurance();
                assurance.setId(rs.getInt("id"));
                assurance.setType(rs.getString("Type"));
                assurance.setDelais(rs.getString("Delais"));
                assurance.setMontant(rs.getInt("Montant"));
                // Ajouter l'assurance à la liste des résultats de la recherche
                table.getItems().add(assurance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception (par exemple, afficher un message d'erreur à l'utilisateur)
        }
    }
    private void generateStatistics() {
        // Récupérer les données de la base de données pour les statistiques
        // Vous pouvez utiliser des requêtes SQL pour obtenir les données nécessaires

        // Exemple de données fictives pour le BarChart
        ObservableList<XYChart.Series<String, Number>> data = FXCollections.observableArrayList();
        XYChart.Series<String, Number> seriesType = new XYChart.Series<>();
        seriesType.setName("Nombre d'assurances par type");
        seriesType.getData().add(new XYChart.Data<>("Vie", 10));
        seriesType.getData().add(new XYChart.Data<>("Habitation", 20));
        seriesType.getData().add(new XYChart.Data<>("Automobile", 15));
        data.add(seriesType);

        // Mise à jour du BarChart avec les données de statistiques
        chartS.setData(data);
    }
}
