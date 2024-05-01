package org.example.services;

import org.example.models.Credit;
import org.example.models.TypeCredit;
import org.example.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class TypeCreditservice implements ICRUD<TypeCredit>{
    private Connection cnx;

    public TypeCreditservice() {
        cnx = DBConnection.getInstance().getCnx();
    }
    @Override
    public void create(TypeCredit typeCredit)throws SQLException, java.sql.SQLIntegrityConstraintViolationException{
       String sql = "INSERT INTO `typecredit`(`nom`, `taux`) VALUES (?,?)";
try (PreparedStatement statement = cnx.prepareStatement(sql)){
statement.setString(1,typeCredit.getNom());
statement.setFloat(2,typeCredit.getTaux());
statement.executeUpdate();

}catch (SQLException e) {
    throw e;
}
    }
    @Override
    public void update(TypeCredit typeCredit)throws SQLException{
        String sql = "UPDATE `typecredit` SET `nom`=?,`taux`=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)){
            ps.setString(1,typeCredit.getNom());
            ps.setFloat(2,typeCredit.getTaux());
            ps.setInt(3,typeCredit.getId());
            ps.executeUpdate();
        }
    }
    @Override
    public void delete(int id)throws SQLException{
        String sql = "DELETE FROM `typecredit` WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        }
    }
    @Override
    public List<TypeCredit> read()throws SQLException{
        String sql =  "SELECT * FROM `typecredit` ";
        List<TypeCredit>typeCredits = new ArrayList<>();
        try(Statement statement = cnx.createStatement();
            ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()){
                TypeCredit typeCredit =new TypeCredit();
              typeCredit.setId(rs.getInt("id"));
              typeCredit.setNom(rs.getString("nom"));
              typeCredit.setTaux(rs.getFloat("taux"));
              typeCredits.add(typeCredit);
            }
        }
        return typeCredits;
    }

    public TypeCredit findById1(int id)throws SQLException{
        String sql =  "SELECT * FROM `typecredit` WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
        ps.setInt(1,id);
        try (ResultSet rs = ps.executeQuery()){
            if(rs.next()){
                TypeCredit typeCredit =new TypeCredit();
                typeCredit.setId(rs.getInt("id"));
                typeCredit.setNom(rs.getString("nom"));
                typeCredit.setTaux(rs.getFloat("taux"));
                return typeCredit;
            }
        }
        }
return null;
    }
    public int findTypeCreditByName(String typeName) throws SQLException {
        String sql = "SELECT id FROM TypeCredit WHERE nom = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, typeName);
          ResultSet rs = ps.executeQuery();
                if (rs.next()) {

                  return rs.getInt("id");
                }
                else {
                    throw new SQLException("Type not found for " + typeName);
                }
            }catch (SQLException e) {
            // Affichage d'un message d'erreur pour débogage
            System.out.println("Erreur SQL lors de la recherche du type de crédit : " + e.getMessage());
            // Propagation de l'exception vers l'appelant
            throw e;
        }


    }
    public float findTauxById(int typeCreditId) throws SQLException {
        String sql = "SELECT taux FROM TypeCredit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, typeCreditId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("taux");
                } else {
                    throw new SQLException("Type de crédit non trouvé pour l'identifiant : " + typeCreditId);
                }
            }
        }
    }
    public List<TypeCredit> findByType(String type) throws SQLException {
        List<TypeCredit> typecredits = new ArrayList<>();
        String sql = "SELECT * FROM typecredit WHERE nom = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TypeCredit typecredit = new TypeCredit();
                    typecredit.setId(rs.getInt("id"));
                    typecredit.setNom(rs.getString("nom"));
                    typecredit.setTaux(rs.getFloat("taux"));

                    typecredits.add(typecredit);
                }
            }
        }
        return typecredits;
    }
}
