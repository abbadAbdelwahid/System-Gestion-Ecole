package gestion.ecole.controllers;

import gestion.ecole.services.DashboardService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController() {
        this.dashboardService = new DashboardService();
    }

    @FXML
    private Label totalEtudiantsLabel;

    @FXML
    private Label totalProfesseursLabel;

    @FXML
    private Label totalModulesLabel;

    @FXML
    private Label mostPopularModuleLabel;

    @FXML
    private Label mostActiveProfessorLabel;

    @FXML
    private Pane graphPane;

    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        // Set totals
        totalEtudiantsLabel.setText(String.valueOf(dashboardService.getTotalEtudiants()));
        totalProfesseursLabel.setText(String.valueOf(dashboardService.getTotalProfesseurs()));
        totalModulesLabel.setText(String.valueOf(dashboardService.getTotalModules()));

        // Set most popular module and most active professor
        mostPopularModuleLabel.setText(dashboardService.getMostPopularModule());
        mostActiveProfessorLabel.setText(dashboardService.getMostActiveProfessor());

        // Generate and display chart
        displayModuleStatisticsChart();
    }

    private void displayModuleStatisticsChart() {
        // Fetch data from the database
        Map<String, Integer> moduleStatistics = dashboardService.getModuleStatistics();

        // Modify module names to truncate if too long
        Map<String, Integer> formattedModuleStatistics = new HashMap<>();
        moduleStatistics.forEach((module, count) -> {
            String formattedName = module.length() > 12 ? module.substring(0, 12) + "..." : module;
            formattedModuleStatistics.put(formattedName, count);
        });

        // Set up axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Modules");
        yAxis.setLabel("Nombre d'étudiants");

        // Adjust label rotation for readability
        xAxis.setTickLabelRotation(0); // Keep labels horizontal

        // Create BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statistiques des Modules");
        barChart.setPrefHeight(250); // Match the pane height
        barChart.setPrefWidth(450);  // Match the pane width

        // Add data to chart
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName("Étudiants par module");
        formattedModuleStatistics.forEach((module, count) -> {
            dataSeries.getData().add(new XYChart.Data<>(module, count));
        });

        barChart.getData().add(dataSeries);

        // Remove the legend if it's not needed
        barChart.setLegendVisible(false);

        // Clear and add the chart to the Pane
        graphPane.getChildren().clear();
        graphPane.getChildren().add(barChart);
    }












//    private void displayModuleStatisticsChart() {
//        // Dummy data for the chart
//        Map<String, Integer> moduleStatistics = new HashMap<>();
//        moduleStatistics.put("Mathématiques", 30);
//        moduleStatistics.put("Physique", 25);
//        moduleStatistics.put("Informatique", 35);
//        moduleStatistics.put("Biologie", 20);
//        moduleStatistics.put("Chimie", 15);
//
//        // Set up axes
//        CategoryAxis xAxis = new CategoryAxis();
//        NumberAxis yAxis = new NumberAxis();
//        xAxis.setLabel("Modules");
//        yAxis.setLabel("Nombre d'étudiants");
//
//        // Set category axis to show labels horizontally
//        xAxis.setTickLabelRotation(0); // Ensure labels are horizontal
//
//        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
//        barChart.setTitle("Statistiques des Modules");
//
//        // Add data to chart
//        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
//        dataSeries.setName("Étudiants par module");
//        moduleStatistics.forEach((module, count) -> {
//            dataSeries.getData().add(new XYChart.Data<>(module, count));
//        });
//
//        barChart.getData().add(dataSeries);
//
//        // Resize chart to fit the Pane
//        barChart.setPrefWidth(graphPane.getPrefWidth());
//        barChart.setPrefHeight(graphPane.getPrefHeight());
//
//        // Clear and add the chart to the Pane
//        graphPane.getChildren().clear();
//        graphPane.getChildren().add(barChart);
//    }



}
