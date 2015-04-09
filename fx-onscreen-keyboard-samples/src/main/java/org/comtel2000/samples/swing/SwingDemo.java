package org.comtel2000.samples.swing;

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.util.Duration;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.swing.robot.AWTRobotHandler;
import org.comtel2000.swing.ui.KeyboardUIManagerTool;

public class SwingDemo extends JApplet {

	private static final long serialVersionUID = 1L;

	private KeyBoardPopup fxKeyboardPopup;

	private Transition transition;

	@Override
	public void init() {

		KeyboardUIManagerTool.installKeyboardDefaults((point, visible) -> setKeyboardVisible(visible, point));

		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(800, 400));
		FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 20, 20);
		panel.setLayout(layout);

		int line = 70;
		panel.add(new JTextField(line));
		panel.add(new JTextField(line));
		panel.add(new JPasswordField(line));
		panel.add(new JTextArea(4, line));
		panel.add(new JEditorPane());
		panel.add(new JSeparator());
		panel.add(new JButton("Ok"));
		panel.add(new JButton("Cancel"));

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		// create javafx panel
		final JFXPanel javafxPanel = new JFXPanel();
		javafxPanel.setFocusable(false);
		javafxPanel.setOpaque(false);

		JWindow fxKeyboard = new JWindow();
		fxKeyboard.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		fxKeyboard.getContentPane().add(javafxPanel);
		fxKeyboard.setFocusable(false);
		fxKeyboard.setBackground(null);

		// fxKeyboard.pack();
		// fxKeyboard.setLocationByPlatform(true);
		// fxKeyboard.setVisible(false);

		// create JavaFX scene
		Platform.runLater(() -> {
			createScene(javafxPanel);
			System.out.println("JavaFX: " + System.getProperty("javafx.runtime.version"));
		});

	}

	public void createScene(JFXPanel javafxPanel) {

		// create empty scene
		Scene scene = new Scene(new Group(), 0, 0);

		javafxPanel.setScene(scene);

		fxKeyboardPopup = KeyBoardPopupBuilder.create().initLocale(Locale.ENGLISH).addIRobot(new AWTRobotHandler()).layer(DefaultLayer.NUMBLOCK).build();

		fxKeyboardPopup.getKeyBoard().setOnKeyboardCloseButton((event) -> setKeyboardVisible(false, null));
		fxKeyboardPopup.setOwner(scene);

	}

	public void setKeyboardVisible(boolean flag, Point point) {
		if (fxKeyboardPopup == null) {
			return;
		}

		final boolean visible = flag;
		final Point location = point;
		Platform.runLater(() -> {

			if (location != null) {
				fxKeyboardPopup.setX(location.getX());
				fxKeyboardPopup.setY(location.getY() + 20);
			}

			if (transition == null) {
				transition = new FadeTransition(Duration.seconds(0.1), fxKeyboardPopup.getKeyBoard());
				// transition = new ScaleTransition(Duration.seconds(0.1),
				// fxKeyboardPopup.getKeyBoard());
				// transition.setCycleCount(1);
				// transition.setAutoReverse(false);
			}
			if (visible) {
				if (fxKeyboardPopup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
					return;
				}
				System.err.println("fade in");
				transition.stop();
				transition.setOnFinished((event) -> {
				});
				// ((ScaleTransition) transition).setFromX(0.0d);
				// ((ScaleTransition) transition).setFromY(0.0d);
				// ((ScaleTransition)
				// transition).setToX(fxKeyboardPopup.getKeyBoard().getScale());
				// ((ScaleTransition)
				// transition).setToY(fxKeyboardPopup.getKeyBoard().getScale());

				fxKeyboardPopup.getKeyBoard().setOpacity(0.0);
				fxKeyboardPopup.setVisible(true);
				((FadeTransition) transition).setFromValue(0.0f);
				((FadeTransition) transition).setToValue(1.0f);
				transition.play();

			} else {
				if (!fxKeyboardPopup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
					return;
				}
				System.err.println("fade out");
				transition.stop();
				transition.setOnFinished((event) -> fxKeyboardPopup.setVisible(false));
				// ((ScaleTransition)
				// transition).setFromX(fxKeyboardPopup.getKeyBoard().getScale());
				// ((ScaleTransition)
				// transition).setFromY(fxKeyboardPopup.getKeyBoard().getScale());
				// ((ScaleTransition) transition).setToX(0.0d);
				// ((ScaleTransition) transition).setToY(0.0d);

				((FadeTransition) transition).setFromValue(1.0f);
				((FadeTransition) transition).setToValue(0.0f);
				transition.play();

				// fxKeyboardPopup.hide();
			}
		});
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
