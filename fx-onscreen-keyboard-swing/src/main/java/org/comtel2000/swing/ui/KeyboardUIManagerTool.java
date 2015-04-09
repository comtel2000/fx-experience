package org.comtel2000.swing.ui;

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

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.UIManager;

public class KeyboardUIManagerTool {

	/**
	 * install listener to basic UI text components
	 *
	 * @param fl
	 *            Component {@link FocusListener}
	 * @param ml
	 *            Component {@link MouseListener}
	 */
	public static void installKeyboardDefaults(FocusListener fl, MouseListener ml) {

		UIManager.put("TextFieldUI", KeyboardTextFieldUI.class.getName());
		UIManager.put("PasswordFieldUI", KeyboardPasswordFieldUI.class.getName());
		UIManager.put("TextAreaUI", KeyboardTextAreaUI.class.getName());
		UIManager.put("EditorPaneUI", KeyboardEditorPaneUI.class.getName());

		KeyboardTextFieldUI.setFocusListener(fl);
		KeyboardTextFieldUI.setMouseListener(ml);

		KeyboardPasswordFieldUI.setFocusListener(fl);
		KeyboardPasswordFieldUI.setMouseListener(ml);

		KeyboardTextAreaUI.setFocusListener(fl);
		KeyboardTextAreaUI.setMouseListener(ml);

		KeyboardEditorPaneUI.setFocusListener(fl);
		KeyboardEditorPaneUI.setMouseListener(ml);
	}

	/**
	 * Register {@link FocusListener} and {@link MouseListener}
	 * 
	 * @param callback
	 *            {@link EventCallback}
	 * @see #installKeyboardDefaults(FocusListener, MouseListener)
	 */
	public static void installKeyboardDefaults(EventCallback callback) {
		installKeyboardDefaults(createFocusListener(callback), createMouseDoubleClickListener(callback));
	}

	private static FocusListener createFocusListener(EventCallback c) {
		FocusListener l = new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				c.setKeyboardVisible(null, false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				c.setKeyboardVisible(e.getComponent().getLocationOnScreen(), true);
			}
		};
		return l;
	}

	private static MouseListener createMouseDoubleClickListener(EventCallback c) {
		return new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					c.setKeyboardVisible(e.getComponent().getLocationOnScreen(), true);
				}
			}
		};
	}

}
