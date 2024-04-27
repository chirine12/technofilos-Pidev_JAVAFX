package org.example.services;

import org.example.models.Credit;
import org.example.models.TypeCredit;
import org.example.utils.DBConnection;

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

}
