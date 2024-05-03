package com.example.test.service;

import com.example.test.model.Virement;
import com.example.test.utils.SQLConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirementService  {
    private  Connection cnx;

    public VirementService(){
        cnx = SQLConnector.getInstance().getConnection();


    }


    public void create(Virement virement) throws SQLException, java.sql.SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Virement (source, destinataire, montant, motif, date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setLong(1, virement.getSource());
            statement.setLong(2, virement.getDestinataire());
            statement.setFloat(3, virement.getMontant());
            statement.setString(4, virement.getMotif());
            // Conversion de LocalDate en java.sql.Date
            if (virement.getDate() != null) {
                statement.setDate(5, java.sql.Date.valueOf(virement.getDate()));
            } else {
                statement.setDate(5, null);
            }

            statement.executeUpdate();
        } catch (SQLException e) {
            // Ici, vous pouvez gérer les exceptions spécifiques ou les re-lancer
            throw e;
        }
    }


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


    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Virement WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public List<Virement> read() throws SQLException {
        String sql = "SELECT * FROM Virement";
        List<Virement> virements = new ArrayList<>();

        // Utilisation d'un bloc try-with-resources pour le Statement
        try (Statement statement = cnx.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Virement virement = new Virement();
                virement.setId(rs.getInt("id"));
                virement.setSource(rs.getLong("source"));
                virement.setDestinataire(rs.getLong("destinataire"));
                virement.setMontant(rs.getFloat("montant"));
                virement.setMotif(rs.getString("motif"));
                // Conversion de java.sql.Date en LocalDate
                java.sql.Date dbSqlDate = rs.getDate("date");
                if (dbSqlDate != null) {
                    LocalDate dbDate = dbSqlDate.toLocalDate();
                    virement.setDate(dbDate);
                }
                virements.add(virement);
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
    public Map<Long, Integer> getVirementCountByClient() throws SQLException {
        Map<Long, Integer> virementCounts = new HashMap<>();
        String sql = "SELECT source, COUNT(*) as count FROM Virement GROUP BY source";

        try (PreparedStatement statement = cnx.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                long clientId = rs.getLong("source");
                int count = rs.getInt("count");
                virementCounts.put(clientId, count);
            }
        } catch (SQLException e) {
            // Handle or throw the SQL exception appropriately
            throw e;
        }
        return virementCounts;
    }




}
