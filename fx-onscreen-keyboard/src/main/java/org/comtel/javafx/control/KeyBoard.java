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
import javafx.scene.Group;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;

import org.comtel.javafx.event.KeyButtonEvent;
import org.comtel.javafx.robot.IRobot;
import org.comtel.javafx.xml.KeyboardLayoutHandler;
import org.comtel.javafx.xml.layout.Keyboard;
import org.slf4j.LoggerFactory;
import org.tbee.javafx.scene.layout.MigPane;

public class KeyBoard extends Group implements EventHandler<KeyButtonEvent> {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyBoard.class);

	private static final int SHIFT_DOWN = -1;
	private static final int SYMBOL_DOWN = -2;
	private static final int CLOSE = -3;
	private static final int TAB = -4;
	private static final int BACK_SPACE = -5;
	private static final int CTRL_DOWN = -6;
	private static final int LOCALE_SWITCH = -7;

	private Path layerPath;
	private Region qwertyKeyboardPane;
	private Region qwertyShiftedKeyboardPane;
	private Region symbolKeyboardPane;
	private Region symbolShiftedKeyboardPane;
	private Region qwertyCtrlKeyboardPane;

	private SimpleBooleanProperty symbolProperty = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty shiftProperty = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty ctrlProperty = new SimpleBooleanProperty(false);

	private SimpleDoubleProperty scaleProperty = new SimpleDoubleProperty(1.0);

	private EventHandler<? super Event> closeEventHandler;

	private double mousePressedX;
	private double mousePressedY;

	private final List<IRobot> robotHandler = new ArrayList<>();
	private Locale layoutLocale;

	/**
	 * @param layerpath
	 */
	public KeyBoard(Path layerpath) {
		this(layerpath, null, Locale.getDefault());
	}

	public KeyBoard(Path layerpath, IRobot robot) {
		this(layerpath, robot, Locale.getDefault());
	}
	
	public KeyBoard(Path layerpath, Locale local) {
		this(layerpath, null, local);
	}
	
	public KeyBoard(Path layerpath, IRobot robot, Locale local) {
		layerPath = layerpath;
		if (robot != null){
			robotHandler.add(robot);
		}
		layoutLocale = local;

		// setAutoSizeChildren(true);
		setFocusTraversable(false);
		
		init();

		scaleProperty.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				setScaleX(arg2.doubleValue());
				setScaleY(arg2.doubleValue());
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
				setKeyboardLayer(symbolProperty.get() ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

		ctrlProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				setKeyboardLayer(arg2 ? KeyboardLayer.CTRL : KeyboardLayer.QWERTY);
			}
		});

		symbolProperty.addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				setKeyboardLayer(arg2 ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
			}
		});

	}

	public void setLayoutLocale(Locale local) throws MalformedURLException, IOException, URISyntaxException {
		logger.debug("try to set keyboard local: {}", local);
		KeyboardLayoutHandler handler = new KeyboardLayoutHandler();

		if (layerPath == null) {
			String xmlPath = "/xml/default/" + (local.getLanguage().equals("en") ? "" : local.getLanguage());
			logger.warn("use default embedded layouts path: {}", xmlPath);
			
			qwertyKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout.xml"));
			qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-shift.xml"));
			qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-ctrl.xml"));
			symbolKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-sym.xml"));
			symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(xmlPath + "/kb-layout-sym-shift.xml"));
			return;
		}

		Path path = layerPath;
		Map<Locale, Path> localMap = getAvailableLocales();
		if (localMap.containsKey(local)) {
			path = localMap.get(local);
		} else if (localMap.containsKey(new Locale(local.getLanguage()))) {
			logger.debug("use language compatible locale: {}", local.getLanguage());
			path = localMap.get(new Locale(local.getLanguage()));
		}

		if (path != null) {
			qwertyKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout.xml").toUri().toURL()));
			qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-shift.xml")
					.toUri().toURL()));
			qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-ctrl.xml").toUri()
					.toURL()));
			symbolKeyboardPane = createKeyboardPane(handler
					.getLayout(path.resolve("kb-layout-sym.xml").toUri().toURL()));
			symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(path.resolve("kb-layout-sym-shift.xml")
					.toUri().toURL()));
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
							if (entry.getFileName().toString()
									.equals(l.getLanguage() + (l.getCountry().isEmpty() ? "" : "_" + l.getCountry()))) {
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

		getChildren().clear();
		getChildren().add(pane);

		// if (getChildren().isEmpty()){
		// qwertyKeyboardPane.setVisible(false);
		// getChildren().add(qwertyKeyboardPane);
		// qwertyShiftedKeyboardPane.setVisible(false);
		// getChildren().add(qwertyShiftedKeyboardPane);
		//
		// symbolKeyboardPane.setVisible(false);
		// getChildren().add(symbolKeyboardPane);
		// symbolShiftedKeyboardPane.setVisible(false);
		// getChildren().add(symbolShiftedKeyboardPane);
		//
		// qwertyCtrlKeyboardPane.setVisible(false);
		// getChildren().add(qwertyCtrlKeyboardPane);
		// }
		// for (javafx.scene.Node node : getChildren()){
		// node.setVisible(false);
		// }
		// pane.setVisible(true);

	}

	/**
	 * with MIG layout manager
	 * 
	 * @param layout
	 * @return
	 */
	private Region createKeyboardPane(Keyboard layout) {

		MigPane pane = new MigPane();

		setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				mousePressedX = event.getX();
				mousePressedY = event.getY();
			}
		});

		setOnMouseDragged(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				getScene().getWindow().setX(event.getScreenX() - mousePressedX);
				getScene().getWindow().setY(event.getScreenY() - mousePressedY);

			}
		});

		pane.setPrefSize(650, 200);
		pane.setId("key-background");
		LC lc = new LC();
		lc.setNoGrid(true);
		lc.setFillX(true);
		lc.setFillY(true);
		// lc.setDebugMillis(1000);
		UnitValue uv = new UnitValue((float) pane.getPrefWidth(), UnitValue.PIXEL, "bSize");
		BoundSize bs = new BoundSize(uv, "bbSize");
		lc.setWidth(bs);
		pane.setLayoutConstraints(lc);

		int defaultKeyWidth = 10;

		if (layout.getKeyWidth() != null) {
			defaultKeyWidth = layout.getKeyWidth();
		}

		int defaultKeyHeight = 45;

		if (layout.getKeyHeight() != null) {
			defaultKeyHeight = layout.getKeyHeight();
		}

		for (Keyboard.Row row : layout.getRow()) {

			for (Keyboard.Row.Key key : row.getKey()) {
				CC lCC = new CC();
				// if (key.getKeyLabel() != null &&
				// !key.getKeyLabel().isEmpty()) {
				// System.err.println(MessageFormat.format("\t<Key codes=\"{0}\" keyLabel=\"{1}\" />",
				// Integer.toString((int) key.getKeyLabel().charAt(0)),
				// key.getKeyLabel()));
				// }
				MultiKeyButton button = new MultiKeyButton();
				button.setFocusTraversable(false);
				button.setOnShortPressed(this);
				button.setMaxWidth(600);
				button.setPrefHeight(defaultKeyHeight);
				if (key.getCodes() != null) {
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
					if (button.getKeyCode() == LOCALE_SWITCH){
						button.addExtKeyCode(LOCALE_SWITCH, Locale.ENGLISH.getLanguage().toUpperCase(Locale.ENGLISH));
						for (Locale l : getAvailableLocales().keySet()){
							button.addExtKeyCode(LOCALE_SWITCH, l.getLanguage().toUpperCase(Locale.ENGLISH));
						}
					}
				}

				if (key.getKeyLabelStyle() != null && key.getKeyLabelStyle().startsWith(".")) {
					button.getStyleClass().add(key.getKeyLabelStyle().substring(1));
				}
				if (key.getKeyIconStyle() != null && key.getKeyIconStyle().startsWith(".")) {
					logger.trace("Load css style: {}", key.getKeyIconStyle());
					Label icon = new Label();
					icon.getStyleClass().add(key.getKeyIconStyle().substring(1));
					button.setContentDisplay(ContentDisplay.BOTTOM);
					button.setGraphic(icon);

				} else if (key.getKeyIconStyle() != null && key.getKeyIconStyle().startsWith("@")) {

					InputStream is = KeyBoard.class.getResourceAsStream(key.getKeyIconStyle().replace("@", "/")
							+ ".png");
					Image image = new Image(is);
					if (!image.isError()) {
						button.setGraphic(new ImageView(image));
					} else {
						logger.error("Image: {} not found", key.getKeyIconStyle());
					}
				}

				button.setText(key.getKeyLabel());
				
				if (button.isContextAvailable() && button.getGraphic() == null) {
					Label icon = new Label();
					icon.getStyleClass().add("extend-style");
					button.setGraphic(icon);
				}

				if (key.getKeyWidth() != null) {
					int p = key.getKeyWidth();
					button.setPrefWidth((pane.getPrefWidth() * p) / 100);
				} else {
					button.setPrefWidth((pane.getPrefWidth() * defaultKeyWidth) / 100);
				}

				lCC.growX();
				if (key.getKeyEdgeFlags() != null) {
					if (key.getHorizontalGap() != null) {
						if (key.getKeyEdgeFlags().equals("right")) {
							lCC.gapRight(key.getHorizontalGap() + "%");
						} else {
							lCC.gapLeft(key.getHorizontalGap() + "%");
						}
					}

					lCC.setWrap(key.getKeyEdgeFlags().equals("right"));
				}
				// use space button as drag pane
				if (button.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
					button.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

						public void handle(MouseEvent event) {
							mousePressedX = event.getSceneX();
							mousePressedY = event.getSceneY();
						}
					});

					button.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

						public void handle(MouseEvent event) {
							event.consume();
							getScene().getWindow().setX(event.getScreenX() - mousePressedX);
							getScene().getWindow().setY(event.getScreenY() - mousePressedY);

						}
					});
				}
				pane.add(button, lCC);

			}
		}
		return pane;
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
		if (kbEvent.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
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
				sendToComponent((char) java.awt.event.KeyEvent.VK_TAB);
				break;
			case BACK_SPACE:
				sendToComponent((char) java.awt.event.KeyEvent.VK_BACK_SPACE);
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
			default:
				// logger.debug(java.awt.event.KeyEvent.getKeyText(kb.getKeyCode()));
				if (kb.getKeyCode() > -1) {
					sendToComponent((char) kb.getKeyCode());
				} else {
					logger.warn("key code: {} not supported", kb.getKeyCode());
				}
				break;
			}
		}
	}

	/**
	 * send keyEvent to iRobot implementation
	 * 
	 * @param ch
	 */
	private void sendToComponent(final char ch) {

		logger.trace("send ({})", ch);

		if (ctrlProperty.get()) {
			switch (Character.toUpperCase(ch)) {
			case java.awt.event.KeyEvent.VK_MINUS:
				scaleProperty.set(scaleProperty.get() - 0.1d);
				return;
			case 0x2B:
				scaleProperty.set(scaleProperty.get() + 0.1d);
				return;
			}
		}

		if (robotHandler.isEmpty()) {
			logger.error("no robot handler available");
			return;
		}
		for (IRobot robot : robotHandler){
			robot.sendToComponent(this, ch, ctrlProperty.get());
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

}
