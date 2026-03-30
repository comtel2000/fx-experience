/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.OS;

import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

class KeyButtonTest {

  @BeforeAll
  static void initToolkit() throws Exception {
    if (!OS.WINDOWS.isCurrentOs()) {
      // Ensure headless property is set early in CI via JAVA_TOOL_OPTIONS; this is
      // just in case.
      System.setProperty("java.awt.headless", System.getProperty("java.awt.headless", "true"));
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.forceGPU", "false");
      System.setProperty("glass.platform", "Monocle");
      System.setProperty("monocle.platform", "Headless");
    }
    // Initialize JavaFX toolkit (safe if already started)
    try {
      Platform.startup(
          () -> {
            /* no-op */
          });
    } catch (IllegalStateException ex) {
      // toolkit already started
    } catch (RuntimeException ex) {
      // Handle glass platform initialization failures in JDK 25+
      // This can occur when the glass platform factory is not properly initialized
      System.err.println("Warning: Failed to initialize JavaFX toolkit: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  @Test
  @Timeout(value = 20, unit = TimeUnit.SECONDS)
  void previousTimelineStoppedWhenNewPressed() throws Exception {
    var latch = new CountDownLatch(1);
    var exception = new AtomicReference<Throwable>();
    Platform.runLater(
        () -> {
          try {
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
                Animation.Status.RUNNING,
                keyboard.timelines().longPressDelayTimeline().getStatus());
            assertTrue(keyboard.timelines().longPressDelayTimeline().isOwner(b1));

            // Press second button; this should stop timeline on b1 and starts on b2
            h2.handle(press);
            // switch owner
            assertTrue(keyboard.timelines().longPressDelayTimeline().isOwner(b2));

            // Press third button; this should stop all timelines
            h3.handle(press);

            // After pressing short press button all timelines should be stopped
            assertEquals(
                Animation.Status.STOPPED,
                keyboard.timelines().longPressDelayTimeline().getStatus());
            assertEquals(
                Animation.Status.STOPPED,
                keyboard.timelines().repeatableButtonTimeline().getStatus());

          } catch (Throwable th) {
            exception.set(th);
          } finally {
            latch.countDown();
          }
        });

    assertTrue(latch.await(2, TimeUnit.SECONDS));
    if (exception.get() != null) {
      AssertionFailureBuilder.assertionFailure().cause(exception.get()).buildAndThrow();
    }
  }
}
