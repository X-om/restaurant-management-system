package com.rms.rms;


import com.rms.rms.database.DatabaseManager;
import com.rms.rms.models.OrderDetail;
import com.rms.rms.models.OrderHistory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OrderController implements Initializable {
    @FXML
    private VBox ordersContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ordersContainer.setFillWidth(true);
        ordersContainer.setMaxWidth(Double.MAX_VALUE);

        loadOrders();
    }


    private void loadOrders(){
        try{
            List<OrderHistory> orders = DatabaseManager.getOrderHistory();
            for(OrderHistory order : orders){
                HBox orderCard = createOrderCard(order);
                orderCard.setMaxWidth(Double.MAX_VALUE);
                ordersContainer.getChildren().add(orderCard);
            }
        } catch (SQLException e){
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load orders. Please try again.");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            ordersContainer.getChildren().add(errorLabel);
        }
    }

    private HBox createOrderCard(OrderHistory order) {
        HBox orderBox = new HBox();
        orderBox.setAlignment(Pos.CENTER);
        orderBox.setPrefHeight(100.0);
        orderBox.setMaxWidth(Double.MAX_VALUE); // Critical
        orderBox.setSpacing(20); // Reduced spacing for better proportions
        orderBox.setStyle("-fx-background-color: #D9D9D9; -fx-background-radius: 10px;");

        // Make HBox fill available width
        HBox.setHgrow(orderBox, Priority.ALWAYS);

        // Margins
        VBox.setMargin(orderBox, new Insets(5, 20, 5, 20));

        // Create stretchable labels
        Label orderNoLabel = createStretchableLabel("#" + order.getOrderId());
        Label tableNoLabel = createStretchableLabel("Table " + order.getTableNumber());
        Label dateLabel = createStretchableLabel(
                order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        );
        Label totalPriceLabel = createStretchableLabel(String.format("$%.2f", order.getTotalPrice()));

        // Configure button
        Button infoButton = new Button("Show more info");
        infoButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(infoButton, Priority.ALWAYS);
        infoButton.setStyle("-fx-font-size: 18; -fx-pref-height: 42;");
        infoButton.setOnAction(event -> {
            try {
                showOrderDetails(order);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        // Add components
        orderBox.getChildren().addAll(
                orderNoLabel,
                tableNoLabel,
                dateLabel,
                totalPriceLabel,
                infoButton
        );

        return orderBox;
    }

    private Label createStretchableLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Hoefler Text Black", 24));
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private void showOrderDetails(OrderHistory order) throws SQLException {
        try {
            List<OrderDetail> details = DatabaseManager.getOrderDetails(order.getOrderId());

            // Create new stage
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Order Details");

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("order-details.fxml"));
            VBox root = loader.load();

            // Get controller and initialize
            OrderDetailsController controller = loader.getController();
            controller.initialize(order, details, dialog);

            // Set scene
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.setMinWidth(700);
            dialog.setMinHeight(500);

            // Show centered on main window
            dialog.initOwner(ordersContainer.getScene().getWindow());
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load order details view");
        }
    }

    public void refreshOrders() {
        loadOrders();
    }


}



