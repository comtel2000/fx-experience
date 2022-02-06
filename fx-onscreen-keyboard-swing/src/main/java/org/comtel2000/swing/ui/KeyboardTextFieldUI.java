package org.comtel2000.swing.ui;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;


public class KeyboardTextFieldUI extends BasicTextFieldUI {

    private static FocusListener fl;

    private static MouseListener ml;

    KeyboardTextFieldUI() {
        super();
    }

    public static void setFocusListener(FocusListener l) {
        fl = l;
    }

    public static void setMouseListener(MouseListener l) {
        ml = l;
    }

    public static ComponentUI createUI(JComponent c) {
        return new KeyboardTextFieldUI();
    }

    @Override
    public void installUI(JComponent c) {
        if (c instanceof JTextComponent) {
            if (fl != null) {
                c.addFocusListener(fl);
            }
            if (ml != null) {
                c.addMouseListener(ml);
            }
        }
        super.installUI(c);
    }
}
