package org.comtel.javafx.robot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

import org.slf4j.LoggerFactory;

/**
 * native OS support
 * <p>
 * dirty unicode char support only by transfer over OS clipboard..
 * 
 * @author comtel
 * 
 */
public class NativeAsciiRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(NativeAsciiRobotHandler.class);

	private final int controlKeyEvent;

	public NativeAsciiRobotHandler() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Mac")) {
			controlKeyEvent = KeyEvent.VK_META;
		} else {
			controlKeyEvent = KeyEvent.VK_CONTROL;
		}
	}

	@Override
	public void sendToComponent(Object kb, char ch, boolean ctrl) {
		logger.trace("fire: {}", ch);
		send(ch, ctrl);
	}

	private void send(char ch, boolean ctrl) {
		Robot robot;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			logger.error(e.getMessage(), e);
			return;
		}

		if (ctrl) {
			switch (Character.toUpperCase(ch)) {
			case KeyEvent.VK_A:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(KeyEvent.VK_A);
				robot.keyRelease(KeyEvent.VK_A);
				robot.keyRelease(controlKeyEvent);
				return;
			case KeyEvent.VK_X:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(KeyEvent.VK_X);
				robot.keyRelease(KeyEvent.VK_X);
				robot.keyRelease(controlKeyEvent);
				return;
			case KeyEvent.VK_C:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(KeyEvent.VK_C);
				robot.keyRelease(KeyEvent.VK_C);
				robot.keyRelease(controlKeyEvent);
				return;
			case KeyEvent.VK_V:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(KeyEvent.VK_V);
				robot.keyRelease(KeyEvent.VK_V);
				robot.keyRelease(controlKeyEvent);
				return;
			}

			switch (ch) {
			case KeyEvent.VK_ENTER:
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				return;
			case KeyEvent.VK_BACK_SPACE:
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
				return;
			case KeyEvent.VK_DELETE:
				robot.keyPress(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_DELETE);
				return;
			case KeyEvent.VK_ESCAPE:
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
				return;
			case KeyEvent.VK_SPACE:
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				return;
			case KeyEvent.VK_TAB:
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				return;
			case KeyEvent.VK_UP:
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_UP);
				return;
			case KeyEvent.VK_DOWN:
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);
				return;
			case KeyEvent.VK_LEFT:
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				return;
			case KeyEvent.VK_RIGHT:
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				return;
			}
		}

		int keyCode = KeyEvent.getExtendedKeyCodeForChar((int) ch);
		if (Character.isWhitespace(ch)) {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
			return;
		}
		if (KeyEvent.VK_UNDEFINED == keyCode || keyCode > 10000) {
			clipboardTransfer(robot, ch);
			return;
		}
		boolean isUpperCase = Character.isUpperCase(ch);

		if (isUpperCase) {
			robot.keyPress(KeyEvent.VK_SHIFT);
		}
		try {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage() + String.format(" %s (%s)", keyCode, ch));
			if (isUpperCase) {
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
			clipboardTransfer(robot, ch);
			return;

		}
		if (isUpperCase) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}

	}

	/**
	 * does not work for extended codepage signs
	 */
	private void winAltDump(Robot robot, int ch) {
		robot.keyPress(KeyEvent.VK_ALT);
		for (int i = 3; i >= 0; --i) {
			int vk = ch / (int) (Math.pow(10, i)) % 10 + KeyEvent.VK_NUMPAD0;
			logger.debug("{} - {}", vk, KeyEvent.getKeyText(vk));
			robot.keyPress(vk);
			robot.keyRelease(vk);
		}
		robot.keyRelease(KeyEvent.VK_ALT);
	}

	private void clipboardTransfer(Robot robot, char ch) {
		try {
			StringSelection stringSelection = new StringSelection(Character.toString(ch));
			java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			Object recover = clpbrd.getData(DataFlavor.stringFlavor);
			clpbrd.setContents(stringSelection, null);

			robot.keyPress(controlKeyEvent);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_V);
			robot.keyRelease(controlKeyEvent);
			robot.delay(50);

			StringSelection recoverSelection = new StringSelection(recover != null ? recover.toString() : "");
			clpbrd.setContents(recoverSelection, null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
