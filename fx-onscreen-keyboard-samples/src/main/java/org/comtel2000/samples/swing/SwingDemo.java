package org.comtel2000.samples.swing;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.swing.control.KeyBoardWindow;
import org.comtel2000.swing.control.KeyBoardWindowBuilder;
import org.comtel2000.swing.robot.AWTRobotHandler;
import org.comtel2000.swing.ui.KeyboardUIManagerTool;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Locale;

import static org.comtel2000.keyboard.control.VkProperties.*;


public class SwingDemo extends JFrame {

    @Serial
    private static final long serialVersionUID = -5808479148214602409L;

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
