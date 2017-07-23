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

package org.comtel2000.samples.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Locale;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.comtel2000.keyboard.control.KeyboardLayer;
import org.comtel2000.keyboard.control.VkProperties;
import org.comtel2000.swing.control.KeyboardWindow;
import org.comtel2000.swing.control.KeyboardWindowBuilder;
import org.comtel2000.swing.robot.AWTRobotHandler;
import org.comtel2000.swing.ui.KeyboardUIManagerTool;

public class SwingDemo extends JApplet {

  private static final long serialVersionUID = 1L;

  @Override
  public void init() {

    KeyboardWindow window =
        KeyboardWindowBuilder.create().initLocale(Locale.forLanguageTag("en")).addIRobot(new AWTRobotHandler()).layer(KeyboardLayer.NUMBLOCK).build();
    KeyboardUIManagerTool.installKeyboardDefaults(window);

    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(800, 400));
    FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 20, 20);
    panel.setLayout(layout);

    panel.add(new JTextField(70));

    JTextField numbers = new JTextField("0-9", 70);
    numbers.setToolTipText("0-9");
    // set numeric kb type
    numbers.getDocument().putProperty(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
    panel.add(numbers);

    panel.add(new JTextField(70));

    JComboBox<String> combo = new JComboBox<>(new String[] { "DEMO", "TEST" });
    combo.setEditable(true);
    if (combo.getEditor().getEditorComponent() instanceof JTextComponent) {
      JTextComponent comp = (JTextComponent) combo.getEditor().getEditorComponent();
      comp.getDocument().putProperty(VkProperties.VK_TYPE, VkProperties.VK_TYPE_TEXT_SHIFT);
    }
    panel.add(combo);
    JEditorPane ePane = new JEditorPane();
    ePane.setPreferredSize(new Dimension(600, 50));
    panel.add(ePane);

    JTextArea area = new JTextArea(4, 70);
    area.setToolTipText("switch to Locale 'de'");
    // set text area to german locale
    area.getDocument().putProperty(VkProperties.VK_LOCALE, VkProperties.VK_LOCALE_DE);
    panel.add(area);

    panel.add(new JPasswordField(70));

    panel.add(new JSeparator(JSeparator.HORIZONTAL));
    panel.add(new JButton("Ok"));
    panel.add(new JButton("Cancel"));

    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);

  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Swing FX Keyboard");
      frame.setResizable(false);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JApplet applet = new SwingDemo();
      applet.init();

      frame.setContentPane(applet.getContentPane());

      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      applet.start();

    });
  }

}
