/*******************************************************************************
 * Copyright (c) 2017 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.comtel2000.keyboard.control;

import org.comtel2000.keyboard.event.KeyButtonEvent;

import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;

public abstract class KeyButton extends Button implements LongPressable {

  private static final long DEFAULT_DELAY = 400;

  private String keyText;

  private boolean movable;
  private boolean repeatable;
  private boolean sticky;

  private int keyCode;

  protected Timeline buttonDelay;

  private EventHandler<? super KeyButtonEvent> longPressed;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;

  private EventHandler<? super KeyButtonEvent> shortPressed;
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
    getStyleClass().add("key-button");
    initEventListener(delay > 0 ? delay : DEFAULT_DELAY);

  }

  protected abstract void initEventListener(long delay);

  protected void fireLongPressed() {
    fireEvent(new KeyButtonEvent(this, KeyButtonEvent.LONG_PRESSED));
  }

  protected void fireShortPressed() {
    fireEvent(new KeyButtonEvent(this, KeyButtonEvent.SHORT_PRESSED));
  }

  @Override
  public final EventHandler<? super KeyButtonEvent> getOnLongPressed() {
    return onLongPressed == null ? longPressed : onLongPressed.get();
  }

  @Override
  public final void setOnLongPressed(EventHandler<? super KeyButtonEvent> h) {
    onLongPressedProperty().set(h);
  }

  @Override
  public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressedProperty() {
    if (onLongPressed == null) {
      onLongPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>(this,
          "onLongPressed", longPressed) {
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
  public final EventHandler<? super KeyButtonEvent> getOnShortPressed() {
    return onShortPressed == null ? shortPressed : onShortPressed.get();
  }

  @Override
  public final void setOnShortPressed(EventHandler<? super KeyButtonEvent> h) {
    onShortPressedProperty().set(h);
  }

  @Override
  public final ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressedProperty() {
    if (onShortPressed == null) {
      onShortPressed = new SimpleObjectProperty<EventHandler<? super KeyButtonEvent>>(this,
          "onShortPressed", shortPressed) {
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
    return repeatable;
  }

  public void setRepeatable(boolean repeatable) {
    this.repeatable = repeatable;
  }

  public boolean isSticky() {
    return sticky;
  }

  public void setSticky(boolean sticky) {
    this.sticky = sticky;
  }

}
