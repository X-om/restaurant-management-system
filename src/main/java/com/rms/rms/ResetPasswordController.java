package com.rms.rms;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class ResetPasswordController {
    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private Label errorLabel;

    @FXML
    private void handleResetPassword() {
        // Add your password reset logic here
        String current = currentPassword.getText().trim();
        String newPass = newPassword.getText().trim();

        if(current.isEmpty() || newPass.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            return;
        }

        if(newPass.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters!");
            return;
        }

        // Add actual password reset logic
        errorLabel.setStyle("-fx-text-fill: #23d000;");
        errorLabel.setText("Password updated successfully!");
    }
}