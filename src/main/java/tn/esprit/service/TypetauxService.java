package tn.esprit.service;

import tn.esprit.model.Compteep;
import tn.esprit.model.TypeTaux;
import tn.esprit.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TypetauxService implements ICRUD<TypeTaux> {

    private Connection cnx;

    public TypetauxService() {
        // Initialiser la connexion à la base de données
        cnx = SQLConnector.getInstance().getConnection();
    }

    @Override
    public void create(TypeTaux typetaux) throws SQLException, SQLIntegrityConstraintViolationException {
        String sql = "INSERT INTO Typetaux (type, taux) VALUES (?, ?)";
        PreparedStatement statement = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        statement.setString(1, typetaux.getType());
        statement.setDouble(2,typetaux.getTaux());


        statement.executeUpdate();

        // Obtenez l'ID généré automatiquement
        ResultSet generatedKeys = statement.getGeneratedKeys();
        if (generatedKeys.next()) {
            int id = generatedKeys.getInt(1);
            typetaux.setId(id); // Mettre à jour l'ID dans l'objet compteep
        } else {
            // Gérer l'erreur, si nécessaire
            throw new SQLException("Échec de la récupération de l'ID auto-incrémenté.");
        }
    }

    @Override
    public void update(TypeTaux typetaux) throws SQLException {
        String sql = "UPDATE Typetaux SET type= ?, taux = ? WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = cnx.prepareStatement(sql);
            ps.setString(1, typetaux.getType());
            ps.setDouble(2, typetaux.getTaux());

            ps.setInt(3, typetaux.getId());
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM TypeTaux WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Override
    public List<TypeTaux> read() throws SQLException {
        List<TypeTaux> typeTauxList = new ArrayList<>();
        String sql = "SELECT * FROM TypeTaux";
        try (Statement statement = cnx.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                TypeTaux typeTaux = new TypeTaux();
                typeTaux.setId(resultSet.getInt("id"));
                typeTaux.setType(resultSet.getString("type"));
                typeTaux.setTaux(resultSet.getDouble("taux"));
                typeTauxList.add(typeTaux);
            }
        }
        return typeTauxList;
    }
    public boolean validateDescription(String type) {
        // Vérifier si la description est vide ou null
        if (type== null || type.isEmpty()) {
            return false;
        }

        // Vérifier si la longueur de la description est supérieure à 10 caractères
        if (type.length() > 10) {
            return false;
        }

        // Vérifier si la description contient des chiffres
        if (Pattern.compile("[0-9]").matcher(type).find()) {
            return false;
        }

        // La description est valide
        return true;
    }


    public boolean validateTaux(String tauxStr) {
        try {
            double taux = Double.parseDouble(tauxStr);
            // Vérifier si le taux est valide (par exemple, s'il est positif)
            return taux >= 0; // Modifier selon vos critères de validation
        } catch (NumberFormatException e) {
            // La chaîne n'est pas un nombre valide
            return false;
        }
    }
    public int findIdByType(String type) throws SQLException {
        String query = "SELECT id FROM TypeTaux WHERE type = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Type not found for " + type);
            }
        }
    }
    public TypeTaux findById(int id) {
        String sql = "SELECT * FROM TypeTaux WHERE id = ?";
        TypeTaux typeTaux = null;
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    typeTaux = new TypeTaux();
                    typeTaux.setId(rs.getInt("id"));
                    typeTaux.setTaux(rs.getDouble("taux"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return typeTaux;
    }

}
