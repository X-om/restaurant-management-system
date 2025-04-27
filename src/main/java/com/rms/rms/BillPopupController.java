package com.rms.rms;

import com.rms.rms.models.OrderDetail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import com.rms.rms.database.DatabaseManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class BillPopupController {
    @FXML private TableView<OrderDetail> billTable;
    @FXML
    private Label grandTotalLabel;

    private Stage stage;
    private int tableNumber;



    public void initialize(int tableNumber, Stage stage) {
        this.tableNumber = tableNumber;
        this.stage = stage;
        List<OrderDetail> details= loadBillData();
        if (details.isEmpty()) {
            showError("No active order found for this table");
            stage.close();
        }
    }

    private List<OrderDetail> loadBillData() {
        try {
            List<OrderDetail> details = DatabaseManager.getActiveOrderDetails(tableNumber);
            if (!details.isEmpty()) {
                billTable.getItems().setAll(details);
                double total = details.stream()
                        .mapToDouble(OrderDetail::getTotalPrice)
                        .sum();
                grandTotalLabel.setText(String.format("₹%.2f", total));
            }
            return details;
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load bill data");
            return Collections.emptyList();
        }
    }

    @FXML
    private void handlePrint() {
        // Implement printing logic here
        try {
            // Mark order as completed
            DatabaseManager.completeOrder(tableNumber);

            // Print logic would go here
            System.out.println("Printing bill for table " + tableNumber);

            // Close the window
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to complete order");
        }
    }

    @FXML
    private void handleClose() {
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
