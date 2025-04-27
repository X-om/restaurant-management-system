package com.rms.rms;

import com.rms.rms.database.DatabaseManager;
import com.rms.rms.models.MenuItemModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuPopupController implements Initializable {

    @FXML private ComboBox<String> sectionComboBox;
    @FXML private TableView<MenuItemModel> menuTableView;
    @FXML private TableColumn<MenuItemModel, String> nameColumn;
    @FXML private TableColumn<MenuItemModel, Double> priceColumn;
    @FXML private Button addButton;
    @FXML private Button closeButton;

    private final ObservableList<MenuItemModel> menuItems = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize columns
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Set up combo box with sections from your database
        sectionComboBox.getItems().addAll(
                "Appetizers",
                "Main Course",
                "Desserts",
                "Beverages",
                "Specials"
        );

        // Add listener to combo box
        sectionComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        loadMenuItemsFromDB(newValue);
                    }
                }
        );

        // Set up buttons
        closeButton.setOnAction(event -> closeButton.getScene().getWindow().hide());

        // Style the table
        menuTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Select first section by default
        if (!sectionComboBox.getItems().isEmpty()) {
            sectionComboBox.getSelectionModel().selectFirst();
        }
    }

    private void loadMenuItemsFromDB(String section) {
        menuItems.clear();
        menuItems.addAll(DatabaseManager.getMenuItems(section));
        menuTableView.setItems(menuItems);
    }

    @FXML
    private void handleAddToOrder() {
        MenuItemModel selectedItem = menuTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Add your logic here for adding to order
            System.out.println("Adding to order: " + selectedItem.getName());

            // Show confirmation
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Item Added");
            alert.setHeaderText(null);
            alert.setContentText(selectedItem.getName() + " added to order!");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item from the menu");
            alert.showAndWait();
        }
    }
}