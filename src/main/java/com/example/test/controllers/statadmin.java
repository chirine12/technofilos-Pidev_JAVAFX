package com.example.test.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import com.example.test.model.Virement;
import com.example.test.service.VirementService;

import java.sql.SQLException;
import java.util.Map;

public class statadmin {

    @FXML
    private BarChart<String, Number> barChart;

    private VirementService virementService;

    public statadmin() {
        virementService = new VirementService();
    }

    @FXML
    private void initialize() {
        loadChartData();
    }

    public void loadChartData() {
        try {
            Map<Long, Integer> data = virementService.getVirementCountByClient();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Virements par Client");

            for (Map.Entry<Long, Integer> entry : data.entrySet()) {
                series.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
            }

            barChart.getData().add(series);
        } catch (SQLException e) {
            // Log the error or show an alert dialog
            System.out.println("Erreur lors de la récupération des données: " + e.getMessage());
        }
    }
}
