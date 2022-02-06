package org.comtel2000.keyboard.control;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.slf4j.LoggerFactory;

import java.util.Collection;

class MultiKeyButton extends KeyButton {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MultiKeyButton.class);
    private final Collection<String> styles;
    private final Parent parent;
    private MultiKeyPopup context;

    MultiKeyButton(Parent parent, Collection<String> styles) {
        super();
        getStyleClass().add("multi-button");
        this.styles = styles;
        this.parent = parent;
    }

    @Override
    protected void initEventListener(double delay) {

        buttonDelay = new Timeline(new KeyFrame(new Duration(delay), event -> fireLongPressed()));

        setOnDragDetected(e -> {
            logger.trace("{} drag detected", getKeyCode());
            if (buttonDelay.getStatus().equals(Status.RUNNING) && buttonDelay.getCurrentRate() > 0) {
                buttonDelay.stop();
                fireLongPressed();
            }
            e.consume();
        });

        setOnMouseClicked(event -> {
            logger.trace("{} clicked: {}", getKeyCode(), buttonDelay.getCurrentRate());

            if (event.getButton().equals(MouseButton.PRIMARY)) {
                if (buttonDelay.getStatus().equals(Status.RUNNING)) {
                    buttonDelay.stop();
                    fireShortPressed();
                }
            }
            setFocused(false);
            event.consume();
        });

        setOnMousePressed(event -> {
            logger.trace("{} pressed: {}", getKeyCode(), buttonDelay.getCurrentRate());
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                buttonDelay.playFromStart();
            }
            event.consume();
        });

    }

    private MultiKeyPopup getContext() {
        if (context == null) {
            context = new MultiKeyPopup();
            context.getStylesheets().setAll(styles);
            context.setOnHidden(event -> {
                parent.setEffect(null);
                parent.setDisable(false);
            });
            setOnLongPressed(event -> {
                Node node = (Node) event.getSource();
                parent.setDisable(true);
                setFocused(false);
                context.show(node, parent.getScaleX());
            });

        }
        return context;
    }

    @Override
    public void addExtKeyCode(int extKeyCode, String label) {
        KeyButton button = new ShortPressKeyButton();
        button.setText(label);
        button.setKeyCode(extKeyCode);

        if (getStyleClass() != null) {
            button.getStyleClass().addAll(getStyleClass());
        } else {
            button.setId("key-context-button");
        }
        button.setFocusTraversable(false);

        button.setPrefWidth(this.getPrefWidth());
        button.setPrefHeight(this.getPrefHeight());

        button.setOnShortPressed(getOnShortPressed());

        getContext().addButton(button);
    }

}
