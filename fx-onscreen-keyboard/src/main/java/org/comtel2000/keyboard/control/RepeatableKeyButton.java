package org.comtel2000.keyboard.control;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.slf4j.LoggerFactory;

class RepeatableKeyButton extends KeyButton {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RepeatableKeyButton.class);
    private static final double KEY_REPEAT_RATE_MIN = 2;
    private static final double KEY_REPEAT_RATE_MAX = 50;
    // key repeat rate (cps)
    private static double KEY_REPEAT_RATE = 25;

    static {
        String s = System.getProperty("org.comtel2000.keyboard.repeatRate");
        if (s != null) {
            Double rate = Double.valueOf(s);
            if (rate <= 0) {
                // disable key repeat
                KEY_REPEAT_RATE = 0;
            } else {
                KEY_REPEAT_RATE = Math.min(Math.max(rate, KEY_REPEAT_RATE_MIN), KEY_REPEAT_RATE_MAX);
            }
        }
    }

    private final long REPEAT_DELAY = 40;
    private Timeline repeatDelay;

    RepeatableKeyButton() {
        super();
        getStyleClass().add("repeatable-button");
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    protected void initEventListener(double delay) {

        if (KEY_REPEAT_RATE > 0) {
            buttonDelay = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
                fireShortPressed();
                // buttonDelay.playFrom(buttonDelay.getCycleDuration().subtract(Duration.millis(REPEAT_DELAY)));
                repeatDelay.playFromStart();
            }));
            repeatDelay = new Timeline(
                    new KeyFrame(Duration.millis(1000.0 / KEY_REPEAT_RATE), event -> fireShortPressed()));
            repeatDelay.setCycleCount(Animation.INDEFINITE);
            setOnDragDetected(e -> {
                logger.trace("{} drag detected", getKeyCode());
                buttonDelay.stop();
                e.consume();
            });

            setOnMousePressed(e -> {
                logger.trace("{} pressed", getKeyCode());
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (!isMovable()) {
                        fireShortPressed();
                    }
                    buttonDelay.playFromStart();
                }
                e.consume();
            });

            setOnMouseReleased(e -> {
                logger.trace("{} released", getKeyCode());
                if (isMovable() && buttonDelay.getStatus() == Status.RUNNING) {
                    fireShortPressed();
                }
                buttonDelay.stop();
                repeatDelay.stop();
                setFocused(false);
                e.consume();
            });
        }
    }

}
