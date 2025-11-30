/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.Node;

class Timelines {

  private RepeatableButtonTimeline repeatableButtonTimeline;
  private LongPressDelayTimeline longPressDelayTimeline;

  ButtonTimeline repeatableButtonTimeline() {
    if (repeatableButtonTimeline == null) {
      repeatableButtonTimeline = new RepeatableButtonTimeline();
    }
    return repeatableButtonTimeline;
  }

  ButtonTimeline longPressDelayTimeline() {
    if (longPressDelayTimeline == null) {
      longPressDelayTimeline = new LongPressDelayTimeline();
    }
    return longPressDelayTimeline;
  }

  ButtonTimeline repeatableButtonTimeline(Node owner, Consumer<ActionEvent> handler) {
    return repeatableButtonTimeline().withOwner(owner, handler);
  }

  ButtonTimeline longPressDelayTimeline(Node owner, Consumer<ActionEvent> handler) {
    return longPressDelayTimeline().withOwner(owner, handler);
  }

  void stopRepeatableButtonTimeline() {
    if (repeatableButtonTimeline != null) {
      repeatableButtonTimeline.stop();
    }
  }

  void stopLongPressDelayTimeline() {
    if (longPressDelayTimeline != null) {
      longPressDelayTimeline.stop();
    }
  }

  void stop() {
    stopRepeatableButtonTimeline();
    stopLongPressDelayTimeline();
  }
}
