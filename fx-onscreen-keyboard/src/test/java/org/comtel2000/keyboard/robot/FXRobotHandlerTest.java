/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.robot;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.comtel2000.keyboard.control.FxTestSupport;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FXRobotHandlerTest {

  private Stage stage;
  private TextField field;
  private KeyboardPane keyboard;
  private final List<javafx.scene.input.KeyEvent> events = new ArrayList<>();
  private final FXRobotHandler robot = new FXRobotHandler();

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @BeforeEach
  void setUp() {
    runFx(
        () -> {
          field = new TextField();
          field.addEventFilter(javafx.scene.input.KeyEvent.ANY, events::add);
          keyboard = new KeyboardPane();
          stage = new Stage();
          stage.setScene(new Scene(new VBox(field, keyboard), 300, 200));
          stage.show();
          field.requestFocus();
        });
  }

  @AfterEach
  void tearDown() {
    runFx(stage::close);
  }

  private void send(char ch, boolean ctrl) {
    runFx(() -> robot.sendToComponent(keyboard, ch, ctrl));
    // the robot posts the key events with Platform.runLater
    FxTestSupport.waitForFxEvents();
  }

  @Test
  void typedCharactersReachFocusedTextField() {
    send('h', false);
    send('i', false);
    runFx(() -> assertEquals("hi", field.getText()));
    assertEquals(6, events.size(), "pressed, typed and released per char");
  }

  @Test
  void controlKeysAreMappedToKeyCodes() {
    send('a', false);
    send('b', false);
    send((char) KeyEvent.VK_BACK_SPACE, true);
    runFx(() -> assertEquals("a", field.getText()));
    send((char) KeyEvent.VK_LEFT, true);
    runFx(() -> assertEquals(0, field.getCaretPosition()));
    send((char) KeyEvent.VK_DELETE, true);
    runFx(() -> assertEquals("", field.getText()));
  }

  @Test
  void allSpecialKeysAreFiredAsPressedAndReleased() {
    char[] keys = {
      KeyEvent.VK_ENTER, KeyEvent.VK_ESCAPE, KeyEvent.VK_SPACE, KeyEvent.VK_TAB,
      KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_HOME,
      KeyEvent.VK_END, KeyEvent.VK_PAGE_UP, KeyEvent.VK_PAGE_DOWN, KeyEvent.VK_HELP,
      KeyEvent.VK_PRINTSCREEN, KeyEvent.VK_F1, KeyEvent.VK_F2, KeyEvent.VK_F3,
      KeyEvent.VK_F4, KeyEvent.VK_F5, KeyEvent.VK_F6, KeyEvent.VK_F7,
      KeyEvent.VK_F8, KeyEvent.VK_F9, KeyEvent.VK_F10, KeyEvent.VK_F11,
      KeyEvent.VK_F12
    };
    KeyCode[] expected = {
      KeyCode.ENTER, KeyCode.ESCAPE, KeyCode.SPACE, KeyCode.TAB,
      KeyCode.UP, KeyCode.DOWN, KeyCode.RIGHT, KeyCode.HOME,
      KeyCode.END, KeyCode.PAGE_UP, KeyCode.PAGE_DOWN, KeyCode.HELP,
      KeyCode.PRINTSCREEN, KeyCode.F1, KeyCode.F2, KeyCode.F3,
      KeyCode.F4, KeyCode.F5, KeyCode.F6, KeyCode.F7,
      KeyCode.F8, KeyCode.F9, KeyCode.F10, KeyCode.F11,
      KeyCode.F12
    };
    for (int i = 0; i < keys.length; i++) {
      events.clear();
      send(keys[i], true);
      // TAB may move the focus away from the field; restore it
      runFx(field::requestFocus);
      assertTrue(events.size() >= 1, "no event for " + expected[i]);
      assertEquals(expected[i], events.get(0).getCode());
      assertEquals(javafx.scene.input.KeyEvent.KEY_PRESSED, events.get(0).getEventType());
    }
  }

  @Test
  void ctrlLetterIsSentAsShortcut() {
    send('a', true);
    assertEquals(2, events.size(), "pressed and released");
    var pressed = events.get(0);
    assertEquals(KeyCode.A, pressed.getCode());
    assertTrue(pressed.isShortcutDown());
  }

  @Test
  void ctrlWithUnknownKeyCodeIsTyped() {
    send('€', true);
    assertEquals(3, events.size());
    assertEquals(KeyCode.UNDEFINED, events.get(0).getCode());
  }

  @Test
  void keyboardInPopupSendsToOwnerScene() {
    var popup = FxTestSupport.callFx(Popup::new);
    runFx(
        () -> {
          var popupKeyboard = new KeyboardPane();
          popup.getContent().add(popupKeyboard);
          popup.show(stage);
          field.requestFocus();
          robot.sendToComponent(popupKeyboard, 'x', false);
        });
    FxTestSupport.waitForFxEvents();
    runFx(
        () -> {
          assertEquals("x", field.getText());
          popup.hide();
        });
  }

  @Test
  void noFocusOwnerIsIgnored() {
    runFx(
        () -> {
          var other = new Stage();
          var kb = new KeyboardPane();
          other.setScene(new Scene(kb));
          robot.sendToComponent(kb, 'x', false);
        });
    FxTestSupport.waitForFxEvents();
    assertTrue(events.isEmpty());
  }
}
