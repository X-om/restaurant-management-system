package com.rms.rms;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

public class SupportController {

    @FXML private VBox contactCard;
    @FXML private ComboBox<String> subjectComboBox;


    @FXML
    public void initialize() {
        System.out.println("Initializing SupportController...");
        System.out.println("subjectComboBox is: " + subjectComboBox); // Debug line

        if (subjectComboBox != null) {
            subjectComboBox.getItems().addAll(
                    "General Inquiry",
                    "Technical Support",
                    "Billing Question",
                    "Feature Request",
                    "Bug Report"
            );
        } else {
            System.err.println("ERROR: subjectComboBox not initialized - check FXML fx:id");
        }
    }

    @FXML
    private void handleSubmit() {
        // Implement form submission logic here
        System.out.println("Form submitted!");
    }
}