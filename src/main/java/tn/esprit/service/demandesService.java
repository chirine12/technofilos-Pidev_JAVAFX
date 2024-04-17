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
        String sql = "SELECT id, compteep_id, raison FROM demande_desac_ce"; // Ajout de l'ID de la demande dans la requête SQL
        try (Statement statement = cnx.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                demande_desac_ce demande_desac_ce = new demande_desac_ce();
                demande_desac_ce.setId(resultSet.getInt("id")); // Récupération de l'ID de la demande
                demande_desac_ce.setCompteepId(resultSet.getLong("compteep_id"));
                demande_desac_ce.setRaison(resultSet.getString("raison"));

                demande_desac_ceList.add(demande_desac_ce);
            }
        }
        return demande_desac_ceList;
    }


}
