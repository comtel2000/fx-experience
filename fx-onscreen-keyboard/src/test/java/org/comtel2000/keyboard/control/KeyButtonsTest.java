/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.Animation.Status;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KeyButtonsTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  static MouseEvent mouse(EventType<MouseEvent> type, MouseButton button) {
    return new MouseEvent(
        type,
        0,
        0,
        0,
        0,
        button,
        1,
        false,
        false,
        false,
        false,
        button == MouseButton.PRIMARY,
        false,
        button == MouseButton.SECONDARY,
        false,
        false,
        false,
        null);
  }

  /** Records short and long presses of a button. */
  static final class Presses {
    final AtomicInteger shortPresses = new AtomicInteger();
    final AtomicInteger longPresses = new AtomicInteger();

    Presses(KeyButton button) {
      button.setOnShortPressed(e -> shortPresses.incrementAndGet());
      button.setOnLongPressed(e -> longPresses.incrementAndGet());
    }
  }

  @Test
  void keyButtonDefaults() {
    runFx(
        () -> {
          var b = new ShortPressKeyButton();
          assertTrue(b.getStyleClass().contains("key-button"));
          assertNull(b.getOnShortPressed());
          assertNull(b.getOnLongPressed());
          assertFalse(b.isMovable());
          assertFalse(b.isSticky());
          assertFalse(b.isRepeatable());
          assertNull(b.getKeyText());

          assertSame(b, b.onShortPressedProperty().getBean());
          assertEquals("onShortPressed", b.onShortPressedProperty().getName());
          assertSame(b, b.onLongPressedProperty().getBean());
          assertEquals("onLongPressed", b.onLongPressedProperty().getName());

          b.setMovable(true);
          b.setSticky(true);
          b.setKeyCode(42);
          b.setKeyText("x");
          assertTrue(b.isMovable());
          assertTrue(b.isSticky());
          assertEquals(42, b.getKeyCode());
          assertEquals("x", b.getKeyText());

          // no-op for simple buttons
          b.addExtKeyCode(new Timelines(), 1, "1");
        });
  }

  @Test
  void keyButtonEventsReachHandlers() {
    runFx(
        () -> {
          var b = new ShortPressKeyButton();
          var presses = new Presses(b);
          b.fireShortPressed();
          b.fireLongPressed();
          assertEquals(1, presses.shortPresses.get());
          assertEquals(1, presses.longPresses.get());
          assertNotNull(new KeyButtonEvent(b, KeyButtonEvent.ANY).toString());
          assertEquals(KeyButtonEvent.ANY, new KeyButtonEvent(KeyButtonEvent.ANY).getEventType());
        });
  }

  @Test
  void shortPressButtonFiresOnPrimaryPressOnly() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var b = new ShortPressKeyButton();
          b.setTimelines(timelines);
          var presses = new Presses(b);

          timelines.repeatableButtonTimeline(new Pane(), e -> {}).playFromStart();
          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.SECONDARY));
          assertEquals(0, presses.shortPresses.get());
          assertEquals(Status.STOPPED, timelines.repeatableButtonTimeline().getStatus());

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());
        });
  }

  @Test
  void repeatableButtonFiresOnPressAndRepeats() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var b = new RepeatableKeyButton();
          b.setTimelines(timelines);
          var presses = new Presses(b);
          assertTrue(b.isRepeatable());
          assertTrue(b.getStyleClass().contains("repeatable-button"));

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.SECONDARY));
          assertEquals(0, presses.shortPresses.get());

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());
          assertEquals(Status.RUNNING, timelines.longPressDelayTimeline().getStatus());

          // long press delay elapsed: repeat starts
          b.onLongPressedEvent(timelines);
          assertEquals(2, presses.shortPresses.get());
          assertEquals(Status.RUNNING, timelines.repeatableButtonTimeline().getStatus());
          timelines.repeatableButtonTimeline().handle(null);
          assertEquals(3, presses.shortPresses.get());

          b.getOnMouseReleased().handle(mouse(MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY));
          assertEquals(Status.STOPPED, timelines.longPressDelayTimeline().getStatus());
          assertEquals(Status.STOPPED, timelines.repeatableButtonTimeline().getStatus());
          assertEquals(3, presses.shortPresses.get());
        });
  }

  @Test
  void movableRepeatableButtonFiresOnRelease() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var b = new RepeatableKeyButton();
          b.setMovable(true);
          b.setTimelines(timelines);
          var presses = new Presses(b);

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          assertEquals(0, presses.shortPresses.get());
          b.getOnMouseReleased().handle(mouse(MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());

          // dragging (moving the keyboard) cancels the key press
          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          b.getOnDragDetected().handle(mouse(MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY));
          assertEquals(Status.STOPPED, timelines.longPressDelayTimeline().getStatus());
          b.getOnMouseReleased().handle(mouse(MouseEvent.MOUSE_RELEASED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());
        });
  }

  @Test
  void multiKeyButtonShortClick() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var parent = new Pane();
          var b = new MultiKeyButton(parent, List.of());
          b.setTimelines(timelines);
          var presses = new Presses(b);
          assertTrue(b.getStyleClass().contains("multi-button"));

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.SECONDARY));
          assertEquals(Status.STOPPED, timelines.longPressDelayTimeline().getStatus());

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          assertEquals(Status.RUNNING, timelines.longPressDelayTimeline().getStatus());
          b.getOnMouseClicked().handle(mouse(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());
          assertEquals(0, presses.longPresses.get());
          assertEquals(Status.STOPPED, timelines.longPressDelayTimeline().getStatus());

          // click after the delay elapsed is not a short press
          b.getOnMouseClicked().handle(mouse(MouseEvent.MOUSE_CLICKED, MouseButton.PRIMARY));
          assertEquals(1, presses.shortPresses.get());
        });
  }

  @Test
  void multiKeyButtonDragIsLongPress() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var b = new MultiKeyButton(new Pane(), List.of());
          b.setTimelines(timelines);
          var presses = new Presses(b);

          b.getOnDragDetected().handle(mouse(MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY));
          assertEquals(0, presses.longPresses.get(), "no press in progress");

          b.getOnMousePressed().handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          b.getOnDragDetected().handle(mouse(MouseEvent.DRAG_DETECTED, MouseButton.PRIMARY));
          assertEquals(1, presses.longPresses.get());
          assertEquals(0, presses.shortPresses.get());
          assertEquals(Status.STOPPED, timelines.longPressDelayTimeline().getStatus());
        });
  }

  @Test
  void multiKeyButtonExtraKeysShareShortPressHandler() {
    runFx(
        () -> {
          var timelines = new Timelines();
          var parent = new Pane();
          var b = new MultiKeyButton(parent, List.of("style.css"));
          b.setTimelines(timelines);
          b.setPrefSize(40, 30);
          var pressed = new ArrayList<Integer>();
          b.setOnShortPressed(e -> pressed.add(((KeyButton) e.getSource()).getKeyCode()));

          b.addExtKeyCode(timelines, 228, "ä");
          b.addExtKeyCode(timelines, 229, "å");
          assertNotNull(b.getOnLongPressed(), "context popup is opened by long press");

          // the extra buttons live in the popup; press them via their mouse handler
          var popup = popupOf(b);
          assertEquals(List.of("style.css"), popup.getStylesheets());
          var extra = KeyboardPaneTest.keyButtons(popup.getContent().get(0));
          assertEquals(2, extra.size());
          assertEquals("ä", extra.get(0).getText());
          assertEquals(40, extra.get(0).getPrefWidth());
          assertFalse(extra.get(0).isFocusTraversable());
          extra
              .get(1)
              .getOnMousePressed()
              .handle(mouse(MouseEvent.MOUSE_PRESSED, MouseButton.PRIMARY));
          assertEquals(List.of(229), pressed);

          // hiding the popup re-enables the parent
          parent.setDisable(true);
          popup.getOnHidden().handle(null);
          assertFalse(parent.isDisable());
        });
  }

  private static MultiKeyPopup popupOf(MultiKeyButton button) throws Exception {
    var field = MultiKeyButton.class.getDeclaredField("context");
    field.setAccessible(true);
    return (MultiKeyPopup) field.get(button);
  }
}
