package org.comtel.swing.ui;

import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

public class KeyboardPasswordFieldUI extends BasicPasswordFieldUI {

	private static FocusListener fl = null;
	private static MouseListener ml = null;

	public static void setFocusListener(FocusListener l) {
		fl = l;
	}

	public static void setMouseListener(MouseListener l) {
		ml = l;
	}
	
	public KeyboardPasswordFieldUI() {
		super();
	}

	public static ComponentUI createUI(JComponent c) {
		return new KeyboardPasswordFieldUI();
	}

	@Override
	public void installUI(JComponent c) {
		if (c instanceof JTextComponent) {
			if (fl != null) {
				((JTextComponent) c).addFocusListener(fl);
			}
			if (ml != null) {
				((JTextComponent) c).addMouseListener(ml);
			}
		}
		super.installUI(c);
	}
}
