package tn.esprit.service;


import tn.esprit.utils.SQLConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



public class StatistiqueService {

    public Map<String, Integer> getNombreComptesParType() {
        Map<String, Integer> stats = new HashMap<>();

        // Instanciation de SQLConnector pour obtenir la connexion à la base de données
        SQLConnector sqlConnector = new SQLConnector();
        try (Connection connection = sqlConnector.getConnection()) {
            String query = "SELECT type, COUNT(*) AS nombre_comptes FROM compteep GROUP BY type";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String typeCompte = resultSet.getString("type");
                    int nombreComptes = resultSet.getInt("nombre_comptes");
                    stats.put(typeCompte, nombreComptes);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stats;
    }
}