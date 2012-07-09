package org.comtel.javafx.examples;

import javafx.animation.Animation.Status;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.comtel.javafx.KeyBoardPanel;
import org.comtel.javafx.robot.FXRobotHandler;
import org.tbee.javafx.scene.layout.MigPane;

public class MainDemo extends Application {

	public Scene scene;
	private Popup fxKeyboardPopup;
	private KeyBoardPanel fxKeyboard;

	@Override
	public void start(Stage stage) {

		stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(true);
		fxKeyboard = new KeyBoardPanel(null);
		fxKeyboard.addRobotHandler(new FXRobotHandler());
		fxKeyboard.setOnKeyboardCloseButton(new EventHandler<Event>() {

			public void handle(Event event) {
				setKeyboardVisible(false, null);
			}
		});

		fxKeyboardPopup = new Popup();
		// fxKeyboardPopup.setAutoFix(true);
		fxKeyboardPopup.getContent().add(fxKeyboard);

		MigPane pane = new MigPane();
		pane.setPrefSize(800, 200);

		final TextField tf = new TextField("");
		tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					Point2D point = new Point2D(tf.getScene().getWindow().getX() + tf.getLayoutX(), tf.getScene()
							.getWindow().getY()
							+ tf.getLayoutY() + 40);
					setKeyboardVisible(true, point);

				} else {
					setKeyboardVisible(false, null);
				}
			}
		});
		final TextField tf2 = new TextField("");

		tf2.focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					Point2D point = new Point2D(tf2.getScene().getWindow().getX() + tf2.getLayoutX(), tf2.getScene()
							.getWindow().getY()
							+ tf2.getLayoutY() + 40);
					setKeyboardVisible(true, point);

				} else {
					setKeyboardVisible(false, null);
				}
			}
		});

		pane.add(tf);
		pane.add(tf2, "wrap");
		Button okButton = new Button("Ok");
		okButton.setDefaultButton(true);
		okButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				System.err.println("ok");
			}
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setCancelButton(true);
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				System.err.println("cancel");
			}
		});
		pane.add(okButton);
		pane.add(cancelButton);

		scene = new Scene(pane, 800, 600);
		String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		fxKeyboardPopup.show(stage);
		stage.setScene(scene);
		stage.show();

	}

	private Transition fadeOut;
	private Transition fadeIn;

	public void setKeyboardVisible(boolean flag, Point2D pos) {
		final boolean visible = flag;
		final Point2D location = pos;
		Platform.runLater(new Runnable() {
			public void run() {
				if (fxKeyboardPopup == null) {
					return;
				}
				if (location != null) {
					fxKeyboardPopup.setX(location.getX());
					fxKeyboardPopup.setY(location.getY() + 20);
				}

				if (fadeOut == null) {
					// transition = new FadeTransition(Duration.millis(200),
					// fxKeyboard);
					fadeOut = new ScaleTransition(Duration.millis(150), fxKeyboard);
					fadeOut.setOnFinished(new EventHandler<ActionEvent>() {

						public void handle(ActionEvent e) {
							fxKeyboardPopup.hide();
						}
					});
				}
				if (fadeIn == null) {
					fadeIn = new ScaleTransition(Duration.millis(150), fxKeyboard);

				}
				if (visible && fadeIn.getStatus() == Status.STOPPED) {
					if (!fxKeyboardPopup.isShowing()) {
						fxKeyboardPopup.show(fxKeyboardPopup.getOwnerWindow());
					}
					System.err.println("fade in");
					fadeOut.stop();

					((ScaleTransition) fadeIn).setFromX(0.1d);
					((ScaleTransition) fadeIn).setFromY(0.1d);
					((ScaleTransition) fadeIn).setToX(fxKeyboard.getScale());
					((ScaleTransition) fadeIn).setToY(fxKeyboard.getScale());

					// ((FadeTransition) transition).setFromValue(0.0f);
					// ((FadeTransition) transition).setToValue(1.0f);
					fadeIn.play();

				} else if (fxKeyboardPopup.isShowing() && fadeOut.getStatus() == Status.STOPPED) {
					System.err.println("fade out");
					fadeIn.stop();
					((ScaleTransition) fadeOut).setFromX(fxKeyboard.getScale());
					((ScaleTransition) fadeOut).setFromY(fxKeyboard.getScale());
					((ScaleTransition) fadeOut).setToX(0.1d);
					((ScaleTransition) fadeOut).setToY(0.1d);

					// ((FadeTransition) transition).setFromValue(1.0f);
					// ((FadeTransition) transition).setToValue(0.0f);
					fadeOut.play();

					// fxKeyboardPopup.hide();
				}
			}
		});
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
