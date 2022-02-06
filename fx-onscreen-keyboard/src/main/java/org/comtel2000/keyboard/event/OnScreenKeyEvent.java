package org.comtel2000.keyboard.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import org.comtel2000.keyboard.control.KeyButton;

import java.io.Serial;

class OnScreenKeyEvent extends InputEvent {

    private static final EventType<? super Event> ANY;
    private static final EventType<? super Event> LONG_PRESSED;
    private static final EventType<? super Event> SHORT_PRESSED;
    @Serial
    private static final long serialVersionUID = 65116620766495525L;

    static {
        ANY = new EventType<>(Event.ANY, "KB_PRESSED");
        LONG_PRESSED = new EventType<>(ANY, "KB_PRESSED_LONG");
        SHORT_PRESSED = new EventType<>(ANY, "KB_PRESSED_SHORT");
    }

    public OnScreenKeyEvent(EventType<? extends InputEvent> type) {
        super(type);
    }

    public OnScreenKeyEvent(KeyButton button, EventType<? extends InputEvent> type) {
        super(button, button, type);

    }

    @Override
    public String toString() {
        return "KeyButtonEvent [" + "source = " + getSource() + ", target = " + getTarget()
                + ", eventType = " + getEventType() + ", consumed = " + isConsumed() + "]";
    }

}
