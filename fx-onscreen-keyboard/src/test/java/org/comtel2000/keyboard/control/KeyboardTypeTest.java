package org.comtel2000.keyboard.control;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.comtel2000.keyboard.control.button.KeyButton;
import org.comtel2000.keyboard.control.button.ShortPressKeyButton;
import org.comtel2000.keyboard.control.button.SymbolCode;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KeyboardTypeTest extends ApplicationTest {

  KeyboardPane keyboard;

  @Override
  public void start(Stage stage) {
    keyboard = KeyboardBuilder.create().layer(KeyboardLayer.DEFAULT).initLocale(Locale.ENGLISH)
        .build();
    Scene scene = new Scene(keyboard, keyboard.getPrefWidth(), keyboard.getPrefHeight());
    stage.setScene(scene);
    stage.show();
  }

  @Test
  public void testKeyboardTypeNumeric() throws InterruptedException {
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.NUMERIC));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNull()));
    IntStream.rangeClosed('0', '9')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));

    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.TEXT));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
  }

  @Test
  public void testKeyboardTypeControl() throws InterruptedException {
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.CTRL));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNull()));
    IntStream.rangeClosed(1, 10).forEach(i -> verifyThat("F" + i, isNotNull()));

    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.TEXT));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
  }

  @Test
  public void testKeyboardTypeSymbol() throws InterruptedException {
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.SYMBOL));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNull()));
    IntStream.rangeClosed('0', '9')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    verifyThat("%", isNotNull());
    verifyThat("$", isNotNull());
    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.TEXT));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
  }

  @Test
  public void toggleNumeric() throws InterruptedException {
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    KeyButton button = new ShortPressKeyButton();
    button.setKeyCode(SymbolCode.NUMERIC_TYPE);
    runAndWait(() -> keyboard.handle(new KeyButtonEvent(button, KeyButtonEvent.SHORT_PRESSED)));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNull()));
    IntStream.rangeClosed('0', '9')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));

    runAndWait(() -> keyboard.setKeyboardType(KeyboardType.TEXT));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
  }

  private void runAndWait(Runnable call) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      call.run();
      latch.countDown();
    });
    latch.await(2, TimeUnit.SECONDS);
  }
}
