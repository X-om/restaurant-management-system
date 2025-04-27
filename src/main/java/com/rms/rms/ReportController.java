package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ReportController {
    @FXML private VBox card1, card2, card3, card4;
    @FXML private PieChart categorySalesChart;
    @FXML private BarChart<String, Number> dailyOrdersChart;
    @FXML private StackedBarChart<String, Number> tableStatusChart;
    @FXML private BarChart<String, Number> topItemsChart;

    @FXML private CategoryAxis dailyOrdersXAxis, tableStatusXAxis, topItemsXAxis;
    @FXML private NumberAxis dailyOrdersYAxis, tableStatusYAxis, topItemsYAxis;

    private Connection connection;

    @FXML
    public void initialize() {
        try {
            connection = DatabaseManager.getConnection();
            setupCharts();
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Could not load dashboard data");
        }
    }

    private void setupCharts() {
        // Configure chart appearances
        categorySalesChart.setTitle("Sales Distribution by Category");
        categorySalesChart.setLabelsVisible(true);

        dailyOrdersChart.setTitle("Orders Last 7 Days");
        dailyOrdersXAxis.setLabel("Day");
        dailyOrdersYAxis.setLabel("Number of Orders");

        tableStatusChart.setTitle("Table Occupancy Status");
        tableStatusXAxis.setLabel("Status");
        tableStatusYAxis.setLabel("Count");

        topItemsChart.setTitle("Top Selling Menu Items");
        topItemsXAxis.setLabel("Menu Item");
        topItemsYAxis.setLabel("Quantity Sold");
    }

    private void loadData() throws SQLException {
        loadCategorySalesData();
        loadDailyOrdersData();
        loadTableStatusData();
        loadTopItemsData();
    }

    private void loadCategorySalesData() throws SQLException {
        String query = "SELECT m.foodSection, SUM(oi.item_price * oi.quantity) as total " +
                "FROM order_items oi " +
                "JOIN menu m ON oi.menu_item_id = m.id " +
                "GROUP BY m.foodSection";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
            while (rs.next()) {
                pieData.add(new PieChart.Data(
                        rs.getString("foodSection"),
                        rs.getDouble("total")
                ));
            }
            categorySalesChart.setData(pieData);
        }
    }

    private void loadDailyOrdersData() throws SQLException {
        String query = "SELECT DATE(order_date) as day, COUNT(*) as count " +
                "FROM orders " +
                "WHERE order_date >= CURRENT_DATE - INTERVAL '7 days' " +
                "GROUP BY DATE(order_date) " +
                "ORDER BY day";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Orders");

        Map<LocalDate, Integer> dailyOrders = new HashMap<>();
        // Initialize with 0 for all days
        for (int i = 6; i >= 0; i--) {
            dailyOrders.put(LocalDate.now().minusDays(i), 0);
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LocalDate day = rs.getDate("day").toLocalDate();
                int count = rs.getInt("count");
                dailyOrders.put(day, count);
            }
        }

        // Add to chart
        for (Map.Entry<LocalDate, Integer> entry : dailyOrders.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().toString(), entry.getValue()));
        }

        dailyOrdersChart.getData().add(series);
    }

    private void loadTableStatusData() throws SQLException {
        String query = "SELECT status, COUNT(*) as count FROM tables GROUP BY status";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tables");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        rs.getString("status"),
                        rs.getInt("count")
                ));
            }
        }

        tableStatusChart.getData().add(series);
    }

    private void loadTopItemsData() throws SQLException {
        String query = "SELECT m.foodName, SUM(oi.quantity) as total " +
                "FROM order_items oi " +
                "JOIN menu m ON oi.menu_item_id = m.id " +
                "GROUP BY m.foodName " +
                "ORDER BY total DESC " +
                "LIMIT 5";

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantity Sold");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                series.getData().add(new XYChart.Data<>(
                        rs.getString("foodName"),
                        rs.getInt("total")
                ));
            }
        }

        topItemsChart.getData().add(series);
    }

    private void showErrorAlert(String title, String message) {
        // Implement your alert dialog here
        System.err.println(title + ": " + message);
    }
}
