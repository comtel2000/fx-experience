/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.comtel2000.keyboard.control.FxTestSupport;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.keyboard.control.VkProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FXOKTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @AfterEach
  void reset() {
    FXOK.registerPopup(null);
  }

  @Test
  void vkPropertiesFromNode() {
    runFx(
        () -> {
          var field = new TextField();
          field.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
          field.getProperties().put(VkProperties.VK_LOCALE, VkProperties.VK_LOCALE_DE);
          field.getProperties().put("other", "ignored");
          assertEquals(
              Map.of(
                  VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC,
                  VkProperties.VK_LOCALE, VkProperties.VK_LOCALE_DE),
              FXOK.getVkProperties(field));
        });
  }

  @Test
  void vkPropertiesFromParent() {
    runFx(
        () -> {
          var field = new TextField();
          var parent = new HBox(field);
          parent.getProperties().put(VkProperties.VK_STATE, VkProperties.VK_STATE_DISABLED);
          parent.getProperties().put("x", 1);
          assertEquals(
              Map.of(VkProperties.VK_STATE, VkProperties.VK_STATE_DISABLED),
              FXOK.getVkProperties(field));
        });
  }

  @Test
  void noVkProperties() {
    runFx(
        () -> {
          assertTrue(FXOK.getVkProperties(new TextField()).isEmpty());
          assertTrue(
              FXOK.getVkProperties(new HBox(new TextField()).getChildren().get(0)).isEmpty());
        });
  }

  @Test
  void registerPopup() {
    runFx(
        () -> {
          assertNull(FXOK.getPopup());
          // without popup this is a no-op
          FXOK.setVisible(Visibility.SHOW, new TextField());

          var popup = new KeyBoardPopup(new KeyboardPane());
          FXOK.registerPopup(popup);
          assertSame(popup, FXOK.getPopup());
          FXOK.setVisible(Visibility.HIDE, new TextField());
        });
  }
}
