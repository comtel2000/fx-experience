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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.FXRobotHandler;
import org.comtel2000.keyboard.robot.IRobot;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

import static org.comtel2000.keyboard.xml.XmlHelper.*;

public class KeyboardPane extends Region implements StandardKeyCode, EventHandler<KeyButtonEvent> {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardPane.class);

    private final Map<Integer, Region> typeRegionMap = new HashMap<>();

    private final XMLInputFactory factory = XMLInputFactory.newInstance();

    private static final String DEFAULT_CSS = "/css/KeyboardButtonStyle.css";

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

    private IExternalLocaleLoader _activeLocaleLoader;
    private ObjectProperty<IExternalLocaleLoader> activeLocaleLoader;

    private IExternalKeyboardTypeFinder _activeKeyboardTypeFinder;
    private ObjectProperty<IExternalKeyboardTypeFinder> activeKeyboardTypeFinder;

    private IKeyboardType _activeType;
    private ObjectProperty<IKeyboardType> activeType;

    private EventHandler<? super Event> closeEventHandler;

    private double mousePressedX;
    private double mousePressedY;

    private final List<IRobot> robots = new ArrayList<>();

    private final Map<URL, Region> layoutCache = new HashMap<>();

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
        return getKeyBoardStyle();
    }

    public void load() throws Exception {

        if (robots.isEmpty()) {
            logger.debug("load default fx robot handler");
            robots.add(new FXRobotHandler());
        }
        getStylesheets().add(getKeyBoardStyle());

        setLayoutLocale(getLocale());
        setKeyboardType(DefaultKeyboardType.TEXT);

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

    public void switchLocale(final Locale local) {
        try {
            if (local.equals(getActiveLocale())) {
                return;
            }
            setLayoutLocale(local);
            setActiveType(null);
            setKeyboardType(DefaultKeyboardType.TEXT);
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
            availableLocales.clear();

            setActiveLocale(null);
            setLayoutLocale(getLocale());
            setActiveType(null);
            setKeyboardType(DefaultKeyboardType.TEXT);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private void setLayoutLocale(final Locale local) throws Exception {

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

        addTypeRegion(DefaultKeyboardType.TEXT, root, "kb-layout.xml");
        addTypeRegion(DefaultKeyboardType.TEXT_SHIFT, root, "kb-layout-shift.xml");
        addTypeRegion(DefaultKeyboardType.SYMBOL, root, "kb-layout-sym.xml");
        addTypeRegion(DefaultKeyboardType.SYMBOL_SHIFT, root, "kb-layout-sym-shift.xml");
        addTypeRegion(DefaultKeyboardType.CTRL, root, "kb-layout-ctrl.xml");
        addTypeRegion(DefaultKeyboardType.NUMERIC, root, "kb-layout-numeric.xml");
        addTypeRegion(DefaultKeyboardType.EMAIL, root, "kb-layout-email.xml");
        addTypeRegion(DefaultKeyboardType.URL, root, "kb-layout-url.xml");

        if (getKeyboardTypeFinder() != null) {
            List<IExternalKeyboardTypeFinder.KeyboardTypeBean> externalKeyboardTypes = getKeyboardTypeFinder().getExternalKeyboardTypes();
            if (externalKeyboardTypes != null) {
                for (IExternalKeyboardTypeFinder.KeyboardTypeBean externalKeyboardType : externalKeyboardTypes) {
                    addTypeRegion(externalKeyboardType.getType(), externalKeyboardType.getRoot(), externalKeyboardType.getFile());
                }
            }
        }

    }

    private void addTypeRegion(IKeyboardType type, String root, String file) throws Exception {
        URL url = KeyboardPane.class.getResource(root + "/" + file);
        if (url == null && Files.exists(Paths.get(root, file))) {
            url = Paths.get(root, file).toUri().toURL();
        }
        if (url != null) {
            logger.debug("add layout: {}", url);
            typeRegionMap.put(type.getKeyboardTypeNumber(), getKeyboardPane(url));
            return;
        }
        String defaultRoot = getAvailableLocales().get(Locale.ENGLISH);
        if (defaultRoot == null) {
            logger.error("layout: {} / {} not found - no default available", root, file);
            return;
        }
        url = KeyboardPane.class.getResource(defaultRoot + "/" + file);
        if (url != null) {
            logger.debug("add default layout: {}", url);
            typeRegionMap.put(type.getKeyboardTypeNumber(), getKeyboardPane(url));
            return;
        }
        if (Files.exists(Paths.get(defaultRoot, file))) {
            url = Paths.get(defaultRoot, file).toUri().toURL();
            logger.debug("add default layout: {}", url);
            typeRegionMap.put(type.getKeyboardTypeNumber(), getKeyboardPane(url));
        }
    }

    private Map<Locale, String> getAvailableLocales() {
        if (!availableLocales.isEmpty()) {
            return availableLocales;
        }
        if (getLayerPath() == null) {
            String layer = getLayer().toString().toLowerCase(Locale.ENGLISH);
            URL url = Objects.requireNonNull(KeyboardPane.class.getResource("/xml/" + layer));
            logger.debug("use embedded layer path: {}", url);
            if (url.toExternalForm().contains("!")) {
                availableLocales.put(Locale.ENGLISH, "/xml/" + layer);
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
                if (Files.isDirectory(p)) {
                    Locale l = new Locale(p.getFileName().toString());
                    availableLocales.put(l, p.toString());
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        if (getActiveLocaleLoader() != null) {
            Map<Locale, String> externalLocaleMap = getActiveLocaleLoader().loadExternalLocale();
            if (externalLocaleMap != null) {
                availableLocales.putAll(externalLocaleMap);
            }
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
                    if (Files.isDirectory(p)) {
                        String lang = p.getFileName().toString().replace("/", "");
                        availableLocales.put(new Locale(lang), array[1] + "/" + lang);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setKeyboardType(IKeyboardType type) {
        logger.debug("try to set type: {}->{}", getActiveType(), type);
        if (type.equals(getActiveType())) {
            return;
        }

        setActiveType(type);

        //default settings
        Region pane = typeRegionMap.get(type.getKeyboardTypeNumber());
        setControl(type.getControl());
        setShift(type.getShift());
        setSymbol(type.getSymbol());

        if (pane == null) {
            pane = typeRegionMap.get(DefaultKeyboardType.TEXT.getKeyboardTypeNumber());
        }
        if (pane != null) {
            getChildren().setAll(pane);
        }
    }

    public void setKeyboardType(Object type) {
        setKeyboardType(DefaultKeyboardType.findValue(type).orElse(DefaultKeyboardType.TEXT));
    }

    private Region getKeyboardPane(URL layout) throws XMLStreamException, IOException {
        if (isCacheLayout()) {
            return layoutCache.computeIfAbsent(layout, this::createKeyboardPane);
        }
        return createKeyboardPane(layout);
    }

    private Region createKeyboardPane(URL layout) {

        GridPane rowPane = new GridPane();
        rowPane.setAlignment(Pos.CENTER);
        rowPane.getStyleClass().add("key-background-row");

        int keyWidth = 10;
        int keyHeight = 35;
        int horizontalGap = 10;
        int colIndex = -1;
        int rowIndex = -1;
        int rowWidth = 0;
        int minRowWidth = Integer.MAX_VALUE;
        int maxRowWidth = 0;

        GridPane colPane = null;
        XMLStreamReader reader = null;
        try {
            reader = factory.createXMLStreamReader(layout.openStream());
            while (reader.hasNext()) {
                reader.next();
                switch (reader.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        switch (reader.getLocalName()) {
                            case KEYBOARD:
                                readIntAttribute(reader, ATTR_V_GAP).ifPresent(a -> rowPane.setVgap(a));
                                horizontalGap = readIntAttribute(reader, ATTR_H_GAP, horizontalGap);
                                keyWidth = readIntAttribute(reader, ATTR_KEY_WIDTH, keyWidth);
                                keyHeight = readIntAttribute(reader, ATTR_KEY_HEIGHT, keyHeight);
                                break;
                            case ROW:
                                rowIndex++;
                                colIndex = -1;
                                maxRowWidth = Math.max(maxRowWidth, rowWidth);
                                if (rowWidth > 0) {
                                    minRowWidth = Math.min(minRowWidth, rowWidth);
                                }
                                logger.trace("{} - [{}/{}] url: {}", rowWidth, minRowWidth, maxRowWidth, layout);
                                rowWidth = 0;
                                colPane = new GridPane();
                                colPane.getStyleClass().add("key-background-column");
                                rowPane.add(colPane, 0, rowIndex);
                                RowConstraints rc = new RowConstraints();
                                rc.setPrefHeight(keyHeight);
                                colPane.getRowConstraints().add(rc);
                                readAttribute(reader, ATTR_ROW_EDGE_FLAGS)
                                        .ifPresent(flag -> rc.setValignment(VPos.valueOf(flag.toUpperCase())));
                                break;
                            case KEY:
                                colIndex++;
                                ColumnConstraints cc = new ColumnConstraints();
                                cc.setHgrow(Priority.SOMETIMES);
                                cc.setFillWidth(true);
                                cc.setPrefWidth(readIntAttribute(reader, ATTR_KEY_WIDTH, keyWidth));
                                String code = reader.getAttributeValue(null, ATTR_CODES);
                                if (code == null || code.isEmpty()) {
                                    Pane placeholder = new Pane();
                                    colPane.add(placeholder, colIndex, 0);
                                    colPane.getColumnConstraints().add(cc);
                                    rowWidth += cc.getPrefWidth();
                                    continue;
                                }

                                String[] codes = code.split(",");
                                final KeyButton button;
                                if (codes.length > 1 || code.equals(Integer.toString(LOCALE_SWITCH))) {
                                    button = new MultiKeyButton(this, getStylesheets());
                                } else if (readBooleanAttribute(reader, ATTR_REPEATABLE, false)) {
                                    button = new RepeatableKeyButton();
                                } else {
                                    button = new ShortPressKeyButton();
                                }

                                button.setFocusTraversable(false);
                                button.setOnShortPressed(this);

                                button.setMinHeight(1);
                                button.setPrefHeight(keyHeight);
                                button.setPrefWidth(keyWidth);
                                button.setMaxWidth(Double.MAX_VALUE);

                                button.setMovable(readBooleanAttribute(reader, ATTR_MOVABLE, false));

                                if (button.isMovable()) {
                                    installMoveHandler(button);
                                    button.getStyleClass().add("movable-style");
                                }
                                button.setSticky(readBooleanAttribute(reader, ATTR_STICKY, false));
                                if (button.isSticky()) {
                                    button.getStyleClass().add("sticky-style");
                                }
                                readAttribute(reader, ATTR_KEY_LABEL_STYLE).ifPresent(s -> {
                                    if (s.charAt(0) == '.') {
                                        for (String style : s.split(";")) {
                                            button.getStyleClass().add(style.substring(1));
                                        }
                                    }
                                });

                                if (codes.length > 0 && !codes[0].isEmpty()) {
                                    button.setKeyCode(parseInt(codes[0]));
                                }
                                if (codes.length > 1) {
                                    for (int i = 1; i < codes.length; i++) {
                                        int keyCode = parseInt(codes[i]);
                                        button.addExtKeyCode(keyCode, Character.toString((char) keyCode));
                                    }
                                }

                                if (button.getKeyCode() == LOCALE_SWITCH) {
                                    for (Locale l : getAvailableLocales().keySet()) {
                                        button.addExtKeyCode(LOCALE_SWITCH, l.getLanguage().toUpperCase(Locale.ENGLISH));
                                    }
                                }

                                readAttribute(reader, ATTR_KEY_ICON_STYLE).ifPresent(s -> {
                                    if (s.charAt(0) == '.') {
                                        logger.trace("Load css style: {}", s);
                                        Label icon = new Label();
                                        for (String style : s.split(";")) {
                                            icon.getStyleClass().add(style.substring(1));
                                        }
                                        icon.setMaxSize(40, 40);
                                        button.setContentDisplay(ContentDisplay.CENTER);
                                        button.setGraphic(icon);
                                    } else if (s.charAt(0) == '@') {
                                        try (InputStream is = KeyboardPane.class
                                                .getResourceAsStream(s.replace('@', '/') + ".png")) {
                                            Image image = new Image(is);
                                            if (!image.isError()) {
                                                button.setGraphic(new ImageView(image));
                                            } else {
                                                logger.error("Image: {} not found", s);
                                            }
                                        } catch (Exception e) {
                                            logger.error(e.getMessage(), e);
                                        }
                                    }

                                });

                                String label = reader.getAttributeValue(null, ATTR_KEY_LABEL);
                                button.setText(label != null ? label : Character.toString((char) button.getKeyCode()));
                                readAttribute(reader, ATTR_KEY_OUTPUT_TEXT).ifPresent(button::setKeyText);

                                cc.setHalignment(HPos.CENTER);
                                button.setAlignment(Pos.BASELINE_CENTER);

                                readAttribute(reader, ATTR_KEY_EDGE_FLAGS).ifPresent(flag -> {
                                    if (flag.equals(FLAG_RIGHT)) {
                                        cc.setHalignment(HPos.RIGHT);
                                        button.setAlignment(Pos.BASELINE_RIGHT);
                                    } else if (flag.equals(FLAG_LEFT)) {
                                        cc.setHalignment(HPos.LEFT);
                                        button.setAlignment(Pos.BASELINE_LEFT);
                                    } else {
                                        cc.setHalignment(HPos.CENTER);
                                    }
                                });

                                switch (button.getKeyCode()) {
                                    case java.awt.event.KeyEvent.VK_SPACE:
                                        installMoveHandler(button);
                                        break;
                                    case BACK_SPACE:
                                    case DELETE:
                                        if (!button.isRepeatable()) {
                                            button.setOnLongPressed(e -> {
                                                sendToComponent((char) 97, true);
                                                sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, isControl());
                                            });
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                colPane.add(button, colIndex, 0);
                                colPane.getColumnConstraints().add(cc);
                                rowWidth += cc.getPrefWidth();
                                break;
                            default:
                                break;
                        }

                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        // rowPane.setMinWidth(minRowWidth);
                        // rowPane.setMaxWidth(maxRowWidth);
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            close(reader);
        }
        return rowPane;
    }

    private void setKeyboardType(boolean ctrl, boolean shift, boolean symbol) {
        if (ctrl) {
            setKeyboardType(DefaultKeyboardType.CTRL);
            return;
        }
        if (symbol) {
            setKeyboardType(shift ? DefaultKeyboardType.SYMBOL_SHIFT : DefaultKeyboardType.SYMBOL);
            return;
        }
        setKeyboardType(shift ? DefaultKeyboardType.TEXT_SHIFT : DefaultKeyboardType.TEXT);
    }

    @Override
    public void handle(KeyButtonEvent event) {
        event.consume();
        if (!event.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
            logger.warn("ignore non short pressed events");
            return;
        }
        KeyButton kb = (KeyButton) event.getSource();
        switch (kb.getKeyCode()) {
            case SHIFT_DOWN:
                // switch shifted
                setKeyboardType(isControl(), !isShift(), isSymbol());
                break;
            case SYMBOL_DOWN:
                // switch sym / qwerty
                setKeyboardType(isControl(), isShift(), !isSymbol());
                break;
            case CLOSE:
                if (closeEventHandler == null) {
                    fireEvent(new WindowEvent(this.getScene().getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
                } else {
                    closeEventHandler.handle(event);
                }
                break;
            case TAB:
                sendToComponent((char) java.awt.event.KeyEvent.VK_TAB, true);
                break;
            case BACK_SPACE:
                sendToComponent((char) java.awt.event.KeyEvent.VK_BACK_SPACE, true);
                break;
            case DELETE:
                sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, true);
                break;
            case CTRL_DOWN:
                // switch ctrl
                setControl(!isControl());
                setKeyboardType(isControl(), isShift(), isSymbol());
                break;
            case LOCALE_SWITCH:
                switchLocale(Locale.forLanguageTag(kb.getText()));
                break;
            case ENTER:
                sendToComponent((char) java.awt.event.KeyEvent.VK_ENTER, true);
                break;
            case ARROW_UP:
                sendToComponent((char) java.awt.event.KeyEvent.VK_UP, true);
                break;
            case ARROW_DOWN:
                sendToComponent((char) java.awt.event.KeyEvent.VK_DOWN, true);
                break;
            case ARROW_LEFT:
                sendToComponent((char) java.awt.event.KeyEvent.VK_LEFT, true);
                break;
            case ARROW_RIGHT:
                sendToComponent((char) java.awt.event.KeyEvent.VK_RIGHT, true);
                break;
            case UNDO:
                sendToComponent((char) java.awt.event.KeyEvent.VK_Z, true);
                break;
            case REDO:
                sendToComponent((char) java.awt.event.KeyEvent.VK_Y, true);
                break;
            case HOME:
                sendToComponent((char) java.awt.event.KeyEvent.VK_HOME, true);
                break;
            case END:
                sendToComponent((char) java.awt.event.KeyEvent.VK_END, true);
                break;
            case PAGE_UP:
                sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_UP, true);
                break;
            case PAGE_DOWN:
                sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_DOWN, true);
                break;
            case HELP:
                sendToComponent((char) java.awt.event.KeyEvent.VK_HELP, true);
                break;
            case NUMERIC_TYPE:
                setKeyboardType(DefaultKeyboardType.NUMERIC);
                break;
            case EMAIL_TYPE:
                setKeyboardType(DefaultKeyboardType.EMAIL);
                break;
            case URL_TYPE:
                setKeyboardType(DefaultKeyboardType.URL);
                break;
            case CAPS_LOCK:
                setCapsLock(!isCapsLock());
                break;
            case PRINTSCREEN:
                sendToComponent((char) java.awt.event.KeyEvent.VK_PRINTSCREEN, true);
                Timeline timeline = new Timeline();
                timeline.setDelay(Duration.millis(1000));
                final double opacity = getScene().getWindow().getOpacity();
                getScene().getWindow().setOpacity(0.0);
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                        new KeyValue(getScene().getWindow().opacityProperty(), opacity)));
                timeline.play();
                break;
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
                break;
            default:
                if (kb.getKeyText() != null) {
                    for (int i = 0; i < kb.getKeyText().length(); i++) {
                        sendToComponent(kb.getKeyText().charAt(i), isControl());
                    }
                } else if (kb.getKeyCode() > -1) {
                    sendToComponent((char) kb.getKeyCode(), isControl());
                } else {
                    logger.debug("unknown key code: {}", kb.getKeyCode());
                    sendToComponent((char) kb.getKeyCode(), true);
                }
                if (!isCapsLock() && isShift()) {
                    setKeyboardType(isControl(), !isShift(), isSymbol());
                }
                break;
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
            URL stylesheet = KeyboardPane.class.getResource(DEFAULT_CSS);
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
                if (o != s) {
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

    public final void setExternalKeyboardTypeFinder(IExternalKeyboardTypeFinder l) {
        if (activeKeyboardTypeFinder == null) {
            _activeKeyboardTypeFinder = l;
        } else {
            activeKeyboardTypeFinder.set(l);
        }
    }

    public final IExternalKeyboardTypeFinder getKeyboardTypeFinder() {
        return activeKeyboardTypeFinder == null ? _activeKeyboardTypeFinder : activeKeyboardTypeFinder.get();
    }

    public final ObjectProperty<IExternalKeyboardTypeFinder> activeKeyboardTypeFinderProperty() {
        if (activeKeyboardTypeFinder == null) {
            activeKeyboardTypeFinder = new SimpleObjectProperty<>(this, "activeKeyboardTypeFinder", _activeKeyboardTypeFinder);
        }
        return activeKeyboardTypeFinder;
    }

    public final void setActiveLocaleLoader(IExternalLocaleLoader l) {
        if (activeLocaleLoader == null) {
            _activeLocaleLoader = l;
        } else {
            activeLocaleLoader.set(l);
        }
    }

    public final IExternalLocaleLoader getActiveLocaleLoader() {
        return activeLocaleLoader == null ? _activeLocaleLoader : activeLocaleLoader.get();
    }

    public final ObjectProperty<IExternalLocaleLoader> activeLocaleLoaderProperty() {
        if (activeLocaleLoader == null) {
            activeLocaleLoader = new SimpleObjectProperty<>(this, "activeLocaleLoader", _activeLocaleLoader);
        }
        return activeLocaleLoader;
    }

    public final ObjectProperty<Locale> activeLocaleProperty() {
        if (activeLocale == null) {
            activeLocale = new SimpleObjectProperty<>(this, "activeLocale", _activeLocale);
        }
        return activeLocale;
    }

    public final IKeyboardType getActiveType() {
        return activeType == null ? _activeType : activeType.get();
    }

    public final void setActiveType(IKeyboardType l) {
        if (activeType == null) {
            _activeType = l;
        } else {
            activeType.set(l);
        }
    }

    public final ObjectProperty<IKeyboardType> activeTypeProperty() {
        if (activeType == null) {
            activeType = new SimpleObjectProperty<>(this, "activeType", _activeType);
        }
        return activeType;
    }

    private void installMoveHandler(Node node) {
        if (movedHandler == null) {
            movedHandler = (e) -> {
                if (isSpaceKeyMove()) {
                    mousePressedX = getScene().getWindow().getX() - e.getScreenX();
                    mousePressedY = getScene().getWindow().getY() - e.getScreenY();
                }
            };
        }
        if (draggedHandler == null) {
            draggedHandler = (e) -> {
                if (isSpaceKeyMove()) {
                    getScene().getWindow().setX(e.getScreenX() + mousePressedX);
                    getScene().getWindow().setY(e.getScreenY() + mousePressedY);
                }
            };
        }
        node.setOnMouseMoved(movedHandler);
        node.setOnMouseDragged(draggedHandler);
    }

}
