package org.comtel.javafx;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.io.InputStream;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Popup;
import javafx.stage.Window;

import javax.swing.SwingUtilities;

import net.miginfocom.layout.BoundSize;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.layout.UnitValue;

import org.comtel.javafx.control.KeyButton;
import org.comtel.javafx.control.KeyboardLayer;
import org.comtel.javafx.control.MultiKeyButton;
import org.comtel.javafx.event.KeyButtonEvent;
import org.comtel.javafx.xml.KeyboardLayoutHandler;
import org.comtel.javafx.xml.layout.Keyboard;
import org.tbee.javafx.scene.layout.MigPane;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

public class KeyBoardPanel extends Group implements EventHandler<KeyButtonEvent> {

	private String layerPath;
	private Region qwertyKeyboardPane;
	private Region qwertyShiftedKeyboardPane;
	private Region symbolKeyboardPane;
	private Region symbolShiftedKeyboardPane;
	private Region qwertyCtrlKeyboardPane;

	private boolean isSymbolDown;
	private boolean isShiftDown;
	private boolean isCtrlDown;

	private boolean useAwtRobot;

	private double scale = 1.0d;

	private EventHandler<? super Event> closeEventHandler;

	/**
	 * default FX robot
	 * 
	 * @param layerpath
	 */
	public KeyBoardPanel(String layerpath) {
		this(layerpath, false);
	}

	public KeyBoardPanel(String layerpath, boolean awtRobot) {
		layerPath = layerpath;
		useAwtRobot = awtRobot;
		// setAutoSizeChildren(true);
		setFocusTraversable(false);
		init();

		setOnKeyPressed(new EventHandler<KeyEvent>() {

			public void handle(KeyEvent e) {
				// e.consume();
				switch (e.getCode()) {
				case SHIFT:
					setShiftDown(!isShiftDown);
					break;
				// case CONTROL:
				// setCtrlDown(!isCtrlDown);
				// break;
				// case ALT:
				// setSymbolDown(!isSymbolDown);
				// break;
				}
			}
		});
	}

	private void init() {

		KeyboardLayoutHandler handler = new KeyboardLayoutHandler();
		qwertyKeyboardPane = createKeyboardPane(handler.getLayout(layerPath + "/kb-layout.xml"));
		qwertyShiftedKeyboardPane = createKeyboardPane(handler.getLayout(layerPath + "/kb-layout-shift.xml"));
		qwertyCtrlKeyboardPane = createKeyboardPane(handler.getLayout(layerPath + "/kb-layout-ctrl.xml"));
		symbolKeyboardPane = createKeyboardPane(handler.getLayout(layerPath + "/kb-layout-sym.xml"));
		symbolShiftedKeyboardPane = createKeyboardPane(handler.getLayout(layerPath + "/kb-layout-sym-shift.xml"));

		setShifted(false);
		setSymbol(false);
		setCtrl(false);
		setKeyboardLayer(KeyboardLayer.QWERTY);

	}

	public void setKeyboardLayer(KeyboardLayer layer) {
		final Region pane;
		switch (layer) {
		case QWERTY:
			pane = isShiftDown ? qwertyShiftedKeyboardPane : qwertyKeyboardPane;
			break;
		case SYMBOL:
			pane = isShiftDown ? symbolShiftedKeyboardPane : symbolKeyboardPane;
			break;
		case CTRL:
			pane = qwertyCtrlKeyboardPane;
			break;
		default:
			pane = qwertyKeyboardPane;
			break;
		}

		getChildren().clear();
		getChildren().add(pane);
	}

	private double mousePressedX;
	private double mousePressedY;

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

				MultiKeyButton button = new MultiKeyButton();
				button.setFocusTraversable(false);
				button.setOnShortPressed(this);
				button.setMaxWidth(600);
				button.setPrefHeight(defaultKeyHeight);
				if (key.getCodes() != null) {
					String[] codes = key.getCodes().split(",");
					if (codes.length > 0) {
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

				}

				if (key.getKeyIcon() != null) {

					InputStream is = KeyBoardPanel.class.getResourceAsStream(key.getKeyIcon().replace("@", "/")
							+ ".png");
					Image image = new Image(is);
					if (!image.isError()) {
						button.setGraphic(new ImageView(image));
					} else {
						System.err.println("Image: " + key.getKeyIcon() + " not found");
					}
				}
				if (key.getKeyLabel() != null) {
					button.setText(key.getKeyLabel());
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
		return isShiftDown;
	}

	public void setShifted(boolean isShifted) {
		this.isShiftDown = isShifted;
	}

	public boolean isSymbol() {
		return isSymbolDown;
	}

	public void setSymbol(boolean isSymbol) {
		this.isSymbolDown = isSymbol;
	}

	public boolean isCtrl() {
		return isCtrlDown;
	}

	public void setCtrl(boolean isCtrl) {
		this.isCtrlDown = isCtrl;
	}

	public void handle(KeyButtonEvent event) {
		event.consume();
		KeyButtonEvent kbEvent = (KeyButtonEvent) event;
		if (kbEvent.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
			KeyButton kb = (KeyButton) kbEvent.getSource();
			switch (kb.getKeyCode()) {
			case -1:
				// switch shifted
				setShiftDown(!isShiftDown);
				break;
			case -2:
				// switch sym / qwerty
				setSymbolDown(!isSymbolDown);
				break;
			case -3:
				if (closeEventHandler == null) {
					System.exit(0);
				} else {
					closeEventHandler.handle(new KeyButtonEvent(KeyButtonEvent.ANY));
				}
				break;
			case -4:
				sendToComponent((char) java.awt.event.KeyEvent.VK_TAB);
				break;
			case -5:
				sendToComponent((char) java.awt.event.KeyEvent.VK_BACK_SPACE);
				break;
			case -6:
				// switch ctrl
				setCtrlDown(!isCtrlDown);
				break;
			default:
				// System.out.println(java.awt.event.KeyEvent.getKeyText(kb.getKeyCode()));
				if (kb.getKeyCode() > -1) {
					sendToComponent((char) kb.getKeyCode());
				}
				break;
			}
		}
	}

	private void setSymbolDown(boolean flag) {
		if (flag == isSymbolDown) {
			return;
		}
		setSymbol(!isSymbolDown);
		setShifted(false);
		setCtrl(false);
		setKeyboardLayer(isSymbolDown ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
	}

	private void setCtrlDown(boolean flag) {
		if (flag == isCtrlDown) {
			return;
		}
		setCtrl(!isCtrlDown);
		setShifted(false);
		setKeyboardLayer(isCtrlDown ? KeyboardLayer.CTRL : KeyboardLayer.QWERTY);
	}

	private void setShiftDown(boolean flag) {
		if (flag == isShiftDown) {
			return;
		}
		setShifted(!isShiftDown);
		setCtrl(false);
		setKeyboardLayer(isSymbolDown ? KeyboardLayer.SYMBOL : KeyboardLayer.QWERTY);
	}

	/**
	 * send keyEvent to java awt or fx
	 * 
	 * @param ch
	 */
	private void sendToComponent(final char ch) {

		System.out.println(ch + "\t" + (int) ch);

		if (useAwtRobot) {
			System.out.println("send to AWT");
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {
					sendToSwingComponent(ch);
				}
			});
		} else {
			final Window popup = getScene().getWindow();
			if (popup != null && popup instanceof Popup) {
				System.out.println("send to FX");
				Platform.runLater(new Runnable() {
					public void run() {
						sendToFxComponent(((Popup) popup).getOwnerWindow().getScene(), ch);
					}
				});
			}
		}
	}

	private void sendToSwingComponent(char ch) {
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c == null || !c.isEnabled()) {
			System.err.println("no awt focus owner");
			return;
		}

		Robot robot = null;
		try {
			robot = new Robot();
		} catch (AWTException e1) {
			e1.printStackTrace();
			return;
		}

		if (isCtrlDown) {
			switch (Character.toUpperCase(ch)) {
			case java.awt.event.KeyEvent.VK_A:
				robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				robot.keyPress(java.awt.event.KeyEvent.VK_A);
				robot.keyRelease(java.awt.event.KeyEvent.VK_A);
				robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_X:
				robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				robot.keyPress(java.awt.event.KeyEvent.VK_X);
				robot.keyRelease(java.awt.event.KeyEvent.VK_X);
				robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_C:
				robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				robot.keyPress(java.awt.event.KeyEvent.VK_C);
				robot.keyRelease(java.awt.event.KeyEvent.VK_C);
				robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_V:
				robot.keyPress(java.awt.event.KeyEvent.VK_CONTROL);
				robot.keyPress(java.awt.event.KeyEvent.VK_V);
				robot.keyRelease(java.awt.event.KeyEvent.VK_V);
				robot.keyRelease(java.awt.event.KeyEvent.VK_CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_MINUS:
				scale -= 0.1d;
				setScaleX(scale);
				setScaleY(scale);
				return;
			case 0x2B:
				scale += 0.1d;
				setScaleX(scale);
				setScaleY(scale);
				return;
			}
		}

		switch (ch) {
		case java.awt.event.KeyEvent.VK_ENTER:
			robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
			robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
			break;
		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			robot.keyPress(java.awt.event.KeyEvent.VK_BACK_SPACE);
			robot.keyRelease(java.awt.event.KeyEvent.VK_BACK_SPACE);
			break;
		case java.awt.event.KeyEvent.VK_DELETE:
			robot.keyPress(java.awt.event.KeyEvent.VK_DELETE);
			robot.keyRelease(java.awt.event.KeyEvent.VK_DELETE);
			break;
		case java.awt.event.KeyEvent.VK_ESCAPE:
			robot.keyPress(java.awt.event.KeyEvent.VK_ESCAPE);
			robot.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			robot.keyPress(java.awt.event.KeyEvent.VK_SPACE);
			robot.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
			break;
		case java.awt.event.KeyEvent.VK_TAB:
			robot.keyPress(java.awt.event.KeyEvent.VK_TAB);
			robot.keyRelease(java.awt.event.KeyEvent.VK_TAB);
			break;
		default:
			int modififiers = Character.isUpperCase(ch) ? java.awt.event.KeyEvent.SHIFT_DOWN_MASK : 0;
			KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
					new java.awt.event.KeyEvent(c, java.awt.event.KeyEvent.KEY_PRESSED, System.currentTimeMillis(),
							modififiers, java.awt.event.KeyEvent.VK_UNDEFINED, ch,
							java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
			KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
					new java.awt.event.KeyEvent(c, java.awt.event.KeyEvent.KEY_TYPED, System.currentTimeMillis(),
							modififiers, java.awt.event.KeyEvent.VK_UNDEFINED, ch,
							java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN));
			KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
					new java.awt.event.KeyEvent(c, java.awt.event.KeyEvent.KEY_RELEASED, System.currentTimeMillis(),
							modififiers, java.awt.event.KeyEvent.VK_UNDEFINED, ch,
							java.awt.event.KeyEvent.KEY_LOCATION_STANDARD));
			break;
		}
	}

	private void sendToFxComponent(Scene scene, char ch) {

		FXRobot robot = FXRobotFactory.createRobot(scene);

		if (isCtrlDown) {
			switch (Character.toUpperCase(ch)) {
			case java.awt.event.KeyEvent.VK_A:
				robot.keyPress(KeyCode.CONTROL);
				robot.keyPress(KeyCode.A);
				robot.keyType(KeyCode.A, "");
				robot.keyRelease(KeyCode.A);
				robot.keyRelease(KeyCode.CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_X:
				robot.keyPress(KeyCode.CUT);
				robot.keyType(KeyCode.CUT, "");
				robot.keyRelease(KeyCode.CUT);
				return;
			case java.awt.event.KeyEvent.VK_C:
				robot.keyPress(KeyCode.COPY);
				robot.keyType(KeyCode.COPY, "");
				robot.keyRelease(KeyCode.COPY);
				return;
			case java.awt.event.KeyEvent.VK_V:
				robot.keyPress(KeyCode.PASTE);
				robot.keyType(KeyCode.PASTE, "");
				robot.keyRelease(KeyCode.PASTE);
				return;
			case java.awt.event.KeyEvent.VK_MINUS:
				scale -= 0.1d;
				setScaleX(scale);
				setScaleY(scale);
				return;
			case 0x2B:
				scale += 0.1d;
				setScaleX(scale);
				setScaleY(scale);
				return;
			}

		}
		switch (ch) {
		case java.awt.event.KeyEvent.VK_ENTER:
			robot.keyPress(KeyCode.ENTER);
			robot.keyType(KeyCode.ENTER, Character.toString(ch));
			robot.keyRelease(KeyCode.ENTER);
			break;
		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			System.err.println("back_space");
			robot.keyPress(KeyCode.BACK_SPACE);
			robot.keyType(KeyCode.BACK_SPACE, Character.toString(ch));
			robot.keyRelease(KeyCode.BACK_SPACE);
			break;
		case java.awt.event.KeyEvent.VK_DELETE:
			robot.keyPress(KeyCode.DELETE);
			robot.keyType(KeyCode.DELETE, Character.toString(ch));
			robot.keyRelease(KeyCode.DELETE);
			break;
		case java.awt.event.KeyEvent.VK_ESCAPE:
			robot.keyPress(KeyCode.ESCAPE);
			robot.keyType(KeyCode.ESCAPE, Character.toString(ch));
			robot.keyRelease(KeyCode.ESCAPE);
			break;
		case java.awt.event.KeyEvent.VK_SPACE:
			robot.keyPress(KeyCode.SPACE);
			robot.keyType(KeyCode.SPACE, " ");
			robot.keyRelease(KeyCode.SPACE);
			break;
		case java.awt.event.KeyEvent.VK_TAB:
			robot.keyPress(KeyCode.TAB);
			robot.keyType(KeyCode.TAB, Character.toString(ch));
			robot.keyRelease(KeyCode.TAB);
			break;
		default:
			robot.keyPress(KeyCode.UNDEFINED);
			robot.keyType(KeyCode.UNDEFINED, Character.toString(ch));
			robot.keyRelease(KeyCode.UNDEFINED);
			break;
		}

	}

	public boolean isUseAwtRobot() {
		return useAwtRobot;
	}

	public void setUseAwtRobot(boolean useAwtRobot) {
		this.useAwtRobot = useAwtRobot;
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
		return scale;
	}

}