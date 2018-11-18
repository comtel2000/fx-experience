/*******************************************************************************
 * Copyright (c) 2017 comtel2000
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

package org.comtel2000.keyboard.robot;

import javafx.application.Platform;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.META;

public class FXRobotHandler implements IRobot {

  private final static Logger logger = LoggerFactory.getLogger(FXRobotHandler.class);

  private final KeyCode controlKeyCode;

  public FXRobotHandler() {
    String osName = System.getProperty("os.name");
    if (osName.toLowerCase().startsWith("mac")) {
      controlKeyCode = META;
    } else {
      controlKeyCode = CONTROL;
    }
  }

  @Override
  public void sendToComponent(Object kb, final char ch, final boolean ctrl) {
    logger.trace("fire: {}", ch);
    final Window keyboardWindow = ((KeyboardPane) kb).getScene().getWindow();
    if (keyboardWindow != null) {
      final Scene scene;
      if (keyboardWindow instanceof Popup) {
        scene = ((Popup) keyboardWindow).getOwnerWindow().getScene();
      } else {
        scene = keyboardWindow.getScene();
      }

      Platform.runLater(() -> send(scene, ch, ctrl));
    }
  }

  private void send(Scene scene, char ch, boolean ctrl) {
    Node focusNode = scene.focusOwnerProperty().get();

    if (focusNode == null) {
      logger.error("no focus owner");
      return;
    }
    if (ctrl) {
      switch (ch) {
      case java.awt.event.KeyEvent.VK_ENTER:
        firePressedReleased(focusNode, ch, KeyCode.ENTER);
        return;
      case java.awt.event.KeyEvent.VK_BACK_SPACE:
        firePressedReleased(focusNode, ch, KeyCode.BACK_SPACE);
        return;
      case java.awt.event.KeyEvent.VK_DELETE:
        firePressedReleased(focusNode, ch, KeyCode.DELETE);
        return;
      case java.awt.event.KeyEvent.VK_ESCAPE:
        firePressedReleased(focusNode, ch, KeyCode.ESCAPE);
        return;
      case java.awt.event.KeyEvent.VK_SPACE:
        firePressedReleased(focusNode, ch, KeyCode.SPACE);
        return;
      case java.awt.event.KeyEvent.VK_TAB:
        firePressedReleased(focusNode, ch, KeyCode.TAB);
        return;
      case java.awt.event.KeyEvent.VK_UP:
        firePressedReleased(focusNode, ch, KeyCode.UP);
        return;
      case java.awt.event.KeyEvent.VK_DOWN:
        firePressedReleased(focusNode, ch, KeyCode.DOWN);
        return;
      case java.awt.event.KeyEvent.VK_LEFT:
        firePressedReleased(focusNode, ch, KeyCode.LEFT);
        return;
      case java.awt.event.KeyEvent.VK_RIGHT:
        firePressedReleased(focusNode, ch, KeyCode.RIGHT);
        return;
      case java.awt.event.KeyEvent.VK_HOME:
        firePressedReleased(focusNode, ch, KeyCode.HOME);
        return;
      case java.awt.event.KeyEvent.VK_END:
        firePressedReleased(focusNode, ch, KeyCode.END);
        return;
      case java.awt.event.KeyEvent.VK_PAGE_UP:
        firePressedReleased(focusNode, ch, KeyCode.PAGE_UP);
        return;
      case java.awt.event.KeyEvent.VK_PAGE_DOWN:
        firePressedReleased(focusNode, ch, KeyCode.PAGE_DOWN);
        return;
      case java.awt.event.KeyEvent.VK_HELP:
        firePressedReleased(focusNode, ch, KeyCode.HELP);
        return;
      case java.awt.event.KeyEvent.VK_PRINTSCREEN:
        firePressedReleased(focusNode, ch, KeyCode.PRINTSCREEN);
        return;
      case java.awt.event.KeyEvent.VK_F1:
        firePressedReleased(focusNode, ch, KeyCode.F1);
        return;
      case java.awt.event.KeyEvent.VK_F2:
        firePressedReleased(focusNode, ch, KeyCode.F2);
        return;
      case java.awt.event.KeyEvent.VK_F3:
        firePressedReleased(focusNode, ch, KeyCode.F3);
        return;
      case java.awt.event.KeyEvent.VK_F4:
        firePressedReleased(focusNode, ch, KeyCode.F4);
        return;
      case java.awt.event.KeyEvent.VK_F5:
        firePressedReleased(focusNode, ch, KeyCode.F5);
        return;
      case java.awt.event.KeyEvent.VK_F6:
        firePressedReleased(focusNode, ch, KeyCode.F6);
        return;
      case java.awt.event.KeyEvent.VK_F7:
        firePressedReleased(focusNode, ch, KeyCode.F7);
        return;
      case java.awt.event.KeyEvent.VK_F8:
        firePressedReleased(focusNode, ch, KeyCode.F8);
        return;
      case java.awt.event.KeyEvent.VK_F9:
        firePressedReleased(focusNode, ch, KeyCode.F9);
        return;
      case java.awt.event.KeyEvent.VK_F10:
        firePressedReleased(focusNode, ch, KeyCode.F10);
        return;
      case java.awt.event.KeyEvent.VK_F11:
        firePressedReleased(focusNode, ch, KeyCode.F11);
        return;
      case java.awt.event.KeyEvent.VK_F12:
        firePressedReleased(focusNode, ch, KeyCode.F12);
        return;
      default:
        break;
      }
      KeyCode fxKeyCode = getKeyCode(ch);

      if (fxKeyCode != null) {
        focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch),
            fxKeyCode, true));
        focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch),
            fxKeyCode, true));
        return;
      }

    }

    focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch),
        KeyCode.UNDEFINED, ctrl));
    focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_TYPED, Character.toString(ch),
        KeyCode.UNDEFINED, ctrl));
    focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch),
        KeyCode.UNDEFINED, ctrl));
  }

  private KeyCode getKeyCode(char c) {
    return KeyCode.getKeyCode(Character.toString(Character.toUpperCase(c)));
  }

  private void firePressedReleased(Node focusNode, char ch, KeyCode code) {
    focusNode.fireEvent(
        createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), code, false));
    focusNode.fireEvent(
        createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), code, false));
  }

  private KeyEvent createKeyEvent(EventTarget target, EventType<KeyEvent> eventType,
      String character, KeyCode code, boolean ctrl) {
    return new KeyEvent(eventType, character, code.toString(), code, false,
        ctrl && controlKeyCode == CONTROL, false, ctrl && controlKeyCode == META);
  }

}
