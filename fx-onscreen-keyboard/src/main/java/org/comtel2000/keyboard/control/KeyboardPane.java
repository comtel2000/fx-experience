/*******************************************************************************
 * Copyright (c) 2017 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.button.SymbolCode.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.comtel2000.keyboard.control.button.KeyButton;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.comtel2000.keyboard.robot.IRobot;
import org.comtel2000.keyboard.xml.LayoutReader;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class KeyboardPane extends Region implements EventHandler<KeyButtonEvent> {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardPane.class);

  private final EnumMap<KeyboardType, Region> typeRegionMap = new EnumMap<>(KeyboardType.class);

  private final LayoutReader layoutReader = new LayoutReader(this);

  private static final String DEFAULT_CSS = "/css/KeyboardButtonStyle.css";

  private String initKeyboardStyle;
  private StringProperty keyboardStyle;

  private boolean initCacheLayout = true;
  private BooleanProperty cacheLayout;

  private boolean initSymbol;
  private BooleanProperty symbol;

  private boolean initShift;
  private BooleanProperty shift;

  private boolean initControl;
  private BooleanProperty control;

  private boolean initSpaceKeyMove = true;
  private BooleanProperty spaceKeyMove;

  private boolean initCapsLock = true;
  private BooleanProperty capsLock;

  private double initScaleOffset = 0.2d;
  private DoubleProperty scaleOffset;

  private double initScale = 1.0d;
  private DoubleProperty scale;

  private double initMinScale = 0.7d;
  private DoubleProperty minScale;

  private double initMaxScale = 5.0d;
  private DoubleProperty maxScale;

  private KeyboardLayer initLayer = KeyboardLayer.DEFAULT;
  private ObjectProperty<KeyboardLayer> layer;

  private Path initLayerPath;
  private ObjectProperty<Path> layerPath;

  private Locale initLocale = Locale.getDefault();
  private ObjectProperty<Locale> locale;

  private Locale intiActiveLocale;
  private ObjectProperty<Locale> activeLocale;

  private KeyboardType initActiveType;
  private ObjectProperty<KeyboardType> activeType;

  private EventHandler<? super Event> closeEventHandler;

  private double mousePressedX;
  private double mousePressedY;

  private final List<IRobot> robots = new ArrayList<>();

  private final Map<URI, Region> layoutCache = new HashMap<>();

  private final Map<Locale, String> availableLocales = new LinkedHashMap<>();

  private EventHandler<MouseEvent> movedHandler;
  private EventHandler<MouseEvent> draggedHandler;

  public KeyboardPane() {
    getStyleClass().add("key-background");
    setFocusTraversable(false);
  }

  // @Override (JDK 8u40 or later)
  @Override
  public String getUserAgentStylesheet() {
    return getKeyboardStyle();
  }

  public void load() throws URISyntaxException {

    if (robots.isEmpty()) {
      logger.debug("load default fx robot handler");
      robots.add(new FXRobotHandler());
    }
    getStylesheets().add(getKeyboardStyle());

    setLayoutLocale(getLocale());
    setKeyboardType(KeyboardType.TEXT);

    if (getScale() != 1.0d) {
      setScaleX(getScale());
      setScaleY(getScale());
    }

    setOnZoom(e -> {
      double s = getScale() * e.getTotalZoomFactor();
      if (s >= getMinScale() && s <= getMaxScale()) {
        setScale(s);
        e.consume();
      }
    });

    setOnScroll(e -> {
      double s = getScale() + (e.getDeltaY() > 0.0d ? getScaleOffset() : -getScaleOffset());
      if (s >= getMinScale() && s <= getMaxScale()) {
        setScale(s);
        e.consume();
      }
    });
  }

  public void resetLocale() {
    switchLocale(getLocale());
  }
  public void switchLocale(final Object local) {
    if (local instanceof Locale) {
      switchLocale((Locale) local);
      return;
    }
    if (local instanceof String) {
      switchLocale(new Locale((String) local));
      return;
    }
  }
  
  public void switchLocale(final Locale local) {
    try {
      if (local.equals(getActiveLocale())) {
        return;
      }
      setLayoutLocale(local);
      setActiveType(null);
      setKeyboardType(KeyboardType.TEXT);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public Collection<Locale> getLocales() {
    return Collections.unmodifiableCollection(getAvailableLocales().keySet());
  }

  public void switchLayer(final KeyboardLayer layer) {
    if (layer.equals(getLayer())) {
      logger.warn("layer already selected");
      return;
    }
    try {
      setLayerPath(null);
      setLayer(layer);
      availableLocales.clear();

      setActiveLocale(null);
      setLayoutLocale(getLocale());
      setActiveType(null);
      setKeyboardType(KeyboardType.TEXT);

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

  }

  private void setLayoutLocale(final Locale local) throws URISyntaxException {

    logger.debug("try to set keyboard local: {}->{}", getActiveLocale(), local);

    Map<Locale, String> localeMap = getAvailableLocales();
    if (localeMap.containsKey(local)) {
      if (local.equals(getActiveLocale())) {
        logger.debug("locale already active: {}", local);
        return;
      }
      setActiveLocale(local);
    } else if (localeMap.containsKey(Locale.forLanguageTag(local.getLanguage()))) {
      if (Locale.forLanguageTag(local.getLanguage()).equals(getActiveLocale())) {
        logger.debug("locale language already active: {}", local);
        return;
      }
      setActiveLocale(Locale.forLanguageTag(local.getLanguage()));
    } else {
      if (Locale.ENGLISH.equals(getActiveLocale())) {
        logger.info("locale language already active: {}", local);
        return;
      }
      setActiveLocale(Locale.ENGLISH);
    }
    logger.debug("use keyboard local: {}", getActiveLocale());
    String root = localeMap.get(getActiveLocale());

    addTypeRegion(KeyboardType.TEXT, root, "kb-layout.xml");
    addTypeRegion(KeyboardType.TEXT_SHIFT, root, "kb-layout-shift.xml");
    addTypeRegion(KeyboardType.SYMBOL, root, "kb-layout-sym.xml");
    addTypeRegion(KeyboardType.SYMBOL_SHIFT, root, "kb-layout-sym-shift.xml");
    addTypeRegion(KeyboardType.CTRL, root, "kb-layout-ctrl.xml");
    addTypeRegion(KeyboardType.NUMERIC, root, "kb-layout-numeric.xml");
    addTypeRegion(KeyboardType.EMAIL, root, "kb-layout-email.xml");
    addTypeRegion(KeyboardType.URL, root, "kb-layout-url.xml");

  }

  private void addTypeRegion(KeyboardType type, String root, String file)
      throws URISyntaxException {
    URL url = KeyboardPane.class.getResource(root + "/" + file);
    if (url != null) {
      logger.debug("add layout: {}", url);
      typeRegionMap.put(type, getKeyboardPane(url.toURI()));
      return;
    }
    if (Paths.get(root, file).toFile().exists()) {
      URI uri = Paths.get(root, file).toUri();
      if (uri != null) {
        logger.debug("add layout: {}", uri);
        typeRegionMap.put(type, getKeyboardPane(uri));
        return;
      }
    }
    String defaultRoot = getAvailableLocales().get(Locale.ENGLISH);
    if (defaultRoot == null) {
      logger.error("layout: {} / {} not found - no default available", root, file);
      return;
    }
    url = KeyboardPane.class.getResource(defaultRoot + "/" + file);
    if (url != null) {
      logger.debug("add default layout: {}", url);
      typeRegionMap.put(type, getKeyboardPane(url.toURI()));
      return;
    }
    if (Paths.get(defaultRoot, file).toFile().exists()) {
      URI uri = Paths.get(root, file).toUri();
      if (uri != null) {
        logger.debug("add default layout: {}", uri);
        typeRegionMap.put(type, getKeyboardPane(uri));
      }
    }
  }

  private Region getKeyboardPane(URI layout) {
    if (isCacheLayout()) {
      return layoutCache.computeIfAbsent(layout, layoutReader::getLayout);
    }
    return layoutReader.getLayout(layout);
  }

  private Map<Locale, String> getAvailableLocales() {
    if (!availableLocales.isEmpty()) {
      return availableLocales;
    }
    if (getLayerPath() == null) {
      String l = getLayer().toString().toLowerCase(Locale.ENGLISH);
      URL url = Objects.requireNonNull(KeyboardPane.class.getResource("/xml/" + l));
      logger.debug("use embedded layer path: {}", url);
      if (url.toExternalForm().contains("!")) {
        availableLocales.put(Locale.ENGLISH, "/xml/" + l);
        readJarLocales(url);
        return availableLocales;
      }

      try {
        setLayerPath(Paths.get(url.toURI()));
      } catch (URISyntaxException e) {
        logger.error(e.getMessage(), e);
      }
    }
    availableLocales.put(Locale.ENGLISH, getLayerPath().toString());
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(getLayerPath())) {
      for (Path p : stream) {
        if (p.toFile().isDirectory()) {
          Locale l = new Locale(p.getFileName().toString());
          availableLocales.put(l, p.toString());
        }
      }
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    logger.debug("locales: {}", availableLocales.keySet());
    return availableLocales;
  }

  private void readJarLocales(URL url) {
    String[] array = url.toExternalForm().split("!");
    try (FileSystem fs = FileSystems.newFileSystem(URI.create(array[0]), Collections.emptyMap())) {
      final Path path = fs.getPath(array[1]);
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        for (Path p : stream) {
          if (p.toFile().isDirectory()) {
            String lang = p.getFileName().toString().replace("/", "");
            availableLocales.put(new Locale(lang), array[1] + "/" + lang);
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void setKeyboardType(KeyboardType type) {
    logger.debug("try to set type: {}->{}", getActiveType(), type);
    if (type.equals(getActiveType())) {
      return;
    }
    setActiveType(type);
    Region pane;
    switch (type) {
    case NUMERIC:
      setControl(false);
      setShift(false);
      setSymbol(false);
      pane = typeRegionMap.getOrDefault(type, typeRegionMap.get(KeyboardType.SYMBOL));
      break;
    case SYMBOL:
      setControl(false);
      setShift(false);
      setSymbol(true);
      pane = typeRegionMap.get(type);
      break;
    case SYMBOL_SHIFT:
      setControl(false);
      setShift(true);
      setSymbol(true);
      pane = typeRegionMap.get(type);
      break;
    case CTRL:
      setControl(true);
      setShift(false);
      setSymbol(false);
      pane = typeRegionMap.get(type);
      break;
    case TEXT_SHIFT:
      setControl(false);
      setShift(true);
      setSymbol(false);
      pane = typeRegionMap.get(type);
      break;
    case EMAIL:
    case URL:
    default:
      setControl(false);
      setShift(false);
      setSymbol(false);
      pane = typeRegionMap.get(type);
      break;
    }
    if (pane == null) {
      pane = typeRegionMap.get(KeyboardType.TEXT);
    }
    if (pane != null) {
      getChildren().setAll(pane);
    }
  }

  public void setKeyboardType(Object type) {
    setKeyboardType(KeyboardType.findValue(type).orElse(KeyboardType.TEXT));
  }

  private void setKeyboardType(boolean ctrl, boolean shift, boolean symbol) {
    if (ctrl) {
      setKeyboardType(KeyboardType.CTRL);
      return;
    }
    if (symbol) {
      setKeyboardType(shift ? KeyboardType.SYMBOL_SHIFT : KeyboardType.SYMBOL);
      return;
    }
    setKeyboardType(shift ? KeyboardType.TEXT_SHIFT : KeyboardType.TEXT);
  }

  @Override
  public void handle(KeyButtonEvent event) {
    event.consume();
    if (!event.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
      logger.warn("ignore non short pressed events");
      return;
    }
    KeyButton kb = (KeyButton) event.getSource();
    if (kb.getKeyCode() == CLOSE) {
      sendCloseEvent(event);
      return;
    }
    if (kb.getKeyCode() == PRINTSCREEN) {
      printScreen();
      return;
    }
    if (switchType(kb)) {
      return;
    }
    if (switchFunc(kb)) {
      return;
    }
    if (switchSymbol(kb)) {
      return;
    }

    String text = kb.getKeyText();
    if (text != null) {
      for (int i = 0; i < text.length(); i++) {
        sendToComponent(text.charAt(i), isControl());
      }
      return;
    }

    if (kb.getKeyCode() > -1) {
      sendToComponent((char) kb.getKeyCode(), isControl());
      return;
    }

    logger.debug("unknown key code: {}", kb.getKeyCode());
    sendToComponent((char) kb.getKeyCode(), true);
    if (!isCapsLock() && isShift()) {
      setKeyboardType(isControl(), !isShift(), isSymbol());
    }
  }

  private void sendCloseEvent(KeyButtonEvent event) {
    if (closeEventHandler == null) {
      fireEvent(new WindowEvent(this.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
    } else {
      closeEventHandler.handle(event);
    }
  }

  private void printScreen() {
    sendToComponent((char) java.awt.event.KeyEvent.VK_PRINTSCREEN, true);
    Timeline timeline = new Timeline();
    timeline.setDelay(Duration.millis(1000));
    final double opacity = getScene().getWindow().getOpacity();
    getScene().getWindow().setOpacity(0.0);
    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
        new KeyValue(getScene().getWindow().opacityProperty(), opacity)));
    timeline.play();
  }

  private boolean switchType(KeyButton kb) {
    switch (kb.getKeyCode()) {
    case LOCALE_SWITCH:
      switchLocale(Locale.forLanguageTag(kb.getText()));
      return true;
    case SHIFT_DOWN:
      setKeyboardType(isControl(), !isShift(), isSymbol());
      return true;
    case SYMBOL_DOWN:
      setKeyboardType(isControl(), isShift(), !isSymbol());
      return true;
    case CTRL_DOWN:
      setControl(!isControl());
      setKeyboardType(isControl(), isShift(), isSymbol());
      return true;
    case CAPS_LOCK:
      setCapsLock(!isCapsLock());
      return true;
    case NUMERIC_TYPE:
      setKeyboardType(KeyboardType.NUMERIC);
      return true;
    case EMAIL_TYPE:
      setKeyboardType(KeyboardType.EMAIL);
      return true;
    case URL_TYPE:
      setKeyboardType(KeyboardType.URL);
      return true;
    case TEXT_TYPE:
      setKeyboardType(KeyboardType.TEXT);
      return true;
    case TEXT_SHIFT_TYPE:
      setKeyboardType(KeyboardType.TEXT_SHIFT);
      return true;
    case SYMBOL_TYPE:
      setKeyboardType(KeyboardType.SYMBOL);
      return true;
    case SYMBOL_SHIFT_TYPE:
      setKeyboardType(KeyboardType.SYMBOL_SHIFT);
      return true;
    case CONTROL_TYPE:
      setKeyboardType(KeyboardType.CTRL);
      return true;
    default:
      return false;
    }
  }

  private boolean switchFunc(KeyButton kb) {
    switch (kb.getKeyCode()) {
    case F1:
    case F2:
    case F3:
    case F4:
    case F5:
    case F6:
    case F7:
    case F8:
    case F9:
    case F10:
    case F11:
    case F12:
      sendToComponent((char) Math.abs(kb.getKeyCode()), true);
      return true;
    default:
      return false;
    }
  }

  private boolean switchSymbol(KeyButton kb) {
    switch (kb.getKeyCode()) {
    case TAB:
      sendToComponent((char) java.awt.event.KeyEvent.VK_TAB, true);
      return true;
    case BACK_SPACE:
      sendToComponent((char) java.awt.event.KeyEvent.VK_BACK_SPACE, true);
      return true;
    case DELETE:
      sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, true);
      return true;
    case ENTER:
      sendToComponent((char) java.awt.event.KeyEvent.VK_ENTER, true);
      return true;
    case ARROW_UP:
      sendToComponent((char) java.awt.event.KeyEvent.VK_UP, true);
      return true;
    case ARROW_DOWN:
      sendToComponent((char) java.awt.event.KeyEvent.VK_DOWN, true);
      return true;
    case ARROW_LEFT:
      sendToComponent((char) java.awt.event.KeyEvent.VK_LEFT, true);
      return true;
    case ARROW_RIGHT:
      sendToComponent((char) java.awt.event.KeyEvent.VK_RIGHT, true);
      return true;
    case UNDO:
      sendToComponent((char) java.awt.event.KeyEvent.VK_Z, true);
      return true;
    case REDO:
      sendToComponent((char) java.awt.event.KeyEvent.VK_Y, true);
      return true;
    case HOME:
      sendToComponent((char) java.awt.event.KeyEvent.VK_HOME, true);
      return true;
    case END:
      sendToComponent((char) java.awt.event.KeyEvent.VK_END, true);
      return true;
    case PAGE_UP:
      sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_UP, true);
      return true;
    case PAGE_DOWN:
      sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_DOWN, true);
      return true;
    case HELP:
      sendToComponent((char) java.awt.event.KeyEvent.VK_HELP, true);
      return true;
    default:
      return false;
    }
  }

  private void sendToComponent(char ch, boolean ctrl) {

    logger.trace("send ({}) ctrl={}", ch, ctrl);

    if (ctrl) {
      switch (Character.toUpperCase(ch)) {
      case java.awt.event.KeyEvent.VK_MINUS:
        if (getScale() - getScaleOffset() >= getMinScale()) {
          setScale(getScale() - getScaleOffset());
        }
        return;
      case 0x2B:
        if (getScale() + getScaleOffset() <= getMaxScale()) {
          setScale(getScale() + getScaleOffset());
        }
        return;

      default:
        break;
      }
    }

    if (robots.isEmpty()) {
      logger.error("no robot handler available");
      return;
    }
    for (IRobot robot : robots) {
      robot.sendToComponent(this, ch, ctrl);
    }

  }

  public void clearAll() {
    sendToComponent((char) 97, true);
    sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, isControl());
  }

  public void addRobotHandler(IRobot robot) {
    robots.add(robot);
  }

  public List<IRobot> getRobotHandler() {
    return Collections.unmodifiableList(robots);
  }

  public void removeRobotHandler(IRobot robot) {
    robots.remove(robot);
  }

  public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
    closeEventHandler = value;
  }

  public final String getKeyboardStyle() {
    if (keyboardStyle != null) {
      return keyboardStyle.get();
    }
    if (initKeyboardStyle == null) {
      URL stylesheet = KeyboardPane.class.getResource(DEFAULT_CSS);
      initKeyboardStyle = stylesheet.toString();
    }
    return initKeyboardStyle;
  }

  public final void setKeyboardStyle(String style) {
    if (keyboardStyle == null) {
      initKeyboardStyle = style;
    } else {
      keyboardStyle.set(style);
    }
  }

  public final StringProperty keyboardStyleProperty() {
    if (keyboardStyle == null) {
      keyboardStyle = new SimpleStringProperty(this, "keyBoardStyle", initKeyboardStyle);
    }
    return keyboardStyle;
  }

  public final boolean isCacheLayout() {
    return cacheLayout == null ? initCacheLayout : cacheLayout.get();
  }

  public final void setCacheLayout(boolean s) {
    if (cacheLayout == null) {
      initCacheLayout = s;
    } else {
      cacheLayout.set(s);
    }
  }

  public final BooleanProperty cacheLayoutProperty() {
    if (cacheLayout == null) {
      cacheLayout = new SimpleBooleanProperty(this, "cacheLayout", initCacheLayout);
    }
    return cacheLayout;
  }

  public final boolean isSymbol() {
    return symbol == null ? initSymbol : symbol.get();
  }

  public final void setSymbol(boolean s) {
    if (symbol == null) {
      initSymbol = s;
    } else {
      symbol.set(s);
    }
  }

  public final BooleanProperty symbolProperty() {
    if (symbol == null) {
      symbol = new SimpleBooleanProperty(this, "symbol", initSymbol);
    }
    return symbol;
  }

  public final boolean isShift() {
    return shift == null ? initShift : shift.get();
  }

  public final void setShift(boolean s) {
    if (shift == null) {
      initShift = s;
    } else {
      shift.set(s);
    }
  }

  public final BooleanProperty shiftProperty() {
    if (shift == null) {
      shift = new SimpleBooleanProperty(this, "shift", initShift);
    }
    return shift;
  }

  public final boolean isControl() {
    return control == null ? initControl : control.get();
  }

  public final void setControl(boolean s) {
    if (control == null) {
      initControl = s;
    } else {
      control.set(s);
    }
  }

  public final BooleanProperty controlProperty() {
    if (control == null) {
      control = new SimpleBooleanProperty(this, "control", initControl);
    }
    return control;
  }

  public final boolean isSpaceKeyMove() {
    return spaceKeyMove == null ? initSpaceKeyMove : spaceKeyMove.get();
  }

  public final void setSpaceKeyMove(boolean s) {
    if (spaceKeyMove == null) {
      initSpaceKeyMove = s;
    } else {
      spaceKeyMove.set(s);
    }
  }

  public final BooleanProperty spaceKeyMoveProperty() {
    if (spaceKeyMove == null) {
      spaceKeyMove = new SimpleBooleanProperty(this, "spaceKeyMove", initSpaceKeyMove);
    }
    return spaceKeyMove;
  }

  public final boolean isCapsLock() {
    return capsLock == null ? initCapsLock : capsLock.get();
  }

  public final void setCapsLock(boolean s) {
    if (capsLock == null) {
      initCapsLock = s;
    } else {
      capsLock.set(s);
    }
  }

  public final BooleanProperty capsLockProperty() {
    if (capsLock == null) {
      capsLock = new SimpleBooleanProperty(this, "capsLock", initCapsLock);
    }
    return capsLock;
  }

  public final double getScaleOffset() {
    return scaleOffset == null ? initScaleOffset : scaleOffset.get();
  }

  public final void setScaleOffset(double s) {
    if (scaleOffset == null) {
      initScaleOffset = s;
    } else {
      scaleOffset.set(s);
    }
  }

  public final DoubleProperty scaleOffsetProperty() {
    if (scaleOffset == null) {
      scaleOffset = new SimpleDoubleProperty(this, "scaleOffset", initScaleOffset);
    }
    return scaleOffset;
  }

  public final double getScale() {
    return scale == null ? initScale : scale.get();
  }

  public final void setScale(double s) {
    if (scale == null) {
      initScale = s;
      setScaleX(initScale);
      setScaleY(initScale);
    } else {
      scale.set(s);
    }
  }

  public final DoubleProperty scaleProperty() {
    if (scale == null) {
      scale = new SimpleDoubleProperty(this, "scale", initScale);
      scale.addListener((l, o, s) -> {
        if (o != s) {
          setScaleX(s.doubleValue());
          setScaleY(s.doubleValue());
        }
      });
    }
    return scale;
  }

  public final double getMinScale() {
    return minScale == null ? initMinScale : minScale.get();
  }

  public final void setMinScale(double s) {
    if (minScale == null) {
      initMinScale = s;
    } else {
      minScale.set(s);
    }
  }

  public final DoubleProperty minScaleProperty() {
    if (minScale == null) {
      minScale = new SimpleDoubleProperty(this, "minScale", initMinScale);
    }
    return minScale;
  }

  public final double getMaxScale() {
    return maxScale == null ? initMaxScale : maxScale.get();
  }

  public final void setMaxScale(double s) {
    if (maxScale == null) {
      initMaxScale = s;
    } else {
      maxScale.set(s);
    }
  }

  public final DoubleProperty maxScaleProperty() {
    if (maxScale == null) {
      maxScale = new SimpleDoubleProperty(this, "maxScale", initMaxScale);
    }
    return maxScale;
  }

  public final KeyboardLayer getLayer() {
    return layer == null ? initLayer : layer.get();
  }

  public final void setLayer(KeyboardLayer l) {
    if (layer == null) {
      initLayer = l;
    } else {
      layer.set(l);
    }
  }

  public final ObjectProperty<KeyboardLayer> layerProperty() {
    if (layer == null) {
      layer = new SimpleObjectProperty<>(this, "layer", initLayer);
    }
    return layer;
  }

  public final Path getLayerPath() {
    return layerPath == null ? initLayerPath : layerPath.get();
  }

  public final void setLayerPath(Path l) {
    if (layerPath == null) {
      initLayerPath = l;
    } else {
      layerPath.set(l);
    }
  }

  public final ObjectProperty<Path> layerPathProperty() {
    if (layerPath == null) {
      layerPath = new SimpleObjectProperty<>(this, "layerPath", initLayerPath);
    }
    return layerPath;
  }

  public final Locale getLocale() {
    return locale == null ? initLocale : locale.get();
  }

  public final void setLocale(Locale l) {
    if (locale == null) {
      initLocale = l;
    } else {
      locale.set(l);
    }
  }

  public final ObjectProperty<Locale> localeProperty() {
    if (locale == null) {
      locale = new SimpleObjectProperty<>(this, "locale", initLocale);
    }
    return locale;
  }

  public final Locale getActiveLocale() {
    return activeLocale == null ? intiActiveLocale : activeLocale.get();
  }

  public final void setActiveLocale(Locale l) {
    if (activeLocale == null) {
      intiActiveLocale = l;
    } else {
      activeLocale.set(l);
    }
  }

  public final ObjectProperty<Locale> activeLocaleProperty() {
    if (activeLocale == null) {
      activeLocale = new SimpleObjectProperty<>(this, "activeLocale", intiActiveLocale);
    }
    return activeLocale;
  }

  public final KeyboardType getActiveType() {
    return activeType == null ? initActiveType : activeType.get();
  }

  public final void setActiveType(KeyboardType l) {
    if (activeType == null) {
      initActiveType = l;
    } else {
      activeType.set(l);
    }
  }

  public final ObjectProperty<KeyboardType> activeTypeProperty() {
    if (activeType == null) {
      activeType = new SimpleObjectProperty<>(this, "activeType", initActiveType);
    }
    return activeType;
  }

  public void installMoveHandler(Node node) {
    if (movedHandler == null) {
      movedHandler = h -> {
        if (isSpaceKeyMove()) {
          mousePressedX = getScene().getWindow().getX() - h.getScreenX();
          mousePressedY = getScene().getWindow().getY() - h.getScreenY();
        }
      };
    }
    if (draggedHandler == null) {
      draggedHandler = h -> {
        if (isSpaceKeyMove()) {
          getScene().getWindow().setX(h.getScreenX() + mousePressedX);
          getScene().getWindow().setY(h.getScreenY() + mousePressedY);
        }
      };
    }
    node.setOnMouseMoved(movedHandler);
    node.setOnMouseDragged(draggedHandler);
  }

}
