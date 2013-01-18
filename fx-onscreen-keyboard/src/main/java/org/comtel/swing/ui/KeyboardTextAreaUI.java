package org.comtel.swing.ui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

public class KeyboardTextAreaUI extends BasicTextAreaUI {

	private static FocusListener fl = null;

	private static MouseListener ml = null;

	public static void setFocusListener(FocusListener l) {
		fl = l;
	}

	public KeyboardTextAreaUI() {
		super();
	}
	
	/**
	 * send only mouseClicked events
	 * 
	 * @param l
	 */
	public static void setMouseListener(MouseListener l) {
		ml = l;
	}

	public static ComponentUI createUI(JComponent c) {
		return new KeyboardTextAreaUI();
	}

	@Override
	public void installUI(JComponent c) {
		if (c instanceof JTextComponent) {
			((JTextComponent) c).addFocusListener(createFocusListener());
			((JTextComponent) c).addMouseListener(createMouseListener());
		}
		super.installUI(c);
	}

	private FocusListener createFocusListener() {
		FocusListener l = new FocusListener() {

			public void focusLost(FocusEvent e) {
				if (fl != null) {
					fl.focusLost(e);
				}
			}

			public void focusGained(FocusEvent e) {
				if (fl != null) {
					fl.focusGained(e);
				}
			}
		};
		return l;
	}

	public MouseListener createMouseListener() {
		return new MouseListener() {
			public void mouseReleased(MouseEvent e) {/* nothing to do */}
			public void mouseEntered(MouseEvent e) {/* nothing to do */}
			public void mouseExited(MouseEvent e) {/* nothing to do */}
			public void mousePressed(MouseEvent e) {/* nothing to do */}
			
			public void mouseClicked(MouseEvent e) {
				if (ml != null) {
					ml.mouseClicked(e);
				}
			}
		};
	}
}
