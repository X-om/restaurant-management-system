package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

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

    private List<Integer> cachedTables = new ArrayList<>();


    private void optimizeUI() {
        tablesContainer.setCache(true);
        tablesContainer.setCacheHint(javafx.scene.CacheHint.SPEED);
    }

    private void restoreTables() {
        if (!cachedTables.isEmpty()) {
            refreshUI();
            return;
        }

        Task<List<Integer>> task = new Task<>() {
            @Override
            protected List<Integer> call() throws Exception {
                return DatabaseManager.fetchTables();
            }
        };

        task.setOnSucceeded(event -> {
            cachedTables = task.getValue();
            refreshUI();
        });

        new Thread(task).start();

    }

    private void refreshUI() {
        tablesContainer.getChildren().clear();
        for (int tableNumber : cachedTables) {
            Pane table = createCustomPTable(tableNumber);
            tablesContainer.getChildren().add(table);
        }
    }

    private Pane createCustomPTable(int tableNumber) {
        Pane table = createTablePane(tableNumber);
        VBox vbox = createVBox();
        table.getChildren().add(vbox);

        HBox hBox1 = createTitleBox(tableNumber);
        HBox hBox2 = createActionBox();

        vbox.getChildren().addAll(hBox1, hBox2);
        return table;
    }

    private Pane createTablePane(int tableNumber) {
        Pane table = new Pane();
        table.setPrefSize(236, 155);
        table.setId("table-" + tableNumber);
        table.getStyleClass().add("table-card");
        table.setCache(true);
        table.setCacheHint(javafx.scene.CacheHint.SPEED);
        return table;
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

        Label tableIdText = new Label(""+tableNumber);
        tableIdText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC, 24));
        hBox.getChildren().add(tableIdText);

        return hBox;
    }

    private HBox createActionBox() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPrefWidth(236);
        hBox.setPrefHeight(50);
        hBox.setSpacing(20);

        HBox pane1 = createIconPane("/com/rms/rms/images/AddTable.png");
        HBox pane2 = createIconPane("/com/rms/rms/images/PrintBill.png");

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

    @FXML
    private void addTable() {

        Task<Integer> task = new Task() {
            @Override
            protected Integer call()  {
                return DatabaseManager.insertTable();
            }
        };

        task.setOnSucceeded(event -> {
            int tableNumber = task.getValue();
            if (tableNumber != -1) {
                cachedTables.add(tableNumber);
                refreshUI();
            }
        });

        new Thread(task).start();
    }
}

