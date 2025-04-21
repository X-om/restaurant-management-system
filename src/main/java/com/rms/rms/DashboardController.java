package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import com.rms.rms.models.RestaurantTable;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class DashboardController {
    @FXML
    private FlowPane tablesContainer;
    @FXML
    private void initialize() {
        optimizeUI();
        restoreTables();
    }

    private List<RestaurantTable> cachedTables = new ArrayList<>();

    private void optimizeUI() {
        tablesContainer.setCache(true);
        tablesContainer.setCacheHint(javafx.scene.CacheHint.SPEED);
    }

    void restoreTables() {
        if (!cachedTables.isEmpty()) {
            refreshUI();
            return;
        }

        Task<List<RestaurantTable>> task = new Task<>() {
            @Override
            protected List<RestaurantTable> call() throws Exception {
                return DatabaseManager.fetchTables();
            }
        };

        task.setOnSucceeded(event -> {
            cachedTables = task.getValue();
            refreshUI();
        });

        new Thread(task).start();
    }

    public void refreshUI() {
        tablesContainer.getChildren().clear();
        for (RestaurantTable table : cachedTables) {
            Pane tablePane = createCustomPTable(table);
            tablesContainer.getChildren().add(tablePane);
        }
    }

    private Pane createCustomPTable(RestaurantTable table) {
        Pane tablePane = createTablePane(table);
        VBox vbox = createVBox();
        tablePane.getChildren().add(vbox);

        HBox hBox1 = createTitleBox(table.getTableNumber());
        HBox hBox2 = createActionBox(table); // Pass table number here

        vbox.getChildren().addAll(hBox1, hBox2);
        return tablePane;
    }

    private Pane createTablePane(RestaurantTable table) {
        Pane pane = new Pane();
        pane.setPrefSize(236, 155);
        pane.setId("table-" + table.getTableNumber());

        if ("occupied".equalsIgnoreCase(table.getStatus())) {
            pane.getStyleClass().addAll("table-card","occupied-table-card");
        } else {
            pane.getStyleClass().addAll("table-card","available-table-card");
        }

        pane.setCache(true);
        pane.setCacheHint(javafx.scene.CacheHint.SPEED);
        return pane;
    }

    private VBox createVBox() {
        VBox vbox = new VBox();
        vbox.setPrefSize(236, 155);
        return vbox;
    }

    private HBox createTitleBox(int tableNumber) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(236);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        Label tableIdText = new Label("" + tableNumber);
        tableIdText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 24));
        hBox.getChildren().add(tableIdText);

        return hBox;
    }

    private HBox createActionBox(RestaurantTable table) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(236);
        hBox.setPrefHeight(50);
        hBox.setSpacing(20);

        HBox pane1 = createIconPane("/com/rms/rms/images/AddTable.png");
        HBox pane2 = createIconPane("/com/rms/rms/images/PrintBill.png");

        pane1.setOnMouseClicked(e -> {
            if ("available".equals(table.getStatus())) {
                showAddItemsPopup(table.getTableNumber());
            }
        });
        pane2.setDisable(!"occupied".equals(table.getStatus()));
        pane2.setOnMouseClicked(e -> {
            if ("occupied".equals(table.getStatus())) {
                showBillPopup(table.getTableNumber());
            }
        });



        hBox.getChildren().addAll(pane1, pane2);
        return hBox;
    }

    private HBox createIconPane(String imagePath) {
        ImageView imageView = createImageView(imagePath);

        HBox hBox = new HBox();
        hBox.setPrefSize(40, 40);
        hBox.setMaxWidth(40);
        hBox.setMaxHeight(40);
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().add(imageView);
        hBox.setCursor(Cursor.HAND);
        hBox.setStyle("-fx-background-color: #f7f7f7; -fx-background-radius: 10px;");



        return hBox;
    }

    private ImageView createImageView(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm(), 30, 30, true, true);
        return new ImageView(image);
    }

    private void showAddItemsPopup(int tableNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rms/rms/add-items-popup.fxml"));
            Parent root = loader.load();

            AddItemsPopupController controller = loader.getController();
            controller.setTableNumber(tableNumber);
            controller.setDashboardController(this);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setScene(new Scene(root));


            dialog.initOwner(tablesContainer.getScene().getWindow());
            dialog.showAndWait();



        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void showBillPopup(int tableNumber){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rms/rms/bill-popup.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablesContainer.getScene().getWindow());
            dialog.setTitle("Bill - Table " + tableNumber);

            BillPopupController controller = loader.getController();
            controller.initialize(tableNumber, dialog);

            dialog.setScene(new Scene(root));
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load bill window");
        }
    }
    @FXML
    private void addTable() {
        Task<Integer> task = new Task<>() {
            @Override
            protected Integer call() {
                DatabaseManager.createTablesTable();
                return DatabaseManager.insertTable();
            }
        };

        task.setOnSucceeded(event -> {
            int tableNumber = task.getValue();
            if (tableNumber != -1) {
                RestaurantTable newTable = new RestaurantTable(tableNumber, "available");
                cachedTables.add(newTable);
                refreshUI();
            }
        });

        new Thread(task).start();
    }
}