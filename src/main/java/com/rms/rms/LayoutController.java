package com.rms.rms;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.*;

public class LayoutController {

    public Label dashboardLabel;
    public Label orderLabel;
    public Label reportLabel;
    public Label settingsLabel;
    public Label supportLabel;
    public Label logoutLabel;
    public HBox menuButton;

    @FXML private VBox drawer;
    private boolean isDrawerExpanded = false;
    private static final double DRAWER_MIN_WIDTH = 80;
    private static final double DRAWER_MAX_WIDTH = 300;
    private Timeline timeline;
    private List<Label> labels;
    private final List<StackPane> menuContainers = new ArrayList<>();

    @FXML
    private void initialize() {
        drawer.setPrefWidth(DRAWER_MIN_WIDTH);

        // Fix: Include all labels in the list
        labels = Arrays.asList(
                dashboardLabel,
                orderLabel,
                reportLabel,
                settingsLabel,
                supportLabel,
                logoutLabel
        );

        // Initialize labels
        labels.forEach(label -> {
            label.setOpacity(0);
            label.setTranslateX(-20);
            label.setVisible(false);
        });

        // Collect menu containers
        VBox contentVBox = (VBox) drawer.getChildren().get(1);
        contentVBox.getChildren().forEach(node -> {
            if (node instanceof StackPane) {
                menuContainers.add((StackPane) node);
                Rectangle clip = (Rectangle) node.getClip();
                clip.setWidth(DRAWER_MIN_WIDTH);
            }
        });

        timeline = new Timeline();
    }

    // Rest of the code remains the same...
    @FXML
    private void toggleDrawer() {
        // Previous implementation remains unchanged
        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.stop();
        }

        boolean targetExpanded = !isDrawerExpanded;
        double targetWidth = targetExpanded ? DRAWER_MAX_WIDTH : DRAWER_MIN_WIDTH;

        List<KeyValue> keyValues = new ArrayList<>();

        // Drawer width animation
        keyValues.add(new KeyValue(
                drawer.prefWidthProperty(),
                targetWidth,
                Interpolator.EASE_BOTH
        ));

        // Clip animations
        menuContainers.forEach(container -> {
            Rectangle clip = (Rectangle) container.getClip();
            KeyValue clipValue = new KeyValue(
                    clip.widthProperty(),
                    targetExpanded ? DRAWER_MAX_WIDTH : DRAWER_MIN_WIDTH,
                    Interpolator.EASE_BOTH
            );
            keyValues.add(clipValue);
        });

        // Label animations
        labels.forEach(label -> {
            if (targetExpanded) label.setVisible(true);

            keyValues.add(new KeyValue(
                    label.opacityProperty(),
                    targetExpanded ? 1 : 0,
                    Interpolator.EASE_BOTH
            ));
            keyValues.add(new KeyValue(
                    label.translateXProperty(),
                    targetExpanded ? 0 : -20,
                    Interpolator.EASE_BOTH
            ));
        });

        timeline.getKeyFrames().setAll(
                new KeyFrame(Duration.millis(250),
                        keyValues.toArray(new KeyValue[0])
                ));

        timeline.setOnFinished(e -> {
            isDrawerExpanded = targetExpanded;
            if (!targetExpanded) {
                labels.forEach(label -> label.setVisible(false));
            }
        });

        menuContainers.forEach(container -> {
            HBox hbox = (HBox) container.getChildren().getFirst();
            keyValues.add(new KeyValue(
                    hbox.maxWidthProperty(),
                    targetExpanded ? DRAWER_MAX_WIDTH : DRAWER_MIN_WIDTH,
                    Interpolator.EASE_BOTH
            ));
        });

        timeline.play();
    }
}