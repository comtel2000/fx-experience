/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.swing.robot;

import static java.awt.event.KeyEvent.*;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import org.comtel2000.keyboard.robot.IRobot;
import org.slf4j.LoggerFactory;

/*******************************************************************************
 * Copyright (c) 2025 comtel2000
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

/**
 * native OS support
 *
 * <p>dirty unicode char support only by transfer over OS clipboard..
 *
 * @author comtel
 */
public class NativeAsciiRobotHandler implements IRobot {

  private static final org.slf4j.Logger logger =
      LoggerFactory.getLogger(NativeAsciiRobotHandler.class);

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
    logger.trace("fire: {} ctrl:{}", ch, ctrl);
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
        case VK_ENTER,
            VK_BACK_SPACE,
            VK_DELETE,
            VK_ESCAPE,
            VK_SPACE,
            VK_TAB,
            VK_UP,
            VK_DOWN,
            VK_LEFT,
            VK_RIGHT,
            VK_UNDO,
            VK_AGAIN,
            VK_HOME,
            VK_END,
            VK_PAGE_UP,
            VK_PAGE_DOWN,
            VK_HELP,
            VK_PRINTSCREEN,
            VK_F1,
            VK_F2,
            VK_F3,
            VK_F4,
            VK_F5,
            VK_F6,
            VK_F7,
            VK_F8,
            VK_F9,
            VK_F10,
            VK_F11,
            VK_F12:
          robot.keyPress(ch);
          robot.keyRelease(ch);
          return;
        default:
          break;
      }

      int upperCase = Character.toUpperCase(ch);
      switch (upperCase) {
        case VK_A, VK_X, VK_C, VK_V, VK_Z, VK_Y:
          robot.keyPress(controlKeyEvent);
          robot.keyPress(upperCase);
          robot.keyRelease(upperCase);
          robot.keyRelease(controlKeyEvent);
          return;
        default:
          break;
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
      logger.warn("{} - {} ({}-{})", e.getMessage(), keyCode, ch, (int) ch);

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

  private void clipboardTransfer(Robot robot, char ch) {
    try {
      StringSelection stringSelection = new StringSelection(Character.toString(ch));
      java.awt.datatransfer.Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      Object recover =
          clpbrd.isDataFlavorAvailable(DataFlavor.stringFlavor)
              ? clpbrd.getData(DataFlavor.stringFlavor)
              : null;
      clpbrd.setContents(stringSelection, null);

      robot.keyPress(controlKeyEvent);
      robot.keyPress(VK_V);
      robot.keyRelease(VK_V);
      robot.keyRelease(controlKeyEvent);
      robot.delay(50);

      StringSelection recoverSelection =
          new StringSelection(recover != null ? recover.toString() : "");
      clpbrd.setContents(recoverSelection, null);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}
