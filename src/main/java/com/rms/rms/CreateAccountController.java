package com.rms.rms;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;


import static com.rms.rms.database.DatabaseManager.insertUserInfo;
import static com.rms.rms.database.DatabaseManager.usernameExists;

public class CreateAccountController {
    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleSubmit() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            return;
        }

        if (usernameExists(username)) {
            errorLabel.setText("Username already exists!");
            return;
        }

        if (insertUserInfo(name, username, password)) {
            errorLabel.setStyle("-fx-text-fill: #4CAF50;");
            errorLabel.setText("Account created successfully!");

            // Close window after 1.5 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e -> usernameField.getScene().getWindow().hide());
            delay.play();
        } else {
            errorLabel.setText("Failed to create account. Please try again.");
        }
    }


}