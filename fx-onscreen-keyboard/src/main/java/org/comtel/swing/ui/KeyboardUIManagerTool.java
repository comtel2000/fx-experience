package org.comtel.swing.ui;

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
	 * @param ml
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

	public static void installKeyboardDefaults(EventCallback c) {
		installKeyboardDefaults(createFocusListener(c), createMouseDoubleClickListener(c));
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
