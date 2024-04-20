package tn.esprit.test;

import tn.esprit.model.Carte;
import tn.esprit.service.CarteService;
import tn.esprit.utils.DBConnection;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Main {
    public static void main(String[] args) {
        // Get DBConnection instance
        DBConnection db1 = DBConnection.getInstance();

        // Create SimpleDateFormat instance for date parsing
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            // Parse the string date to java.util.Date
            java.util.Date utilDate = dateFormat.parse("12/02/2024");

            // Convert java.util.Date to java.sql.Date
            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

            // Create Carte object with the parsed date
            Carte c1 = new Carte(15,123,"hello", sqlDate, 165); // Assuming constructor parameters are (int id, int num, String nom, Date dateexp)
            Carte carte1 = new Carte(19, 123, "bonjour", sqlDate, 123);
            Carte carte2 = new Carte(16, 456, "aslema", sqlDate, 456);

            // Create CarteService instance
            CarteService cs1 = new CarteService();

            // Perform operations
            cs1.create(carte1);
            cs1.create(carte2);

            // Update carte2
            carte2.setNom("newNom");
            cs1.update(carte2);

            // Delete carte1
            cs1.delete(15);

            // Read and print cartes
            cs1.read().forEach(System.out::println);
            // Create CarteService instance
            CarteService css1 = new CarteService();

            // Now you can use c1 and cs1 as needed
        } catch (ParseException  | SQLException e) {
            e.printStackTrace(); // Handle parsing exception or SQL exception
        }

    }
}
