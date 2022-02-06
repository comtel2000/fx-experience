package org.comtel2000.swing.ui;

import java.awt.*;

@FunctionalInterface
public interface EventCallback {

    void call(Component component, boolean vis);

}
