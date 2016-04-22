package org.comtel2000.keyboard.control;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * Helper class to create a {@link KeyboardPane}
 * 
 * @author comtel
 *
 */
public class KeyBoardPopup extends Popup implements VkProperties {

  enum Visiblity {
    /** Set position and visible true */
    SHOW,

    /** Set visible false */
    HIDE,

    /** Set positioning only if visible true */
    POS
  }

  private final KeyboardPane keyboard;

  private Scene owner;

  /** default vertical keyboard to text component offset */
  private final DoubleProperty offsetProperty = new SimpleDoubleProperty(this, "offset", 5);

  private FadeTransition animation;

  public final static EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = (event) -> {
    if (event.getSource() instanceof Node) {
      ((Node) event.getSource()).fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
  };

  public KeyBoardPopup(final KeyboardPane panel) {
    keyboard = Objects.requireNonNull(panel);
    getContent().add(keyboard);
  }

  public final KeyboardPane getKeyBoard() {
    return keyboard;
  }

  public boolean isVisible() {
    return isShowing();
  }

  public void setVisible(boolean visible) {
    setVisible(visible ? Visiblity.SHOW : Visiblity.HIDE);
  }

  public Scene getRegisteredScene() {
    return owner;
  }

  public void registerScene(final Scene scene) {
    owner = Objects.requireNonNull(scene);
  }

  /**
   * Adds a FocusListener to Scene and open keyboard on {@link TextInputControl}
   * 
   * @param scene {@link Scene} to connect with the keyboard
   */
  public void addFocusListener(final Scene scene) {
    addFocusListener(scene, false);
  }

  /**
   * Adds a FocusListener to Scene and open keyboard on {@link TextInputControl}
   * 
   * @param scene {@link Scene} to connect with the keyboard
   * @param doNotOpen on hidden keyboard do nothing and on showing keyboard move to current
   *        component
   */
  public void addFocusListener(final Scene scene, boolean doNotOpen) {
    registerScene(scene);
    scene.focusOwnerProperty().addListener((value, n1, n2) -> {
      if (n2 instanceof TextInputControl) {
        setVisible(doNotOpen ? Visiblity.POS : Visiblity.SHOW, (TextInputControl) n2);
      } else if (n2 instanceof Parent) {
        TextInputControl control = findTextInputControl((Parent) n2);
        setVisible((control != null ? (doNotOpen ? Visiblity.POS : Visiblity.SHOW) : Visiblity.HIDE), control);
      } else {
        setVisible(Visiblity.HIDE);
      }
    });
  }

  /**
   * search for nested input controls like {@code FakeFocusTextField}
   * 
   * @see ComboBox#isEditable()
   * @param parent
   * @return embedded TextInputControl or null
   */
  private static TextInputControl findTextInputControl(Parent parent) {
    for (Node child : parent.getChildrenUnmodifiable()) {
      if (child instanceof TextInputControl) {
        return (TextInputControl) child;
      }
    }
    return null;
  }

  /**
   * Adds a mouse listener to the Stage and open the keyboards on 'double' click a
   * {@link TextInputControl}
   * 
   * @param stage {@link Stage} to connect with the keyboard
   */
  public void addDoubleClickEventFilter(final Stage stage) {
    Objects.requireNonNull(stage).addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getClickCount() == 2 && stage.getScene() != null) {
        Node node = stage.getScene().getFocusOwner();
        if (node instanceof TextInputControl) {
          setVisible(Visiblity.SHOW, (TextInputControl) node);
        }
      }
    });
  }

  public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
    getKeyBoard().setOnKeyboardCloseButton(value);
  }

  void setVisible(Visiblity visible) {
    setVisible(visible, null);
  }

  void setVisible(final Visiblity visible, final TextInputControl textNode) {

    if ((visible == Visiblity.POS || visible == Visiblity.SHOW) && textNode != null) {
      Map<String, String> vkProps = getVkProperties(textNode);
      if (vkProps.isEmpty()) {
        getKeyBoard().setKeyboardType(KeyboardType.TEXT);
      } else {
        getKeyBoard().setKeyboardType(vkProps.getOrDefault(VK_TYPE, VK_TYPE_TEXT));
        if (vkProps.containsKey(VK_LOCALE)) {
          getKeyBoard().switchLocale(new Locale(vkProps.get(VK_LOCALE)));
        }
      }

      Bounds textNodeBounds = textNode.localToScreen(textNode.getBoundsInLocal());
      Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
      if (textNodeBounds.getMinX() + getWidth() > screenBounds.getMaxX()) {
        setX(screenBounds.getMaxX() - getWidth());
      } else {
        setX(textNodeBounds.getMinX());
      }
      if (textNodeBounds.getMaxY() + getHeight() > screenBounds.getMaxY()) {
        setY(textNodeBounds.getMinY() - getHeight() - offsetProperty.get());
      } else {
        setY(textNodeBounds.getMaxY() + offsetProperty.get());
      }
    }

    if (visible == Visiblity.POS || visible == Visiblity.HIDE && !isShowing()) {
      return;
    }
    if (animation != null) {
      animation.stop();
    } else {
      animation = new FadeTransition(Duration.millis(100), getKeyBoard());
      animation.setOnFinished(e -> {
        if (animation.toValueProperty().get() == 0.0) {
          KeyBoardPopup.this.hide();
        }
      });
    }
    animation.setFromValue(visible == Visiblity.SHOW ? 0.0 : 1.0);
    animation.setToValue(visible == Visiblity.SHOW ? 1.0 : 0.0);

    if (visible == Visiblity.SHOW && !isShowing()) {
      // initial start
      super.show(owner != null ? owner.getWindow() : getOwnerWindow());
    }
    animation.playFromStart();
  }

  private Map<String, String> getVkProperties(Node node) {
    if (node.hasProperties()) {
      Map<String, String> vkProps = new HashMap<>(3);
      node.getProperties().forEach((key, value) -> {
        if (key.toString().startsWith("vk")) {
          vkProps.put(String.valueOf(key), String.valueOf(value));
        }
      });
      return vkProps;
    }
    if (node.getParent() != null && node.getParent().hasProperties()) {
      Map<String, String> vkProps = new HashMap<>(3);
      node.getParent().getProperties().forEach((key, value) -> {
        if (key.toString().startsWith("vk")) {
          vkProps.put(String.valueOf(key), String.valueOf(value));
        }
      });
      return vkProps;
    }
    return Collections.emptyMap();

  }

  public final DoubleProperty offsetProperty() {
    return offsetProperty;
  }

  public final void setOffset(double offsetProperty) {
    offsetProperty().set(offsetProperty);
  }

}
