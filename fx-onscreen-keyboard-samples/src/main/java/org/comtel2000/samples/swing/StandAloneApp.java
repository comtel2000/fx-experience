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
import java.awt.Point;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.util.Duration;

import javax.swing.JApplet;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.swing.robot.NativeAsciiRobotHandler;
import org.slf4j.LoggerFactory;

public class StandAloneApp extends JApplet {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(StandAloneApp.class);

	private static int posX = 100;

	private static int posY = 100;

	private static Locale locale;

	private static String xmlPath;

	private static final long serialVersionUID = 1L;

	private KeyBoardPopup fxKeyboardPopup;

	private Transition transition;

	public StandAloneApp() {
	}

	@Override
	public void init() {

		String fontUrl = this.getClass().getResource("/font/FontKeyboardFX.ttf").toExternalForm();
		Font.loadFont(fontUrl, -1);

		setLayout(new BorderLayout());

		// create javafx panel
		final JFXPanel javafxPanel = new JFXPanel();
		javafxPanel.setFocusable(false);
		javafxPanel.setOpaque(false);

		add(javafxPanel, BorderLayout.CENTER);

		JWindow fxKeyboard = new JWindow();
		fxKeyboard.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		fxKeyboard.getContentPane().add(javafxPanel);
		fxKeyboard.setFocusable(false);
		fxKeyboard.setBackground(null);

		fxKeyboard.pack();
		fxKeyboard.setLocationByPlatform(true);

		// create JavaFX scene
		Platform.runLater(() -> createScene(javafxPanel));

	}

	public void createScene(JFXPanel javafxPanel) {

		Scene scene = new Scene(new Group(), 0, 0);
		javafxPanel.setScene(scene);

		Path path = null;
		try {
			if (xmlPath != null) {
				path = Paths.get(this.getClass().getResource(xmlPath).toURI());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		fxKeyboardPopup = KeyBoardPopupBuilder.create().initLocale(locale).addIRobot(new NativeAsciiRobotHandler()).layerPath(path).layer(DefaultLayer.NUMBLOCK).build();
		fxKeyboardPopup.getKeyBoard().setOnKeyboardCloseButton(e -> System.exit(0));

		fxKeyboardPopup.setOwner(scene);
		setKeyboardVisible(true, new Point(posX, posY));
	}

	public void setKeyboardVisible(boolean flag, Point point) {
		final boolean visible = flag;
		final Point location = point;
		Platform.runLater(() -> {
			if (fxKeyboardPopup == null) {
				return;
			}
			if (location != null) {
				fxKeyboardPopup.setX(location.getX());
				fxKeyboardPopup.setY(location.getY());
			}

			if (transition == null) {
				transition = new FadeTransition(Duration.seconds(0.1), fxKeyboardPopup.getKeyBoard());
			}
			if (visible) {
				if (fxKeyboardPopup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
					return;
				}
				transition.stop();

				fxKeyboardPopup.getKeyBoard().setOpacity(0.0);
				fxKeyboardPopup.setVisible(true);
				((FadeTransition) transition).setFromValue(0.0f);
				((FadeTransition) transition).setToValue(1.0f);
				transition.play();

			} else {
				if (!fxKeyboardPopup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
					return;
				}
				transition.stop();
				transition.setOnFinished((e) -> fxKeyboardPopup.setVisible(false));

				((FadeTransition) transition).setFromValue(1.0f);
				((FadeTransition) transition).setToValue(0.0f);
				transition.play();

			}
		});
	}

	private static void showHelp() {
		System.out.println();
		System.out.println("\t-lang <locale>\t\tsetting keyboard language (en,de,ru,..)");
		System.out.println("\t-layout <path>\t\tpath to custom layout xml");
		System.out.println("\t-pos <x,y>\t\tinitial keyboard position");
		System.out.println("\t-help\t\t\tthis help screen");
	}

	private static void parseLocale(String l) throws Exception {
		if (l == null || l.isEmpty()) {
			throw new ParseException("invalid locale", 0);
		}

		String[] lang = l.split("_");
		if (lang.length == 2) {
			locale = new Locale(lang[0], lang[1]);
		} else if (lang.length == 1) {
			locale = Locale.forLanguageTag(l);
		}
	}

	private static void parsePosition(String p) throws Exception {
		if (p == null || p.isEmpty()) {
			throw new ParseException("invalid position", 0);
		}

		String[] pos = p.split(",");
		if (pos.length == 2) {
			posX = Integer.valueOf(pos[0]);
			posY = Integer.valueOf(pos[1]);
		}
	}

	public static void main(String[] args) {

		try {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-lang")) {
					parseLocale(args[++i]);
				} else if (args[i].equals("-layout")) {
					xmlPath = args[++i];
				} else if (args[i].equals("-pos")) {
					parsePosition(args[++i]);
				} else {
					showHelp();
					return;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			showHelp();
			return;
		}

		SwingUtilities.invokeLater(() -> {
			JApplet applet = new StandAloneApp();
			applet.init();
			applet.start();
		});
	}
}
