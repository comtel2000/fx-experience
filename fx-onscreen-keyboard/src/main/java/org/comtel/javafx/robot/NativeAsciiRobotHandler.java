package org.comtel.javafx.robot;

import static java.awt.event.KeyEvent.VK_A;
import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_BACK_SPACE;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_DELETE;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_META;
import static java.awt.event.KeyEvent.VK_NUMPAD0;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_UNDEFINED;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_V;
import static java.awt.event.KeyEvent.VK_X;
import static java.awt.event.KeyEvent.VK_Y;
import static java.awt.event.KeyEvent.VK_Z;
import static java.awt.event.KeyEvent.getExtendedKeyCodeForChar;
import static java.awt.event.KeyEvent.getKeyText;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;

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
			controlKeyEvent = VK_META;
		} else {
			controlKeyEvent = VK_CONTROL;
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
			switch (ch) {
			case VK_ENTER:
			case VK_BACK_SPACE:
			case VK_DELETE:
			case VK_ESCAPE:
			case VK_SPACE:
			case VK_TAB:
			case VK_UP:
			case VK_DOWN:
			case VK_LEFT:
			case VK_RIGHT:
				robot.keyPress(ch);
				robot.keyRelease(ch);
				return;
			}
			
			int upperCase = Character.toUpperCase(ch);
			switch (upperCase) {
			case VK_A:
			case VK_X:
			case VK_C:
			case VK_V:
			case VK_Z:
			case VK_Y:
				robot.keyPress(controlKeyEvent);
				robot.keyPress(upperCase);
				robot.keyRelease(upperCase);
				robot.keyRelease(controlKeyEvent);
				return;
			}


		}

		int keyCode = getExtendedKeyCodeForChar(ch);
		if (Character.isWhitespace(ch)) {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
			return;
		}
		if (VK_UNDEFINED == keyCode || keyCode > 10000) {
			clipboardTransfer(robot, ch);
			return;
		}
		boolean isUpperCase = Character.isUpperCase(ch);

		if (isUpperCase) {
			robot.keyPress(VK_SHIFT);
		}
		try {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		} catch (IllegalArgumentException e) {
			logger.warn(e.getMessage() + String.format(" %s (%s)", keyCode, ch));
			if (isUpperCase) {
				robot.keyRelease(VK_SHIFT);
			}
			clipboardTransfer(robot, ch);
			return;

		}
		if (isUpperCase) {
			robot.keyRelease(VK_SHIFT);
		}

	}

	/**
	 * does not work for extended codepage signs
	 */
	private void winAltDump(Robot robot, int ch) {
		robot.keyPress(VK_ALT);
		for (int i = 3; i >= 0; --i) {
			int vk = ch / (int) (Math.pow(10, i)) % 10 + VK_NUMPAD0;
			logger.debug("{} - {}", vk, getKeyText(vk));
			robot.keyPress(vk);
			robot.keyRelease(vk);
		}
		robot.keyRelease(VK_ALT);
	}

	private void clipboardTransfer(Robot robot, char ch) {
		try {
			StringSelection stringSelection = new StringSelection(Character.toString(ch));
			java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			Object recover = clpbrd.getData(DataFlavor.stringFlavor);
			clpbrd.setContents(stringSelection, null);

			robot.keyPress(controlKeyEvent);
			robot.keyPress(VK_V);
			robot.keyRelease(VK_V);
			robot.keyRelease(controlKeyEvent);
			robot.delay(50);

			StringSelection recoverSelection = new StringSelection(recover != null ? recover.toString() : "");
			clpbrd.setContents(recoverSelection, null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
