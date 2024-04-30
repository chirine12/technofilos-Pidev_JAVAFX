package tn.esprit.service;

import tn.esprit.model.TypeTaux;
import tn.esprit.model.demande_desac_ce;
import tn.esprit.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class demandesService  {
    private Connection cnx;

    public demandesService() {
        // Initialiser la connexion à la base de données
        cnx = SQLConnector.getInstance().getConnection();
    }




    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM demande_desac_ce WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }


    public List<demande_desac_ce> read() throws SQLException {
        List<demande_desac_ce> demande_desac_ceList = new ArrayList<>();
        String sql = "SELECT d.id, d.compteep_id, d.raison, c.rib,d.date" +
                "FROM demande_desac_ce d " +
                "JOIN compteep c ON d.compteep_id = c.id";
        try (Statement statement = cnx.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                demande_desac_ce demande = new demande_desac_ce();
                demande.setId(resultSet.getInt("id"));
                demande.setCompteepId(resultSet.getLong("compteep_id"));
                demande.setRaison(resultSet.getString("raison"));
                demande.setRib(resultSet.getString("rib")); // Add this line to fetch the RIB
                demande.setDate(resultSet.getDate("date")); //

                demande_desac_ceList.add(demande);
            }
        }
        return demande_desac_ceList;
    }

    public List<demande_desac_ce> readSpecificClient() throws SQLException {
        List<demande_desac_ce> demande_desac_ceList = new ArrayList<>();
        // Ajoutez une jointure avec la table compteep pour récupérer le RIB
        String sql = "SELECT d.id, d.compteep_id, d.raison, c.rib,d.date FROM demande_desac_ce d JOIN compteep c ON d.compteep_id = c.id WHERE d.client_id = 1";
        try (Statement statement = cnx.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                demande_desac_ce demande = new demande_desac_ce();
                demande.setId(resultSet.getInt("id"));
                demande.setCompteepId(resultSet.getLong("compteep_id"));
                demande.setRaison(resultSet.getString("raison"));
                demande.setRib(resultSet.getString("rib")); // Récupérez le RIB depuis le ResultSet
                demande.setDate(resultSet.getDate("date")); //

                demande_desac_ceList.add(demande);
            }
        }
        return demande_desac_ceList;
    }



}
