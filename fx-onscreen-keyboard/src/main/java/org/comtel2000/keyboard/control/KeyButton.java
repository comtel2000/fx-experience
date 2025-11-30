/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.comtel2000.keyboard.event.KeyButtonEvent;

public abstract class KeyButton extends Button implements LongPressable {

  private String keyText;
  private boolean movable;
  private boolean sticky;
  private int keyCode;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressed;

  protected KeyButton() {
    this(null, null);
  }

  protected KeyButton(String label) {
    this(label, null);
  }

  protected KeyButton(Node graphic) {
    this(null, graphic);
  }

  protected KeyButton(String label, Node graphic) {
    super(label, graphic);
    getStyleClass().add("key-button");
  }

  final void setTimelines(Timelines timelines) {
    initEventListener(timelines);
  }

  protected abstract void initEventListener(Timelines timelines);

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
      onLongPressed =
          new ObjectPropertyBase<>() {
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
      onShortPressed =
          new ObjectPropertyBase<>() {
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

  int getKeyCode() {
    return keyCode;
  }

  void setKeyCode(int keyCode) {
    this.keyCode = keyCode;
  }

  String getKeyText() {
    return keyText;
  }

  void setKeyText(String keyText) {
    this.keyText = keyText;
  }

  void addExtKeyCode(Timelines timelines, int keyCode, String label) {}

  boolean isMovable() {
    return movable;
  }

  void setMovable(boolean movable) {
    this.movable = movable;
  }

  boolean isRepeatable() {
    return false;
  }

  boolean isSticky() {
    return sticky;
  }

  void setSticky(boolean sticky) {
    this.sticky = sticky;
  }
}
