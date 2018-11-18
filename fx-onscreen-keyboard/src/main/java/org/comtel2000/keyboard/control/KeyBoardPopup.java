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

import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
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
import javafx.stage.*;
import javafx.util.Duration;
import org.comtel2000.keyboard.FXOK;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static org.comtel2000.keyboard.control.VkProperties.*;

/**
 * Helper class to create a {@link KeyboardPane}
 *
 * @author comtel
 */
public class KeyBoardPopup extends Popup {

  public enum Visibility {
    /**
     * Set position and visible true
     */
    SHOW,

    /**
     * Set visible false
     */
    HIDE,

    /**
     * Set positioning only if visible true
     */
    POS
  }

  public static final EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = event -> {
    if (event.getSource() instanceof Node) {
      ((Node) event.getSource()).fireEvent(new WindowEvent(null, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
  };
  private static final String STYLE_CSS = "/css/KeyboardTextInputSkin.css";
  private final KeyboardPane keyboard;

  private boolean doNotOpenHiddenKeyboard = false;

  private final ChangeListener<? super Node> focusChangeListener = (value, n1, n2) -> {
    if (n2 instanceof TextInputControl) {
      setVisible(doNotOpenHiddenKeyboard ? Visibility.POS : Visibility.SHOW, (TextInputControl) n2);
      return;
    }
    if (n2 instanceof Parent) {
      TextInputControl control = findTextInputControl((Parent) n2);
      if (control != null) {
        setVisible(doNotOpenHiddenKeyboard ? Visibility.POS : Visibility.SHOW, control);
        return;
      }
    }
    setVisible(Visibility.HIDE);
  };

  private final EventHandler<? super MouseEvent> doubleClickEventFilter = event -> {
    if (event.getClickCount() == 2 && event.getSource() instanceof Stage) {
      Node node = ((Stage) event.getSource()).getScene().getFocusOwner();
      if (node instanceof TextInputControl) {
        setVisible(Visibility.SHOW, (TextInputControl) node);
      }
    }
  };

  private Scene owner;

  private double offsetValue = 5d;

  /**
   * default vertical keyboard to text component offset
   */
  private DoubleProperty offset;

  private FadeTransition animation;

  public KeyBoardPopup(final KeyboardPane panel) {
    keyboard = Objects.requireNonNull(panel);
    getContent().add(keyboard);
  }

  public static String getUserAgentStyleSheet() {
    return KeyBoardPopup.class.getResource(STYLE_CSS).toExternalForm();
  }

  /**
   * search for nested input controls like {@code FakeFocusTextField}
   *
   * @param parent
   * @return embedded TextInputControl or null
   * @see ComboBox#isEditable()
   */
  private static TextInputControl findTextInputControl(Parent parent) {
    for (Node child : parent.getChildrenUnmodifiable()) {
      if (child instanceof TextInputControl) {
        return (TextInputControl) child;
      }
    }
    return null;
  }

  public final KeyboardPane getKeyBoard() {
    return keyboard;
  }

  public boolean isVisible() {
    return isShowing();
  }

  public void setVisible(boolean visible) {
    setVisible(visible ? Visibility.SHOW : Visibility.HIDE);
  }

  void setVisible(Visibility visible) {
    setVisible(visible, null);
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
   * @param scene
   *          {@link Scene} to connect with the keyboard
   * @see #addGlobalFocusListener()
   */
  public void addFocusListener(final Scene scene) {
    addFocusListener(scene, false);
  }

  /**
   * Adds a FocusListener to Scene and open keyboard on {@link TextInputControl}
   *
   * @param scene
   *          {@link Scene} to connect with the keyboard
   * @param doNotOpen
   *          on hidden keyboard do nothing and on showing keyboard move to current component
   * @see #addGlobalFocusListener()
   */
  public void addFocusListener(final Scene scene, boolean doNotOpen) {
    doNotOpenHiddenKeyboard = doNotOpen;
    registerScene(scene);
    scene.focusOwnerProperty().addListener(focusChangeListener);
  }

  /**
   * Add keyboard popup listener to all open dialogs, stages, etc.
   *
   * @see #addFocusListener(Scene)
   */
  public void addGlobalFocusListener() {
    addGlobalFocusListener(doNotOpenHiddenKeyboard);
  }

  /**
   * Add keyboard popup listener to all open dialogs, stages, etc.
   *
   * @param doNotOpen
   *          on hidden keyboard do nothing and on showing keyboard move to current component
   */
  public void addGlobalFocusListener(boolean doNotOpen) {
    doNotOpenHiddenKeyboard = doNotOpen;
    Window.getWindows().addListener((ListChangeListener.Change<? extends Window> c) -> {
      while (c.next()) {
        if (!c.wasPermutated()) {
          c.getAddedSubList()
              .forEach(win -> win.getScene().focusOwnerProperty().addListener(focusChangeListener));
          c.getRemoved().forEach(
              win -> win.getScene().focusOwnerProperty().removeListener(focusChangeListener));
        }
      }
    });
  }

  /**
   * Adds a mouse listener to the Stage and open the keyboards on 'double' click a
   * {@link TextInputControl}
   *
   * @param stage
   *          {@link Stage} to connect with the keyboard
   *
   * @see #addGlobalDoubleClickEventFilter()
   */
  public void addDoubleClickEventFilter(final Stage stage) {
    stage.addEventFilter(MouseEvent.MOUSE_CLICKED, doubleClickEventFilter);
  }

  /**
   * Adds a mouse listener to all Stage and open the keyboards on 'double' click a
   * {@link TextInputControl}
   *
   * @see #addDoubleClickEventFilter(Stage)
   */
  public void addGlobalDoubleClickEventFilter() {
    Window.getWindows().addListener((ListChangeListener.Change<? extends Window> c) -> {
      while (c.next()) {
        if (!c.wasPermutated()) {
          c.getAddedSubList()
              .forEach(win -> win.addEventFilter(MouseEvent.MOUSE_CLICKED, doubleClickEventFilter));
          c.getRemoved().forEach(
              win -> win.removeEventFilter(MouseEvent.MOUSE_CLICKED, doubleClickEventFilter));
        }
      }
    });
  }

  public void setOnKeyboardCloseButton(EventHandler<? super Event> value) {
    getKeyBoard().setOnKeyboardCloseButton(value);
  }

  public void setVisible(final Visibility visible, final TextInputControl textNode) {

    if ((visible == Visibility.POS || visible == Visibility.SHOW) && textNode != null) {
      Map<String, String> vkProps = FXOK.getVkProperties(textNode);
      if (vkProps.isEmpty()) {
        getKeyBoard().setKeyboardType(KeyboardType.TEXT);
      } else {
        if (VK_STATE_DISABLED.equals(vkProps.get(VK_STATE))) {
          startFade(Visibility.HIDE, textNode);
          return;
        }
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
        setY(textNodeBounds.getMinY() - getHeight() - getOffset());
      } else {
        setY(textNodeBounds.getMaxY() + getOffset());
      }
    }
    startFade(visible, textNode);
  }

  private void startFade(final Visibility visible, final TextInputControl textNode) {
    if (visible == Visibility.POS || visible == Visibility.HIDE && !isShowing()) {
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
    animation.setFromValue(visible == Visibility.SHOW ? 0.0 : 1.0);
    animation.setToValue(visible == Visibility.SHOW ? 1.0 : 0.0);

    if (visible == Visibility.SHOW) {
      // initial start
      Window win;
      if (textNode != null && textNode.getScene() != null) {
        win = textNode.getScene().getWindow();
      } else if (owner != null) {
        win = owner.getWindow();
      } else {
        win = getOwnerWindow();
      }
      if (isShowing() && getOwnerWindow() != win) {
        hide();
      }
      show(win);
    }
    animation.playFromStart();
  }

  /**
   * The vertical keyboard to text component offset
   *
   * @return offset
   */
  public final double getOffset() {
    return offset == null ? offsetValue : offset.get();
  }

  /**
   * Set the vertical keyboard to text component offset
   *
   * @param value
   *          offset
   */
  public final void setOffset(double value) {
    if (offset == null) {
      offsetValue = value;
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
      offset = new SimpleDoubleProperty(this, "offset", offsetValue);
    }
    return offset;
  }

  public ChangeListener<? super Node> getFocusChangeListener() {
    return focusChangeListener;
  }

  public EventHandler<? super MouseEvent> getDoubleClickEventFilter() {
    return doubleClickEventFilter;
  }

}
