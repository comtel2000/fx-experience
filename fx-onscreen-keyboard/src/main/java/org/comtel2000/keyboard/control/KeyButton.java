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

import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import org.comtel2000.keyboard.event.KeyButtonEvent;

import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class KeyButton extends Button implements LongPressable {

  private static double KEY_LONG_PRESS_DELAY = 400;

  private static final double KEY_LONG_PRESS_DELAY_MIN = 100;
  private static final double KEY_LONG_PRESS_DELAY_MAX = 1000;

  Timeline buttonDelay;
  private String keyText;
  private boolean movable;
  private boolean sticky;
  private int keyCode;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onLongPressed;
  private ObjectProperty<EventHandler<? super KeyButtonEvent>> onShortPressed;

  static {
    AccessController.doPrivileged((PrivilegedAction<Void>) () -> {

      String s = System.getProperty("org.comtel2000.keyboard.longPressDelay");
      if (s != null) {
        Double delay = Double.valueOf(s);
        KEY_LONG_PRESS_DELAY = Math.min(Math.max(delay, KEY_LONG_PRESS_DELAY_MIN),
            KEY_LONG_PRESS_DELAY_MAX);
      }
      return null;
    });
  }

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
