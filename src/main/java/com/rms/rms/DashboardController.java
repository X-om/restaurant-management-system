package com.rms.rms;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.util.Duration;
import java.io.IOException;

public class DashboardController {
    @FXML private VBox drawer;
    @FXML private StackPane contentArea;
    @FXML private Label dashboardLabel, settingsLabel, reportLabel, historyLabel, logoutLabel;

    private boolean isDrawerExpanded = false;
    private static final double DRAWER_MIN_WIDTH = 44;  // Just enough for 40px icon + 4px padding
    private static final double DRAWER_MAX_WIDTH = 300; // Expanded width

    @FXML
    private void initialize() {
        // Set initial state
        drawer.setPrefWidth(DRAWER_MIN_WIDTH);
        setLabelsVisibility(false);
    }

    @FXML
    private void toggleDrawer() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(250), drawer);

        if (isDrawerExpanded) {
            // Collapse drawer
            drawer.setPrefWidth(DRAWER_MIN_WIDTH);
            setLabelsVisibility(false);
        } else {
            // Expand drawer
            drawer.setPrefWidth(DRAWER_MAX_WIDTH);
            setLabelsVisibility(true);
        }

        isDrawerExpanded = !isDrawerExpanded;
    }

    private void setLabelsVisibility(boolean visible) {
        dashboardLabel.setVisible(visible);
        settingsLabel.setVisible(visible);
        reportLabel.setVisible(visible);
        historyLabel.setVisible(visible);
        logoutLabel.setVisible(visible);
    }

    private void loadContent(String fxml) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(fxml));
            contentArea.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToDashboard() {
        loadContent("dashboard-content.fxml");
    }

    @FXML
    private void navigateToSettings() {
        loadContent("settings-content.fxml");
    }

    @FXML
    private void navigateToReport() {
        loadContent("report-content.fxml");
    }

    @FXML
    private void navigateToHistory() {
        loadContent("history-content.fxml");
    }

    @FXML
    private void handleLogout() {
        // Implement logout logic
    }
}