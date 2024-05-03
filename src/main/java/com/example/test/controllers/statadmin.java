package com.example.test.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.test.service.VirementService;
import com.example.test.service.VirementService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;

public class statadmin {
    @FXML
    private BarChart<String, Number> barChart; // Assurez-vous que ce BarChart est bien lié dans votre FXML avec fx:id="barChart"


    private VirementService virementService = new VirementService();


    @FXML
    void afficherStatistiques(ActionEvent event) {
        barChart.getData().clear(); // Clear previous data
        try {
            Map<Integer, Integer> stats = virementService.getVirementsByDestinataire();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre de Virements par Client");

            stats.forEach((destinataire, count) -> {
                series.getData().add(new XYChart.Data<>(String.valueOf(destinataire), count));
            });

            barChart.getData().add(series);
        } catch (SQLException e) {
            showAlertWithError("Erreur SQL", "Erreur lors de la récupération des statistiques : " + e.getMessage());
        }
    }

    public void initializeStatistics() {
        afficherStatistiques(null); // Assuming afficherStatistiques doesn't strictly require the ActionEvent parameter
    }


    private void showAlertWithError(String title, String message) {
        // Implémentez cette méthode pour afficher les erreurs
    }
}
