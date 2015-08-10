package org.comtel2000.keyboard.robot;

/*
 * #%L
 * fx-onscreen-keyboard
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import static javafx.scene.input.KeyCode.CONTROL;
import static javafx.scene.input.KeyCode.META;

import org.comtel2000.keyboard.control.KeyboardPane;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.stage.Window;

public class FXRobotHandler implements IRobot {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FXRobotHandler.class);

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
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.ENTER, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.ENTER, false));
				return;
			case java.awt.event.KeyEvent.VK_BACK_SPACE:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.BACK_SPACE, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.BACK_SPACE, false));
				return;
			case java.awt.event.KeyEvent.VK_DELETE:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.DELETE, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.DELETE, false));
				return;
			case java.awt.event.KeyEvent.VK_ESCAPE:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.ESCAPE, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.ESCAPE, false));
				return;
			case java.awt.event.KeyEvent.VK_SPACE:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.SPACE, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.SPACE, false));
				return;
			case java.awt.event.KeyEvent.VK_TAB:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.TAB, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.TAB, false));
				return;
			case java.awt.event.KeyEvent.VK_UP:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.UP, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.UP, false));
				return;
			case java.awt.event.KeyEvent.VK_DOWN:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.DOWN, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.DOWN, false));
				return;
			case java.awt.event.KeyEvent.VK_LEFT:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.LEFT, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.LEFT, false));
				return;
			case java.awt.event.KeyEvent.VK_RIGHT:
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.RIGHT, false));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.RIGHT, false));
				return;
			}

			KeyCode fxKeyCode = getKeyCode(ch);
			if (fxKeyCode != null) {
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), fxKeyCode, ctrl));
				focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), fxKeyCode, ctrl));
				return;
			}

		}

		focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_PRESSED, Character.toString(ch), KeyCode.UNDEFINED, ctrl));
		if (Character.toString(ch) != KeyEvent.CHAR_UNDEFINED) {
			focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_TYPED, Character.toString(ch), KeyCode.UNDEFINED, ctrl));
		}
		focusNode.fireEvent(createKeyEvent(focusNode, KeyEvent.KEY_RELEASED, Character.toString(ch), KeyCode.UNDEFINED, ctrl));
	}

	private KeyCode getKeyCode(char c) {
		return KeyCode.getKeyCode(Character.toString(Character.toUpperCase(c)));

	}

	private KeyEvent createKeyEvent(EventTarget target, EventType<KeyEvent> eventType, String character, KeyCode code, boolean ctrl) {
		return new KeyEvent(eventType, character, code.toString(), code, false, ctrl && controlKeyCode == CONTROL, false, ctrl && controlKeyCode == META);
	}

}
