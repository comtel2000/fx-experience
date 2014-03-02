package org.comtel.javafx.robot;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.META;

import java.awt.event.KeyEvent;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.stage.Window;

import org.comtel.javafx.control.KeyboardPane;
import org.slf4j.LoggerFactory;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

public class FXRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXRobotHandler.class);

	private final KeyCode controlKeyCode;

	public FXRobotHandler() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("mac")) {
			controlKeyCode = META;
		} else {
			controlKeyCode = CONTROL;
		}
	}

	@Override
	public void sendToComponent(Object kb, final char ch, final boolean ctrl) {
		logger.trace("fire: {}", ch);

		final Window keyboardWindow = ((KeyboardPane) kb).getScene().getWindow();
		if (keyboardWindow != null) {
			final Scene scene;
			if (keyboardWindow instanceof Popup) {
				scene = ((Popup) keyboardWindow).getOwnerWindow().getScene();
			} else {
				scene = keyboardWindow.getScene();
			}

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					send(scene, ch, ctrl);
				}
			});
		}
	}

	private void send(Scene scene, char ch, boolean ctrl) {
		FXRobot robot = FXRobotFactory.createRobot(scene);
		if (ctrl) {
			switch (Character.toUpperCase(ch)) {
			case KeyEvent.VK_A:
				robot.keyPress(controlKeyCode);
				robot.keyPress(KeyCode.A);
				robot.keyType(KeyCode.A, "");
				robot.keyRelease(KeyCode.A);
				robot.keyRelease(controlKeyCode);
				return;
			case KeyEvent.VK_X:
				robot.keyPress(controlKeyCode);
				robot.keyPress(KeyCode.X);
				robot.keyType(KeyCode.X, "");
				robot.keyRelease(KeyCode.X);
				robot.keyRelease(controlKeyCode);
				return;
			case KeyEvent.VK_C:
				robot.keyPress(controlKeyCode);
				robot.keyPress(KeyCode.C);
				robot.keyType(KeyCode.C, "");
				robot.keyRelease(KeyCode.C);
				robot.keyRelease(controlKeyCode);
				return;
			case KeyEvent.VK_V:
				robot.keyPress(controlKeyCode);
				robot.keyPress(KeyCode.V);
				robot.keyType(KeyCode.V, "");
				robot.keyRelease(KeyCode.V);
				robot.keyRelease(controlKeyCode);
				return;
			}
			switch (ch) {
			case KeyEvent.VK_ENTER:
				robot.keyPress(KeyCode.ENTER);
				robot.keyType(KeyCode.ENTER, Character.toString(ch));
				robot.keyRelease(KeyCode.ENTER);
				return;
			case KeyEvent.VK_BACK_SPACE:
				robot.keyPress(KeyCode.BACK_SPACE);
				robot.keyType(KeyCode.BACK_SPACE, Character.toString(ch));
				robot.keyRelease(KeyCode.BACK_SPACE);
				return;
			case KeyEvent.VK_DELETE:
				robot.keyPress(KeyCode.DELETE);
				robot.keyType(KeyCode.DELETE, Character.toString(ch));
				robot.keyRelease(KeyCode.DELETE);
				return;
			case KeyEvent.VK_ESCAPE:
				robot.keyPress(KeyCode.ESCAPE);
				robot.keyType(KeyCode.ESCAPE, Character.toString(ch));
				robot.keyRelease(KeyCode.ESCAPE);
				return;
			case KeyEvent.VK_SPACE:
				robot.keyPress(KeyCode.SPACE);
				robot.keyType(KeyCode.SPACE, " ");
				robot.keyRelease(KeyCode.SPACE);
				return;
			case KeyEvent.VK_TAB:
				robot.keyPress(KeyCode.TAB);
				robot.keyType(KeyCode.TAB, Character.toString(ch));
				robot.keyRelease(KeyCode.TAB);
				return;
			case KeyEvent.VK_UP:
				robot.keyPress(KeyCode.UP);
				robot.keyType(KeyCode.UP, "");
				robot.keyRelease(KeyCode.UP);
				return;
			case KeyEvent.VK_DOWN:
				robot.keyPress(KeyCode.DOWN);
				robot.keyType(KeyCode.DOWN, "");
				robot.keyRelease(KeyCode.DOWN);
				return;
			case KeyEvent.VK_LEFT:
				robot.keyPress(KeyCode.LEFT);
				robot.keyType(KeyCode.LEFT, "");
				robot.keyRelease(KeyCode.LEFT);
				return;
			case KeyEvent.VK_RIGHT:
				robot.keyPress(KeyCode.RIGHT);
				robot.keyType(KeyCode.RIGHT, "");
				robot.keyRelease(KeyCode.RIGHT);
				return;
			}

		}

		robot.keyPress(KeyCode.UNDEFINED);
		robot.keyType(KeyCode.UNDEFINED, Character.toString(ch));
		robot.keyRelease(KeyCode.UNDEFINED);

	}

}
