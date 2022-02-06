package org.comtel2000.keyboard.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.comtel2000.keyboard.control.KeyButton;

public class KeyButtonEvent extends Event {

    public static final EventType<Event> ANY;
    public static final EventType<Event> LONG_PRESSED;
    public static final EventType<Event> SHORT_PRESSED;
    private static final long serialVersionUID = 647301812232489628L;

    static {
        ANY = new EventType<>(Event.ANY, "KB_PRESSED");
        LONG_PRESSED = new EventType<>(ANY, "KB_PRESSED_LONG");
        SHORT_PRESSED = new EventType<>(ANY, "KB_PRESSED_SHORT");
    }

    public KeyButtonEvent(EventType<Event> type) {
        super(type);
    }

    public KeyButtonEvent(KeyButton button, EventType<Event> type) {
        super(button, button, type);
    }

    @Override
    public String toString() {
        return "KeyButtonEvent [" + "source = " + getSource() + ", target = " + getTarget()
                + ", eventType = " + getEventType() + ", consumed = " + isConsumed() + "]";
    }

}
