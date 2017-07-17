package org.comtel2000.keyboard.control;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KeyboardPopupTest extends ApplicationTest {

  Scene scene;

  @Override
  public void start(Stage stage) {
    scene = new Scene(new Group(), 0.1, 0.1);
    stage.setScene(scene);
    stage.show();
  }

  private void runAndWait(Runnable call) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      call.run();
      latch.countDown();
    });
    latch.await(2, TimeUnit.SECONDS);
  }

  @Test
  public void testLayers() throws InterruptedException {
    KeyboardPopup keyboard = KeyboardPopupBuilder.create().layer(KeyboardLayer.DEFAULT)
        .initLocale(Locale.ENGLISH).build();
    keyboard.registerScene(scene);
    runAndWait(() -> keyboard.setVisible(true));

    Arrays.stream(KeyboardLayer.values()).forEach(l -> {
      try {
        System.out.println("change layer to: " + l);
        runAndWait(() -> keyboard.getKeyboard().switchLayer(l));
        TimeUnit.MILLISECONDS.sleep(100);
        verifyThat("a", isNotNull());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    
    runAndWait(() -> keyboard.setVisible(false));
  }

  @Test
  public void testLayouts() throws InterruptedException {
    KeyboardPopup keyboard = KeyboardPopupBuilder.create().layer(KeyboardLayer.DEFAULT)
        .initLocale(Locale.ENGLISH).build();
    keyboard.registerScene(scene);
    runAndWait(() -> keyboard.setVisible(true));

    Arrays.stream(KeyboardType.values()).forEach(t -> {
      try {
        System.out.println("change type to: " + t);
        runAndWait(() -> keyboard.getKeyboard().setKeyboardType(t));
        TimeUnit.MILLISECONDS.sleep(100);
        verifyThat("ENTER", isNotNull());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    
    runAndWait(() -> keyboard.setVisible(false));
  }

  @Test
  public void multiSwitch() throws InterruptedException {
    KeyboardPopup keyboard = KeyboardPopupBuilder.create().layer(KeyboardLayer.DEFAULT)
        .initLocale(Locale.ENGLISH).build();
    keyboard.registerScene(scene);
    runAndWait(() -> keyboard.setVisible(true));
    IntStream.range(0, 100).forEach(i -> {
      try {
        runAndWait(() -> {
          keyboard.setVisible(false);
          keyboard.getKeyboard().setKeyboardType(KeyboardType.NUMERIC);
          keyboard.setVisible(true);
        });
        verifyThat("0", isNotNull());
        TimeUnit.MILLISECONDS.sleep(10);
        runAndWait(() -> {
          keyboard.setVisible(false);
          keyboard.getKeyboard().setKeyboardType(KeyboardType.TEXT);
          keyboard.setVisible(true);
        });
        TimeUnit.MILLISECONDS.sleep(10);
        verifyThat("a", isNotNull());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
    
    runAndWait(() -> keyboard.setVisible(false));
  }
}
