package tn.esprit.service;
import java.sql.SQLException;

import tn.esprit.model.Carte;
import tn.esprit.utils.DBConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CarteService implements ICRUD <Carte> {
    private Connection cnx;


    public CarteService(){
        cnx = DBConnection.getInstance().getConnection();




    }
    @Override
    public void create(Carte carte) throws SQLException, java.sql.SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Carte (num, nom, dateexp,cvv) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = cnx.prepareStatement(sql);




        statement.setLong(1, carte.getNum());
        statement.setString(2, carte.getNom());
        statement.setDate(3, new java.sql.Date(carte.getDateexp().getTime()));
        statement.setInt(4, carte.getCvv());



        statement.executeUpdate();
    }
    @Override
    public void update(Carte carte) throws SQLException {
        String sql = "UPDATE Carte SET num = ?, nom = ?, dateexp = ?, cvv = ? WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = cnx.prepareStatement(sql);
            ps.setLong(1, carte.getNum());
            ps.setString(2, carte.getNom());
            ps.setDate(3, new java.sql.Date(carte.getDateexp().getTime()));
            ps.setInt(4, carte.getCvv());
            ps.setInt(5, carte.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Carte WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
    @Override
    public List<Carte> read() throws SQLException {
        String sql = "SELECT * FROM Carte";
        Statement statement = cnx.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        List<Carte> cartes = new ArrayList<>();
        while (rs.next()) {
            Carte carte = new Carte();
            carte.setId(rs.getInt("id"));
            carte.setNum(rs.getInt("Num"));
            carte.setNom(rs.getString("Nom"));
            carte.setDateexp(rs.getDate("dateexp"));
            carte.setCvv(rs.getInt("cvv"));
            cartes.add(carte);
        }
        return cartes;
    }

    public Carte readCarte(int carteId) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE id = ?";
        PreparedStatement statement = cnx.prepareStatement(sql);
        statement.setInt(1, carteId);


        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Carte carte = new Carte();
            carte.setId(resultSet.getInt("id"));
            carte.setNum(resultSet.getInt("Num"));
            carte.setNom(resultSet.getString("Nom"));
            carte.setDateexp(resultSet.getDate("dateexp"));
            carte.setCvv(resultSet.getInt("cvv"));


            return carte;
        } else {
            // Virement with the given ID not found
            return null;
        }
    }

    public Carte findById(int id) throws SQLException {
        String sql = "SELECT * FROM Carte WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Carte carte = new Carte();
                    carte.setId(rs.getInt("id"));
                    carte.setNum(rs.getInt("Num"));
                    carte.setNom(rs.getString("Nom"));
                    carte.setDateexp(rs.getDate("dateexp"));
                    carte.setCvv(rs.getInt("cvv"));
                    return carte;
                }
            }
        }
        return null; // Retourne null si aucun crédit n'est trouvé avec cet identifiant
    }

}


