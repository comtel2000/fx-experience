package org.comtel.javafx.event;

import javafx.event.Event;
import javafx.event.EventType;

import org.comtel.javafx.control.KeyButton;

public class KeyButtonEvent extends Event {

	private static final long serialVersionUID = 65116620766495525L;

	public KeyButtonEvent(EventType<Event> type) {
		super(type);
	}

	public KeyButtonEvent(KeyButton button, EventType<Event> type) {
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

	public static final EventType<Event> ANY;
	public static final EventType<Event> LONG_PRESSED;
	public static final EventType<Event> SHORT_PRESSED;

	static {
		ANY = new EventType<Event>(Event.ANY, "KB_PRESSED");
		LONG_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_LONG");
		SHORT_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_SHORT");
	}

}
