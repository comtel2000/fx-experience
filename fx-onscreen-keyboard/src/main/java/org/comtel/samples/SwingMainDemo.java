package org.comtel.samples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
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

import org.comtel.javafx.control.KeyBoardPopup;
import org.comtel.javafx.control.KeyBoardPopupBuilder;
import org.comtel.javafx.robot.RobotFactory;
import org.comtel.swing.ui.EventCallback;
import org.comtel.swing.ui.KeyboardUIManagerTool;

public class SwingMainDemo extends JApplet {

	private static final long serialVersionUID = 1L;

	private KeyBoardPopup fxKeyboardPopup;
	private Transition transition;

	public SwingMainDemo() {
	}

	@Override
	public void init() {

		String fontUrl = this.getClass().getResource("/font/FontKeyboardFX.ttf").toExternalForm();
		Font.loadFont(fontUrl, -1);

		KeyboardUIManagerTool.installKeyboardDefaults(new EventCallback() {

			@Override
			public void openKeyboard(Point p) {
				setKeyboardVisible(true, p);
			}

			@Override
			public void closeKeyboard() {
				setKeyboardVisible(false, null);
			}
		});

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
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				createScene(javafxPanel);
				System.out.println("JavaFX: " + System.getProperty("javafx.runtime.version"));
			}
		});

	}

	public void createScene(JFXPanel javafxPanel) {

		// set default embedded css style
		String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();

		// create empty scene
		Scene scene = new Scene(new Group(), 0, 0);

		javafxPanel.setScene(scene);
		scene.getStylesheets().add(css);

		Path numblockLayout = null;
		try {
			numblockLayout = Paths.get(this.getClass().getResource("/xml/numblock").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		fxKeyboardPopup = KeyBoardPopupBuilder.create().initLocale(Locale.ENGLISH).addIRobot(RobotFactory.createAWTRobot()).layerPath(numblockLayout).build();
		fxKeyboardPopup.getKeyBoard().setOnKeyboardCloseButton(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				setKeyboardVisible(false, null);
			}
		});
		fxKeyboardPopup.setOwner(scene);

	}

	public void setKeyboardVisible(boolean flag, Point point) {
		final boolean visible = flag;
		final Point location = point;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (fxKeyboardPopup == null) {
					return;
				}
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
					transition.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
						}
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
					transition.setOnFinished(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							fxKeyboardPopup.setVisible(false);
						}
					});
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
			}
		});
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = new JFrame("Swing FX Keyboard");
				frame.setResizable(false);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				JApplet applet = new SwingMainDemo();
				applet.init();

				frame.setContentPane(applet.getContentPane());

				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);

				applet.start();
			}
		});
	}

}
