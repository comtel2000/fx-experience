package org.comtel.javafx.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;

import org.comtel.javafx.event.KeyButtonEvent;
import org.comtel.javafx.robot.IRobot;
import org.comtel.javafx.xml.KeyboardLayoutHandler;
import org.comtel.javafx.xml.layout.Keyboard;
import org.comtel.samples.FxStandAloneApp;
import org.slf4j.LoggerFactory;

public class KeyboardPane extends Region implements StandardKeyCode, EventHandler<KeyButtonEvent> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardPane.class);

	private final String DEFAULT_CSS = "/css/KeyboardButtonStyle.css";
	private final String DEFAULT_FONT_URL = "/font/FontKeyboardFX.ttf";
	
	private final StringProperty keyBoardStyleProperty = new SimpleStringProperty();
	
	private Region qwertyKeyboardPane;
	private Region qwertyShiftedKeyboardPane;
	private Region symbolKeyboardPane;
	private Region symbolShiftedKeyboardPane;
	private Region qwertyCtrlKeyboardPane;
	
	private final BooleanProperty symbolProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty shiftProperty = new SimpleBooleanProperty(false);
	private final BooleanProperty ctrlProperty = new SimpleBooleanProperty(false);

	private final DoubleProperty scaleOffsetProperty = new SimpleDoubleProperty(0.2);
	
	private final DoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);
	private final DoubleProperty minScaleProperty = new SimpleDoubleProperty(0.7);
	private final DoubleProperty maxScaleProperty = new SimpleDoubleProperty(5.0);

	private final ObjectProperty<DefaultLayers> layerProperty = new SimpleObjectProperty<>(DefaultLayers.DEFAULT);
	private final ObjectProperty<Path> layerPathProperty = new SimpleObjectProperty<>();

	private final ObjectProperty<Locale> localeProperty = new SimpleObjectProperty<>(Locale.getDefault());

	private EventHandler<? super Event> closeEventHandler;

	private double mousePressedX;
	private double mousePressedY;

	private final ObservableList<IRobot> robots = FXCollections.observableArrayList();

	public KeyboardPane() {
		setId("key-background");
		setFocusTraversable(false);
	}

	public void load() throws MalformedURLException, IOException, URISyntaxException {

		if (keyBoardStyleProperty.get() != null){
			getStylesheets().add(keyBoardStyleProperty.get());
		} else {
			getStylesheets().add(this.getClass().getResource(DEFAULT_CSS).toExternalForm());
		}

		if (layerPathProperty.get() == null) {
			String fontUrl = FxStandAloneApp.class.getResource(DEFAULT_FONT_URL).toExternalForm();
			Font.loadFont(fontUrl, -1);
		}
		
		setLayoutLocale(localeProperty.get());
		setKeyboardLayer(KeyboardLayer.QWERTY);

		if (scaleProperty.get() != 1.0) {
			getTransforms().setAll(new Scale(scaleProperty.get(), scaleProperty.get(), 1, 0, 0, 0));
		}

		registerListener();
	}

	private void registerListener() {

		shiftProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (ctrlProperty.get()) {
					logger.warn("ignore in ctrl mode");
					return;
				}
				setKeyboardLayer(symbolProperty.get() ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

		ctrlProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean ctrl) {
				if (ctrl) {
					setKeyboardLayer(KeyboardLayer.CTRL);
				} else {
					setKeyboardLayer(symbolProperty.get() ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
				}
			}
		});

		symbolProperty.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (ctrlProperty.get()) {
					logger.warn("ignore in ctrl mode");
					return;
				}
				setKeyboardLayer(arg2 ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

		scaleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number o, Number s) {
				if (o != s) {
					getTransforms().setAll(new Scale(s.doubleValue(), s.doubleValue(), 1, 0, 0, 0));
				}
			}
		});

		// setOnKeyPressed(new EventHandler<KeyEvent>() {
		//
		// public void handle(KeyEvent e) {
		// // e.consume();
		// switch (e.getCode()) {
		// case SHIFT:
		// isShiftDown.set(isShiftDown.get());
		// break;
		// // case CONTROL:
		// // setCtrlDown(!isCtrlDown);
		// // break;
		// // case ALT:
		// // setSymbolDown(!isSymbolDown);
		// // break;
		// }
		// }
		// });
	}

	public void setLayoutLocale(Locale local) throws MalformedURLException, IOException, URISyntaxException {
		logger.debug("try to set keyboard local: {}", local);
		KeyboardLayoutHandler handler = new KeyboardLayoutHandler();

		if (layerPathProperty.get() == null) {
			String xmlPath = "/xml/" + layerProperty.get().toString().toLowerCase(Locale.ENGLISH) + (local.getLanguage().equals("en") ? "/" : "/" + local.getLanguage() + "/");
			logger.info("use embedded layouts path: {}", xmlPath);

			getChildren().clear();
			qwertyKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "kb-layout.xml"));
			qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "kb-layout-shift.xml"));
			qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "kb-layout-ctrl.xml"));
			symbolKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "kb-layout-sym.xml"));
			symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "kb-layout-sym-shift.xml"));
			getChildren().addAll(qwertyKeyboardPane, qwertyShiftedKeyboardPane, qwertyCtrlKeyboardPane, symbolKeyboardPane, symbolShiftedKeyboardPane);
			
			for (javafx.scene.Node node : getChildren()) {
				node.setVisible(false);
			}
			return;
		}

		Path path = layerPathProperty.get();
		Map<Locale, Path> localMap = getAvailableLocales();
		if (localMap.containsKey(local)) {
			path = localMap.get(local);
		} else if (localMap.containsKey(new Locale(local.getLanguage()))) {
			logger.debug("use language compatible locale: {}", local.getLanguage());
			path = localMap.get(new Locale(local.getLanguage()));
		} else {
			logger.warn("locale: {} not available. try to use default", local);
		}

		if (path != null) {
			getChildren().clear();
			qwertyKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout.xml").toUri().toURL()));
			qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-shift.xml").toUri().toURL()));
			qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-ctrl.xml").toUri().toURL()));
			symbolKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-sym.xml").toUri().toURL()));
			symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-sym-shift.xml").toUri().toURL()));
			getChildren().addAll(qwertyKeyboardPane, qwertyShiftedKeyboardPane, qwertyCtrlKeyboardPane, symbolKeyboardPane, symbolShiftedKeyboardPane);
			
			for (javafx.scene.Node node : getChildren()) {
				node.setVisible(false);
			}
		}

	}

	public Map<Locale, Path> getAvailableLocales() {

		Map<Locale, Path> localList = new HashMap<>();
		if (layerPathProperty.get() == null) {
			localList.put(new Locale("de"), null);
			localList.put(new Locale("ru"), null);
			return localList;
		}
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(layerPathProperty.get())) {
			for (Path entry : stream) {
				if (entry.toFile().isDirectory()) {
					for (Locale l : Locale.getAvailableLocales()) {
						if (entry.getFileName().toString().equals(l.getLanguage() + (l.getCountry().isEmpty() ? "" : "_" + l.getCountry()))) {
							localList.put(l, entry);
							break;
						}
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("available locales: {}", localList.keySet());
		return localList;
	}

	public void setKeyboardLayer(KeyboardLayer layer) {
		final Region pane;
		switch (layer) {
		case QWERTY:
			pane = shiftProperty.get() ? qwertyShiftedKeyboardPane : qwertyKeyboardPane;
			break;
		case SYMBOL:
			pane = shiftProperty.get() ? symbolShiftedKeyboardPane : symbolKeyboardPane;
			break;
		case CTRL:
			pane = qwertyCtrlKeyboardPane;
			break;
		case NUMBER:
			pane = qwertyCtrlKeyboardPane;
			break;
		default:
			pane = qwertyKeyboardPane;
			break;
		}

		for (javafx.scene.Node node : getChildren()) {
			node.setVisible(false);
		}
		pane.setVisible(true);

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

				MultiKeyButton button = new MultiKeyButton(scaleProperty, getStylesheets());
				button.setFocusTraversable(false);
				button.setOnShortPressed(this);
				button.setCache(true);

				button.setMinHeight(10);
				button.setPrefHeight(defaultKeyHeight);
				button.setPrefWidth(defaultKeyWidth);
				button.setMaxWidth(defaultKeyWidth * 100);

				String[] codes = key.getCodes().split(",");
				if (codes.length > 0 && !codes[0].isEmpty()) {
					button.setKeyCode(parseInt(codes[0]));
				}
				if (codes.length > 1) {
					for (String code : codes) {
						int keyCode = parseInt(code);
						if (keyCode != button.getKeyCode()) {
							button.addExtKeyCode(keyCode);
						}
					}
				}

				if (key.getKeyLabelStyle() != null && key.getKeyLabelStyle().startsWith(".")) {
					for (String style : key.getKeyLabelStyle().split(";")) {
						button.getStyleClass().add(style.substring(1));
					}
				}

				if (button.getKeyCode() == LOCALE_SWITCH) {
					button.addExtKeyCode(LOCALE_SWITCH, Locale.ENGLISH.getLanguage().toUpperCase(Locale.ENGLISH), button.getStyleClass());
					for (Locale l : getAvailableLocales().keySet()) {
						button.addExtKeyCode(LOCALE_SWITCH, l.getLanguage().toUpperCase(Locale.ENGLISH), button.getStyleClass());
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

					InputStream is = KeyboardPane.class.getResourceAsStream(key.getKeyIconStyle().replace("@", "/") + ".png");
					Image image = new Image(is);
					if (!image.isError()) {
						button.setGraphic(new ImageView(image));
					} else {
						logger.error("Image: {} not found", key.getKeyIconStyle());
					}
				}

				button.setText(key.getKeyLabel());

				if (button.isContextAvailable() && button.getGraphic() == null) {
					button.getStyleClass().add("extend-style");
				}

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
					// use space button as drag pane
					button.setOnMouseMoved(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {
							// on Double.isNaN(getScene().getWindow().getX())
							// init window position
							mousePressedX = getScene().getWindow().getX() - event.getScreenX();
							mousePressedY = getScene().getWindow().getY() - event.getScreenY();
						}
					});

					button.setOnMouseDragged(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent mouseEvent) {
							getScene().getWindow().setX(mouseEvent.getScreenX() + mousePressedX);
							getScene().getWindow().setY(mouseEvent.getScreenY() + mousePressedY);

						}
					});
					break;
				case BACK_SPACE:
				case DELETE:
					button.setOnLongPressed(new EventHandler<Event>() {

						@Override
						public void handle(Event e) {
							e.consume();
							sendToComponent((char) 97, true);
							sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, ctrlProperty.get());
						}
					});
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
			shiftProperty.set(!shiftProperty.get());
			break;
		case SYMBOL_DOWN:
			// switch sym / qwerty
			symbolProperty.set(!symbolProperty.get());
			break;
		case CLOSE:
			if (closeEventHandler == null) {
				System.exit(0);
			} else {
				closeEventHandler.handle(new KeyButtonEvent(KeyButtonEvent.ANY));
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
			break;
		case LOCALE_SWITCH:
			try {
				Locale l = new Locale(kb.getText());
				setLayoutLocale(l);
			} catch (IOException | URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
			if (ctrlProperty.get()) {
				ctrlProperty.set(false);
			} else if (symbolProperty.get()) {
				symbolProperty.set(false);
			} else {
				setKeyboardLayer(KeyboardLayer.QWERTY);
			}
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

		default:
			// logger.debug(java.awt.event.KeyEvent.getKeyText(kb.getKeyCode()));
			if (kb.getKeyCode() > -1) {
				sendToComponent((char) kb.getKeyCode(), ctrlProperty.get());
			} else {
				logger.debug("unknown key code: {}", kb.getKeyCode());
				sendToComponent((char) kb.getKeyCode(), true);
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

		logger.trace("send ({})", ch);

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

	public void setLayer(DefaultLayers dl) {
		layerProperty.set(dl);
	}

	public ReadOnlyObjectProperty<DefaultLayers> layerProperty() {
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
		return scaleProperty;
	}

	public void setScaleOffset(double offset) {
		scaleOffsetProperty.set(offset);
	}

	public ReadOnlyDoubleProperty scaleOffsetProperty() {
		return scaleOffsetProperty;
	}
	
	public ReadOnlyDoubleProperty minScaleProperty() {
		return minScaleProperty;
	}

	public void setMinimumScale(double min) {
		minScaleProperty.set(min);
	}

	public ReadOnlyDoubleProperty maxScaleProperty() {
		return maxScaleProperty;
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
	
}
