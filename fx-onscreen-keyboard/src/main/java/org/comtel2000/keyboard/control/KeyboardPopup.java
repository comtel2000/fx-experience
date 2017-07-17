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

import java.util.Objects;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;

/**
 * Helper class to create a {@link KeyboardPane}
 *
 * @author comtel
 *
 */
public class KeyboardPopup extends Popup {

  private final KeyboardPane keyboard;

  private Scene owner;

  private double initOffset = 5d;

  /** default vertical keyboard to text component offset */
  private DoubleProperty offset;

  public static final EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = event -> {
    if (event.getSource() instanceof Node) {
      Node node = (Node) event.getSource();
      node.fireEvent(new WindowEvent(node.getScene() != null ? node.getScene().getWindow() : null,
          WindowEvent.WINDOW_CLOSE_REQUEST));
    }
  };

  public KeyboardPopup(final KeyboardPane panel) {
    keyboard = Objects.requireNonNull(panel);
    getContent().add(keyboard);
  }

  public final KeyboardPane getKeyboard() {
    return keyboard;
  }

  public Scene getRegisteredScene() {
    return owner;
  }

  public void registerScene(final Scene scene) {
    owner = Objects.requireNonNull(scene);
  }

  public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
    getKeyboard().setOnKeyboardCloseButton(value);
  }

  /**
   * The vertical keyboard to text component offset
   *
   * @return offset
   */
  public final double getOffset() {
    return offset == null ? initOffset : offset.get();
  }

  /**
   * Set the vertical keyboard to text component offset
   *
   * @param value
   *          offset
   */
  public final void setOffset(double value) {
    if (offset == null) {
      initOffset = value;
    } else {
      offset.set(value);
    }
  }

  /**
   * The vertical keyboard to text component offset
   *
   * @return offset
   */
  public final DoubleProperty offsetProperty() {
    if (offset == null) {
      offset = new SimpleDoubleProperty(this, "offset", initOffset);
    }
    return offset;
  }

  /**
   * Gets the value of the property showing.
   * 
   * @return is showing
   */
  public boolean isVisible() {
    return isShowing();
  }

  /**
   * Show or hide the popup
   * 
   * @param flag
   *          show or hide
   */
  public void setVisible(boolean flag) {
    if (flag && owner != null) {
      show(owner.getWindow());
      return;
    }
    if (flag) {
      show();
      return;
    }
    hide();
  }

}
