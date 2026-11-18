/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.animation.Animation;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KeyButtonTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @Test
  void previousTimelineStoppedWhenNewPressed() {
    FxTestSupport.runFx(
        () -> {
          KeyboardPane keyboard = new KeyboardPane();

          var b1 = new RepeatableKeyButton();
          var b2 = new RepeatableKeyButton();
          var b3 = new ShortPressKeyButton();

          b1.setTimelines(keyboard.timelines());
          b2.setTimelines(keyboard.timelines());
          b3.setTimelines(keyboard.timelines());

          var h1 = b1.getOnMousePressed();
          var h2 = b2.getOnMousePressed();
          var h3 = b3.getOnMousePressed();

          // Build a basic primary mouse event
          var press =
              new MouseEvent(
                  MouseEvent.MOUSE_PRESSED,
                  0,
                  0,
                  0,
                  0,
                  MouseButton.PRIMARY,
                  1,
                  false,
                  false,
                  false,
                  false,
                  true,
                  false,
                  false,
                  false,
                  false,
                  false,
                  null);
          // Press first button
          h1.handle(press);

          // After pressing b1 its buttonDelay should be running
          assertEquals(
              Animation.Status.RUNNING, keyboard.timelines().longPressDelayTimeline().getStatus());
          assertTrue(keyboard.timelines().longPressDelayTimeline().isOwner(b1));

          // Press second button; this should stop timeline on b1 and starts on b2
          h2.handle(press);
          // switch owner
          assertTrue(keyboard.timelines().longPressDelayTimeline().isOwner(b2));

          // Press third button; this should stop all timelines
          h3.handle(press);

          // After pressing short press button all timelines should be stopped
          assertEquals(
              Animation.Status.STOPPED, keyboard.timelines().longPressDelayTimeline().getStatus());
          assertEquals(
              Animation.Status.STOPPED,
              keyboard.timelines().repeatableButtonTimeline().getStatus());
        });
  }
}
