package org.comtel2000.keyboard.control;

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

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.slf4j.LoggerFactory;

public class KeyButton extends Button implements LongPressable {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyButton.class);

	private final static long DEFAULT_DELAY = 400;

	private int keyCode;

	private final Timeline timer;

	private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;

	private ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressed;

	public KeyButton() {
		this(null, null, DEFAULT_DELAY);
	}

	public KeyButton(String label) {
		this(label, null, DEFAULT_DELAY);
	}

	public KeyButton(Node graphic) {
		this(null, graphic, DEFAULT_DELAY);
	}

	public KeyButton(String label, Node graphic) {
		this(label, graphic, DEFAULT_DELAY);
	}

	public KeyButton(String label, long delay) {
		this(label, null, delay);
	}

	public KeyButton(String label, Node graphic, long delay) {
		super(label, graphic);
		setId("key-button");

		timer = new Timeline(new KeyFrame(new Duration(20), new KeyValue[0]));
		if (delay > 0) {
			timer.setDelay(new Duration(delay));
			initEventListener();
		}
	}

	protected void initEventListener() {
		timer.setOnFinished(event -> fireLongPressed());

		setOnDragDetected(event -> {
			logger.trace("{} drag detected", keyCode);
			event.consume();
		});

		setOnMouseClicked(event -> {
			logger.trace("{} clicked: {}", keyCode, timer.getCurrentRate());

			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (timer.getStatus().equals(Status.RUNNING)) {
					timer.stop();
					fireShortPressed();

				}

			}
			setFocused(false);
			event.consume();
		});

		setOnMousePressed(event -> {
			logger.trace("{} pressed: {}", keyCode, timer.getCurrentRate());
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				timer.playFromStart();
			}
			event.consume();
		});

		setOnMouseDragged(event -> {
			logger.trace("{} dragged: {}", keyCode, timer.getCurrentRate());

			if (event.getButton().equals(MouseButton.PRIMARY)) {
				if (timer.getStatus().equals(Status.RUNNING) && timer.getCurrentRate() > 1) {
					timer.stop();
					fireLongPressed();
				}
				event.consume();
			}

		});

	}

	public Duration getDelay() {
		return timer.getDelay();
	}

	public void setDelay(Duration delay) {
		timer.setDelay(delay);
	}

	protected void fireLongPressed() {
		fireEvent(new KeyButtonEvent(this, KeyButtonEvent.LONG_PRESSED));
	}

	protected void fireShortPressed() {
		fireEvent(new KeyButtonEvent(this, KeyButtonEvent.SHORT_PRESSED));
	}

	@Override
	public final void setOnLongPressed(EventHandler<? super KeyButtonEvent> eventhandler) {
		onLongPressedProperty().set(eventhandler);
	}

	@Override
	public final EventHandler<? super KeyButtonEvent> getOnLongPressed() {
		return onLongPressedProperty().get();
	}

	@Override
	public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressedProperty() {
		if (onLongPressed == null) {
			onLongPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>() {

				@SuppressWarnings("unchecked")
				@Override
				protected void invalidated() {
					setEventHandler(KeyButtonEvent.LONG_PRESSED, (EventHandler<? super Event>) get());
				}
			};
		}
		return onLongPressed;
	}

	@Override
	public final void setOnShortPressed(EventHandler<? super KeyButtonEvent> eventhandler) {
		onShortPressedProperty().set(eventhandler);
	}

	@Override
	public final EventHandler<? super KeyButtonEvent> getOnShortPressed() {
		return onShortPressedProperty().get();
	}

	@Override
	public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressedProperty() {
		if (onShortPressed == null) {
			onShortPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>() {

				@SuppressWarnings("unchecked")
				@Override
				protected void invalidated() {
					setEventHandler(KeyButtonEvent.SHORT_PRESSED, (EventHandler<? super Event>) get());
				}
			};
		}
		return onShortPressed;

	}

	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

}
