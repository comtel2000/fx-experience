/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import java.util.function.Consumer;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.util.Duration;

abstract class ButtonTimeline {

  protected final Timeline timeline;
  private Node owner;
  protected Consumer<ActionEvent> handler;

  protected ButtonTimeline(double delay) {
    timeline =
        !Double.isNaN(delay) && delay > 0.0d
            ? new Timeline(new KeyFrame(Duration.millis(delay), this::handle))
            : new Timeline();
  }

  ButtonTimeline withOwner(Node owner, Consumer<ActionEvent> handler) {
    if (!isOwner(owner)) {
      timeline.stop();
      this.owner = owner;
    }
    this.handler = handler;
    return this;
  }

  boolean isOwner(Node node) {
    return owner != null && node == owner;
  }

  boolean isEnabled() {
    return !timeline.getKeyFrames().isEmpty();
  }

  void playFromStart() {
    timeline.playFromStart();
  }

  void stop() {
    timeline.stop();
  }

  Status getStatus() {
    return timeline.getStatus();
  }

  double getCurrentRate() {
    return timeline.getCurrentRate();
  }

  void handle(ActionEvent event) {
    if (handler != null) {
      handler.accept(event);
    }
  }
}
