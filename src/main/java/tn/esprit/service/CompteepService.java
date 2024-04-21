package tn.esprit.service;

import tn.esprit.model.Compteep;
import tn.esprit.model.TypeTaux;
import tn.esprit.utils.SQLConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CompteepService  {
    private Connection cnx;

    public CompteepService() {
        cnx = SQLConnector.getInstance().getConnection();
    }





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
