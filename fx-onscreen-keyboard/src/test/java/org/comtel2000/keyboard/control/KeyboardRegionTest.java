/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.comtel2000.keyboard.control.KeyboardPaneTest.keyButtons;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.RecordingRobot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KeyboardRegionTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  private static URL layout(String name) {
    return KeyboardRegionTest.class.getResource("/xml/region/" + name);
  }

  private static KeyButton byCode(List<KeyButton> buttons, int code) {
    return buttons.stream().filter(b -> b.getKeyCode() == code).findFirst().orElseThrow();
  }

  @Test
  void parsesKeyboardAttributes() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          var region = new KeyboardRegion(kb, layout("kb-features.xml"));
          assertEquals(Pos.CENTER, region.getAlignment());
          assertEquals(4, region.getVgap());
          assertTrue(region.getStyleClass().contains("key-background-row"));

          var rows = region.getChildren();
          assertEquals(2, rows.size());
          var firstRow = (GridPane) rows.get(0);
          assertEquals(3, firstRow.getHgap());
          assertEquals(7, firstRow.getChildren().size(), "placeholder + 6 keys");
          assertEquals(10, firstRow.getColumnConstraints().get(0).getPrefWidth());
          assertEquals(30, firstRow.getRowConstraints().get(0).getPrefHeight());
          assertNull(firstRow.getRowConstraints().get(0).getValignment());

          var secondRow = (GridPane) rows.get(1);
          assertEquals(VPos.BOTTOM, secondRow.getRowConstraints().get(0).getValignment());
        });
  }

  @Test
  void createsButtonTypesFromAttributes() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.setLocale(Locale.ENGLISH);
          var region = new KeyboardRegion(kb, layout("kb-features.xml"));
          var buttons = keyButtons(region);
          assertEquals(12, buttons.size());

          var multi = byCode(buttons, 97);
          assertInstanceOf(MultiKeyButton.class, multi);
          assertEquals("a", multi.getText());

          var hex = byCode(buttons, 0x62);
          assertInstanceOf(ShortPressKeyButton.class, hex);
          assertEquals(Pos.BASELINE_LEFT, hex.getAlignment());

          var noLabel = byCode(buttons, 99);
          assertEquals("c", noLabel.getText(), "label defaults to the key code char");
          assertEquals(Pos.BASELINE_RIGHT, noLabel.getAlignment());

          var outputText = byCode(buttons, 100);
          assertEquals("dot.", outputText.getKeyText());
          assertEquals(Pos.BASELINE_CENTER, outputText.getAlignment());

          var backspace = byCode(buttons, StandardKeyCode.BACK_SPACE);
          assertInstanceOf(RepeatableKeyButton.class, backspace);
          assertTrue(backspace.isRepeatable());
          assertTrue(backspace.getStyleClass().contains("font-fxkeyboard"));
          assertTrue(backspace.getStyleClass().contains("small-style"));
          assertNull(backspace.getOnLongPressed(), "repeatable delete keys repeat instead");

          var delete = byCode(buttons, StandardKeyCode.DELETE);
          assertFalse(delete.getOnLongPressed() == null, "delete gets a long press handler");

          var shift = byCode(buttons, StandardKeyCode.SHIFT_DOWN);
          assertTrue(shift.isSticky());
          assertTrue(shift.getStyleClass().contains("sticky-style"));

          var space = byCode(buttons, 32);
          assertTrue(space.isMovable());
          assertTrue(space.getStyleClass().contains("movable-style"));
          assertTrue(space.getOnMouseDragged() != null);

          var locale = byCode(buttons, StandardKeyCode.LOCALE_SWITCH);
          assertInstanceOf(MultiKeyButton.class, locale);

          var close = byCode(buttons, StandardKeyCode.CLOSE);
          var icon = assertInstanceOf(Label.class, close.getGraphic());
          assertTrue(icon.getStyleClass().contains("icon-close"));
          assertEquals(ContentDisplay.CENTER, close.getContentDisplay());

          var missingImage = byCode(buttons, StandardKeyCode.ENTER);
          assertNull(missingImage.getGraphic());

          var ctrl = byCode(buttons, StandardKeyCode.CTRL_DOWN);
          assertEquals(
              List.of("button", "key-button"),
              ctrl.getStyleClass(),
              "styles without a leading '.' are ignored");

          for (var b : buttons) {
            assertFalse(b.isFocusTraversable());
            assertTrue(b.isPickOnBounds());
            assertEquals(kb, b.getOnShortPressed());
          }
        });
  }

  @Test
  void columnAlignmentFollowsEdgeFlags() {
    runFx(
        () -> {
          var region = new KeyboardRegion(new KeyboardPane(), layout("kb-features.xml"));
          var row = (GridPane) region.getChildren().get(0);
          var cc = row.getColumnConstraints();
          assertEquals(HPos.CENTER, cc.get(1).getHalignment());
          assertEquals(HPos.LEFT, cc.get(2).getHalignment());
          assertEquals(HPos.RIGHT, cc.get(3).getHalignment());
          assertEquals(HPos.CENTER, cc.get(4).getHalignment());
        });
  }

  @Test
  void deleteLongPressSendsSelectAllAndDelete() {
    runFx(
        () -> {
          var robot = new RecordingRobot();
          var kb = new KeyboardPane();
          kb.addRobotHandler(robot);
          var region = new KeyboardRegion(kb, layout("kb-features.xml"));
          var delete = byCode(keyButtons(region), StandardKeyCode.DELETE);
          delete.fireEvent(new KeyButtonEvent(delete, KeyButtonEvent.LONG_PRESSED));
          assertEquals(2, robot.sent().size());
          assertEquals('a', robot.sent().get(0).ch());
          assertTrue(robot.sent().get(0).ctrl());
          assertEquals((char) java.awt.event.KeyEvent.VK_DELETE, robot.sent().get(1).ch());
        });
  }

  @Test
  void emptyStyleAttributesAreIgnored() {
    runFx(
        () -> {
          var region = new KeyboardRegion(new KeyboardPane(), layout("kb-empty-style.xml"));
          var buttons = keyButtons(region);
          assertEquals(2, buttons.size(), "an empty style must not abort the layout");
          assertEquals("a", buttons.get(0).getText());
          assertNull(buttons.get(0).getGraphic());
        });
  }

  @Test
  void invalidKeyCodeStopsParsingWithoutException() {
    runFx(
        () -> {
          var region = new KeyboardRegion(new KeyboardPane(), layout("kb-invalid.xml"));
          var buttons = keyButtons(region);
          assertEquals(1, buttons.size(), "parsing stops at the invalid key");
          assertEquals("a", buttons.get(0).getText());
        });
  }

  @Test
  void allEmbeddedLayoutsParse() throws Exception {
    var base = KeyboardPane.class.getResource("layer");
    try (var files = java.nio.file.Files.walk(java.nio.file.Path.of(base.toURI()))) {
      var layouts =
          files
              .filter(p -> p.getFileName().toString().endsWith(".xml"))
              .map(
                  p -> {
                    try {
                      return p.toUri().toURL();
                    } catch (java.net.MalformedURLException e) {
                      throw new IllegalStateException(e);
                    }
                  })
              .toList();
      assertFalse(layouts.isEmpty());
      runFx(
          () -> {
            var kb = new KeyboardPane();
            kb.setLocale(Locale.ENGLISH);
            for (var url : layouts) {
              var region = new KeyboardRegion(kb, url);
              assertFalse(keyButtons(region).isEmpty(), "no keys in " + url);
            }
          });
    }
  }
}
