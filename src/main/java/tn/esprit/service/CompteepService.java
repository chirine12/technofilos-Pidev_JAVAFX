package tn.esprit.service;

import tn.esprit.model.Compteep;
import tn.esprit.model.TypeTaux;
import tn.esprit.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CompteepService implements ICRUD<Compteep> {
    private Connection cnx;

    public CompteepService() {
        cnx = SQLConnector.getInstance().getConnection();
    }


    @Override
    public void create(Compteep compteep) throws SQLException, SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Compteep (rib, solde, type, dateouv, description, etat, typetaux_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        statement.setLong(1, compteep.getRib());
        statement.setDouble(2, compteep.getSolde());
        statement.setString(3, compteep.getType());
        statement.setDate(4, compteep.getDateouv());
        statement.setString(5, compteep.getDescription());
        statement.setBoolean(6, compteep.getEtat());
        statement.setInt(7, compteep.getTypeTauxId()); // Assurez-vous que le typeTauxId est correctement passé

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

    public Compteep readCompteep(int compteepId) throws SQLException {
        String sql = "SELECT * FROM Compteep WHERE id = ?";
        PreparedStatement statement = cnx.prepareStatement(sql);
        statement.setInt(1, compteepId);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            Compteep compteep = new Compteep();

            compteep.setRib(resultSet.getLong("rib"));
            compteep.setSolde(resultSet.getDouble("solde"));
            compteep.setType(resultSet.getString("type"));
            compteep.setDateouv(resultSet.getDate("dateouv"));
            compteep.setDescription(resultSet.getString("description"));
            compteep.setEtat(resultSet.getBoolean("etat"));
            return compteep;
        } else {
            // Compteep with the given ID not found
            return null;
        }
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

    public double calculerInteret(Compteep compte, Map<Integer, TypeTaux> tauxMap) {
        TypeTaux taux = tauxMap.get(compte.getTypeTauxId());
        if (taux != null) {
            double tauxAnnuel = taux.getTaux();
            double tauxMensuel = tauxAnnuel / 12; // Utiliser directement le taux en pourcentage
            return compte.getSolde() * tauxMensuel / 100; // Calculer l'intérêt mensuel
        } else {
            return 0.0; // Aucun intérêt si aucun taux n'est trouvé
        }
    }
    public List<Compteep> findAllActive() {
        List<Compteep> activeComptes = new ArrayList<>();
        String sql = "SELECT * FROM Compteep WHERE etat = TRUE"; // Assurez-vous que la colonne `etat` est de type approprié
        try (PreparedStatement ps = cnx.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Compteep compte = new Compteep();
                compte.setId(rs.getInt("id"));
                compte.setRib(rs.getLong("rib"));
                compte.setSolde(rs.getDouble("solde"));
                compte.setType(rs.getString("type"));
                compte.setDateouv(rs.getDate("dateouv"));
                compte.setDescription(rs.getString("description"));
                compte.setEtat(rs.getBoolean("etat"));
                compte.setTypeTauxId(rs.getInt("typetaux_id"));
                activeComptes.add(compte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return activeComptes;
    }
}
