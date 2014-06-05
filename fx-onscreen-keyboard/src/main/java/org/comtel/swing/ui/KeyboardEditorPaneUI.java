package org.comtel.swing.ui;

import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import javax.swing.text.JTextComponent;

public class KeyboardEditorPaneUI extends BasicEditorPaneUI {

	private static FocusListener fl = null;
	private static MouseListener ml = null;

	public static void setFocusListener(FocusListener l) {
		fl = l;
	}

	public KeyboardEditorPaneUI() {
		super();
	}

	public static void setMouseListener(MouseListener l) {
		ml = l;
	}

	public static ComponentUI createUI(JComponent c) {
		return new KeyboardEditorPaneUI();
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
