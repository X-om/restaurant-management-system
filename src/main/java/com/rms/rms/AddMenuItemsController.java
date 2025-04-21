package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddMenuItemsController {
    @FXML private MenuButton sectionDropDown;
    @FXML private TextField foodName;
    @FXML private TextField foodPrice;

    @FXML private Label successMessage;
    @FXML private Stage stage;

    private String selectedSection = "Main Course";

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Initialize section dropdown
        for (MenuItem item : sectionDropDown.getItems()) {
            item.setOnAction(e -> {
                selectedSection = item.getText();
                sectionDropDown.setText(selectedSection);
            });
        }


        sectionDropDown.setText(selectedSection);

    }

    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void addMenuItemToDB() {
        String name = foodName.getText().trim();
        // Extract numeric value safely
        String priceText = foodPrice.getText().replaceAll("[^\\d.]", "").trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert("Error", "Please fill all fields", Alert.AlertType.ERROR);
            return;
        }

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                showAlert("Error", "Price must be positive", Alert.AlertType.ERROR);
                return;
            }

            // Create async task
            Task<Boolean> insertTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    return DatabaseManager.insertMenuItem(name, selectedSection, price) > 0;
                }
            };

            // Set success handler
            insertTask.setOnSucceeded(e -> {
                boolean success = insertTask.getValue();
                if (success) {
                    successMessage.setText("Item added successfully!");
                    successMessage.setVisible(true);

                    // Clear form after delay
                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            Platform.runLater(() -> {
                                foodName.clear();
                                foodPrice.clear();
                                successMessage.setVisible(false);
                            });
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                } else {
                    showAlert("Error", "Failed to add menu item", Alert.AlertType.ERROR);
                }
            });

            // Set failure handler
            insertTask.setOnFailed(e -> {
                Throwable ex = insertTask.getException();
                showAlert("Database Error", ex.getMessage(), Alert.AlertType.ERROR);
            });

            // Execute task in background thread
            new Thread(insertTask).start();

        } catch (NumberFormatException e) {
            showAlert("Invalid Price", "Please enter a valid number", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}