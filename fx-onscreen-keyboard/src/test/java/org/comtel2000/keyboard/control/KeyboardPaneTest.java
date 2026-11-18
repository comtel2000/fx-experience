/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Region;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.comtel2000.keyboard.robot.RecordingRobot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class KeyboardPaneTest {

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  static KeyboardPane loadedKeyboard(Locale locale, RecordingRobot robot) throws Exception {
    var kb = new KeyboardPane();
    kb.setLocale(locale);
    kb.addRobotHandler(robot);
    kb.load();
    return kb;
  }

  static List<KeyButton> keyButtons(Node node) {
    var result = new ArrayList<KeyButton>();
    collect(node, result);
    return result;
  }

  private static void collect(Node node, List<KeyButton> result) {
    if (node instanceof KeyButton kb) {
      result.add(kb);
    }
    if (node instanceof Parent p) {
      for (var child : p.getChildrenUnmodifiable()) {
        collect(child, result);
      }
    }
  }

  static Region activeRegion(KeyboardPane kb) {
    assertEquals(1, kb.getChildrenUnmodifiable().size());
    return (Region) kb.getChildrenUnmodifiable().get(0);
  }

  @Test
  void defaultProperties() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          assertTrue(kb.getStyleClass().contains("key-background"));
          assertFalse(kb.isFocusTraversable());
          assertTrue(kb.isCacheLayout());
          assertFalse(kb.isSymbol());
          assertFalse(kb.isShift());
          assertFalse(kb.isControl());
          assertTrue(kb.isSpaceKeyMove());
          assertTrue(kb.isCapsLock());
          assertEquals(0.2d, kb.getScaleOffset());
          assertEquals(1.0d, kb.getScale());
          assertEquals(0.7d, kb.getMinScale());
          assertEquals(5.0d, kb.getMaxScale());
          assertEquals(DefaultLayer.DEFAULT, kb.getLayer());
          assertNull(kb.getLayerPath());
          assertEquals(Locale.getDefault(), kb.getLocale());
          assertNull(kb.getActiveLocale());
          assertNull(kb.getActiveType());
          assertTrue(kb.getRobotHandler().isEmpty());
          assertTrue(kb.getKeyBoardStyle().endsWith("KeyboardButtonStyle.css"));
          assertEquals(kb.getKeyBoardStyle(), kb.getUserAgentStylesheet());
        });
  }

  @Test
  void plainValuesAreCopiedIntoLazyProperties() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.setCacheLayout(false);
          kb.setSymbol(true);
          kb.setShift(true);
          kb.setControl(true);
          kb.setSpaceKeyMove(false);
          kb.setCapsLock(false);
          kb.setScaleOffset(0.5);
          kb.setScale(2.0);
          kb.setMinScale(0.5);
          kb.setMaxScale(3.0);
          kb.setLayer(DefaultLayer.NUMBLOCK);
          kb.setLayerPath(Path.of("x"));
          kb.setLocale(Locale.GERMAN);
          kb.setActiveLocale(Locale.FRENCH);
          kb.setActiveType(KeyboardType.CTRL);
          kb.setKeyBoardStyle("my.css");

          assertFalse(kb.cacheLayoutProperty().get());
          assertTrue(kb.symbolProperty().get());
          assertTrue(kb.shiftProperty().get());
          assertTrue(kb.controlProperty().get());
          assertFalse(kb.spaceKeyMoveProperty().get());
          assertFalse(kb.capsLockProperty().get());
          assertEquals(0.5, kb.scaleOffsetProperty().get());
          assertEquals(2.0, kb.scaleProperty().get());
          assertEquals(2.0, kb.getScaleX());
          assertEquals(2.0, kb.getScaleY());
          assertEquals(0.5, kb.minScaleProperty().get());
          assertEquals(3.0, kb.maxScaleProperty().get());
          assertEquals(DefaultLayer.NUMBLOCK, kb.layerProperty().get());
          assertEquals(Path.of("x"), kb.layerPathProperty().get());
          assertEquals(Locale.GERMAN, kb.localeProperty().get());
          assertEquals(Locale.FRENCH, kb.activeLocaleProperty().get());
          assertEquals(KeyboardType.CTRL, kb.activeTypeProperty().get());
          assertEquals("my.css", kb.keyBoardStyleProperty().get());

          // properties are created once
          assertSame(kb.scaleProperty(), kb.scaleProperty());
        });
  }

  @Test
  void settersWriteThroughToProperties() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.cacheLayoutProperty();
          kb.symbolProperty();
          kb.shiftProperty();
          kb.controlProperty();
          kb.spaceKeyMoveProperty();
          kb.capsLockProperty();
          kb.scaleOffsetProperty();
          kb.scaleProperty();
          kb.minScaleProperty();
          kb.maxScaleProperty();
          kb.layerProperty();
          kb.layerPathProperty();
          kb.localeProperty();
          kb.activeLocaleProperty();
          kb.activeTypeProperty();
          kb.keyBoardStyleProperty();

          kb.setCacheLayout(false);
          kb.setSymbol(true);
          kb.setShift(true);
          kb.setControl(true);
          kb.setSpaceKeyMove(false);
          kb.setCapsLock(false);
          kb.setScaleOffset(0.3);
          kb.setScale(1.5);
          kb.setMinScale(0.4);
          kb.setMaxScale(4.0);
          kb.setLayer(DefaultLayer.NUMBLOCK);
          kb.setLayerPath(Path.of("y"));
          kb.setLocale(Locale.ITALIAN);
          kb.setActiveLocale(Locale.GERMAN);
          kb.setActiveType(KeyboardType.NUMERIC);
          kb.setKeyBoardStyle("other.css");

          assertFalse(kb.isCacheLayout());
          assertTrue(kb.isSymbol());
          assertTrue(kb.isShift());
          assertTrue(kb.isControl());
          assertFalse(kb.isSpaceKeyMove());
          assertFalse(kb.isCapsLock());
          assertEquals(0.3, kb.getScaleOffset());
          assertEquals(1.5, kb.getScale());
          assertEquals(1.5, kb.getScaleX(), "scale listener updates the node scale");
          assertEquals(0.4, kb.getMinScale());
          assertEquals(4.0, kb.getMaxScale());
          assertEquals(DefaultLayer.NUMBLOCK, kb.getLayer());
          assertEquals(Path.of("y"), kb.getLayerPath());
          assertEquals(Locale.ITALIAN, kb.getLocale());
          assertEquals(Locale.GERMAN, kb.getActiveLocale());
          assertEquals(KeyboardType.NUMERIC, kb.getActiveType());
          assertEquals("other.css", kb.getKeyBoardStyle());
        });
  }

  @Test
  void loadBuildsTextLayoutAndDefaultRobot() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.setLocale(Locale.ENGLISH);
          kb.load();

          assertEquals(1, kb.getRobotHandler().size());
          assertTrue(kb.getRobotHandler().get(0) instanceof FXRobotHandler);
          assertTrue(kb.getStylesheets().contains(kb.getKeyBoardStyle()));
          assertEquals(Locale.ENGLISH, kb.getActiveLocale());
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          assertTrue(activeRegion(kb) instanceof KeyboardRegion);
          assertEquals(37, keyButtons(activeRegion(kb)).size(), "38 keys minus one placeholder");
        });
  }

  @Test
  void loadAppliesInitialScale() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.setLocale(Locale.ENGLISH);
          kb.addRobotHandler(new RecordingRobot());
          kb.setScale(1.5);
          kb.load();
          assertEquals(1.5, kb.getScaleX());
          assertEquals(1.5, kb.getScaleY());
        });
  }

  @Test
  void availableLocalesOfEmbeddedLayers() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          var locales = kb.getAvailableLocales();
          assertTrue(locales.containsKey(Locale.ENGLISH));
          assertTrue(locales.containsKey(Locale.GERMAN));
          assertTrue(locales.containsKey(new Locale("es")));
          assertTrue(locales.containsKey(new Locale("ru")));
          assertNotNull(kb.getLayerPath(), "embedded directory is resolved to a path");
          assertSame(locales, kb.getAvailableLocales(), "locales are cached");
        });
  }

  @Test
  void unknownLocaleFallsBackToEnglish() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.JAPANESE, new RecordingRobot());
          assertEquals(Locale.ENGLISH, kb.getActiveLocale());
        });
  }

  @Test
  void countryLocaleUsesLanguageLayout() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.GERMANY, new RecordingRobot());
          assertEquals(Locale.GERMAN, kb.getActiveLocale());
        });
  }

  @Test
  void switchLocaleReplacesLayout() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          var english = activeRegion(kb);

          kb.switchLocale(Locale.GERMAN);
          assertEquals(Locale.GERMAN, kb.getActiveLocale());
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          assertNotSame(english, activeRegion(kb));
          assertTrue(
              keyButtons(activeRegion(kb)).stream().anyMatch(b -> "STRG".equals(b.getText())),
              "german layout has a STRG key");

          var german = activeRegion(kb);
          kb.switchLocale(Locale.GERMAN);
          assertSame(german, activeRegion(kb), "same locale is a no-op");

          kb.resetLocale();
          assertEquals(Locale.ENGLISH, kb.getActiveLocale());
          assertSame(english, activeRegion(kb), "layouts are cached");
        });
  }

  @Test
  void keyboardTypeFromString() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          kb.setKeyboardType("numeric");
          assertEquals(KeyboardType.NUMERIC, kb.getActiveType());
          kb.setKeyboardType((String) null);
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          kb.setKeyboardType("ctrl");
          assertEquals(KeyboardType.CTRL, kb.getActiveType());
          kb.setKeyboardType("");
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          kb.setKeyboardType("symbol");
          kb.setKeyboardType("no-such-type");
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
        });
  }

  @Test
  void keyboardTypeFromModifiers() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());

          kb.setKeyboardType(false, true, false);
          assertEquals(KeyboardType.TEXT_SHIFT, kb.getActiveType());
          assertTrue(kb.isShift());
          assertFalse(kb.isSymbol());

          kb.setKeyboardType(false, false, true);
          assertEquals(KeyboardType.SYMBOL, kb.getActiveType());
          assertTrue(kb.isSymbol());
          assertFalse(kb.isShift());

          kb.setKeyboardType(false, true, true);
          assertEquals(KeyboardType.SYMBOL_SHIFT, kb.getActiveType());
          assertTrue(kb.isSymbol());
          assertTrue(kb.isShift());

          kb.setKeyboardType(true, true, true);
          assertEquals(KeyboardType.CTRL, kb.getActiveType());
          assertTrue(kb.isControl());
          assertFalse(kb.isShift());
          assertFalse(kb.isSymbol());

          kb.setKeyboardType(false, false, false);
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          assertFalse(kb.isControl());
        });
  }

  @Test
  void everyKeyboardTypeHasARegion() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          for (var type : KeyboardType.values()) {
            kb.setKeyboardType(type);
            assertEquals(type, kb.getActiveType());
            assertFalse(keyButtons(activeRegion(kb)).isEmpty(), type + " has keys");
          }
          // email and url layouts do not exist and fall back to the text layout
          kb.setKeyboardType(KeyboardType.TEXT);
          var text = activeRegion(kb);
          kb.setKeyboardType(KeyboardType.EMAIL);
          assertSame(text, activeRegion(kb));
          kb.setKeyboardType(KeyboardType.URL);
          assertSame(text, activeRegion(kb));
        });
  }

  @Test
  void switchLayerLoadsNumblockLayout() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          int defaultKeys = keyButtons(activeRegion(kb)).size();

          kb.switchLayer(DefaultLayer.NUMBLOCK);
          assertEquals(DefaultLayer.NUMBLOCK, kb.getLayer());
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
          assertTrue(keyButtons(activeRegion(kb)).size() > defaultKeys);

          var numblock = activeRegion(kb);
          kb.switchLayer(DefaultLayer.NUMBLOCK);
          assertSame(numblock, activeRegion(kb), "same layer is a no-op");
        });
  }

  @Test
  void customLayerPath() throws URISyntaxException {
    var path = Path.of(KeyboardPaneTest.class.getResource("/xml/default").toURI());
    runFx(
        () -> {
          var kb = new KeyboardPane();
          kb.setLayerPath(path);
          kb.setLocale(Locale.ENGLISH);
          kb.addRobotHandler(new RecordingRobot());
          kb.load();
          var locales = kb.getAvailableLocales();
          assertEquals(path.toString(), locales.get(Locale.ENGLISH));
          assertTrue(locales.containsKey(Locale.GERMAN));
          assertTrue(locales.containsKey(new Locale("ru")));
          assertEquals(KeyboardType.TEXT, kb.getActiveType());

          // ru has no numeric layout and falls back to the english one
          kb.switchLocale(new Locale("ru"));
          kb.setKeyboardType(KeyboardType.NUMERIC);
          assertFalse(keyButtons(activeRegion(kb)).isEmpty());
        });
  }

  @Test
  void layoutCache() {
    runFx(
        () -> {
          var kb = new KeyboardPane();
          var url = KeyboardPane.class.getResource("layer/default/kb-layout.xml");
          assertSame(kb.getKeyboardPane(url), kb.getKeyboardPane(url));
          kb.setCacheLayout(false);
          assertNotSame(kb.getKeyboardPane(url), kb.getKeyboardPane(url));
        });
  }

  @Test
  void sendToComponentForwardsToAllRobots() {
    runFx(
        () -> {
          var r1 = new RecordingRobot();
          var r2 = new RecordingRobot();
          var kb = new KeyboardPane();
          kb.addRobotHandler(r1);
          kb.addRobotHandler(r2);
          assertThrows(UnsupportedOperationException.class, () -> kb.getRobotHandler().add(r1));

          kb.sendToComponent('x', false);
          assertEquals("x", r1.text());
          assertEquals("x", r2.text());

          kb.removeRobotHandler(r2);
          kb.sendToComponent('y', true);
          assertEquals("xy", r1.text());
          assertTrue(r1.last().ctrl());
          assertEquals("x", r2.text());

          kb.removeRobotHandler(r1);
          kb.sendToComponent('z', false); // logs an error, must not fail
        });
  }

  @Test
  void ctrlPlusMinusChangesScale() {
    runFx(
        () -> {
          var robot = new RecordingRobot();
          var kb = new KeyboardPane();
          kb.addRobotHandler(robot);

          kb.sendToComponent('+', true);
          assertEquals(1.2, kb.getScale(), 1e-9);
          kb.sendToComponent('-', true);
          kb.sendToComponent('-', true);
          assertEquals(0.8, kb.getScale(), 1e-9);
          kb.sendToComponent('-', true);
          assertEquals(0.8, kb.getScale(), 1e-9, "min scale is respected");

          kb.setScale(4.9);
          kb.sendToComponent('+', true);
          assertEquals(4.9, kb.getScale(), 1e-9, "max scale is respected");
          assertTrue(robot.sent().isEmpty(), "scale keys are not forwarded");

          kb.sendToComponent('+', false);
          assertEquals("+", robot.text(), "without ctrl the key is forwarded");
        });
  }

  @Test
  void scrollAndZoomChangeScale() {
    runFx(
        () -> {
          var kb = loadedKeyboard(Locale.ENGLISH, new RecordingRobot());
          kb.getOnScroll().handle(scroll(10));
          assertEquals(1.2, kb.getScale(), 1e-9);
          kb.getOnScroll().handle(scroll(-10));
          assertEquals(1.0, kb.getScale(), 1e-9);

          kb.getOnZoom().handle(zoom(2.0));
          assertEquals(2.0, kb.getScale(), 1e-9);
          kb.getOnZoom().handle(zoom(10.0));
          assertEquals(2.0, kb.getScale(), 1e-9, "zoom beyond max scale is ignored");
        });
  }

  private static ScrollEvent scroll(double deltaY) {
    return new ScrollEvent(
        ScrollEvent.SCROLL,
        0,
        0,
        0,
        0,
        false,
        false,
        false,
        false,
        false,
        false,
        0,
        deltaY,
        0,
        deltaY,
        ScrollEvent.HorizontalTextScrollUnits.NONE,
        0,
        ScrollEvent.VerticalTextScrollUnits.NONE,
        0,
        0,
        null);
  }

  private static ZoomEvent zoom(double totalFactor) {
    return new ZoomEvent(
        ZoomEvent.ZOOM,
        0,
        0,
        0,
        0,
        false,
        false,
        false,
        false,
        false,
        false,
        totalFactor,
        totalFactor,
        null);
  }
}
