/*******************************************************************************
 * Copyright (c) 2025 comtel2000
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

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.comtel2000.keyboard.robot.IRobot;
import org.slf4j.LoggerFactory;

import javafx.animation.KeyFrame;
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

  private static final String DEFAULT_CSS = "KeyboardButtonStyle.css";

  private final List<IRobot> robots = new ArrayList<>();
  private final Map<URL, Region> layoutCache = new HashMap<>();

  private String _keyBoardStyle;
  private StringProperty keyBoardStyle;
  private boolean _cacheLayout = true;
  private BooleanProperty cacheLayout;
  private boolean _symbol;
  private BooleanProperty symbol;
  private boolean _shift;
  private BooleanProperty shift;
  private boolean _control;
  private BooleanProperty control;
  private boolean _spaceKeyMove = true;
  private BooleanProperty spaceKeyMove;
  private boolean _capsLock = true;
  private BooleanProperty capsLock;
  private double _scaleOffset = 0.2d;
  private DoubleProperty scaleOffset;
  private double _scale = 1.0d;
  private DoubleProperty scale;
  private double _minScale = 0.7d;
  private DoubleProperty minScale;
  private double _maxScale = 5.0d;
  private DoubleProperty maxScale;
  private DefaultLayer _layer = DefaultLayer.DEFAULT;
  private ObjectProperty<DefaultLayer> layer;
  private Path _layerPath;
  private ObjectProperty<Path> layerPath;
  private Locale _locale = Locale.getDefault();
  private ObjectProperty<Locale> locale;
  private Locale _activeLocale;
  private ObjectProperty<Locale> activeLocale;
  private KeyboardType _activeType;
  private ObjectProperty<KeyboardType> activeType;
  private EventHandler<? super Event> closeEventHandler;

  private double mousePressedX;

  private double mousePressedY;

  private EventHandler<MouseEvent> movedHandler;

  private EventHandler<MouseEvent> draggedHandler;

  private final KeyButtonEventHandler keyButtonEventHandler;
  private final LayoutLocaleSwitcher layoutLocaleSwitcher;

  private final KeyboardLocales keyboardLocalesSupplier;

  public KeyboardPane() {
    getStyleClass().add("key-background");
    setFocusTraversable(false);
    keyButtonEventHandler = new KeyButtonEventHandler(this);
    layoutLocaleSwitcher = new LayoutLocaleSwitcher(this);
    keyboardLocalesSupplier = new KeyboardLocales(this);
  }

  @Override
  public String getUserAgentStylesheet() {
    return getKeyBoardStyle();
  }

  public void load() throws Exception {
    if (robots.isEmpty()) {
      logger.debug("load default fx robot handler");
      robots.add(new FXRobotHandler());
    }
    getStylesheets().add(getKeyBoardStyle());

    layoutLocaleSwitcher.setLayout(getLocale());
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

  void setKeyboardType(boolean ctrl, boolean shift, boolean symbol) {
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

  public void resetLocale() {
    switchLocale(getLocale());
  }

  public void switchLocale(final Locale local) {
    try {
      if (local.equals(getActiveLocale())) {
        return;
      }
      layoutLocaleSwitcher.setLayout(local);
      setActiveType(null);
      setKeyboardType(KeyboardType.TEXT);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public void switchLayer(final DefaultLayer layer) {
    if (layer.equals(getLayer())) {
      logger.warn("layer already selected");
      return;
    }
    try {
      setLayerPath(null);
      setLayer(layer);
      keyboardLocalesSupplier.reset();

      setActiveLocale(null);
      layoutLocaleSwitcher.setLayout(getLocale());
      setActiveType(null);
      setKeyboardType(KeyboardType.TEXT);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

  }

  Map<Locale, String> getAvailableLocales() {
    return keyboardLocalesSupplier.get();
  }

  public void setKeyboardType(String type) {
    try {
      setKeyboardType(type == null || type.isEmpty() ? KeyboardType.TEXT : KeyboardType.valueOf(type.toUpperCase(Locale.ENGLISH)));
    } catch (Exception e) {
      logger.error("unknown type: {}", type);
      setKeyboardType(KeyboardType.TEXT);
    }
  }

  public void setKeyboardType(KeyboardType type) {
    layoutLocaleSwitcher.switchKeyboardTypeRegion(type, getChildren()::setAll);
  }

  Region getKeyboardPane(URL layout) {
    if (isCacheLayout()) {
      return layoutCache.computeIfAbsent(layout, this::createKeyboardPane);
    }
    return createKeyboardPane(layout);
  }

  private Region createKeyboardPane(URL layout) {
    return new KeyboardRegion(this, layout);
  }

  void fireCloseEvent(Event event) {
    if (closeEventHandler == null) {
      new Timeline(new KeyFrame(Duration.millis(50), ev -> fireEvent(new WindowEvent(getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST)))).playFromStart();
    } else {
      new Timeline(new KeyFrame(Duration.millis(50), ev -> closeEventHandler.handle(event))).playFromStart();
    }
  }

  /**
   * send keyEvent to iRobot implementation
   *
   * @param ch
   * @param ctrl
   */
  void sendToComponent(char ch, boolean ctrl) {

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

  public final String getKeyBoardStyle() {
    if (keyBoardStyle != null) {
      return keyBoardStyle.get();
    }
    if (_keyBoardStyle == null) {
      var stylesheet = KeyboardPane.class.getResource(DEFAULT_CSS);
      _keyBoardStyle = stylesheet.toString();
    }
    return _keyBoardStyle;
  }

  public final void setKeyBoardStyle(String style) {
    if (keyBoardStyle == null) {
      _keyBoardStyle = style;
    } else {
      keyBoardStyle.set(style);
    }
  }

  public final StringProperty keyBoardStyleProperty() {
    if (keyBoardStyle == null) {
      keyBoardStyle = new SimpleStringProperty(this, "keyBoardStyle", _keyBoardStyle);
    }
    return keyBoardStyle;
  }

  public final boolean isCacheLayout() {
    return cacheLayout == null ? _cacheLayout : cacheLayout.get();
  }

  public final void setCacheLayout(boolean s) {
    if (cacheLayout == null) {
      _cacheLayout = s;
    } else {
      cacheLayout.set(s);
    }
  }

  public final BooleanProperty cacheLayoutProperty() {
    if (cacheLayout == null) {
      cacheLayout = new SimpleBooleanProperty(this, "cacheLayout", _cacheLayout);
    }
    return cacheLayout;
  }

  public final boolean isSymbol() {
    return symbol == null ? _symbol : symbol.get();
  }

  public final void setSymbol(boolean s) {
    if (symbol == null) {
      _symbol = s;
    } else {
      symbol.set(s);
    }
  }

  public final BooleanProperty symbolProperty() {
    if (symbol == null) {
      symbol = new SimpleBooleanProperty(this, "symbol", _symbol);
    }
    return symbol;
  }

  public final boolean isShift() {
    return shift == null ? _shift : shift.get();
  }

  public final void setShift(boolean s) {
    if (shift == null) {
      _shift = s;
    } else {
      shift.set(s);
    }
  }

  public final BooleanProperty shiftProperty() {
    if (shift == null) {
      shift = new SimpleBooleanProperty(this, "shift", _shift);
    }
    return shift;
  }

  public final boolean isControl() {
    return control == null ? _control : control.get();
  }

  public final void setControl(boolean s) {
    if (control == null) {
      _control = s;
    } else {
      control.set(s);
    }
  }

  public final BooleanProperty controlProperty() {
    if (control == null) {
      control = new SimpleBooleanProperty(this, "control", _control);
    }
    return control;
  }

  public final boolean isSpaceKeyMove() {
    return spaceKeyMove == null ? _spaceKeyMove : spaceKeyMove.get();
  }

  public final void setSpaceKeyMove(boolean s) {
    if (spaceKeyMove == null) {
      _spaceKeyMove = s;
    } else {
      spaceKeyMove.set(s);
    }
  }

  public final BooleanProperty spaceKeyMoveProperty() {
    if (spaceKeyMove == null) {
      spaceKeyMove = new SimpleBooleanProperty(this, "spaceKeyMove", _spaceKeyMove);
    }
    return spaceKeyMove;
  }

  public final boolean isCapsLock() {
    return capsLock == null ? _capsLock : capsLock.get();
  }

  public final void setCapsLock(boolean s) {
    if (capsLock == null) {
      _capsLock = s;
    } else {
      capsLock.set(s);
    }
  }

  public final BooleanProperty capsLockProperty() {
    if (capsLock == null) {
      capsLock = new SimpleBooleanProperty(this, "capsLock", _capsLock);
    }
    return capsLock;
  }

  public final double getScaleOffset() {
    return scaleOffset == null ? _scaleOffset : scaleOffset.get();
  }

  public final void setScaleOffset(double s) {
    if (scaleOffset == null) {
      _scaleOffset = s;
    } else {
      scaleOffset.set(s);
    }
  }

  public final DoubleProperty scaleOffsetProperty() {
    if (scaleOffset == null) {
      scaleOffset = new SimpleDoubleProperty(this, "scaleOffset", _scaleOffset);
    }
    return scaleOffset;
  }

  public final double getScale() {
    return scale == null ? _scale : scale.get();
  }

  public final void setScale(double s) {
    if (scale == null) {
      _scale = s;
      setScaleX(_scale);
      setScaleY(_scale);
    } else {
      scale.set(s);
    }
  }

  public final DoubleProperty scaleProperty() {
    if (scale == null) {
      scale = new SimpleDoubleProperty(this, "scale", _scale);
      scale.addListener((l, o, s) -> {
        if (!Objects.equals(o, s)) {
          setScaleX(s.doubleValue());
          setScaleY(s.doubleValue());
        }
      });
    }
    return scale;
  }

  public final double getMinScale() {
    return minScale == null ? _minScale : minScale.get();
  }

  public final void setMinScale(double s) {
    if (minScale == null) {
      _minScale = s;
    } else {
      minScale.set(s);
    }
  }

  public final DoubleProperty minScaleProperty() {
    if (minScale == null) {
      minScale = new SimpleDoubleProperty(this, "minScale", _minScale);
    }
    return minScale;
  }

  public final double getMaxScale() {
    return maxScale == null ? _maxScale : maxScale.get();
  }

  public final void setMaxScale(double s) {
    if (maxScale == null) {
      _maxScale = s;
    } else {
      maxScale.set(s);
    }
  }

  public final DoubleProperty maxScaleProperty() {
    if (maxScale == null) {
      maxScale = new SimpleDoubleProperty(this, "maxScale", _maxScale);
    }
    return maxScale;
  }

  public final DefaultLayer getLayer() {
    return layer == null ? _layer : layer.get();
  }

  public final void setLayer(DefaultLayer l) {
    if (layer == null) {
      _layer = l;
    } else {
      layer.set(l);
    }
  }

  public final ObjectProperty<DefaultLayer> layerProperty() {
    if (layer == null) {
      layer = new SimpleObjectProperty<>(this, "layer", _layer);
    }
    return layer;
  }

  public final Path getLayerPath() {
    return layerPath == null ? _layerPath : layerPath.get();
  }

  public final void setLayerPath(Path l) {
    if (layerPath == null) {
      _layerPath = l;
    } else {
      layerPath.set(l);
    }
  }

  public final ObjectProperty<Path> layerPathProperty() {
    if (layerPath == null) {
      layerPath = new SimpleObjectProperty<>(this, "layerPath", _layerPath);
    }
    return layerPath;
  }

  public final Locale getLocale() {
    return locale == null ? _locale : locale.get();
  }

  public final void setLocale(Locale l) {
    if (locale == null) {
      _locale = l;
    } else {
      locale.set(l);
    }
  }

  public final ObjectProperty<Locale> localeProperty() {
    if (locale == null) {
      locale = new SimpleObjectProperty<>(this, "locale", _locale);
    }
    return locale;
  }

  public final Locale getActiveLocale() {
    return activeLocale == null ? _activeLocale : activeLocale.get();
  }

  public final void setActiveLocale(Locale l) {
    if (activeLocale == null) {
      _activeLocale = l;
    } else {
      activeLocale.set(l);
    }
  }

  public final ObjectProperty<Locale> activeLocaleProperty() {
    if (activeLocale == null) {
      activeLocale = new SimpleObjectProperty<>(this, "activeLocale", _activeLocale);
    }
    return activeLocale;
  }

  public final KeyboardType getActiveType() {
    return activeType == null ? _activeType : activeType.get();
  }

  public final void setActiveType(KeyboardType l) {
    if (activeType == null) {
      _activeType = l;
    } else {
      activeType.set(l);
    }
  }

  public final ObjectProperty<KeyboardType> activeTypeProperty() {
    if (activeType == null) {
      activeType = new SimpleObjectProperty<>(this, "activeType", _activeType);
    }
    return activeType;
  }

  void installMoveHandler(Node node) {
    if (movedHandler == null) {
      movedHandler = e -> {
        if (isSpaceKeyMove()) {
          mousePressedX = getScene().getWindow().getX() - e.getScreenX();
          mousePressedY = getScene().getWindow().getY() - e.getScreenY();
        }
      };
    }
    if (draggedHandler == null) {
      draggedHandler = e -> {
        if (isSpaceKeyMove()) {
          getScene().getWindow().setX(e.getScreenX() + mousePressedX);
          getScene().getWindow().setY(e.getScreenY() + mousePressedY);
        }
      };
    }
    node.setOnMouseMoved(movedHandler);
    node.setOnMouseDragged(draggedHandler);
  }

  @Override
  public void handle(KeyButtonEvent event) {
    keyButtonEventHandler.handle(event);
  }

}
