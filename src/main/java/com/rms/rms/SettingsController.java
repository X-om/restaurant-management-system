package com.rms.rms;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class SettingsController {
    @FXML
    private Button addMenuItems;

    @FXML
    private void handleAddMenuItemsClick() {
        try {
            // Create new stage
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(addMenuItems.getScene().getWindow());
            popupStage.setTitle("Add Menu Items");

            // Load FXML content
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("add-menu-items-popup.fxml")
            );
            VBox root = loader.load();

            // Set up controller
            AddMenuItemsController controller = loader.getController();
            controller.setStage(popupStage);

            // Create scene and show
            Scene scene = new Scene(root);
            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}