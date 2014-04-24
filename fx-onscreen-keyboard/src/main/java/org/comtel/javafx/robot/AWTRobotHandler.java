package org.comtel.javafx.robot;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.META_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.KEY_LOCATION_STANDARD;
import static java.awt.event.KeyEvent.KEY_LOCATION_UNKNOWN;
import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;
import static java.awt.event.KeyEvent.KEY_TYPED;
import static java.awt.event.KeyEvent.VK_UNDEFINED;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import org.slf4j.LoggerFactory;

public class AWTRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AWTRobotHandler.class);

	private final int controlDownMask;

	public AWTRobotHandler() {
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().startsWith("mac")) {
			controlDownMask = META_DOWN_MASK;
		} else {
			controlDownMask = CTRL_DOWN_MASK;
		}
	}

	@Override
	public void sendToComponent(Object source, final char ch, final boolean ctrl) {
		logger.trace("fire: {}", ch);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
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

		KeyboardFocusManager kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();

		if (ctrl) {
			switch (ch) {
			case java.awt.event.KeyEvent.VK_ENTER:
			case java.awt.event.KeyEvent.VK_BACK_SPACE:
			case java.awt.event.KeyEvent.VK_DELETE:
			case java.awt.event.KeyEvent.VK_ESCAPE:
			case java.awt.event.KeyEvent.VK_SPACE:
			case java.awt.event.KeyEvent.VK_TAB:
			case java.awt.event.KeyEvent.VK_UP:
			case java.awt.event.KeyEvent.VK_DOWN:
			case java.awt.event.KeyEvent.VK_LEFT:
			case java.awt.event.KeyEvent.VK_RIGHT:
			case java.awt.event.KeyEvent.VK_UNDO:
			case java.awt.event.KeyEvent.VK_AGAIN:
				kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), 0, (int) ch, ch, KEY_LOCATION_STANDARD));
				kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), 0, (int) ch, ch, KEY_LOCATION_STANDARD));
				return;
			}

			int keycode = KeyEvent.getExtendedKeyCodeForChar(ch);
			if (keycode != KeyEvent.VK_UNDEFINED) {
				kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), controlDownMask, keycode, ch, KEY_LOCATION_STANDARD));
				kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), controlDownMask, keycode, ch, KEY_LOCATION_STANDARD));
				return;
			}
		}

		int modififiers = Character.isUpperCase(ch) ? SHIFT_DOWN_MASK : 0;

		kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), modififiers, VK_UNDEFINED, ch, KEY_LOCATION_STANDARD));
		kfm.dispatchKeyEvent(new KeyEvent(c, KEY_TYPED, System.currentTimeMillis(), modififiers, VK_UNDEFINED, ch, KEY_LOCATION_UNKNOWN));
		kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), modififiers, VK_UNDEFINED, ch, KEY_LOCATION_STANDARD));

	}

}
