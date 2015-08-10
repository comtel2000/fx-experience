package org.comtel2000.keyboard.control;

/*
 * #%L
 * fx-onscreen-keyboard
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.comtel2000.keyboard.robot.IRobot;
import org.comtel2000.keyboard.xml.KeyboardLayoutHandler;
import org.comtel2000.keyboard.xml.layout.Keyboard;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.transform.Scale;

public class KeyboardPane extends Region implements StandardKeyCode, EventHandler<KeyButtonEvent> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardPane.class);

	private final String DEFAULT_CSS = "/css/KeyboardButtonStyle.css";

	private final StringProperty keyBoardStyleProperty = new SimpleStringProperty(DEFAULT_CSS);

	private final EnumMap<KeyboardType, Region> typeRegionMap = new EnumMap<>(KeyboardType.class);

	private final BooleanProperty symbolProperty = new SimpleBooleanProperty(false);

	private final BooleanProperty shiftProperty = new SimpleBooleanProperty(false);

	private final BooleanProperty ctrlProperty = new SimpleBooleanProperty(false);

	private final BooleanProperty spaceKeyMoveProperty = new SimpleBooleanProperty(true);

	private final BooleanProperty capsLockProperty = new SimpleBooleanProperty(true);

	private final BooleanProperty cacheLayoutProperty = new SimpleBooleanProperty(false);

	private final DoubleProperty scaleOffsetProperty = new SimpleDoubleProperty(0.2);

	private final DoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);

	private final DoubleProperty minScaleProperty = new SimpleDoubleProperty(0.7);

	private final DoubleProperty maxScaleProperty = new SimpleDoubleProperty(5.0);

	private final ObjectProperty<DefaultLayer> layerProperty = new SimpleObjectProperty<>(DefaultLayer.DEFAULT);

	private final ObjectProperty<Path> layerPathProperty = new SimpleObjectProperty<>();

	private final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

	private final ObjectProperty<Locale> activeLocaleProperty = new SimpleObjectProperty<>();

	private final ObjectProperty<KeyboardType> activeTypeProperty = new SimpleObjectProperty<>();

	private EventHandler<? super Event> closeEventHandler;

	private double mousePressedX, mousePressedY;

	private final List<IRobot> robots = new ArrayList<>();

	private final Map<Locale, String> availableLocales = new LinkedHashMap<>();

	private final KeyboardLayoutHandler handler;

	public KeyboardPane() {
		setId("key-background");
		setFocusTraversable(false);
		handler = new KeyboardLayoutHandler(isCacheLayout());
	}

	// @Override (JDK 8u40 or later)
	public String getUserAgentStylesheet() {
		return keyBoardStyleProperty.get();
	}

	public void load() throws MalformedURLException, IOException, URISyntaxException {

		getStylesheets().add(keyBoardStyleProperty.get());

		setLayoutLocale(localeProperty.get());
		setKeyboardType(KeyboardType.TEXT);

		if (scaleProperty.get() != 1.0) {
			getTransforms().setAll(new Scale(scaleProperty.get(), scaleProperty.get(), 1, 0, 0, 0));
		}

		scaleProperty.addListener((arg0, o, s) -> {
			if (o != s) {
				getTransforms().setAll(new Scale(s.doubleValue(), s.doubleValue(), 1, 0, 0, 0));
			}
		});
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

	public void resetLocale() {
		switchLocale(localeProperty.get());
	}

	public void switchLocale(final Locale local) {
		try {
			if (local.equals(activeLocaleProperty.get())) {
				return;
			}
			setLayoutLocale(local);
			activeTypeProperty.set(null);
			setKeyboardType(KeyboardType.TEXT);
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void switchLayer(final DefaultLayer layer) {
		if (layer.equals(layerProperty.get())) {
			logger.warn("layer already selected");
			return;
		}
		try {
			layerPathProperty.set(null);
			layerProperty.set(layer);
			availableLocales.clear();

			activeLocaleProperty.set(null);
			setLayoutLocale(localeProperty.get());
			activeTypeProperty.set(null);
			setKeyboardType(KeyboardType.TEXT);
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}

	}

	private void setLayoutLocale(final Locale local) throws MalformedURLException, IOException, URISyntaxException {

		logger.debug("try to set keyboard local: {}->{}", activeLocaleProperty.get(), local);

		Map<Locale, String> localeMap = getAvailableLocales();
		if (localeMap.containsKey(local)) {
			if (local.equals(activeLocaleProperty.get())) {
				logger.debug("locale already active: {}", local);
				return;
			}
			activeLocaleProperty.set(local);
		} else if (localeMap.containsKey(Locale.forLanguageTag(local.getLanguage()))) {
			if (Locale.forLanguageTag(local.getLanguage()).equals(activeLocaleProperty.get())) {
				logger.debug("locale language already active: {}", local);
				return;
			}
			activeLocaleProperty.set(Locale.forLanguageTag(local.getLanguage()));
		} else {
			if (Locale.ENGLISH.equals(activeLocaleProperty.get())) {
				logger.info("locale language already active: {}", local);
				return;
			}
			activeLocaleProperty.set(Locale.ENGLISH);
		}
		logger.debug("use keyboard local: {}", activeLocaleProperty.get());
		String root = localeMap.get(activeLocaleProperty.get());

		addTypeRegion(KeyboardType.TEXT, root, "kb-layout.xml");
		addTypeRegion(KeyboardType.TEXT_SHIFT, root, "kb-layout-shift.xml");
		addTypeRegion(KeyboardType.SYMBOL, root, "kb-layout-sym.xml");
		addTypeRegion(KeyboardType.SYMBOL_SHIFT, root, "kb-layout-sym-shift.xml");
		addTypeRegion(KeyboardType.CTRL, root, "kb-layout-ctrl.xml");
		addTypeRegion(KeyboardType.NUMERIC, root, "kb-layout-numeric.xml");
		addTypeRegion(KeyboardType.EMAIL, root, "kb-layout-email.xml");
		addTypeRegion(KeyboardType.URL, root, "kb-layout-url.xml");

	}

	private void addTypeRegion(KeyboardType type, String root, String file) throws MalformedURLException, IOException {
		URL url = KeyboardLayoutHandler.class.getResource(root + "/" + file);
		if (url == null && Files.exists(Paths.get(root, file))) {
			url = Paths.get(root, file).toUri().toURL();
		}
		if (url != null) {
			logger.debug("add layout: {}", url);
			typeRegionMap.put(type, createKeyboardPane(handler.getLayout(url)));
			return;
		}
		String defaultRoot = getAvailableLocales().get(Locale.ENGLISH);
		if (defaultRoot == null) {
			logger.error("layout: {} / {} not found - no default available", root, file);
			return;
		}
		if ((url = KeyboardLayoutHandler.class.getResource(defaultRoot + "/" + file)) != null) {
			logger.debug("add default layout: {}", url);
			typeRegionMap.put(type, createKeyboardPane(handler.getLayout(url)));
			return;
		}
		if (Files.exists(Paths.get(defaultRoot, file))) {
			url = Paths.get(defaultRoot, file).toUri().toURL();
			logger.debug("add default layout: {}", url);
			typeRegionMap.put(type, createKeyboardPane(handler.getLayout(url)));
			return;
		}
	}

	private Map<Locale, String> getAvailableLocales() {
		if (!availableLocales.isEmpty()) {
			return availableLocales;
		}
		if (layerPathProperty.get() == null) {
			String layer = layerProperty.get().toString().toLowerCase(Locale.ENGLISH);
			URL url = Objects.requireNonNull(KeyboardLayoutHandler.class.getResource("/xml/" + layer));
			logger.debug("use embedded layer path: {}", url);
			if (url.toExternalForm().contains("!")) {
				availableLocales.put(Locale.ENGLISH, "/xml/" + layer);
				readJarLocales(url);
				return availableLocales;
			}

			try {
				layerPathProperty.set(Paths.get(url.toURI()));
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
		}
		availableLocales.put(Locale.ENGLISH, layerPathProperty.get().toString());
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(layerPathProperty.get())) {
			for (Path p : stream) {
				if (Files.isDirectory(p)) {
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

	public void setKeyboardType(String type) {
		KeyboardType kType;
		try {
			kType = type == null || type.isEmpty() ? KeyboardType.TEXT : KeyboardType.valueOf(type.toUpperCase(Locale.ENGLISH));
		} catch (Exception e) {
			logger.error("unknown type: {}", type);
			kType = KeyboardType.TEXT;
		}
		setKeyboardType(kType);
	}

	public void setKeyboardType(KeyboardType type) {
		logger.debug("try to set type: {}->{}", activeTypeProperty.get(), type);
		if (type.equals(activeTypeProperty.get())) {
			return;
		}
		activeTypeProperty.set(type);
		Region pane;
		switch (type) {
		case NUMERIC:
			ctrlProperty.set(false);
			shiftProperty.set(false);
			symbolProperty.set(false);
			pane = typeRegionMap.getOrDefault(type, typeRegionMap.get(KeyboardType.SYMBOL));
			break;
		case EMAIL:
			ctrlProperty.set(false);
			shiftProperty.set(false);
			symbolProperty.set(false);
			pane = typeRegionMap.get(type);
			break;
		case URL:
			ctrlProperty.set(false);
			shiftProperty.set(false);
			symbolProperty.set(false);
			pane = typeRegionMap.get(type);
			break;
		case SYMBOL:
			ctrlProperty.set(false);
			shiftProperty.set(false);
			symbolProperty.set(true);
			pane = typeRegionMap.get(type);
			break;
		case SYMBOL_SHIFT:
			ctrlProperty.set(false);
			shiftProperty.set(true);
			symbolProperty.set(true);
			pane = typeRegionMap.get(type);
			break;
		case CTRL:
			ctrlProperty.set(true);
			shiftProperty.set(false);
			symbolProperty.set(false);
			pane = typeRegionMap.get(type);
			break;
		case TEXT_SHIFT:
			ctrlProperty.set(false);
			shiftProperty.set(true);
			symbolProperty.set(false);
			pane = typeRegionMap.get(type);
			break;
		default:
			ctrlProperty.set(false);
			shiftProperty.set(false);
			symbolProperty.set(false);
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

	private Region createKeyboardPane(Keyboard layout) {

		GridPane rPane = new GridPane();
		rPane.setAlignment(Pos.CENTER);
		// pane.setPrefSize(600, 200);

		if (layout.getVerticalGap() != null) {
			rPane.setVgap(layout.getVerticalGap());
		}
		rPane.setId("key-background-row");

		int defaultKeyWidth = 10;
		if (layout.getKeyWidth() != null) {
			defaultKeyWidth = layout.getKeyWidth();
		}

		int defaultKeyHeight = 35;
		if (layout.getKeyHeight() != null) {
			defaultKeyHeight = layout.getKeyHeight();
		}

		int rowIdx = 0;
		for (Keyboard.Row row : layout.getRow()) {
			int colIdx = 0;
			GridPane colPane = new GridPane();
			colPane.setId("key-background-column");
			// colPane.setVgap(20);
			// colPane.setPrefWidth(Region.USE_COMPUTED_SIZE);

			RowConstraints rc = new RowConstraints();
			rc.setPrefHeight(defaultKeyHeight);

			if (row.getRowEdgeFlags() != null) {
				if (row.getRowEdgeFlags().equals("bottom")) {
					rc.setValignment(VPos.BOTTOM);
				}
				if (row.getRowEdgeFlags().equals("top")) {
					rc.setValignment(VPos.TOP);
				}
			}
			int rowWidth = 0;
			for (Keyboard.Row.Key key : row.getKey()) {

				if (key.getHorizontalGap() != null) {
					colPane.setHgap(key.getHorizontalGap());
				} else if (layout.getHorizontalGap() != null) {
					colPane.setHgap(layout.getHorizontalGap());
				}
				ColumnConstraints cc = new ColumnConstraints();
				cc.setHgrow(Priority.SOMETIMES);
				cc.setFillWidth(true);
				cc.setPrefWidth(key.getKeyWidth() != null ? key.getKeyWidth() : defaultKeyWidth);

				if (key.getCodes() == null || key.getCodes().isEmpty()) {
					// add placeholder
					Pane placeholder = new Pane();
					colPane.add(placeholder, colIdx, 0);
					colPane.getColumnConstraints().add(cc);

					logger.trace("placeholder: {}", cc);
					colIdx++;
					rowWidth += cc.getPrefWidth();
					continue;
				}

				String[] codes = key.getCodes().split(",");
				KeyButton button;
				if (codes.length > 1 || key.getCodes().equals(Integer.toString(LOCALE_SWITCH))) {
					button = new MultiKeyButton(scaleProperty, getStylesheets());
				} else if (Boolean.TRUE == key.isRepeatable()) {
					button = new RepeatableKeyButton();
				} else {
					button = new ShortPressKeyButton();
				}

				button.setFocusTraversable(false);
				button.setOnShortPressed(this);
				// button.setCache(true);

				button.setMinHeight(10);
				button.setPrefHeight(defaultKeyHeight);
				button.setPrefWidth(defaultKeyWidth);
				button.setMaxWidth(defaultKeyWidth * 100);

				button.setMovable(Boolean.TRUE == key.isMovable());
				button.setRepeatable(Boolean.TRUE == key.isRepeatable());
				if (button.isMovable()) {
					installMoveHandler(button);
					button.getStyleClass().add("movable-style");
				}

				if (key.getKeyLabelStyle() != null && key.getKeyLabelStyle().startsWith(".")) {
					for (String style : key.getKeyLabelStyle().split(";")) {
						button.getStyleClass().add(style.substring(1));
					}
				}

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

				if (key.getKeyIconStyle() != null && key.getKeyIconStyle().startsWith(".")) {
					logger.trace("Load css style: {}", key.getKeyIconStyle());
					Label icon = new Label();
					// icon.setSnapToPixel(true);
					// do not reduce css shape quality JavaFX8
					// icon.setCacheShape(false);

					for (String style : key.getKeyIconStyle().split(";")) {
						icon.getStyleClass().add(style.substring(1));
					}
					icon.setMaxSize(40, 40);
					button.setContentDisplay(ContentDisplay.CENTER);
					button.setGraphic(icon);

				} else if (key.getKeyIconStyle() != null && key.getKeyIconStyle().startsWith("@")) {

					try (InputStream is = KeyboardPane.class.getResourceAsStream(key.getKeyIconStyle().replace("@", "/") + ".png")) {
						Image image = new Image(is);
						if (!image.isError()) {
							button.setGraphic(new ImageView(image));
						} else {
							logger.error("Image: {} not found", key.getKeyIconStyle());
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}

				button.setText(key.getKeyLabel() != null ? key.getKeyLabel() : Character.toString((char) button.getKeyCode()));
				button.setKeyText(key.getKeyOutputText());

				if (key.getKeyEdgeFlags() != null) {
					if (key.getKeyEdgeFlags().equals("right")) {
						cc.setHalignment(HPos.RIGHT);
						button.setAlignment(Pos.BASELINE_RIGHT);
					} else if (key.getKeyEdgeFlags().equals("left")) {
						cc.setHalignment(HPos.LEFT);
						button.setAlignment(Pos.BASELINE_LEFT);
					} else {
						cc.setHalignment(HPos.CENTER);
					}
				} else {
					cc.setHalignment(HPos.CENTER);
					button.setAlignment(Pos.BASELINE_CENTER);
				}

				switch (button.getKeyCode()) {
				case java.awt.event.KeyEvent.VK_SPACE:
					installMoveHandler(button);
					break;
				case BACK_SPACE:
				case DELETE:
					if (!button.isRepeatable()) {
						button.setOnLongPressed(e -> {
							// e.consume();
							sendToComponent((char) 97, true);
							sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, ctrlProperty.get());
						});
					}
					break;
				}

				colPane.add(button, colIdx, 0);
				colPane.getColumnConstraints().add(cc);

				logger.trace("btn: {} {}", button.getText(), cc);
				colIdx++;
				rowWidth += cc.getPrefWidth();
			}
			logger.trace("row[{}] - {}", rowIdx, rowWidth);
			colPane.getRowConstraints().add(rc);
			// colPane.setGridLinesVisible(true);
			rPane.add(colPane, 0, rowIdx);
			rowIdx++;
		}

		logger.trace("-----end pane-----");
		return rPane;
	}

	private static int parseInt(String i) {
		return i.startsWith("0x") ? Integer.parseInt(i.substring(2), 16) : Integer.parseInt(i);
	}

	public boolean isShifted() {
		return shiftProperty.get();
	}

	public boolean isSymbol() {
		return symbolProperty.get();
	}

	public boolean isCtrl() {
		return ctrlProperty.get();
	}

	@Override
	public void handle(KeyButtonEvent event) {
		event.consume();
		KeyButtonEvent kbEvent = event;
		if (!kbEvent.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
			logger.warn("ignore non short pressed events");
			return;
		}
		KeyButton kb = (KeyButton) kbEvent.getSource();
		switch (kb.getKeyCode()) {
		case SHIFT_DOWN:
			// switch shifted
			setKeyboardType(ctrlProperty.get(), !shiftProperty.get(), symbolProperty.get());
			break;
		case SYMBOL_DOWN:
			// switch sym / qwerty
			setKeyboardType(ctrlProperty.get(), shiftProperty.get(), !symbolProperty.get());
			break;
		case CLOSE:
			if (closeEventHandler == null) {
				if (getScene() != null && getScene().getWindow() != null) {
					getScene().getWindow().hide();
				} else {
					System.exit(0);
				}
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
			ctrlProperty.set(!ctrlProperty.get());
			setKeyboardType(ctrlProperty.get(), shiftProperty.get(), symbolProperty.get());
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
		case NUMERIC_TYPE:
			setKeyboardType(KeyboardType.NUMERIC);
			break;
		case EMAIL_TYPE:
			setKeyboardType(KeyboardType.EMAIL);
			break;
		case URL_TYPE:
			setKeyboardType(KeyboardType.URL);
			break;
		case CAPS_LOCK:
			capsLockProperty.set(!capsLockProperty.get());
			break;
		default:
			if (kb.getKeyText() != null) {
				for (int i = 0; i < kb.getKeyText().length(); i++) {
					sendToComponent(kb.getKeyText().charAt(i), ctrlProperty.get());
				}
			} else if (kb.getKeyCode() > -1) {
				sendToComponent((char) kb.getKeyCode(), ctrlProperty.get());
			} else {
				logger.debug("unknown key code: {}", kb.getKeyCode());
				sendToComponent((char) kb.getKeyCode(), true);
			}
			if (!capsLockProperty.get() && shiftProperty.get()) {
				setKeyboardType(ctrlProperty.get(), !shiftProperty.get(), symbolProperty.get());
			}
			break;
		}

	}

	/**
	 * send keyEvent to iRobot implementation
	 *
	 * @param ch
	 * @param ctrl
	 */
	private void sendToComponent(char ch, boolean ctrl) {

		logger.trace("send ({}) ctrl={}", ch, ctrl);

		if (ctrl) {
			switch (Character.toUpperCase(ch)) {
			case java.awt.event.KeyEvent.VK_MINUS:
				if (scaleProperty.get() > minScaleProperty.get()) {
					scaleProperty.set(scaleProperty.get() - scaleOffsetProperty.get());
				}
				return;
			case 0x2B:
				if (scaleProperty.get() < maxScaleProperty.get()) {
					scaleProperty.set(scaleProperty.get() + scaleOffsetProperty.get());
				}
				return;
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

	public void removeRobotHandler(IRobot robot) {
		robots.remove(robot);
	}

	public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
		closeEventHandler = value;
	}

	public void setLayer(DefaultLayer dl) {
		layerProperty.set(dl);
	}

	public ReadOnlyObjectProperty<DefaultLayer> layerProperty() {
		return layerProperty;
	}

	public void setLayerPath(Path xml) {
		layerPathProperty.set(xml);
	}

	public ReadOnlyObjectProperty<Path> layerPathProperty() {
		return layerPathProperty;
	}

	public void setLocale(Locale locale) {
		localeProperty.set(locale);
	}

	public ReadOnlyObjectProperty<Locale> localeProperty() {
		return localeProperty;
	}

	public void setScale(double scale) {
		scaleProperty.set(scale);
	}

	public ReadOnlyDoubleProperty scaleProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(scaleProperty);
	}

	public void setScaleOffset(double offset) {
		scaleOffsetProperty.set(offset);
	}

	public ReadOnlyDoubleProperty scaleOffsetProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(scaleOffsetProperty);
	}

	public ReadOnlyDoubleProperty minScaleProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(minScaleProperty);
	}

	public void setMinimumScale(double min) {
		minScaleProperty.set(min);
	}

	public ReadOnlyDoubleProperty maxScaleProperty() {
		return ReadOnlyDoubleProperty.readOnlyDoubleProperty(maxScaleProperty);
	}

	public void setMaximumScale(double max) {
		maxScaleProperty.set(max);
	}

	public StringProperty keyBoardStyleProperty() {
		return keyBoardStyleProperty;
	}

	public void setKeyBoardStyle(String css) {
		keyBoardStyleProperty.set(css);
	}

	public BooleanProperty spaceKeyMoveProperty() {
		return spaceKeyMoveProperty;
	}

	public void setSpaceKeyMove(boolean m) {
		spaceKeyMoveProperty.set(m);
	}

	public boolean isSpaceKeyMove() {
		return spaceKeyMoveProperty().get();
	}

	public BooleanProperty capsLockProperty() {
		return capsLockProperty;
	}

	public boolean isCapsLock() {
		return capsLockProperty().get();
	}

	public void setCapsLock(boolean c) {
		capsLockProperty().set(c);
	}

	public BooleanProperty cacheLayoutProperty() {
		return cacheLayoutProperty;
	}

	public boolean isCacheLayout() {
		return cacheLayoutProperty().get();
	}

	public void setCacheLayout(boolean c) {
		cacheLayoutProperty().set(c);
	}

	private MouseMovedHandler movedHandler;
	private MouseDraggedHandler draggedHandler;

	private void installMoveHandler(Node node) {
		if (movedHandler == null) {
			movedHandler = new MouseMovedHandler();
		}
		if (draggedHandler == null) {
			draggedHandler = new MouseDraggedHandler();
		}
		node.setOnMouseMoved(movedHandler);
		node.setOnMouseDragged(draggedHandler);
	}

	class MouseMovedHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent event) {
			// on
			// Double.isNaN(getScene().getWindow().getX())
			// init window position
			if (spaceKeyMoveProperty.get()) {
				mousePressedX = getScene().getWindow().getX() - event.getScreenX();
				mousePressedY = getScene().getWindow().getY() - event.getScreenY();
			}
		}
	}

	class MouseDraggedHandler implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (spaceKeyMoveProperty.get()) {
				getScene().getWindow().setX(mouseEvent.getScreenX() + mousePressedX);
				getScene().getWindow().setY(mouseEvent.getScreenY() + mousePressedY);
			}
		}
	}
}
