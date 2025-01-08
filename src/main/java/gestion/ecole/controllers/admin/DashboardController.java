package gestion.ecole.controllers.admin;

import gestion.ecole.controllers.BundleAware;
import gestion.ecole.controllers.MainController;
import gestion.ecole.controllers.MainControllerAware;
import gestion.ecole.services.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController implements BundleAware{

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

    private ResourceBundle bundle;



    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.bundle = bundle;
        updateTexts();
        loadDashboardData();
    }



    private void updateTexts() {
        // Set translated static texts
        mostPopularModuleLabel.setText(dashboardService.getMostPopularModule());
        mostActiveProfessorLabel.setText(dashboardService.getMostActiveProfessor());
    }

    private void loadDashboardData() {
        // Set totals
        totalEtudiantsLabel.setText(String.valueOf(dashboardService.getTotalEtudiants()));
        totalProfesseursLabel.setText(String.valueOf(dashboardService.getTotalProfesseurs()));
        totalModulesLabel.setText(String.valueOf(dashboardService.getTotalModules()));

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
        xAxis.setLabel(bundle.getString("dashboard.chart.xAxis"));
        yAxis.setLabel(bundle.getString("dashboard.chart.yAxis"));

        // Adjust label rotation for readability
        xAxis.setTickLabelRotation(0); // Keep labels horizontal

        // Create BarChart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(bundle.getString("dashboard.chart.title"));
        barChart.setPrefHeight(250); // Match the pane height
        barChart.setPrefWidth(450);  // Match the pane width

        // Add data to chart
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
        dataSeries.setName(bundle.getString("dashboard.chart.series"));
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
}
