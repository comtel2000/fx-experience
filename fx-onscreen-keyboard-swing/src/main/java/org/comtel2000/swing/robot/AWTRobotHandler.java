package org.comtel2000.swing.robot;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

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

import org.comtel2000.keyboard.robot.IRobot;
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
        default:
          break;
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
