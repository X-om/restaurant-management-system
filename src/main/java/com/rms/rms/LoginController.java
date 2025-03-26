package com.rms.rms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField password;
    @FXML
    private Label errorLabel;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String myPassword = "om@123";

    public void checkPassword(ActionEvent event) throws IOException {
        if (Objects.equals(myPassword, password.getText())) {
            errorLabel.setTextFill(Color.GREEN);
            errorLabel.setText("Login....");

            // Load the new FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));
            Parent newRoot = loader.load();

            // Get the current scene and set the new root
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot); // ✅ Just replace the root instead of setting a new scene

        } else if (Objects.equals(password.getText(), "")) {
            errorLabel.setText("Please enter the password");

        } else {
            errorLabel.setText("Incorrect password");
        }
    }


    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }
}