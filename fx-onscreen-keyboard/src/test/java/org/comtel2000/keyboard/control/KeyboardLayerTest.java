package org.comtel2000.keyboard.control;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KeyboardLayerTest extends ApplicationTest {

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
  public void toggleLayer() throws InterruptedException {
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    verifyThat("0", isNull());
    verifyThat("1", isNull());
    runAndWait(() -> keyboard.switchLayer(KeyboardLayer.NUMBLOCK));

    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    IntStream.rangeClosed(0, 9).forEach(i -> verifyThat("" + i, isNotNull()));

    runAndWait(() -> keyboard.switchLayer(KeyboardLayer.DEFAULT));
    IntStream.rangeClosed('a', 'z')
        .forEach(i -> verifyThat(Character.toString((char) i), isNotNull()));
    verifyThat("0", isNull());
    verifyThat("1", isNull());
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
