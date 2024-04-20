
    package com.example.test.service;

import com.example.test.model.Beneficiaire;
import com.example.test.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class BeneficiaireService implements ICRUD<Beneficiaire> {
        private Connection cnx;

        public BeneficiaireService() {
            cnx = SQLConnector.getInstance().getConnection();
        }

        @Override
        public void create(Beneficiaire beneficiaire, int clientId) throws SQLException {
            String sql = "INSERT INTO Beneficiaire (nom, prenom, rib, client_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setString(1, beneficiaire.getNom());
                statement.setString(2, beneficiaire.getPrenom());
                statement.setLong(3, beneficiaire.getRib());
                statement.setInt(4, clientId);
                statement.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException e) {
                // Cette exception est spécifiquement lancée pour une contrainte d'unicité violée
                throw new SQLException("Le RIB fourni existe déjà dans la base de données.");
            } catch (SQLException e) {
                // Autres exceptions SQL
                throw e;
            }
        }




        @Override
        public void update(Beneficiaire beneficiaire) throws SQLException {
            String sql = "UPDATE Beneficiaire SET nom = ?, prenom = ?, rib = ? WHERE id = ?";
            PreparedStatement ps = null;
            try {
                ps = cnx.prepareStatement(sql);
                ps.setString(1, beneficiaire.getNom());
                ps.setString(2, beneficiaire.getPrenom());
                ps.setLong(3, beneficiaire.getRib());
                ps.setInt(4, beneficiaire.getId());

                ps.executeUpdate();
            } finally {
                if (ps != null) {
                    ps.close();
                }
            }
        }

        @Override
        public void delete(int id) throws SQLException {
            String sql = "DELETE FROM Beneficiaire WHERE id = ?";
            try (PreparedStatement preparedStatement = cnx.prepareStatement(sql)) {
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
            }
        }

        @Override
        public List<Beneficiaire> read(int clientId) throws SQLException {
            String sql = "SELECT * FROM Beneficiaire WHERE client_id=?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, clientId);

                // Exécutez la requête après avoir défini les paramètres
                try (ResultSet rs = statement.executeQuery()) {
                    List<Beneficiaire> beneficiaires = new ArrayList<>();
                    while (rs.next()) {
                        Beneficiaire beneficiaire = new Beneficiaire();
                        beneficiaire.setId(rs.getInt("id"));
                        beneficiaire.setNom(rs.getString("nom"));
                        beneficiaire.setPrenom(rs.getString("prenom"));
                        beneficiaire.setRib(rs.getLong("rib"));
                        beneficiaires.add(beneficiaire);
                    }
                    return beneficiaires;
                }
            }
        }

        public Beneficiaire readBeneficiaire(int beneficiaireId) throws SQLException {
            String sql = "SELECT * FROM Beneficiaire WHERE id = ?";
            try (PreparedStatement statement = cnx.prepareStatement(sql)) {
                statement.setInt(1, beneficiaireId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        Beneficiaire beneficiaire = new Beneficiaire();
                        beneficiaire.setId(resultSet.getInt("id"));
                        beneficiaire.setNom(resultSet.getString("nom"));
                        beneficiaire.setPrenom(resultSet.getString("prenom"));
                        beneficiaire.setRib(resultSet.getLong("rib"));

                        return beneficiaire;
                    } else {
                        // Beneficiaire with the given ID not found
                        return null;
                    }
                }
            }
        }
    }


