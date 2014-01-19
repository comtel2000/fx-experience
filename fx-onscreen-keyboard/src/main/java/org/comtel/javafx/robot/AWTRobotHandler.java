package org.comtel.javafx.robot;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import static java.awt.event.KeyEvent.*;

import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

public class AWTRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AWTRobotHandler.class);

	private final int controlKeyEvent;

	public AWTRobotHandler() {
		String osName = System.getProperty("os.name");      
		if (osName.toLowerCase().startsWith("mac")){
			controlKeyEvent = VK_META;
		}else{
			controlKeyEvent = VK_CONTROL;
		}
	}
	
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
			case VK_A:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(VK_A);
				robot.keyRelease(VK_A);
				robot.keyRelease(controlKeyEvent);
				return;
			case VK_X:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(VK_X);
				robot.keyRelease(VK_X);
				robot.keyRelease(controlKeyEvent);
				return;
			case VK_C:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(VK_C);
				robot.keyRelease(VK_C);
				robot.keyRelease(controlKeyEvent);
				return;
			case VK_V:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(VK_V);
				robot.keyRelease(VK_V);
				robot.keyRelease(controlKeyEvent);
				return;
			}

			switch (ch) {
			case VK_ENTER:
				robot.keyPress(VK_ENTER);
				robot.keyRelease(VK_ENTER);
				return;
			case VK_BACK_SPACE:
				robot.keyPress(VK_BACK_SPACE);
				robot.keyRelease(VK_BACK_SPACE);
				return;
			case VK_DELETE:
				robot.keyPress(VK_DELETE);
				robot.keyRelease(VK_DELETE);
				return;
			case VK_ESCAPE:
				robot.keyPress(VK_ESCAPE);
				robot.keyRelease(VK_ESCAPE);
				return;
			case VK_SPACE:
				robot.keyPress(VK_SPACE);
				robot.keyRelease(VK_SPACE);
				return;
			case VK_TAB:
				robot.keyPress(VK_TAB);
				robot.keyRelease(VK_TAB);
				return;
			case VK_UP:
				robot.keyPress(VK_UP);
				robot.keyRelease(VK_UP);
				return;
			case VK_DOWN:
				robot.keyPress(VK_DOWN);
				robot.keyRelease(VK_DOWN);
				return;
			case VK_LEFT:
				robot.keyPress(VK_LEFT);
				robot.keyRelease(VK_LEFT);
				return;
			case VK_RIGHT:
				robot.keyPress(VK_RIGHT);
				robot.keyRelease(VK_RIGHT);
				return;
			}
		}

		if (Character.isWhitespace(ch)){
			robot.keyPress(ch);
			robot.keyRelease(ch);
			return;
		}
		
		int modififiers = Character.isUpperCase(ch) ? SHIFT_DOWN_MASK : 0;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
				new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(),
						modififiers, VK_UNDEFINED, ch,
						KEY_LOCATION_STANDARD));
		KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
				new KeyEvent(c, KEY_TYPED, System.currentTimeMillis(),
						modififiers, VK_UNDEFINED, ch,
						KEY_LOCATION_UNKNOWN));
		KeyboardFocusManager.getCurrentKeyboardFocusManager().dispatchKeyEvent(
				new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(),
						modififiers, VK_UNDEFINED, ch,
						KEY_LOCATION_STANDARD));

	}

}
