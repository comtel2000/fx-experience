package org.comtel.javafx.robot;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import org.slf4j.LoggerFactory;

/**
 * native OS support<p>
 * dirty unicode char support only by transfer over OS clipboard..
 * 
 * @author comtel
 * 
 */
public class NativeAsciiRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(NativeAsciiRobotHandler.class);

	private final int controlKeyEvent;
	String clipboardRecovered;

	public NativeAsciiRobotHandler() {
		String osName = System.getProperty("os.name");      
		if (osName.startsWith("Mac")){
			controlKeyEvent = KeyEvent.VK_META;
		}else{
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

		if (Character.isWhitespace(ch)) {
			robot.keyPress(ch);
			robot.keyRelease(ch);
			return;
		}

		boolean isUpperCase = Character.isUpperCase(ch);
		int keyCode = KeyEvent.getExtendedKeyCodeForChar(ch);
		System.err.println("key code: " + keyCode);

		if (isUpperCase) {
			robot.keyPress(KeyEvent.VK_SHIFT);
		}
		try {
			robot.keyPress(keyCode);
			robot.keyRelease(keyCode);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage() + " " + ch);
			clipboardTransfer(robot, ch);
		}
		if (isUpperCase) {
			robot.keyRelease(KeyEvent.VK_SHIFT);
		}

	}

	private void clipboardTransfer(Robot robot, char ch) {
		final String clipboardRecovered = Clipboard.getSystemClipboard().getString();

		ClipboardContent content = new ClipboardContent();
		content.putString(Character.toString(ch));
		Clipboard.getSystemClipboard().setContent(content);

		robot.keyPress(controlKeyEvent);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(controlKeyEvent);

		// ClipboardContent recover = new ClipboardContent();
		// recover.putString(clipboardRecovered != null ? clipboardRecovered :
		// "");
		// Clipboard.getSystemClipboard().setContent(recover);

		// Thread recoverThread = new Thread(new Runnable() {
		//
		// @Override
		// public void run() {
		// try {
		// Thread.sleep(200);
		// } catch (final InterruptedException e) {
		// }
		// ClipboardContent recover = new ClipboardContent();
		// recover.putString(clipboardRecovered != null ? clipboardRecovered :
		// "");
		// Clipboard.getSystemClipboard().setContent(recover);
		//
		// }
		// });
		// recoverThread.setDaemon(true);
		// recoverThread.start();

		// recover old content
		// clipboard.setContent(Collections.singletonMap(DataFormat.PLAIN_TEXT,
		// content != null ? content : ""));
	}

}
