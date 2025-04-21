package com.rms.rms;

import com.rms.rms.models.OrderDetail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import com.rms.rms.database.DatabaseManager;

import java.sql.SQLException;
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
        loadBillData();
    }

    private void loadBillData() {
        try {

            List<OrderDetail> details = DatabaseManager.getOrderDetails(tableNumber);
            billTable.getItems().setAll(details);

            // Calculate grand total
            double total = details.stream()
                    .mapToDouble(OrderDetail::getTotalPrice)
                    .sum();
            grandTotalLabel.setText(String.format("₹%.2f", total));

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Failed to load bill data");
        }
    }

    @FXML
    private void handlePrint() {
        // Implement printing logic here
        System.out.println("Printing bill for table " + tableNumber);
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
