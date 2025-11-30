/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import javafx.animation.Animation.Status;
import javafx.scene.input.MouseButton;
import org.slf4j.LoggerFactory;

class RepeatableKeyButton extends KeyButton {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RepeatableKeyButton.class);

  RepeatableKeyButton() {
    super();
    getStyleClass().add("repeatable-button");
  }

  @Override
  public boolean isRepeatable() {
    return true;
  }

  @Override
  protected void initEventListener(Timelines timelines) {

    setOnDragDetected(
        e -> {
          logger.trace("{} drag detected", getKeyCode());
          timelines.stopLongPressDelayTimeline();
          e.consume();
        });

    setOnMousePressed(
        e -> {
          logger.trace("{} pressed", getKeyCode());
          if (e.getButton().equals(MouseButton.PRIMARY)) {
            if (!isMovable()) {
              fireShortPressed();
            }
            longPressDelayTimeline(timelines).playFromStart();
          }
          e.consume();
        });

    setOnMouseReleased(
        e -> {
          logger.trace("{} released", getKeyCode());
          var delayTimeLine = longPressDelayTimeline(timelines);
          if (isMovable() && delayTimeLine.getStatus() == Status.RUNNING) {
            fireShortPressed();
          }
          delayTimeLine.stop();
          timelines.stopRepeatableButtonTimeline();
          setFocused(false);
          e.consume();
        });
  }

  ButtonTimeline longPressDelayTimeline(Timelines timelines) {
    return timelines.longPressDelayTimeline(this, evt -> onLongPressedEvent(timelines));
  }

  void onLongPressedEvent(Timelines timelines) {
    fireShortPressed();
    timelines.repeatableButtonTimeline(this, evt -> fireShortPressed()).playFromStart();
  }
}
