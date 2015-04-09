package org.comtel2000.samples.fx;

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

import java.util.Locale;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.keyboard.robot.FXRobotHandler;

public class MainDemo extends Application {

	private Animation fadeAnimation;

	private KeyBoardPopup popup;

	@Override
	public void start(Stage stage) {

		stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(true);

		popup = KeyBoardPopupBuilder.create().initScale(1.0).initLocale(Locale.ENGLISH).addIRobot(new FXRobotHandler()).build();
		popup.getKeyBoard().setOnKeyboardCloseButton(event -> setPopupVisible(false, null));

		VBox pane = new VBox(20);

		Button okButton = new Button("Ok");
		okButton.setDefaultButton(true);

		Button cancelButton = new Button("Cancel");
		cancelButton.setCancelButton(true);

		CheckBox spaceKeyMoveCb = new CheckBox("Movable");
		spaceKeyMoveCb.setSelected(true);
		popup.getKeyBoard().spaceKeyMoveProperty().bind(spaceKeyMoveCb.selectedProperty());

		pane.getChildren().add(new ToolBar(okButton, cancelButton, spaceKeyMoveCb));
		pane.getChildren().add(new Label("Text1"));
		pane.getChildren().add(new TextField(""));
		pane.getChildren().add(new TextArea(""));
		pane.getChildren().add(new Label("Password"));
		pane.getChildren().add(new PasswordField());

		// pane.getChildren().add(KeyBoardBuilder.create().addIRobot(RobotFactory.createFXRobot()).build());

		Scene scene = new Scene(pane, 600, 400);

		// add keyboard scene listener to all text components
		scene.focusOwnerProperty().addListener((value, n1, n2) -> {
			if (n2 != null && n2 instanceof TextInputControl) {
				setPopupVisible(true, (TextInputControl) n2);
			} else {
				setPopupVisible(false, null);
			}
		});

		// add double click listener
		stage.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() == 2) {
				Node node = scene.getFocusOwner();
				if (node != null && node instanceof TextInputControl) {
					setPopupVisible(true, (TextInputControl) node);
				}
			}
		});

		stage.setOnCloseRequest((event) -> System.exit(0));

		stage.setScene(scene);
		popup.show(stage);
		stage.show();

	}

	private void setPopupVisible(final boolean b, final TextInputControl textNode) {

		Platform.runLater(() -> {
			if (b && textNode != null) {
				Rectangle2D textNodeBounds = new Rectangle2D(textNode.getScene().getWindow().getX() + textNode.getLocalToSceneTransform().getTx(), textNode.getScene().getWindow().getY()
						+ textNode.getLocalToSceneTransform().getTy(), textNode.getWidth(), textNode.getHeight());

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

			if (fadeAnimation != null) {
				fadeAnimation.stop();
			}
			if (!b) {
				popup.hide();
				return;
			}
			if (popup.isShowing()) {
				return;
			}
			popup.getKeyBoard().setOpacity(.0);

			FadeTransition fade = new FadeTransition(Duration.seconds(.5), popup.getKeyBoard());
			fade.setToValue(b ? 1. : .8);
			// fade.setOnFinished((event) -> fadeAnimation = null);

			ScaleTransition scale = new ScaleTransition(Duration.seconds(.5), popup.getKeyBoard());
			scale.setToX(b ? 1. : .8);
			scale.setToY(b ? 1. : .8);

			ParallelTransition tx = new ParallelTransition(fade, scale);
			fadeAnimation = tx;
			tx.play();
			if (b && !popup.isShowing()) {
				popup.show(popup.getOwnerWindow());
			}

		});
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
