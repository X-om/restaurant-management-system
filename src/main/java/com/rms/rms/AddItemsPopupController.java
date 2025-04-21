package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import com.rms.rms.models.MenuItemModel;
import com.rms.rms.models.OrderItems;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;


import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.binding.Bindings;

import java.sql.SQLException;
import java.util.*;

public class AddItemsPopupController {
    private DashboardController dashboardController;

    @FXML private MenuButton sectionSelector;
    @FXML private TableView<MenuItemModel> menuTable;
    @FXML private TableColumn<MenuItemModel, String> nameColumn;
    @FXML private TableColumn<MenuItemModel, Double> priceColumn;
    @FXML private TableColumn<MenuItemModel, Integer> noColumn;
    @FXML private Label amountLabel;
    private DoubleProperty grandTotal = new SimpleDoubleProperty(0.0);

    @FXML private VBox menuEditSection;
    private Map<MenuItemModel, QuantityCard> itemCards = new HashMap<>();

    @FXML private StackPane mainPopUp;
    @FXML private Button saveButton;
    private int currentTableNumber;

    public void setTableNumber(int tableNumber) {
        this.currentTableNumber = tableNumber;
    }

    public void setDashboardController(DashboardController controller) {
        if (controller == null) {
            throw new IllegalArgumentException("Dashboard controller cannot be null");
        }
        this.dashboardController = controller;
    }

    private class QuantityCard {
        private MenuItemModel item;
        private int quantity = 1;
        private HBox card;
        private Label totalLabel;
        private Label quantityLabel; // Class member for the quantity display
        private Font font;
        public QuantityCard(MenuItemModel item) {
            this.item = item;
            this.font = Font.font("Hoefler Text",14);
            createCard();
        }

        private void createCard() {
            card = new HBox();
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-spacing: 20; -fx-padding: 10;");
            card.getStyleClass().add("quantity-card");

            // Item Name
            Label nameLabel = new Label(item.getName());
            nameLabel.setPrefWidth(200);
            nameLabel.setMaxWidth(200);
            nameLabel.setFont(font);

            // Quantity Controls
            HBox quantityBox = new HBox(5);
            quantityBox.setAlignment(Pos.CENTER);

            Button minusButton = new Button("-");
            minusButton.getStyleClass().add("quantity-button");
            this.quantityLabel = new Label("1"); // Use class member
            Button plusButton = new Button("+");
            plusButton.getStyleClass().add("quantity-button");

            quantityBox.getChildren().addAll(minusButton, quantityLabel, plusButton);

            // Total Price
            totalLabel = new Label("₹" + item.getPrice());
            totalLabel.setPrefWidth(100);
            totalLabel.setAlignment(Pos.CENTER_RIGHT);
            totalLabel.setStyle("-fx-text-fill: #4D3F76;");

            // Setup actions
            minusButton.setOnAction(e -> updateQuantity(-1));
            plusButton.setOnAction(e -> updateQuantity(1));

            card.getChildren().addAll(nameLabel, quantityBox, totalLabel);

            // Set growing priorities
            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            HBox.setHgrow(quantityBox, Priority.NEVER);
            HBox.setHgrow(totalLabel, Priority.ALWAYS);
        }

        private void updateQuantity(int delta) {
            quantity += delta;
            if (quantity < 1) {
                removeCard();
                return;
            }
            quantityLabel.setText(String.valueOf(quantity));
            totalLabel.setText("₹" + (item.getPrice() * quantity));
            updateGrandTotal();
        }

        private void removeCard() {
            menuEditSection.getChildren().remove(card);
            itemCards.remove(item);
            updateGrandTotal();
        }
    }



    @FXML
    public void initialize() {
        // Configure column widths and behavior
        setupTableColumns();
        setupTableInteraction();

        amountLabel.textProperty().bind(Bindings.createStringBinding(() ->
                        "₹" + String.format("%.2f", grandTotal.get()),
                grandTotal
        ));

        // Initial update
        updateGrandTotal();

        // Setup price formatting
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : "₹" + String.format("%.2f", price));
            }
        });

        // Load initial data
        loadMenuItems("Main Course");

        // Setup section selector
        sectionSelector.getItems().forEach(item ->
                item.setOnAction(e -> loadMenuItems(item.getText()))
        );

        saveButton.setOnAction(event -> saveOrder());

    }
    private void saveOrder() {
        // Validate items and table number
        if (itemCards.isEmpty()) {
            System.err.println("No item selected");
            return;
        }
        if (currentTableNumber == 0) {
            System.err.println("No table selected");
            return;
        }

        // Prepare order items
        List<OrderItems> orderItems = new ArrayList<>();
        for (QuantityCard card : itemCards.values()) {
            orderItems.add(new OrderItems(
                    card.item.getId(),
                    card.quantity,
                    card.item.getPrice()
            ));
        }

        try {
            // Save order and update table status
            DatabaseManager.saveOrder(currentTableNumber, grandTotal.get(), orderItems);

            System.out.println("Order saved successfully");
            dashboardController.refreshUI();
            dashboardController.restoreTables();

            closeTheWindow();

        } catch (SQLException e) {
            System.err.println("Failed to save order");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void setupTableInteraction() {
        menuTable.setRowFactory(tv -> {
            TableRow<MenuItemModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    MenuItemModel selectedItem = row.getItem();
                    if(itemCards.containsKey(selectedItem)) {
                        // Update existing card's quantity
                        itemCards.get(selectedItem).updateQuantity(1);
                    } else {
                        // Create new card
                        QuantityCard card = new QuantityCard(selectedItem);
                        itemCards.put(selectedItem, card);
                        menuEditSection.getChildren().add(card.card);
                    }
                    updateGrandTotal();
                }
            });
            return row;
        });
    }

    private void setupTableColumns() {
        // Prevent column resizing
        noColumn.setResizable(false);
        nameColumn.setResizable(false);
        priceColumn.setResizable(false);

        // Set fixed column widths (adjust values as needed)
        noColumn.setPrefWidth(60);        // Serial number
        noColumn.setMinWidth(60);
        noColumn.setMaxWidth(60);

        nameColumn.setPrefWidth(200);     // Food name
        nameColumn.setMinWidth(200);
        nameColumn.setMaxWidth(200);

        priceColumn.setPrefWidth(100);     // Price
        priceColumn.setMinWidth(100);
        priceColumn.setMaxWidth(100);

        String centerStyle = "-fx-alignment: CENTER;";
        noColumn.setStyle(centerStyle);
        nameColumn.setStyle(centerStyle);
        priceColumn.setStyle(centerStyle);

        // Bind column properties to table width
        menuTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Configure data bindings
        noColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(menuTable.getItems().indexOf(cellData.getValue()) + 1
                ));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    }

    private void loadMenuItems(String section) {
        Vector<MenuItemModel> items = DatabaseManager.getMenuItems(section);
        ObservableList<MenuItemModel> observableList = FXCollections.observableArrayList(items);
        menuTable.setItems(observableList);
        sectionSelector.setText(section);
    }

    public void closeTheWindow(){
        Stage stage = (Stage) mainPopUp.getScene().getWindow();
        stage.close();
        stage.close();
    }

    private void updateGrandTotal() {
        double total = itemCards.values().stream()
                .mapToDouble(card -> card.item.getPrice() * card.quantity)
                .sum();
        grandTotal.set(total);
    }

}