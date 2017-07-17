package org.comtel2000.keyboard.control;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.comtel2000.keyboard.control.button.KeyButton;
import org.comtel2000.keyboard.control.button.SymbolCode;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.NodeQueryUtils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class KeyboardPaneTest extends ApplicationTest {

  private static final int SHORT_PRESS_DELAY = 20;
  private static final int LONG_PRESS_DELAY = 450;

  private static final Function<Integer, Predicate<Node>> KEY_CODE_FUNC = keyCode -> {
    return node -> ((node instanceof KeyButton)
        && keyCode == KeyButton.class.cast(node).getKeyCode());
  };

  @Override
  public void start(Stage stage) {
    KeyboardPane keyboard = KeyboardBuilder.create().layer(KeyboardLayer.DEFAULT)
        .initLocale(Locale.ENGLISH).build();
    Scene scene = new Scene(keyboard, keyboard.getPrefWidth(), keyboard.getPrefHeight());
    stage.setScene(scene);
    stage.show();
  }

  private void pressShort(String query) {
    pressKey(query, SHORT_PRESS_DELAY);
  }

  private void pressLong(String query) {
    pressKey(query, LONG_PRESS_DELAY);
  }

  private void pressKey(String query, int millis) {
    moveTo(point(query)).press(MouseButton.PRIMARY).sleep(millis, TimeUnit.MILLISECONDS)
        .release(MouseButton.PRIMARY);
  }

  private void pressShort(int keyCode) {
    clickOn(KEY_CODE_FUNC.apply(keyCode), MouseButton.PRIMARY).sleep(SHORT_PRESS_DELAY,
        TimeUnit.MILLISECONDS);
  }

  @Test
  public void toggleShift() {
    IntStream.range('a', 'z' + 1).forEach(i -> verifyThat(Character.toString((char)i), isNotNull()));
    IntStream.range('A', 'H').forEach(i -> verifyThat(Character.toString((char)i), isNull()));
    pressShort(SymbolCode.SHIFT_DOWN);
    IntStream.range('a', 'z' + 1).forEach(i -> verifyThat(Character.toString((char)i), isNull()));
    IntStream.range('A', 'H').forEach(i -> verifyThat(Character.toString((char)i), isNotNull()));
    pressShort(SymbolCode.SHIFT_DOWN);
    IntStream.range('a', 'z' + 1).forEach(i -> verifyThat(Character.toString((char)i), isNotNull()));
    IntStream.range('A', 'H').forEach(i -> verifyThat(Character.toString((char)i), isNull()));
  }

  @Test
  public void toggleControl() {
    verifyThat("a", isNotNull());
    verifyThat("F1", isNull());
    pressShort(SymbolCode.CTRL_DOWN);
    verifyThat("a", isNull());
    verifyThat("F1", isNotNull());
    pressShort(SymbolCode.CTRL_DOWN);
    verifyThat("a", isNotNull());
    verifyThat("F1", isNull());
  }

  @Test
  public void toggleSymbol() {
    verifyThat("a", isNotNull());
    verifyThat("(", isNull());
    pressShort(SymbolCode.SYMBOL_DOWN);
    verifyThat("a", isNull());
    verifyThat("(", isNotNull());
    pressShort(SymbolCode.SYMBOL_DOWN);
    verifyThat("a", isNotNull());
    verifyThat("(", isNull());
  }

  @Test
  public void toggleSymbolShift() {
    verifyThat("a", isNotNull());
    verifyThat("[", isNull());
    pressShort(SymbolCode.SHIFT_DOWN);
    pressShort(SymbolCode.SYMBOL_DOWN);
    verifyThat("a", isNull());
    verifyThat("[", isNotNull());
    pressShort(SymbolCode.SHIFT_DOWN);
    pressShort(SymbolCode.SYMBOL_DOWN);
    verifyThat("a", isNotNull());
    verifyThat("[", isNull());
  }

  @Test
  public void shortEnter() {
    clickOn(point("ENTER"), MouseButton.PRIMARY);
  }

  @Test
  public void shortPressO() {
    pressShort("o");
    verifyThat(".key-background", isEnabled());
    clickOn(MouseButton.PRIMARY);
    verifyThat(".key-background", isEnabled());
  }

  @Test
  public void longPressO() {
    pressLong("o");
    verifyThat(".key-background", isDisabled());
    clickOn(MouseButton.PRIMARY);
    verifyThat(".key-background", isEnabled());
  }

  @Test
  public void longPressA() {
    pressLong("a");
    verifyThat(".key-background", isDisabled());
    clickOn(MouseButton.PRIMARY);
    verifyThat(".key-background", isEnabled());
  }

  @Test
  public void longPressUandAbort() {
    pressLong("u");
    verifyThat(".key-background", isDisabled());
    moveTo(NodeQueryUtils.hasText("a")).clickOn(MouseButton.PRIMARY);
    verifyThat(".key-background", isEnabled());
  }
}
