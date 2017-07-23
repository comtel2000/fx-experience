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

package org.comtel2000.keyboard;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.comtel2000.keyboard.control.KeyboardPopup;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.keyboard.control.VkProperties;

import com.sun.javafx.css.StyleManager;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.MouseEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("restriction")
public class FXOK {

  private static KeyboardPopup popup;

  private static final List<Scene> REGISTERED_SCENES = new CopyOnWriteArrayList<>();

  private static final GlobalFocusListener FOCUS_LISTENER = new GlobalFocusListener();

  private static Transition showAnimation;

  private static boolean typeChanged;

  private FXOK() {
  }

  enum Control {
    /** Set position and visible true */
    SHOW,

    /** Set visible false */
    HIDE,

    /** Set positioning only if visible true */
    POS
  }

  private static Transition getShowTransition(boolean show) {
    if (showAnimation == null) {
      showAnimation = new FadeTransition(Duration.millis(150), popup.getKeyboard());
      showAnimation.setAutoReverse(false);
      showAnimation.setCycleCount(1);
      showAnimation.setDelay(Duration.ZERO);
      showAnimation.setOnFinished(e -> {
        if (((FadeTransition) showAnimation).toValueProperty().intValue() == 0) {
          popup.hide();
        }
      });
    } else {
      showAnimation.stop();
      popup.getKeyboard().setOpacity(show ? 0.0 : 1.0);
    }
    ((FadeTransition) showAnimation).setFromValue(show ? 0.0 : 1.0);
    ((FadeTransition) showAnimation).setToValue(show ? 1.0 : 0.0);
    showAnimation.setInterpolator(show ? Interpolator.EASE_OUT : Interpolator.EASE_IN);
    return showAnimation;
  }

  public static void registerScene(Scene scene) {
    if (scene == null || REGISTERED_SCENES.contains(scene)) {
      return;
    }
    REGISTERED_SCENES.add(scene);
    scene.focusOwnerProperty().addListener(FOCUS_LISTENER);
  }

  public static void registerPopup(KeyboardPopup p) {
    popup = p;
  }

  public static void setHideOnCloseButton() {
    popup.getKeyboard().setOnKeyboardCloseButton(e -> fire(Control.HIDE));
  }

  static void fire(final Control control) {
    fire(control, null);
  }

  static void fire(final Control ctrl, final TextInputControl textNode) {
    if (popup == null) {
      return;
    }
    if (ctrl == Control.HIDE) {
      hide();
      return;
    }
    if (textNode == null) {
      return;
    }
    typeChanged = false;
    updateVkProperties(textNode, 1);

    if (ctrl == Control.SHOW) {
      moveTransition(textNode);
      showTransition(textNode);
    } else if (ctrl == Control.POS) {
      moveTransition(textNode);
    }

  }

  private static void hide() {
    if (!popup.isShowing()) {
      return;
    }
    getShowTransition(false).play();
  }

  private static void showTransition(final TextInputControl textNode) {
    if (popup.getOwnerWindow() != textNode.getScene().getWindow()) {
      popup.hide();
    }
    if (!popup.isShowing()) {
      popup.show(textNode.getScene().getWindow());
    }
    getShowTransition(true).playFromStart();
  }

  private static void moveTransition(final TextInputControl textNode) {
    double x;
    double y;
    Bounds textNodeBounds = textNode.localToScreen(textNode.getBoundsInLocal());
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    if (textNodeBounds.getMinX() + popup.getWidth() > screenBounds.getMaxX()) {
      x = screenBounds.getMaxX() - popup.getWidth();
    } else {
      x = textNodeBounds.getMinX();
    }
    if (textNodeBounds.getMaxY() + popup.getHeight() > screenBounds.getMaxY()) {
      y = textNodeBounds.getMinY() - popup.getHeight() - popup.getOffset();
    } else {
      y = textNodeBounds.getMaxY() + popup.getOffset();
    }
    if (popup.getX() != x) {
      popup.setX(x);
    }
    if (popup.getY() != y) {
      popup.setY(y);
    }
  }

  /**
   * Adds a FocusListener to Scene and open keyboard on {@link TextInputControl}
   *
   * @param scene
   *          {@link Scene} to connect with the keyboard
   *
   * @see #addGlobalFocusListener()
   */
  public static void addFocusListener(final Scene scene) {
    addFocusListener(scene, false);
  }

  /**
   * Adds a FocusListener to Scene and open keyboard on {@link TextInputControl}
   *
   * @param scene
   *          {@link Scene} to connect with the keyboard
   * @param doNotOpen
   *          on hidden keyboard do nothing and on showing keyboard move to current component
   *
   * @see #addGlobalFocusListener()
   */
  public static void addFocusListener(final Scene scene, boolean doNotOpen) {
    Objects.requireNonNull(popup, "keyboard not registered");
    popup.registerScene(scene);
    scene.focusOwnerProperty().addListener((value, n1, n2) -> {
      if (n2 instanceof TextInputControl) {
        fire(doNotOpen ? Control.POS : Control.SHOW, (TextInputControl) n2);
        return;
      }
      if (n2 instanceof Parent) {
        TextInputControl control = findTextInputControl((Parent) n2);
        if (control != null) {
          fire(doNotOpen ? Control.POS : Control.SHOW, control);
          return;
        }
      }
      fire(Control.HIDE);
    });
  }

  /**
   * Set the focus listener as user agent stylesheet for the whole application with main theme
   * Application.STYLESHEET_MODENA.
   * 
   * <p>
   * Reference: <a href="https://bugs.openjdk.java.net/browse/JDK-8077918">JDK-8077918</a>
   *
   * @see #addFocusListener(Scene)
   * @see #addGlobalFocusListener(String)
   */
  public static void addGlobalFocusListener() {
    addGlobalFocusListener(Application.STYLESHEET_MODENA);
  }

  /**
   * Set the focus listener as user agent stylesheet for the whole application with given main
   * theme.
   * 
   * <p>
   * Reference: <a href="https://bugs.openjdk.java.net/browse/JDK-8077918">JDK-8077918</a>
   *
   * @param url
   *          of main theme stylesheet
   *
   * @see #addFocusListener(Scene)
   * @see #addGlobalFocusListener()
   */
  public static void addGlobalFocusListener(String url) {
    Application.setUserAgentStylesheet(url);
    String style = FXOK.class.getResource("/css/KeyboardTextInputSkin.css").toExternalForm();
    StyleManager.getInstance().addUserAgentStylesheet(style);
  }

  /**
   * Adds a mouse listener to the Stage and open the keyboards on 'double' click a
   * {@link TextInputControl}
   *
   * @param stage
   *          {@link Stage} to connect with the keyboard
   */
  public static void addDoubleClickEventFilter(final Stage stage) {
    Objects.requireNonNull(stage).addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
      if (event.getClickCount() == 2 && stage.getScene() != null) {
        Node node = stage.getScene().getFocusOwner();
        if (node instanceof TextInputControl) {
          fire(Control.SHOW, (TextInputControl) node);
        }
      }
    });
  }

  /**
   * search for nested input controls like {@code FakeFocusTextField}
   *
   * @see ComboBox#isEditable()
   * @param parent
   *          Parent of TextInputControl
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

  private static void updateVkProperties(Node node, int iterations) {
    if (node.hasProperties()) {
      node.getProperties().forEach(FXOK::updateVk);
    }
    if (!typeChanged && iterations > 0 && node.getParent() != null) {
      updateVkProperties(node.getParent(), iterations - 1);
    }
    if (!typeChanged && popup.getKeyboard().getActiveType() != KeyboardType.TEXT) {
      popup.getKeyboard().setKeyboardType(KeyboardType.TEXT);
    }
  }

  private static void updateVk(Object key, Object value) {
    if (value == null) {
      return;
    }
    if (VkProperties.VK_TYPE.equals(key)) {
      popup.getKeyboard().setKeyboardType(value);
      typeChanged = true;
    } else if (VkProperties.VK_LOCALE.equals(key)) {
      popup.getKeyboard().switchLocale(value);
    }
  }

  private static final class GlobalFocusListener implements ChangeListener<Node> {

    @Override
    public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
      if (newValue instanceof ComboBox) {
        if (oldValue instanceof ComboBox) {
          update(((ComboBox<?>) oldValue).getEditor(), ((ComboBox<?>) newValue).getEditor());
          return;
        }
        update(oldValue, ((ComboBox<?>) newValue).getEditor());
        return;
      }
      if (oldValue instanceof ComboBox) {
        update(((ComboBox<?>) oldValue).getEditor(), newValue);
        return;
      }
      update(oldValue, newValue);
    }

    void update(Node oldValue, Node newValue) {
      if (!(newValue instanceof TextInputControl) || newValue.isDisabled()
          || !((TextInputControl) newValue).isEditable()) {
        fire(Control.HIDE);
        return;
      }
      TextInputControl control = (TextInputControl) newValue;
      if (oldValue instanceof TextInputControl && ((TextInputControl) oldValue).isEditable()) {
        fire(Control.POS, control);
        return;
      }
      fire(Control.SHOW, control);
    }
  }
}
