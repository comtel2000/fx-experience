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

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.comtel2000.keyboard.FXOK;

import com.sun.javafx.css.StyleManager;

import javafx.animation.FadeTransition;
import javafx.application.Application;
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

  private static final String STYLE_CSS = "/css/KeyboardTextInputSkin.css";

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

  private double _offset = 5d;

  /** default vertical keyboard to text component offset */
  private DoubleProperty offset;

  private FadeTransition animation;

  public static final EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = (event) -> {
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
   * @param scene
   *          {@link Scene} to connect with the keyboard
   *
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
   *
   * @see #addGlobalFocusListener()
   */
  public void addFocusListener(final Scene scene, boolean doNotOpen) {
    registerScene(scene);
    scene.focusOwnerProperty().addListener((value, n1, n2) -> {
      if (n2 instanceof TextInputControl) {
        setVisible(doNotOpen ? Visiblity.POS : Visiblity.SHOW, (TextInputControl) n2);
      } else if (n2 instanceof Parent) {
        TextInputControl control = findTextInputControl((Parent) n2);
        setVisible(
            (control != null ? (doNotOpen ? Visiblity.POS : Visiblity.SHOW) : Visiblity.HIDE),
            control);
      } else {
        setVisible(Visiblity.HIDE);
      }
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
  public void addGlobalFocusListener() {
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
  public void addGlobalFocusListener(String url) {
    FXOK.registerPopup(this);
    Application.setUserAgentStylesheet(url);
    StyleManager.getInstance().addUserAgentStylesheet(getUserAgentStyleSheet());
  }

  public static String getUserAgentStyleSheet() {
    return KeyBoardPopup.class.getResource(STYLE_CSS).toExternalForm();
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

  /**
   * Adds a mouse listener to the Stage and open the keyboards on 'double' click a
   * {@link TextInputControl}
   *
   * @param stage
   *          {@link Stage} to connect with the keyboard
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

    boolean vkDisabled = false;
    Map<String, Object> vkProps = FXOK.getVkProperties(textNode);
    if (vkProps.containsKey(VK_DISABLED)) {
      vkDisabled = (boolean) vkProps.get(VK_DISABLED);
    }

    if ((visible == Visiblity.POS || visible == Visiblity.SHOW) && textNode != null) {
      if (vkProps.isEmpty()) {
        getKeyBoard().setKeyboardType(DefaultKeyboardType.TEXT);
      } else {
        getKeyBoard().setKeyboardType((IKeyboardType) vkProps.getOrDefault(VK_TYPE, DefaultKeyboardType.TEXT));
        if (vkProps.containsKey(VK_LOCALE)) {
          getKeyBoard().switchLocale(new Locale(vkProps.get(VK_LOCALE).toString()));
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

    if (vkDisabled){
      hide();
      return;
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

  /**
   * The vertical keyboard to text component offset
   *
   * @return offset
   */
  public final double getOffset() {
    return offset == null ? _offset : offset.get();
  }

  /**
   * Set the vertical keyboard to text component offset
   *
   * @param value
   *          offset
   */
  public final void setOffset(double value) {
    if (offset == null) {
      _offset = value;
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
      offset = new SimpleDoubleProperty(this, "offset", _offset);
    }
    return offset;
  }

}
