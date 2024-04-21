package tn.esprit.service;

import tn.esprit.model.Compteep;
import tn.esprit.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ClientCompteepService  implements ICRUD<Compteep>  {
        private Connection cnx;

        public ClientCompteepService () {
            cnx = SQLConnector.getInstance().getConnection();
        }


    @Override
    public void create(Compteep compteep) throws SQLException, SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Compteep (rib, solde, type, dateouv, description, etat, typetaux_id, client_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        statement.setLong(1, compteep.getRib());
        statement.setDouble(2, compteep.getSolde());
        statement.setString(3, compteep.getType());
        statement.setDate(4, compteep.getDateouv());
        statement.setString(5, compteep.getDescription());
        statement.setBoolean(6, compteep.getEtat());
        statement.setInt(7, compteep.getTypeTauxId()); // Assurez-vous que le typeTauxId est correctement passé
        statement.setInt(8, 1); // client_id = 2

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating compteep failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                compteep.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating compteep failed, no ID obtained.");
            }
        }
    }



    @Override
        public void update(Compteep compteep) throws SQLException {
            String sql = "UPDATE Compteep SET rib = ?, solde = ?, type = ?, dateouv = ?, description = ?, etat = ? WHERE id = ?";
            PreparedStatement ps = null;
            try {
                ps = cnx.prepareStatement(sql);
                ps.setLong(1, compteep.getRib());
                ps.setDouble(2, compteep.getSolde());
                ps.setString(3, compteep.getType());
                ps.setDate(4, compteep.getDateouv());
                ps.setString(5, compteep.getDescription());
                ps.setBoolean(6, compteep.getEtat());
                ps.setInt(7, compteep.getId());
                ps.executeUpdate();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }

        @Override
        public void delete(int id) throws SQLException {
            String sql = "DELETE FROM Compteep WHERE id = ?";
            try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
        }

        @Override
        public List<Compteep> read() throws SQLException {
            String sql = "SELECT * FROM Compteep";
            Statement statement = cnx.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            List<Compteep> compteeps = new ArrayList<>();
            while (rs.next()) {
                Compteep compteep = new Compteep();

                compteep.setRib(rs.getLong("rib"));
                compteep.setSolde(rs.getDouble("solde"));
                compteep.setType(rs.getString("type"));
                compteep.setDateouv(rs.getDate("dateouv"));
                compteep.setDescription(rs.getString("description"));
                compteep.setEtat(rs.getBoolean("etat"));
                compteeps.add(compteep);
            }
            return compteeps;
        }
    public List<Compteep> readC(int clientId) throws SQLException {
        String sql = "SELECT id, rib, solde, type, dateouv, description, etat FROM Compteep WHERE client_id = ? AND etat = TRUE";
        PreparedStatement statement = cnx.prepareStatement(sql);
        statement.setInt(1, clientId);

        ResultSet rs = statement.executeQuery();
        List<Compteep> compteeps = new ArrayList<>();
        while (rs.next()) {
            Compteep compteep = new Compteep();

            compteep.setId(rs.getInt("id")); // Récupération de l'ID depuis la base de données
            compteep.setRib(rs.getLong("rib"));
            compteep.setSolde(rs.getDouble("solde"));
            compteep.setType(rs.getString("type"));
            compteep.setDateouv(rs.getDate("dateouv"));
            compteep.setDescription(rs.getString("description"));
            compteep.setEtat(rs.getBoolean("etat"));
            compteeps.add(compteep);
        }
        return compteeps;
    }





        public boolean validateDescription(String description) {
            // Vérifier si la description est vide ou null
            if (description == null || description.isEmpty()) {
                return false;
            }

            // Vérifier si la longueur de la description est supérieure à 10 caractères
            if (description.length() > 10) {
                return false;
            }

            // Vérifier si la description contient des chiffres
            if (Pattern.compile("[0-9]").matcher(description).find()) {
                return false;
            }

            // La description est valide
            return true;
        }



    }


