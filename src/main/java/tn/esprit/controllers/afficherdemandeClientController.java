package tn.esprit.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.model.demande_desac_ce;
import tn.esprit.service.demandesService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class afficherdemandeClientController implements Initializable {
    @FXML
    private TableView table;

    @FXML
    private TableColumn ribColumn;
    @FXML
    private TableColumn date;

    @FXML
    private TableColumn raison;


    @FXML
    private Button btnretour;
    @FXML
    private  demandesService demandesService;
    private ObservableList<demande_desac_ce> demandesData = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        demandesService = new demandesService();
        initializeColumns(); // Initialise les colonnes de la table
        loadData(); // Charge les données dans la table
    }
    private void initializeColumns() {
        // Initialise les colonnes de la table en associant chaque colonne à une propriété du modèle Compteep
        ribColumn.setCellValueFactory(new PropertyValueFactory<>("rib")); // Ajoutez ceci
        raison.setCellValueFactory(new PropertyValueFactory<>("raison"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

    }

    private void loadData() {
        try {
            // Charge les données à partir du service CompteepService
            List<demande_desac_ce> demandesList = demandesService.readSpecificClient();

            demandesData.addAll(demandesList);
            table.setItems(demandesData);
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'exception selon les besoins, par exemple, afficher un message d'erreur
        }
    }


    @FXML
    private void clientdashbordpage() {
        try {
            // Charge le FXML pour le clientdashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/clientdashbord.fxml"));
            Parent clientDashboard = loader.load();

            // Configure le contrôleur du dashboard client, si nécessaire
            ComptepClientController controller = loader.getController();
            controller.setMainController(mainController);

            // Change le contenu pour afficher le dashboard client
            mainController.setContenu(clientDashboard);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Maindashbord mainController;

    public void setMainController(Maindashbord mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void generateAndSendPDF() {
        Document document = new Document();
        try {
            // Générer le fichier PDF dans un répertoire temporaire
            File tempFile = File.createTempFile("demandes_desactivation", ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(tempFile));
            document.open();

            // Création du titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Demandes de Désactivation", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title); // Ajoute le titre au document

            // Création de la table
            PdfPTable pdfTable = new PdfPTable(3); // 3 colonnes : RIB, Date, Raison
            pdfTable.setWidthPercentage(100); // Largeur à 100% de la page

            // Ajout des en-têtes de colonnes
            pdfTable.addCell("RIB");
            pdfTable.addCell("Date");
            pdfTable.addCell("Raison");

            // Ajout des données des demandes
            for (demande_desac_ce demande : demandesData) {
                pdfTable.addCell(demande.getRib());
                pdfTable.addCell(demande.getDate().toString());
                pdfTable.addCell(demande.getRaison());
            }

            document.add(pdfTable); // Ajoute la table au document
            document.close(); // Ferme le document

            // Envoie du PDF par email
            sendEmailWithAttachment(tempFile, "emnanaija@gmail.com");

            // Afficher un message de réussite
            showAlert("Succès", "Le PDF a été généré et envoyé par e-mail avec succès.");

            // Supprimer le fichier temporaire
            tempFile.delete();
        } catch (DocumentException | IOException | MessagingException e) {
            showAlert("Erreur", "Impossible de générer ou d'envoyer le PDF : " + e.getMessage());
        }
    }

    private void sendEmailWithAttachment(File file, String toAddress) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("Ebanking.Society@gmail.com", "ypbuklkwyqlktqmi");
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("Ebanking.Society@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject("Liste des Demandes de Désactivation");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Veuillez trouver ci-joint le fichier PDF des demandes de désactivation.");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (IOException e) {
            // Gérez ici l'exception IOException
            System.err.println("Error while attaching file: " + e.getMessage());
            // Vous pouvez aussi re-lancer l'exception ou prendre d'autres mesures
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
