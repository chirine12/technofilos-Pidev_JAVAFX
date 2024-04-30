package com.example.test.service;

import com.example.test.model.Virement;
import com.example.test.utils.SQLConnector;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.property.UnitValue;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;



import java.net.MalformedURLException;





    public class VirementClientService implements ICRUD <Virement> {
        private Connection cnx;

        public VirementClientService(){
            cnx = SQLConnector.getInstance().getConnection();


        }
        public int getCompteCourantIdByClientId(int clientId) throws SQLException {
            String sql = "SELECT comptecourant_id FROM Client WHERE id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, clientId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("comptecourant_id");
                    } else {
                        throw new SQLException("Aucun compte courant trouvé pour ce client.");
                    }
                }
            }
        }
        public int getCompteCourantIdByRib(long rib) throws SQLException {
            String sql = "SELECT id FROM CompteCourant WHERE rib = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setLong(1, rib);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("id");
                    } else {
                        throw new SQLException("Aucun compte courant trouvé pour ce RIB.");
                    }
                }
            }
        }
        @Override

        public void create(Virement virement, int clientId) throws SQLException {
            // Début de la transaction
            try {
                cnx.setAutoCommit(false);

                // Deduire le montant du compte courant du client
                int compteCourantId = getCompteCourantIdByClientId(clientId);
                if (!soldeSuffisant(compteCourantId, virement.getMontant())) {
                    throw new SQLException("Solde insuffisant pour effectuer le virement.");
                }

                deduireMontantDuCompte(compteCourantId, virement.getMontant());
                int compteCourantIdDestinataire = getCompteCourantIdByRib(virement.getDestinataire());
                ajouterMontantAuCompte(compteCourantIdDestinataire, virement.getMontant());
                // Insertion du virement
                String sql = "INSERT INTO Virement (source, destinataire, montant, motif, date, client_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                    statement.setLong(1, virement.getSource());
                    statement.setLong(2, virement.getDestinataire());
                    statement.setFloat(3, virement.getMontant());
                    statement.setString(4, virement.getMotif());
                    if (virement.getDate() != null) {
                        statement.setDate(5, java.sql.Date.valueOf(virement.getDate()));
                    } else {
                        System.out.println("Warning: Date is null for Virement ID " + virement.getId());
                        statement.setDate(5, null);
                    }
                    statement.setInt(6, clientId); // Ajoutez le client_id comme paramètre à la fin de la requête
                    statement.executeUpdate();
                }

                cnx.commit();
            } catch (SQLException e) {
                if (cnx != null) {
                    cnx.rollback(); // En cas d'erreur, annuler la transaction
                }
                throw e; // Propager l'exception
            } finally {
                if (cnx != null) {
                    cnx.setAutoCommit(true); // Rétablir l'auto-commit
                }
            }
        }
        public void ajouterMontantAuCompte(int compteCourantId, float montant) throws SQLException {
            String sql = "UPDATE CompteCourant SET solde = solde + ? WHERE id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setFloat(1, montant);
                statement.setInt(2, compteCourantId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Aucun compte n'a été mis à jour pour créditer, vérifiez l'ID du compte.");
                }
            }
        }
        // ... (Reste des méthodes CRUD)

        public void deduireMontantDuCompte(int compteCourantId, float montant) throws SQLException {
            String sql = "UPDATE CompteCourant SET solde = solde - ? WHERE id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setFloat(1, montant);
                statement.setInt(2, compteCourantId);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Aucun compte n'a été mis à jour, vérifiez l'ID du compte.");
                }
            }
        }

        public boolean soldeSuffisant(int compteCourantId, float montant) throws SQLException {
            String sql = "SELECT solde FROM CompteCourant WHERE id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, compteCourantId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        float soldeActuel = rs.getFloat("solde");
                        return soldeActuel >= montant;
                    } else {
                        return false; // Aucun solde trouvé pour ce compte
                    }
                }
            }
        }
        public String getRibByClientId(int clientId) throws SQLException {

            String sql = "SELECT cc.rib FROM Client c JOIN CompteCourant cc ON c.comptecourant_id = cc.id WHERE c.id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, clientId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("rib");
                    } else {
                        return null;
                    }
                }
            } catch (SQLException e) {
                // Imprimer l'erreur SQL et la renvoyer
                System.out.println("Erreur SQL lors de la récupération du RIB: " + e.getMessage());
                throw e;
            }
        }


        @Override
        public void update(Virement virement) throws SQLException {
            String sql = "UPDATE Virement SET source = ?, destinataire = ?, montant = ?, motif = ? WHERE id = ?";
            // Utilisation d'un bloc try-with-resources pour s'assurer que le PreparedStatement est bien fermé
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setLong(1, virement.getSource());
                ps.setLong(2, virement.getDestinataire());
                ps.setFloat(3, virement.getMontant());
                ps.setString(4, virement.getMotif());
                // Conversion de LocalDate en java.sql.Date

                ps.setInt(5, virement.getId());

                ps.executeUpdate();
            } // Pas besoin de catch ici puisque les exceptions sont propagées
        }

        @Override
        public void delete(int id) throws SQLException {
            String sql = "DELETE FROM Virement WHERE id = ?";
            try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
        }

        @Override
        public List<Virement> read(int clientId) throws SQLException {
            String sql = "SELECT * FROM Virement WHERE client_id=?";
            List<Virement> virements = new ArrayList<>();

            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, clientId);
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        Virement virement = new Virement();
                        virement.setId(rs.getInt("id"));
                        virement.setSource(rs.getLong("source"));
                        virement.setDestinataire(rs.getLong("destinataire"));
                        virement.setMontant(rs.getFloat("montant"));
                        virement.setMotif(rs.getString("motif"));

                        java.sql.Date dbSqlDate = rs.getDate("date");
                        System.out.println("Fetched date from DB: " + dbSqlDate);  // Debugging output

                        if (dbSqlDate != null) {
                            LocalDate dbDate = dbSqlDate.toLocalDate();
                            virement.setDate(dbDate);
                        } else {
                            System.out.println("Date is null for Virement ID: " + virement.getId()); // More debugging output
                        }

                        virements.add(virement);
                    }
                }
            }
            return virements;
        }



        public Virement readVirement(int virementId) throws SQLException {
            String sql = "SELECT * FROM Virement WHERE id = ?";
            // Utilisation d'un bloc try-with-resources pour le PreparedStatement
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, virementId);

                // Utilisation d'un bloc try-with-resources pour le ResultSet
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Virement virement = new Virement();
                        virement.setId(resultSet.getInt("id"));
                        virement.setSource(resultSet.getLong("source"));
                        virement.setDestinataire(resultSet.getLong("destinataire"));
                        virement.setMontant(resultSet.getFloat("montant"));
                        virement.setMotif(resultSet.getString("motif"));
                        // Conversion de java.sql.Date en LocalDate
                        java.sql.Date dbSqlDate = resultSet.getDate("date");
                        if (dbSqlDate != null) {
                            LocalDate dbDate = dbSqlDate.toLocalDate();
                            virement.setDate(dbDate);
                        }

                        return virement;
                    } else {
                        // Virement with the given ID not found
                        return null;
                    }
                }
            }
        }

        public void generateVirementReport(int clientId, String destPath) {
            String sql = "SELECT * FROM Virement WHERE client_id=?";
            try (Connection cnx = SQLConnector.getInstance().getConnection();
                 PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, clientId);
                ResultSet rs = statement.executeQuery();

                PdfWriter writer = new PdfWriter(destPath);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                // Attempt to add logo
                try {
                    ImageData imageData = ImageDataFactory.create(getClass().getResource("/com/example/test/IMAGES/logoebank2.png").toExternalForm());
                    // Adjust this path
                    Image logo = new Image(imageData);
                    logo.setWidth(UnitValue.createPercentValue(20));  // set width to 20% of page
                    document.add(logo);
                } catch (MalformedURLException e) {
                    System.err.println("URL is malformed, check your path to the image: " + e.getMessage());
                    // Optionally add a fallback image or skip adding an image
                }

                // Adding a title
               /* Paragraph title = new Paragraph("Liste des virements pour le client ID: " + clientId)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(14)
                        .setFontColor(new DeviceRgb(0, 153, 204));
                document.add(title);*/

                // Creating a table
                float[] columnWidths = {1, 3, 3, 3, 3, 2};
                Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
                String[] headers = {"ID", "Source", "Destinataire", "Montant", "Motif", "Date"};
                DeviceRgb headerBgColor = new DeviceRgb(224, 224, 224);

                for (String header : headers) {
                    table.addHeaderCell(new Paragraph(header)
                            .setBackgroundColor(headerBgColor)
                            .setBold()
                            .setFontColor(new DeviceRgb(0, 0, 0)));
                }

                while (rs.next()) {
                    table.addCell(String.valueOf(rs.getInt("id")));
                    table.addCell(String.valueOf(rs.getLong("source")));
                    table.addCell(String.valueOf(rs.getLong("destinataire")));
                    table.addCell(String.valueOf(rs.getFloat("montant")));
                    table.addCell(rs.getString("motif"));
                    java.sql.Date date = rs.getDate("date");
                    table.addCell(date != null ? date.toString() : "N/A");
                }

                document.add(table);
                document.close();

                System.out.println("PDF Created: " + destPath);
            } catch (SQLException | FileNotFoundException e) {
                System.err.println("An error occurred while creating PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }


        public void start(Stage primaryStage) {
            WebView webView = new WebView();
            webView.getEngine().load(getClass().getResource("com/example/test/recaptcha.html").toExternalForm());

            Scene scene = new Scene(webView, 800, 600);
            primaryStage.setTitle("Google reCAPTCHA dans JavaFX");
            primaryStage.setScene(scene);
            primaryStage.show();
        }


        public boolean verifyRecaptcha(String gRecaptchaResponse) {
            // Remplacez 'votre_clé_secrète' par votre clé secrète obtenue de Google reCAPTCHA
            String secretKey = "6Lek_8ApAAAAAFENG9lBqbXX821RP01bIruxpVbV";
            HttpClient client = HttpClient.newHttpClient();
            String url = "https://www.google.com/recaptcha/api/siteverify";

            // Préparation des paramètres de la requête
            String params = "secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8) +
                    "&response=" + URLEncoder.encode(gRecaptchaResponse, StandardCharsets.UTF_8);

            // Créer une requête POST
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(params))
                    .build();

            try {
                // Envoyer la requête
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // La réponse de Google est au format JSON, vous pouvez utiliser une bibliothèque JSON pour parser la réponse
                String json = response.body();
                // Exemple basique de vérification si succès sans une bibliothèque JSON :
                return json.contains("\"success\": true");
            } catch (IOException | InterruptedException e) {
                System.err.println("Error during reCAPTCHA verification: " + e.getMessage());
                return false;
            }
        }
        public List<Long> getRibs() {
            List<Long> ribs = new ArrayList<>();
            try (
                 PreparedStatement ps = cnx.prepareStatement("SELECT rib FROM beneficiaire");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    ribs.add(rs.getLong("rib"));
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des RIBs : " + e.getMessage());
                e.printStackTrace();
            }
            return ribs;
        }

        public String getClientEmailByRib(long destinataireRib) throws SQLException {
            String sql = "SELECT c.email " +
                    "FROM Client c " +
                    "JOIN CompteCourant cc ON c.comptecourant_id = cc.id " +
                    "WHERE cc.rib = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setLong(1, destinataireRib);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("email");
                    } else {
                        throw new SQLException("Aucun email trouvé pour ce RIB de destinataire.");
                    }
                }
            }
        }

    }




