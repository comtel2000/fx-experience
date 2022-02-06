package org.comtel2000.keyboard.control;

import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.comtel2000.keyboard.event.KeyButtonEvent;

public abstract class KeyButton extends Button implements LongPressable {

    private static final double KEY_LONG_PRESS_DELAY_MIN = 100;
    private static final double KEY_LONG_PRESS_DELAY_MAX = 1000;
    private static double KEY_LONG_PRESS_DELAY = 400;

    static {
        String s = System.getProperty("org.comtel2000.keyboard.longPressDelay");
        if (s != null) {
            Double delay = Double.valueOf(s);
            KEY_LONG_PRESS_DELAY = Math.min(Math.max(delay, KEY_LONG_PRESS_DELAY_MIN),
                    KEY_LONG_PRESS_DELAY_MAX);
        }
    }

    Timeline buttonDelay;
    private String keyText;
    private boolean movable;
    private boolean sticky;
    private int keyCode;
    private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;
    private ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressed;

    public KeyButton() {
        this(null, null, KEY_LONG_PRESS_DELAY);
    }

    public KeyButton(String label) {
        this(label, null, KEY_LONG_PRESS_DELAY);
    }

    public KeyButton(Node graphic) {
        this(null, graphic, KEY_LONG_PRESS_DELAY);
    }

    public KeyButton(String label, Node graphic) {
        this(label, graphic, KEY_LONG_PRESS_DELAY);
    }

    public KeyButton(String label, double delay) {
        this(label, null, delay);
    }

    public KeyButton(String label, Node graphic, double delay) {
        super(label, graphic);
        getStyleClass().add("key-button");
        initEventListener(delay > 0 ? delay : KEY_LONG_PRESS_DELAY);

    }

    protected abstract void initEventListener(double delay);

    void fireLongPressed() {
        fireEvent(new KeyButtonEvent(this, KeyButtonEvent.LONG_PRESSED));
    }

    void fireShortPressed() {
        fireEvent(new KeyButtonEvent(this, KeyButtonEvent.SHORT_PRESSED));
    }

    @Override
    public final EventHandler<? super KeyButtonEvent> getOnLongPressed() {
        return onLongPressed != null ? onLongPressed.get() : null;
    }

    @Override
    public final void setOnLongPressed(EventHandler<? super KeyButtonEvent> h) {
        onLongPressedProperty().set(h);
    }

    @Override
    public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressedProperty() {
        if (onLongPressed == null) {
            onLongPressed = new ObjectPropertyBase<>() {
                @SuppressWarnings("unchecked")
                @Override
                protected void invalidated() {
                    setEventHandler(KeyButtonEvent.LONG_PRESSED, (EventHandler<? super Event>) get());
                }

                @Override
                public Object getBean() {
                    return KeyButton.this;
                }

                @Override
                public String getName() {
                    return "onLongPressed";
                }
            };
        }
        return onLongPressed;
    }

    @Override
    public final EventHandler<? super KeyButtonEvent> getOnShortPressed() {
        return onShortPressed != null ? onShortPressed.get() : null;
    }

    @Override
    public final void setOnShortPressed(EventHandler<? super KeyButtonEvent> h) {
        onShortPressedProperty().set(h);
    }

    @Override
    public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressedProperty() {
        if (onShortPressed == null) {
            onShortPressed = new ObjectPropertyBase<>() {
                @SuppressWarnings("unchecked")
                @Override
                protected void invalidated() {
                    setEventHandler(KeyButtonEvent.SHORT_PRESSED, (EventHandler<? super Event>) get());
                }

                @Override
                public Object getBean() {
                    return KeyButton.this;
                }

                @Override
                public String getName() {
                    return "onShortPressed";
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

    public String getKeyText() {
        return keyText;
    }

    public void setKeyText(String keyText) {
        this.keyText = keyText;
    }

    public void addExtKeyCode(int keyCode, String label) {
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

}
