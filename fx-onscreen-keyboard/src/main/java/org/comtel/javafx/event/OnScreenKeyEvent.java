package org.comtel.javafx.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;

import org.comtel.javafx.control.KeyButton;

public class OnScreenKeyEvent extends InputEvent {

	private static final long serialVersionUID = 65116620766495525L;

	public OnScreenKeyEvent(EventType<? extends InputEvent> type) {
		super(type);
	}

	public OnScreenKeyEvent(KeyButton button, EventType<? extends InputEvent> type) {
		super(button, button, type);

	}

	public String toString() {
		StringBuilder stringbuilder = new StringBuilder("KeyButtonEvent [");
		stringbuilder.append("source = ").append(getSource());
		stringbuilder.append(", target = ").append(getTarget());
		stringbuilder.append(", eventType = ").append(getEventType());
		stringbuilder.append(", consumed = ").append(isConsumed());
		return stringbuilder.append("]").toString();
	}

	public static final EventType<? super Event> ANY;
	public static final EventType<? super Event> LONG_PRESSED;
	public static final EventType<? super Event> SHORT_PRESSED;

	static {
		ANY = new EventType<Event>(Event.ANY, "KB_PRESSED");
		LONG_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_LONG");
		SHORT_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_SHORT");
	}

}
