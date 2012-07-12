package org.comtel.javafx.robot;

import org.comtel.javafx.control.KeyBoard;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import javafx.stage.Window;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

public class FXRobotHandler implements IRobot{

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXRobotHandler.class);
	
	@Override
	public void sendToComponent(Object kb, final char ch, final boolean ctrl) {
		logger.trace("fire: {}", ch);
		final Window popup = ((KeyBoard)kb).getScene().getWindow();
		if (popup != null && popup instanceof Popup) {
			Platform.runLater(new Runnable() {
				public void run() {
					send(((Popup) popup).getOwnerWindow().getScene(), ch, ctrl);
				}
			});
		}
	}

	private void send(Scene scene, char ch, boolean ctrl) {
		FXRobot robot = FXRobotFactory.createRobot(scene);
		if (ctrl) {
			switch (Character.toUpperCase(ch)) {
			case java.awt.event.KeyEvent.VK_A:
				robot.keyPress(KeyCode.CONTROL);
				robot.keyPress(KeyCode.A);
				robot.keyType(KeyCode.A, "");
				robot.keyRelease(KeyCode.A);
				robot.keyRelease(KeyCode.CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_X:
				robot.keyPress(KeyCode.CONTROL);
				robot.keyPress(KeyCode.X);
				robot.keyType(KeyCode.X, "");
				robot.keyRelease(KeyCode.X);
				robot.keyRelease(KeyCode.CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_C:
				robot.keyPress(KeyCode.CONTROL);
				robot.keyPress(KeyCode.C);
				robot.keyType(KeyCode.C, "");
				robot.keyRelease(KeyCode.C);
				robot.keyRelease(KeyCode.CONTROL);
				return;
			case java.awt.event.KeyEvent.VK_V:
				robot.keyPress(KeyCode.CONTROL);
				robot.keyPress(KeyCode.V);
				robot.keyType(KeyCode.V, "");
				robot.keyRelease(KeyCode.V);
				robot.keyRelease(KeyCode.CONTROL);
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

}
