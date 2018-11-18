package org.comtel2000.samples.swing;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.swing.control.KeyBoardWindow;
import org.comtel2000.swing.control.KeyBoardWindowBuilder;
import org.comtel2000.swing.robot.AWTRobotHandler;
import org.comtel2000.swing.ui.KeyboardUIManagerTool;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

import static org.comtel2000.keyboard.control.VkProperties.*;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
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

public class SwingDemo extends JFrame {

  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Swing FX Keyboard");
      frame.setResizable(false);
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      SwingDemo kb = new SwingDemo();
      kb.init();
      frame.setContentPane(kb.getContentPane());
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

    });
  }

  public void init() {

    KeyBoardWindow window =
        KeyBoardWindowBuilder.create().initLocale(Locale.forLanguageTag("en")).addIRobot(new AWTRobotHandler()).layer(DefaultLayer.NUMBLOCK).build();
    KeyboardUIManagerTool.installKeyboardDefaults(window);

    JPanel panel = new JPanel();
    panel.setPreferredSize(new Dimension(800, 400));
    FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 20, 20);
    panel.setLayout(layout);

    panel.add(new JTextField(70));

    JTextField numbers = new JTextField("0-9", 70);
    numbers.setToolTipText("0-9");
    // set numeric kb type
    numbers.getDocument().putProperty(VK_TYPE, VK_TYPE_NUMERIC);
    panel.add(numbers);
    panel.add(new JPasswordField(70));

    JTextArea area = new JTextArea(4, 70);
    area.setToolTipText("switch to Locale 'de'");
    // set text area to german locale
    area.getDocument().putProperty(VK_LOCALE, VK_LOCALE_DE);
    panel.add(area);
    panel.add(new JEditorPane());
    panel.add(new JSeparator());
    panel.add(new JButton("Ok"));
    panel.add(new JButton("Cancel"));

    setLayout(new BorderLayout());
    add(panel, BorderLayout.CENTER);

  }

}
