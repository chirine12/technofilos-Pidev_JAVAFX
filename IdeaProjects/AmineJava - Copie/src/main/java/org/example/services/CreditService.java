package org.example.services;

import org.example.models.Credit;
import org.example.models.TypeCredit;
import org.example.utils.DBConnection;
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

import java.net.MalformedURLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CreditService implements CRUD<Credit>{
    private Connection cnx;

    public CreditService() {
        cnx = DBConnection.getInstance().getCnx();
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
    public void generateCreditReport( String destPath) {
        String sql = "SELECT * FROM Credit ";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();

            PdfWriter writer = new PdfWriter(destPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Attempt to add logo
            try {
                ImageData imageData = ImageDataFactory.create(getClass().getResource("/logoebank2.png").toExternalForm());
                // Adjust this path
                Image logo = new Image(imageData);
                logo.setWidth(UnitValue.createPercentValue(20));  // set width to 20% of page
                document.add(logo);
            } catch (MalformedURLException e) {
                System.err.println("URL is malformed, check your path to the image: " + e.getMessage());
                // Optionally add a fallback image or skip adding an image
            }



            // Creating a table
            float[] columnWidths = {1, 3, 3, 3, 3, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
            String[] headers = { "Type", "Montant", "Payement", "Duree", "DateDeb","DateFin"};
            DeviceRgb headerBgColor = new DeviceRgb(224, 224, 224);

            for (String header : headers) {
                table.addHeaderCell(new Paragraph(header)
                        .setBackgroundColor(headerBgColor)
                        .setBold()
                        .setFontColor(new DeviceRgb(0, 0, 0)));
            }

            while (rs.next()) {
                table.addCell(rs.getString("type"));
                table.addCell(String.valueOf(rs.getInt("montant")));
                table.addCell(String.valueOf(rs.getFloat("payement")));
                table.addCell(String.valueOf(rs.getInt("duree")));
                java.sql.Date datedeb = rs.getDate("datedeb");
                table.addCell(datedeb != null ? datedeb.toString() : "N/A");
                java.sql.Date datefin = rs.getDate("datefin");
                table.addCell(datefin != null ? datefin.toString() : "N/A");
            }

            document.add(table);
            document.close();

            System.out.println("PDF Created: " + destPath);
        } catch (SQLException | FileNotFoundException e) {
            System.err.println("An error occurred while creating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
