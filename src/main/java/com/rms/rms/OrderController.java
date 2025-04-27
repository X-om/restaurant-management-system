package com.rms.rms;


import com.rms.rms.database.DatabaseManager;
import com.rms.rms.models.OrderDetail;
import com.rms.rms.models.OrderHistory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
        // Main card container
        HBox orderBox = new HBox();
        orderBox.setAlignment(Pos.CENTER_LEFT);
        orderBox.setPrefHeight(100.0);
        orderBox.setMaxWidth(Double.MAX_VALUE);
        orderBox.setPadding(new Insets(15, 25, 15, 25));
        orderBox.setSpacing(30);
        orderBox.getStyleClass().add("order-card");

        // Apply drop shadow effect for depth
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        orderBox.setEffect(shadow);

        // Make HBox fill available width
        HBox.setHgrow(orderBox, Priority.ALWAYS);

        // Margins for spacing between cards
        VBox.setMargin(orderBox, new Insets(12, 20, 12, 20));

        // Order ID with circle background
        StackPane orderIdContainer = createCircularLabel("#" + order.getOrderId());
        orderIdContainer.setPrefWidth(80);
        orderIdContainer.setMinWidth(80);

        // Information columns with equal width distribution
        VBox tableInfoBox = createInfoColumn("Table", "Table " + order.getTableNumber());
        VBox dateInfoBox = createInfoColumn("Date",
                order.getOrderDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        VBox timeInfoBox = createInfoColumn("Time",
                order.getOrderDate().format(DateTimeFormatter.ofPattern("HH:mm")));
        VBox priceInfoBox = createInfoColumn("Total", String.format("$%.2f", order.getTotalPrice()));

        // Set equal growth for all info columns
        HBox.setHgrow(tableInfoBox, Priority.ALWAYS);
        HBox.setHgrow(dateInfoBox, Priority.ALWAYS);
        HBox.setHgrow(timeInfoBox, Priority.ALWAYS);
        HBox.setHgrow(priceInfoBox, Priority.ALWAYS);

        // Status indicator
        String status = determineOrderStatus(order);
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("status-label");
        statusLabel.getStyleClass().add(status.toLowerCase().replace(" ", "-"));
        statusLabel.setPrefWidth(120);
        statusLabel.setMinWidth(120);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Action button with icon
        Button infoButton = new Button("Details");
        infoButton.getStyleClass().add("info-button");
        infoButton.setGraphic(createInfoIcon());
        infoButton.setContentDisplay(ContentDisplay.RIGHT);
        infoButton.setPrefWidth(120);
        infoButton.setMinWidth(120);
        infoButton.setOnAction(event -> {
            try {
                showOrderDetails(order);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Add all components
        orderBox.getChildren().addAll(
                orderIdContainer,
                tableInfoBox,
                dateInfoBox,
                timeInfoBox,
                priceInfoBox,
                statusLabel,
                infoButton
        );

        // Add hover effect
        orderBox.setOnMouseEntered(e -> {
            orderBox.getStyleClass().add("order-card-hover");
        });

        orderBox.setOnMouseExited(e -> {
            orderBox.getStyleClass().remove("order-card-hover");
        });

        return orderBox;
    }

    // Helper method to create circular label for order ID
    private StackPane createCircularLabel(String text) {
        StackPane container = new StackPane();
        container.setMinSize(60, 60);
        container.setMaxSize(60, 60);

        Circle circle = new Circle(30);
        circle.setFill(Color.web("#4D3F76"));

        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.font("System", FontWeight.BOLD, 16));

        container.getChildren().addAll(circle, label);
        return container;
    }

    // Helper method to create information columns with title and value
    private VBox createInfoColumn(String title, String value) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.NORMAL, 12));
        titleLabel.setTextFill(Color.web("#666666"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        valueLabel.setTextFill(Color.web("#333333"));

        container.getChildren().addAll(titleLabel, valueLabel);
        return container;
    }

    // Helper method to create an info icon
    private Node createInfoIcon() {
        // Create a text-based icon using standard JavaFX
        Text infoIcon = new Text("i");
        infoIcon.setFont(Font.font("System", FontWeight.BOLD, 14));
        infoIcon.setFill(Color.WHITE);

        // Create a circle background
        Circle background = new Circle(8);
        background.setFill(Color.TRANSPARENT);
        background.setStroke(Color.WHITE);
        background.setStrokeWidth(1.5);

        // Stack them together
        StackPane iconContainer = new StackPane(background, infoIcon);
        iconContainer.setPadding(new Insets(0, 0, 0, 5));

        return iconContainer;
    }

    // Helper method to determine order status (you can customize based on your logic)
    private String determineOrderStatus(OrderHistory order) {
        // Example logic - replace with your actual status determination
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderDate = order.getOrderDate();

        if (orderDate.plusDays(30).isBefore(now)) {
            return "Archived";
        } else if (orderDate.plusDays(1).isBefore(now)) {
            return "Completed";
        } else {
            return "Recent";
        }
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



