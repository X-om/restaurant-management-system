package com.rms.rms;

import com.rms.rms.models.OrderDetail;
import com.rms.rms.models.OrderHistory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class OrderDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label orderIdLabel;
    @FXML private Label tableLabel;
    @FXML private Label totalLabel;
    @FXML private Label dateLabel;
    @FXML private TableView<OrderDetail> itemsTable;

    private Stage stage;

    public void initialize(OrderHistory order, List<OrderDetail> details, Stage stage) {
        this.stage = stage;

        // Set header information
        titleLabel.setText("Order Details - #" + order.getOrderId());
        orderIdLabel.setText("Order ID: #" + order.getOrderId());
        tableLabel.setText("Table: " + order.getTableNumber());
        totalLabel.setText(String.format("Total: ₹%.2f", order.getTotalPrice()));
        dateLabel.setText("Date: " + order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

        // Configure table
        itemsTable.getItems().setAll(details);
        // Format price columns
        formatPriceColumn((TableColumn<OrderDetail, Number>) itemsTable.getColumns().get(3));
        formatPriceColumn((TableColumn<OrderDetail, Number>) itemsTable.getColumns().get(4));
    }

    private void formatPriceColumn(TableColumn<OrderDetail, Number> column) {
        column.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", price.doubleValue()));
                }
            }
        });
    }

    @FXML
    private void handleClose() {
        stage.close();
    }
}
