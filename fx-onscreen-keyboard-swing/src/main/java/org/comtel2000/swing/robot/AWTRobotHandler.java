package org.comtel2000.swing.robot;

import org.comtel2000.keyboard.robot.IRobot;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static java.awt.event.InputEvent.SHIFT_DOWN_MASK;
import static java.awt.event.KeyEvent.*;

public class AWTRobotHandler implements IRobot {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(AWTRobotHandler.class);

    private final int controlDownMask;

    public AWTRobotHandler() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().startsWith("mac")) {
            controlDownMask = InputEvent.META_DOWN_MASK;
        } else {
            controlDownMask = CTRL_DOWN_MASK;
        }
    }

    @Override
    public void sendToComponent(Object source, final char ch, final boolean ctrl) {
        logger.trace("fire: {}", ch);
        SwingUtilities.invokeLater(() -> send(ch, ctrl));
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
                case java.awt.event.KeyEvent.VK_HOME:
                case java.awt.event.KeyEvent.VK_END:
                case java.awt.event.KeyEvent.VK_PAGE_UP:
                case java.awt.event.KeyEvent.VK_PAGE_DOWN:
                case java.awt.event.KeyEvent.VK_HELP:
                case java.awt.event.KeyEvent.VK_PRINTSCREEN:
                case java.awt.event.KeyEvent.VK_F1:
                case java.awt.event.KeyEvent.VK_F2:
                case java.awt.event.KeyEvent.VK_F3:
                case java.awt.event.KeyEvent.VK_F4:
                case java.awt.event.KeyEvent.VK_F5:
                case java.awt.event.KeyEvent.VK_F6:
                case java.awt.event.KeyEvent.VK_F7:
                case java.awt.event.KeyEvent.VK_F8:
                case java.awt.event.KeyEvent.VK_F9:
                case java.awt.event.KeyEvent.VK_F10:
                case java.awt.event.KeyEvent.VK_F11:
                case java.awt.event.KeyEvent.VK_F12:
                    kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), 0, ch, ch, KEY_LOCATION_STANDARD));
                    kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), 0, ch, ch, KEY_LOCATION_STANDARD));
                    return;
            }

            int keycode = KeyEvent.getExtendedKeyCodeForChar(ch);
            if (keycode != KeyEvent.VK_UNDEFINED) {
                kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), controlDownMask, keycode, ch, KEY_LOCATION_STANDARD));
                kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), controlDownMask, keycode, ch, KEY_LOCATION_STANDARD));
                return;
            }
        }

        int modifiers = Character.isUpperCase(ch) ? SHIFT_DOWN_MASK : 0;

        kfm.dispatchKeyEvent(new KeyEvent(c, KEY_PRESSED, System.currentTimeMillis(), modifiers, VK_UNDEFINED, ch, KEY_LOCATION_STANDARD));
        kfm.dispatchKeyEvent(new KeyEvent(c, KEY_TYPED, System.currentTimeMillis(), modifiers, VK_UNDEFINED, ch, KEY_LOCATION_UNKNOWN));
        kfm.dispatchKeyEvent(new KeyEvent(c, KEY_RELEASED, System.currentTimeMillis(), modifiers, VK_UNDEFINED, ch, KEY_LOCATION_STANDARD));

    }

}
