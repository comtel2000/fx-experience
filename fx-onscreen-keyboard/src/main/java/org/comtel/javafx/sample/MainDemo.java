package org.comtel.javafx.sample;

import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import org.comtel.javafx.control.KeyBoardPopup;
import org.comtel.javafx.control.KeyBoardPopupBuilder;
import org.comtel.javafx.robot.RobotFactory;

public class MainDemo extends Application {

	private KeyBoardPopup popup;

	@Override
	public void start(Stage stage) {

		stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(true);


		String fontUrl = this.getClass().getResource("/font/FontKeyboardFX.ttf").toExternalForm();
		Font f = Font.loadFont(fontUrl, -1);
		System.err.println(f);
		
		/*
		Path numblockLayout = null;
		try {
			numblockLayout = Paths.get(this.getClass().getResource("/xml/numblock").toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		popup = KeyBoardPopupBuilder.create().initScale(1.0).initLocale(Locale.ENGLISH).addIRobot(RobotFactory.createFXRobot()).layerPath(numblockLayout)
				.build();
		*/
		
		popup = KeyBoardPopupBuilder.create().initScale(1.0).initLocale(Locale.ENGLISH).addIRobot(RobotFactory.createFXRobot()).build();
		
		
		popup.getKeyBoard().setOnKeyboardCloseButton(new EventHandler<Event>() {
			public void handle(Event event) {
				setPopupVisible(false, null);
			}
		});

		FlowPane pane = new FlowPane();
		pane.setVgap(20);
		pane.setHgap(20);
		pane.setPrefWrapLength(100);

		final TextField tf = new TextField("");
		final TextArea ta = new TextArea("");

		Button okButton = new Button("Ok");
		okButton.setDefaultButton(true);

		Button cancelButton = new Button("Cancel");
		cancelButton.setCancelButton(true);

		pane.getChildren().add(new Label("Text1"));
		pane.getChildren().add(tf);
		pane.getChildren().add(new Label("Text2"));
		pane.getChildren().add(ta);
		pane.getChildren().add(okButton);
		pane.getChildren().add(cancelButton);
		//pane.getChildren().add(KeyBoardBuilder.create().addIRobot(RobotFactory.createFXRobot()).build());
		Scene scene = new Scene(pane, 200, 300);

		// add keyboard scene listener to all text components
		scene.focusOwnerProperty().addListener(new ChangeListener<Node>() {
			@Override
			public void changed(ObservableValue<? extends Node> value, Node n1, Node n2) {
				if (n2 != null && n2 instanceof TextInputControl) {
					setPopupVisible(true, (TextInputControl) n2);
					
				} else {
					setPopupVisible(false, null);
				}
			}
		});

		String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();
		scene.getStylesheets().add(css);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		
		stage.setScene(scene);
		popup.show(stage);
		stage.show();

	}

	private void setPopupVisible(final boolean b, final TextInputControl textNode) {

		Platform.runLater(new Runnable() {

			private Animation fadeAnimation;

			@Override
			public void run() {
				if (b) {
					if (textNode != null) {
						Rectangle2D textNodeBounds = new Rectangle2D(textNode.getScene().getWindow().getX()
								+ textNode.getLocalToSceneTransform().getTx(), textNode.getScene().getWindow().getY()
								+ textNode.getLocalToSceneTransform().getTy(), textNode.getWidth(), textNode
								.getHeight());

						Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
						if (textNodeBounds.getMinX() + popup.getWidth() > screenBounds.getMaxX()) {
							popup.setX(screenBounds.getMaxX() - popup.getWidth());
						} else {
							popup.setX(textNodeBounds.getMinX());
						}

						if (textNodeBounds.getMaxY() + popup.getHeight() > screenBounds.getMaxY()) {
							popup.setY(textNodeBounds.getMinY() - popup.getHeight() + 20);
						} else {
							popup.setY(textNodeBounds.getMaxY() + 40);
						}
					}

				}

				if (fadeAnimation != null) {
					fadeAnimation.stop();
				}
				if (!b){
					popup.hide();
					return;
				}
				if (popup.isShowing()){
					return;
				}
				popup.getKeyBoard().setOpacity(0.0);

				FadeTransition fade = new FadeTransition(Duration.seconds(.5), popup.getKeyBoard());
				fade.setToValue(b ? 1.0 : 0.0);
				fade.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						fadeAnimation = null;
					}
				});

				ScaleTransition scale = new ScaleTransition(Duration.seconds(.5), popup.getKeyBoard());
				scale.setToX(b ? 1 : 0.8);
				scale.setToY(b ? 1 : 0.8);

				ParallelTransition tx = new ParallelTransition(fade, scale);
				fadeAnimation = tx;
				tx.play();
				if (b) {
					if (!popup.isShowing()) {
						popup.show(popup.getOwnerWindow());
					}
				}

				

			}
		});
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
