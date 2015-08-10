package org.comtel2000.swing.robot;

/*
 * #%L
 * fx-onscreen-keyboard-swing
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

import org.comtel2000.keyboard.robot.IRobot;
import org.slf4j.LoggerFactory;

/**
 * native OS support
 * <p>
 * dirty unicode char support only by transfer over OS clipboard..
 *
 * @author comtel
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
