package org.comtel.javafx.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.transform.Scale;

import org.comtel.javafx.event.KeyButtonEvent;
import org.comtel.javafx.robot.IRobot;
import org.comtel.javafx.xml.KeyboardLayoutHandler;
import org.comtel.javafx.xml.layout.Keyboard;
import org.slf4j.LoggerFactory;

public class KeyboardPane extends Region implements StandardKeyCode, EventHandler<KeyButtonEvent> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardPane.class);

	private Path layerPath;
	private Region qwertyKeyboardPane;
	private Region qwertyShiftedKeyboardPane;
	private Region symbolKeyboardPane;
	private Region symbolShiftedKeyboardPane;
	private Region qwertyCtrlKeyboardPane;

	private SimpleBooleanProperty symbolProperty = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty shiftProperty = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty ctrlProperty = new SimpleBooleanProperty(false);

	private final double SCALE_OFFSET = 0.2;
	private final SimpleDoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);

	private SimpleDoubleProperty minScaleProperty = new SimpleDoubleProperty(0.7);
	private SimpleDoubleProperty maxScaleProperty = new SimpleDoubleProperty(5.0);

	private EventHandler<? super Event> closeEventHandler;

	private double mousePressedX;
	private double mousePressedY;

	private final List<IRobot> robotHandler = new ArrayList<>();
	private Locale layoutLocale;

	public KeyboardPane(Path layerpath) {
		this(layerpath, 1.0, Locale.getDefault());
	}

	public KeyboardPane(Path layerpath, Locale local) {
		this(layerpath, 1.0, local);
	}

	/**
	 * create KeyBoard Region with layer XML root path, intial scale and locale
	 * 
	 * @param layerpath
	 * @param scale
	 * @param local
	 */
	public KeyboardPane(Path layerpath, double scale, Locale local) {
		layerPath = layerpath;
		// setScaleShape(true);

		layoutLocale = local != null ? local : Locale.getDefault();
		setId("key-background");

		setFocusTraversable(false);

		init();

		if (scale != 1.0) {
			scaleProperty.set(scale);
			getTransforms().setAll(new Scale(scaleProperty.get(), scaleProperty.get(), 1, 0, 0, 0));
		}

		scaleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number s) {
				getTransforms().setAll(new Scale(s.doubleValue(), s.doubleValue(), 1, 0, 0, 0));
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

	private void init() {

		try {
			setLayoutLocale(layoutLocale);
		} catch (IOException | URISyntaxException e) {
			logger.error(e.getMessage(), e);
			return;
		}
		setKeyboardLayer(KeyboardLayer.QWERTY);

		shiftProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (ctrlProperty.get()) {
					logger.warn("ignore in ctrl mode");
					return;
				}
				setKeyboardLayer(symbolProperty.get() ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

		ctrlProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (arg2) {
					setKeyboardLayer(KeyboardLayer.CTRL);
				} else {
					setKeyboardLayer(symbolProperty.get() ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
				}
			}
		});

		symbolProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				if (ctrlProperty.get()) {
					logger.warn("ignore in ctrl mode");
					return;
				}
				setKeyboardLayer(arg2 ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

	}

	public void setLayoutLocale(Locale local) throws MalformedURLException, IOException, URISyntaxException {
		logger.debug("try to set keyboard local: {}", local);
		KeyboardLayoutHandler handler = new KeyboardLayoutHandler();

		if (layerPath == null) {
			String xmlPath = "/xml/default" + (local.getLanguage().equals("en") ? "" : "/" + local.getLanguage());
			logger.warn("use default embedded layouts path: {}", xmlPath);

			getChildren().clear();

			qwertyKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout.xml"));
			qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-shift.xml"));
			qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-ctrl.xml"));
			symbolKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-sym.xml"));
			symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-sym-shift.xml"));

			getChildren().addAll(qwertyKeyboardPane, qwertyShiftedKeyboardPane, qwertyCtrlKeyboardPane, symbolKeyboardPane, symbolShiftedKeyboardPane);
			for (javafx.scene.Node node : getChildren()) {
				node.setVisible(false);
			}
			return;
		}

		Path path = layerPath;
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
		if (layerPath == null) {
			localList.put(new Locale("de"), null);
			localList.put(new Locale("ru"), null);
			return localList;
		}
		try {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(layerPath)) {
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
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.debug("available locales: {}", localList.keySet());
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

				MultiKeyButton button = new MultiKeyButton(scaleProperty);
				button.setFocusTraversable(false);
				button.setOnShortPressed(this);
				button.setCache(true);

				button.setMinHeight(10);
				button.setPrefHeight(defaultKeyHeight);
				button.setPrefWidth(defaultKeyWidth);
				button.setMaxWidth(defaultKeyWidth * 100);

				String[] codes = key.getCodes().split(",");
				if (codes.length > 0 && !codes[0].isEmpty()) {
					int keyCode = Integer.valueOf(codes[0]);
					button.setKeyCode(keyCode);
				}
				if (codes.length > 1) {
					for (String code : codes) {
						int keyCode = Integer.valueOf(code);
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

				// use space button as drag pane
				if (button.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {

					button.setOnMouseMoved(new EventHandler<MouseEvent>() {

						@Override
						public void handle(MouseEvent event) {
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

				}
				if (button.getKeyCode() == BACK_SPACE || button.getKeyCode() == DELETE) {
					button.setOnLongPressed(new EventHandler<Event>() {

						@Override
						public void handle(Event e) {
							e.consume();
							sendToComponent((char) 97, true);
							sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, ctrlProperty.get());
						}
					});
				}

				colPane.add(button, colIdx, 0);
				colPane.getColumnConstraints().add(cc);

				logger.trace("btn: {} {}", button.getText(), cc);
				colIdx++;
				rowWidth += cc.getPrefWidth();
			}
			logger.info("row[{}] - {}", rowIdx, rowWidth);
			colPane.getRowConstraints().add(rc);
			// colPane.setGridLinesVisible(true);
			rPane.add(colPane, 0, rowIdx);
			rowIdx++;
		}

		logger.debug("-----end pane-----");
		return rPane;
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

	public void handle(KeyButtonEvent event) {
		event.consume();
		KeyButtonEvent kbEvent = (KeyButtonEvent) event;
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
					scaleProperty.set(scaleProperty.get() - SCALE_OFFSET);
				}
				return;
			case 0x2B:
				if (scaleProperty.get() < maxScaleProperty.get()) {
					scaleProperty.set(scaleProperty.get() + SCALE_OFFSET);
				}
				return;
			}
		}

		if (robotHandler.isEmpty()) {
			logger.error("no robot handler available");
			return;
		}
		for (IRobot robot : robotHandler) {
			robot.sendToComponent(this, ch, ctrl);
		}

	}

	public void addRobotHandler(IRobot robot) {
		robotHandler.add(robot);
	}

	public void removeRobotHandler(IRobot robot) {
		robotHandler.remove(robot);
	}

	public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
		closeEventHandler = value;
	}

	/**
	 * default keyboard scale
	 * 
	 * @param scale
	 */
	public double getScale() {
		return scaleProperty.get();
	}

	public void setScale(double scale) {
		scaleProperty.set(scale);
	}

	public double getMinimumScale() {
		return minScaleProperty.get();
	}

	public void setMinimumScale(double min) {
		minScaleProperty.set(min);
	}

	public double getMaximumScale() {
		return maxScaleProperty.get();
	}

	public void setMaximumScale(double max) {
		maxScaleProperty.set(max);
	}

}
