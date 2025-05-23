package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void init() throws Exception {
        DatabaseManager.createUsersTable();
        DatabaseManager.createMenuTable();
        DatabaseManager.createTablesTable();
        DatabaseManager.createOrderTable();
        DatabaseManager.createOrderItemsTable();
        System.out.println("Database tables initialized");
    }


    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);



        if (root instanceof HBox hbox) {
            hbox.prefWidthProperty().bind(scene.widthProperty());
            hbox.prefHeightProperty().bind(scene.heightProperty());
        }


        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
