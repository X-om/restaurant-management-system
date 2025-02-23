package com.rms.rms;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.util.HashMap;

public class DashboardController {
    @FXML
    private FlowPane tablesContainer;

    private int tableCounter = 1;
    private HashMap<String, Pane> tableMap = new HashMap<>();


    private Pane createCustomPTable() {
        String tableId = "table-" + tableCounter;
        tableCounter++;

        Pane table = new Pane();
        table.setPrefSize(236, 155);
        table.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 20;");
        table.setId(tableId);


        VBox vbox = new VBox();
        vbox.setPrefWidth(table.getPrefWidth());
        vbox.setPrefHeight(table.getPrefHeight());
        table.getChildren().add(vbox);


        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setPrefWidth(vbox.getPrefWidth());


        VBox.setVgrow(hBox1, Priority.ALWAYS);


        HBox hBox2 = new HBox();
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setPrefWidth(vbox.getPrefWidth());


        hBox2.prefHeightProperty().bind(vbox.heightProperty().divide(2));

        vbox.getChildren().addAll(hBox1, hBox2);


        Label tableIdText = new Label("" + (tableCounter - 1));
        tableIdText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.ITALIC,24));
        hBox1.getChildren().add(tableIdText);


        Image addTableIcon = new Image(getClass().getResource("/com/rms/rms/images/AddTable.png").toExternalForm());
        Image printBillIcon = new Image(getClass().getResource("/com/rms/rms/images/PrintBill.png").toExternalForm());

        ImageView addTableView = new ImageView(addTableIcon);
        ImageView printBillView = new ImageView(printBillIcon);

        addTableView.setFitWidth(30);
        addTableView.setFitHeight(30);

        printBillView.setFitWidth(30);
        printBillView.setFitHeight(30);

        hBox2.setSpacing(20);
        hBox2.getChildren().addAll(addTableView, printBillView);


        tableMap.put(tableId, table);
        return table;
    }


    @FXML
    private void addTable(){
        Pane table = createCustomPTable();
        tablesContainer.getChildren().add(table);

    }



}
