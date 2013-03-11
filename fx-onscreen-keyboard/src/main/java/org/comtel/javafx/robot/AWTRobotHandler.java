package org.comtel.javafx.robot;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;

import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

public class AWTRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AWTRobotHandler.class);

	@Override
	public void sendToComponent(Object source, final char ch, final boolean ctrl) {
		logger.trace("fire: {}", ch);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				send(ch, ctrl);
			}
		});
	}

	private void send(char ch, boolean ctrl) {
		Component c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if (c == null || !c.isEnabled()) {
			logger.warn("no awt focus owner");
			return;
		}

		final Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error(e.getMessage(), e);
			return;
		}

		if (ctrl) {
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
			}

			switch (ch) {
			case java.awt.event.KeyEvent.VK_ENTER:
				robot.keyPress(java.awt.event.KeyEvent.VK_ENTER);
				robot.keyRelease(java.awt.event.KeyEvent.VK_ENTER);
				return;
			case java.awt.event.KeyEvent.VK_BACK_SPACE:
				robot.keyPress(java.awt.event.KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(java.awt.event.KeyEvent.VK_BACK_SPACE);
				return;
			case java.awt.event.KeyEvent.VK_DELETE:
				robot.keyPress(java.awt.event.KeyEvent.VK_DELETE);
				robot.keyRelease(java.awt.event.KeyEvent.VK_DELETE);
				return;
			case java.awt.event.KeyEvent.VK_ESCAPE:
				robot.keyPress(java.awt.event.KeyEvent.VK_ESCAPE);
				robot.keyRelease(java.awt.event.KeyEvent.VK_ESCAPE);
				return;
			case java.awt.event.KeyEvent.VK_SPACE:
				robot.keyPress(java.awt.event.KeyEvent.VK_SPACE);
				robot.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
				return;
			case java.awt.event.KeyEvent.VK_TAB:
				robot.keyPress(java.awt.event.KeyEvent.VK_TAB);
				robot.keyRelease(java.awt.event.KeyEvent.VK_TAB);
				return;
			case java.awt.event.KeyEvent.VK_UP:
				robot.keyPress(java.awt.event.KeyEvent.VK_UP);
				robot.keyRelease(java.awt.event.KeyEvent.VK_UP);
				return;
			case java.awt.event.KeyEvent.VK_DOWN:
				robot.keyPress(java.awt.event.KeyEvent.VK_DOWN);
				robot.keyRelease(java.awt.event.KeyEvent.VK_DOWN);
				return;
			case java.awt.event.KeyEvent.VK_LEFT:
				robot.keyPress(java.awt.event.KeyEvent.VK_LEFT);
				robot.keyRelease(java.awt.event.KeyEvent.VK_LEFT);
				return;
			case java.awt.event.KeyEvent.VK_RIGHT:
				robot.keyPress(java.awt.event.KeyEvent.VK_RIGHT);
				robot.keyRelease(java.awt.event.KeyEvent.VK_RIGHT);
				return;
			}
		}

		if (Character.isWhitespace(ch)){
			robot.keyPress(ch);
			robot.keyRelease(ch);
			return;
		}
		
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

	}

}
