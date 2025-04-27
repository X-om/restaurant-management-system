package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;


public class LoginController {
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label errorLabel;

    @FXML
    public void checkPassword(ActionEvent event) {
        String inputUsername = username.getText().trim();
        String inputPassword = password.getText().trim();

        if (inputUsername.isEmpty()) {
            errorLabel.setText("Please enter username");
            return;
        }
        if (inputPassword.isEmpty()) {
            errorLabel.setText("Please enter password");
            return;
        }

        try {
            if (!DatabaseManager.checkUsernameExists(inputUsername)) {
                errorLabel.setText("User not found");
                return;
            }

            if (!DatabaseManager.verifyPassword(inputUsername, inputPassword)) {
                errorLabel.setText("Incorrect password");
                return;
            }

            // Successful login flow
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Login successful!");

            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> {
                try {
                    loadMainScene(event);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            delay.play();

        } catch (Exception e) {
            errorLabel.setText("Database error. Try again.");
            System.err.println("Login error: " + e.getMessage());
        }
    }

    private void loadMainScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));
        Parent newRoot = loader.load();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(newRoot);
    }

    @FXML
    public void createAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("create-account.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setTitle("Create New Account");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

