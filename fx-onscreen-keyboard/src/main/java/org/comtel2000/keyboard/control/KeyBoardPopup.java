package org.comtel2000.keyboard.control;

import java.util.Collections;
import java.util.HashMap;

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
import java.util.Map;
import java.util.Objects;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class KeyBoardPopup extends Popup implements VkProperties {

	enum Visiblity {
		/** Set position and visible true */
		SHOW, 
		
		/** Set visible false */
		HIDE, 
		
		/** Set positioning only if visible true */
		POS
	}
	
	private final KeyboardPane keyboard;

	private Scene owner;

	private Animation animation;

	public final static EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = (event) -> {
		if (event.getSource() instanceof Node){
			((Node)event.getSource()).getScene().getWindow().hide();
		}
	};
	
	public KeyBoardPopup(final KeyboardPane panel) {
		keyboard = Objects.requireNonNull(panel);
		getContent().add(keyboard);
	}

	public final KeyboardPane getKeyBoard() {
		return keyboard;
	}

	public boolean isVisible() {
		return isShowing();
	}

	public void setVisible(boolean visible) {
		setVisible(visible ? Visiblity.SHOW : Visiblity.HIDE);
	}

	public Scene getRegisteredScene() {
		return owner;
	}

	public void registerScene(final Scene scene) {
		owner = Objects.requireNonNull(scene);
	}

	public void addFocusListener(final Scene scene) {
		addFocusListener(scene, false);
	}
	
	public void addFocusListener(final Scene scene, boolean doNotOpen) {
		registerScene(scene);
		scene.focusOwnerProperty().addListener((value, n1, n2) -> {
			if (n2 != null && n2 instanceof TextInputControl) {
				setVisible(doNotOpen ? Visiblity.POS : Visiblity.SHOW, (TextInputControl) n2);
			} else {
				setVisible(Visiblity.HIDE);
			}
		});
	}
	
	public void addDoubleClickEventFilter(final Stage stage) {
		Objects.requireNonNull(stage).addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getClickCount() == 2 && stage.getScene() != null) {
				Node node = stage.getScene().getFocusOwner();
				if (node != null && node instanceof TextInputControl) {
					setVisible(Visiblity.SHOW, (TextInputControl) node);
				}
			}
		});
	}

	void setVisible(Visiblity visible){
		setVisible(visible, null);
	}
	
	void setVisible(final Visiblity visible, final TextInputControl textNode) {
		if ((visible == Visiblity.POS || visible == Visiblity.SHOW) && textNode != null) {
			Map<String, String> vkProps = getVkProperties(textNode);
			if (vkProps.isEmpty()) {
				getKeyBoard().setKeyboardType(KeyboardType.TEXT);
			} else {
				getKeyBoard().setKeyboardType(vkProps.getOrDefault(VK_TYPE, VK_TYPE_TEXT));
				if (vkProps.containsKey(VK_LOCALE)) {
					getKeyBoard().switchLocale(new Locale(vkProps.get(VK_LOCALE)));
				}
			}
			Rectangle2D textNodeBounds = new Rectangle2D(textNode.getScene().getWindow().getX() + textNode.getLocalToSceneTransform().getTx(),
					textNode.getScene().getWindow().getY() + textNode.getLocalToSceneTransform().getTy(), textNode.getWidth(), textNode.getHeight());
			Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
			if (textNodeBounds.getMinX() + getWidth() > screenBounds.getMaxX()) {
				setX(screenBounds.getMaxX() - getWidth());
			} else {
				setX(textNodeBounds.getMinX());
			}
			if (textNodeBounds.getMaxY() + getHeight() > screenBounds.getMaxY()) {
				setY(textNodeBounds.getMinY() - getHeight() + 20);
			} else {
				setY(textNodeBounds.getMaxY() + 40);
			}
		}

		if (visible == Visiblity.POS || visible == Visiblity.HIDE && !isShowing()) {
			return;
		}
		if (animation != null) {
			animation.stop();
		}
		getKeyBoard().setOpacity(0.0);

		FadeTransition fade = new FadeTransition(Duration.seconds(.1), getKeyBoard());
		fade.setToValue(visible == Visiblity.SHOW ? 1.0 : 0.0);
		fade.setOnFinished(e -> animation = null);

		ScaleTransition scale = new ScaleTransition(Duration.seconds(.1), getKeyBoard());
		scale.setToX(visible == Visiblity.SHOW ? 1 : 0.8);
		scale.setToY(visible == Visiblity.SHOW ? 1 : 0.8);
		ParallelTransition tx = new ParallelTransition(fade, scale);
		animation = tx;
		if (visible == Visiblity.SHOW && !isShowing()) {
			// initial start
			super.show(owner != null ? owner.getWindow() : getOwnerWindow());
		}
		tx.play();
	}

	private Map<String, String> getVkProperties(Node node) {
		if (node.hasProperties()) {
			Map<String, String> vkProps = new HashMap<>(3);
			node.getProperties().forEach((key, value) -> {
				if (key.toString().startsWith("vk")) {
					vkProps.put(String.valueOf(key), String.valueOf(value));
				}
			});
			return vkProps;
		}
		if (node.getParent() != null && node.getParent().hasProperties()) {
			Map<String, String> vkProps = new HashMap<>(3);
			node.getParent().getProperties().forEach((key, value) -> {
				if (key.toString().startsWith("vk")) {
					vkProps.put(String.valueOf(key), String.valueOf(value));
				}
			});
			return vkProps;
		}
		return Collections.emptyMap();

	}
}
