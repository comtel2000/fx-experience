/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.Animation;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.RecordingRobot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class KeyButtonEventHandlerTest {

  private RecordingRobot robot;
  private KeyboardPane kb;

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @BeforeEach
  void setUp() {
    robot = new RecordingRobot();
    kb = FxTestSupport.callFx(() -> KeyboardPaneTest.loadedKeyboard(Locale.ENGLISH, robot));
  }

  private void press(int keyCode) {
    press(keyCode, null, null);
  }

  private void press(int keyCode, String text, String keyText) {
    var button = new ShortPressKeyButton();
    button.setKeyCode(keyCode);
    button.setText(text);
    button.setKeyText(keyText);
    var event = new KeyButtonEvent(button, KeyButtonEvent.SHORT_PRESSED);
    kb.handle(event);
    assertTrue(event.isConsumed());
  }

  @Test
  void printableKeyIsSent() {
    runFx(
        () -> {
          press('a');
          assertEquals("a", robot.text());
          assertFalse(robot.last().ctrl());
        });
  }

  @Test
  void keyOutputTextIsSentCharByChar() {
    runFx(
        () -> {
          press('w', "www", "www.");
          assertEquals("www.", robot.text());
        });
  }

  @Test
  void unknownNegativeKeyIsSentWithCtrl() {
    runFx(
        () -> {
          press(-99);
          assertEquals(1, robot.sent().size());
          assertTrue(robot.last().ctrl());
        });
  }

  @Test
  void controlModeSendsCtrlFlag() {
    runFx(
        () -> {
          press(StandardKeyCode.CTRL_DOWN);
          assertTrue(kb.isControl());
          assertEquals(KeyboardType.CTRL, kb.getActiveType());
          press('c');
          assertTrue(robot.last().ctrl());

          press(StandardKeyCode.CTRL_DOWN);
          assertFalse(kb.isControl());
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
        });
  }

  @Test
  void shiftAndSymbolToggleLayout() {
    runFx(
        () -> {
          press(StandardKeyCode.SHIFT_DOWN);
          assertEquals(KeyboardType.TEXT_SHIFT, kb.getActiveType());
          press(StandardKeyCode.SYMBOL_DOWN);
          assertEquals(KeyboardType.SYMBOL_SHIFT, kb.getActiveType());
          press(StandardKeyCode.SHIFT_DOWN);
          assertEquals(KeyboardType.SYMBOL, kb.getActiveType());
          press(StandardKeyCode.SYMBOL_DOWN);
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
        });
  }

  @Test
  void shiftIsReleasedAfterKeyWithoutCapsLock() {
    runFx(
        () -> {
          press(StandardKeyCode.CAPS_LOCK);
          assertFalse(kb.isCapsLock());
          press(StandardKeyCode.SHIFT_DOWN);
          assertTrue(kb.isShift());
          press('A');
          assertFalse(kb.isShift());
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
        });
  }

  @Test
  void shiftStaysWithCapsLock() {
    runFx(
        () -> {
          assertTrue(kb.isCapsLock());
          press(StandardKeyCode.SHIFT_DOWN);
          press('A');
          press('B');
          assertTrue(kb.isShift());
          assertEquals("AB", robot.text());
        });
  }

  @Test
  void typeKeysSwitchLayout() {
    runFx(
        () -> {
          press(StandardKeyCode.NUMERIC_TYPE);
          assertEquals(KeyboardType.NUMERIC, kb.getActiveType());
          press(StandardKeyCode.EMAIL_TYPE);
          assertEquals(KeyboardType.EMAIL, kb.getActiveType());
          press(StandardKeyCode.URL_TYPE);
          assertEquals(KeyboardType.URL, kb.getActiveType());
        });
  }

  @Test
  void localeSwitchKey() {
    runFx(
        () -> {
          press(StandardKeyCode.LOCALE_SWITCH, "DE", null);
          assertEquals(Locale.GERMAN, kb.getActiveLocale());
          press(StandardKeyCode.LOCALE_SWITCH, "EN", null);
          assertEquals(Locale.ENGLISH, kb.getActiveLocale());
        });
  }

  @ParameterizedTest
  @CsvSource({
    "-4, " + KeyEvent.VK_TAB,
    "-5, " + KeyEvent.VK_BACK_SPACE,
    "-8, " + KeyEvent.VK_DELETE,
    "-9, " + KeyEvent.VK_ENTER,
    "-10, " + KeyEvent.VK_UP,
    "-11, " + KeyEvent.VK_DOWN,
    "-12, " + KeyEvent.VK_LEFT,
    "-13, " + KeyEvent.VK_RIGHT,
    "-14, " + KeyEvent.VK_Z,
    "-15, " + KeyEvent.VK_Y,
    "-20, " + KeyEvent.VK_HOME,
    "-21, " + KeyEvent.VK_END,
    "-22, " + KeyEvent.VK_PAGE_UP,
    "-23, " + KeyEvent.VK_PAGE_DOWN,
    "-24, " + KeyEvent.VK_HELP,
    "-112, " + KeyEvent.VK_F1,
    "-117, " + KeyEvent.VK_F6,
    "-123, " + KeyEvent.VK_F12
  })
  void specialKeysAreSentAsControlKeys(int keyCode, int expected) {
    runFx(
        () -> {
          press(keyCode);
          assertEquals(1, robot.sent().size());
          assertEquals((char) expected, robot.last().ch());
          assertTrue(robot.last().ctrl());
        });
  }

  @Test
  void nonShortPressedEventsStopTimelines() {
    runFx(
        () -> {
          var button = new RepeatableKeyButton();
          kb.timelines().longPressDelayTimeline(button, e -> {}).playFromStart();
          kb.handle(new KeyButtonEvent(button, KeyButtonEvent.LONG_PRESSED));
          assertEquals(
              Animation.Status.STOPPED, kb.timelines().longPressDelayTimeline().getStatus());
          assertTrue(robot.sent().isEmpty());
        });
  }

  @Test
  void closeKeyCallsCloseHandler() {
    var closed = new AtomicReference<Event>();
    runFx(
        () -> {
          kb.setOnKeyboardCloseButton(closed::set);
          press(StandardKeyCode.CLOSE);
        });
    assertTrue(FxTestSupport.waitFor(() -> closed.get() != null, 2000));
  }

  @Test
  void closeKeyWithoutHandlerRequestsWindowClose() {
    var stage = FxTestSupport.callFx(Stage::new);
    var closeRequested = new AtomicReference<Boolean>(false);
    runFx(
        () -> {
          stage.setScene(new Scene(kb));
          kb.addEventHandler(
              javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST, e -> closeRequested.set(true));
          press(StandardKeyCode.CLOSE);
        });
    assertTrue(FxTestSupport.waitFor(closeRequested::get, 2000));
  }

  @Test
  void printScreenHidesWindowTemporarily() {
    var stage = FxTestSupport.callFx(Stage::new);
    runFx(
        () -> {
          stage.setScene(new Scene(kb));
          stage.setOpacity(0.8);
          press(StandardKeyCode.PRINTSCREEN);
          assertEquals(0.0, stage.getOpacity());
          assertEquals((char) KeyEvent.VK_PRINTSCREEN, robot.last().ch());
        });
    assertTrue(FxTestSupport.waitFor(() -> stage.getOpacity() > 0.79, 5000));
  }
}
