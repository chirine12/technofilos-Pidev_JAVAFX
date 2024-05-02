package tn.esprit.service;

import com.itextpdf.text.pdf.PdfPCell;
import javafx.scene.control.Alert;
import tn.esprit.model.Credit;
import  tn.esprit.model.TypeCredit;
import tn.esprit.utils.SQLConnector;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CreditService implements CRUD<Credit>{
    private Connection cnx;

    public CreditService() {
        cnx = SQLConnector.getInstance().getConnection();
    }

    @Override
    public void create(Credit credit) throws SQLException, java.sql.SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Credit ( `type`, `montant`, `payement`, `duree`, `datedeb`, `datefin`, `typecredit_id`) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, credit.getType());
            statement.setInt(2, credit.getMontant());
            statement.setFloat(3, credit.getPayement());
            statement.setInt(4, credit.getDuree());
            statement.setDate(5, credit.getDatedeb());
            statement.setDate(6, credit.getDatefin());
            statement.setInt(7,credit.getTypecreditId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }

    }

    @Override
    public void update(Credit credit) throws SQLException {
        String sql = "UPDATE Credit SET `type`=?,`montant`=?,`payement`=?,`duree`=?,`datedeb`=?,`datefin`=? WHERE id =?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, credit.getType());
            ps.setInt(2, credit.getMontant());
            ps.setFloat(3, credit.getPayement());
            ps.setInt(4, credit.getDuree());
            ps.setDate(5, credit.getDatedeb());
            ps.setDate(6, credit.getDatefin());
            ps.setInt(7, credit.getId());
            ps.executeUpdate();
        }

    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Credit WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public List<Credit> read() throws SQLException {
        String sql = "SELECT * FROM Credit";
        List<Credit> credits = new ArrayList<>();
        try (Statement statement = cnx.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Credit credit = new Credit();
                credit.setId(rs.getInt("id"));
                credit.setType(rs.getString("type"));
                credit.setMontant(rs.getInt("montant"));
                credit.setPayement(rs.getFloat("payement"));
                credit.setDuree(rs.getInt("duree"));
                credit.setDatedeb(rs.getDate("datedeb"));
                credit.setDatefin(rs.getDate("datefin"));

                credits.add(credit);
            }
        }
        return credits;
    }
    public Credit findById(int id) throws SQLException {
        String sql = "SELECT * FROM Credit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Credit credit = new Credit();
                    credit.setId(rs.getInt("id"));
                    credit.setType(rs.getString("type"));
                    credit.setMontant(rs.getInt("montant"));
                    credit.setPayement(rs.getFloat("payement"));
                    credit.setDuree(rs.getInt("duree"));
                    credit.setDatedeb(rs.getDate("datedeb"));
                    credit.setDatefin(rs.getDate("datefin"));
                    return credit;
                }
            }
        }
        return null; // Retourne null si aucun crédit n'est trouvé avec cet identifiant
    }
    public List<Credit> findByType(String type) throws SQLException {
        List<Credit> credits = new ArrayList<>();
        String sql = "SELECT * FROM Credit WHERE type = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Credit credit = new Credit();
                    credit.setId(rs.getInt("id"));
                    credit.setType(rs.getString("type"));
                    credit.setMontant(rs.getInt("montant"));
                    credit.setPayement(rs.getFloat("payement"));
                    credit.setDuree(rs.getInt("duree"));
                    credit.setDatedeb(rs.getDate("datedeb"));
                    credit.setDatefin(rs.getDate("datefin"));
                    credits.add(credit);
                }
            }
        }
        return credits;
    }



    public void generateCreditReport(String outputPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Création du titre
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.BOLD);
            Paragraph title = new Paragraph("Liste des Crédits", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Création de la table
            PdfPTable pdfTable = new PdfPTable(6); // 6 colonnes
            pdfTable.setWidthPercentage(100);

            // Ajout des en-têtes de colonnes
            String[] headers = {"ID", "Type", "Montant", "Paiement", "Date Début", "Date Fin"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Paragraph(header, FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(cell);
            }

            // Récupération et ajout des données des crédits
            String sql = "SELECT * FROM Credit";
            try (PreparedStatement statement = cnx.prepareStatement(sql);
                 ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    pdfTable.addCell(String.valueOf(rs.getInt("id")));
                    pdfTable.addCell(rs.getString("type"));
                    pdfTable.addCell(String.format("%.2f", rs.getDouble("montant")));
                    pdfTable.addCell(String.format("%.2f", rs.getDouble("payement")));
                    pdfTable.addCell(rs.getDate("datedeb").toString());
                    pdfTable.addCell(rs.getDate("datefin").toString());
                }
            }

            document.add(pdfTable);
            document.close();

            System.out.println("PDF Created: " + outputPath);
        } catch (DocumentException | IOException | SQLException e) {
            System.err.println("Impossible de générer le PDF : " + e.getMessage());
            e.printStackTrace();
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