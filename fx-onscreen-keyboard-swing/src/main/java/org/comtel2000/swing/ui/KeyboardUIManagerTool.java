/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.swing.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.util.Duration;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.keyboard.control.VkProperties;
import org.comtel2000.swing.control.KeyBoardWindow;

/*******************************************************************************
 * Copyright (c) 2025 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

public class KeyboardUIManagerTool {

  private KeyboardUIManagerTool() {}

  /**
   * install listener to basic UI text components
   *
   * @param fl Component {@link FocusListener}
   * @param ml Component {@link MouseListener}
   */
  private static void installKeyboardDefaults(FocusListener fl, MouseListener ml) {

    UIManager.put("TextFieldUI", KeyboardTextFieldUI.class.getName());
    UIManager.put("PasswordFieldUI", KeyboardPasswordFieldUI.class.getName());
    UIManager.put("TextAreaUI", KeyboardTextAreaUI.class.getName());
    UIManager.put("EditorPaneUI", KeyboardEditorPaneUI.class.getName());

    KeyboardTextFieldUI.setFocusListener(fl);
    KeyboardTextFieldUI.setMouseListener(ml);

    KeyboardPasswordFieldUI.setFocusListener(fl);
    KeyboardPasswordFieldUI.setMouseListener(ml);

    KeyboardTextAreaUI.setFocusListener(fl);
    KeyboardTextAreaUI.setMouseListener(ml);

    KeyboardEditorPaneUI.setFocusListener(fl);
    KeyboardEditorPaneUI.setMouseListener(ml);
  }

  /**
   * Register {@link FocusListener} and {@link MouseListener}
   *
   * @param callback {@link EventCallback}
   * @see #installKeyboardDefaults(FocusListener, MouseListener)
   */
  public static void installKeyboardDefaults(EventCallback callback) {
    installKeyboardDefaults(
        createFocusListener(callback), createMouseDoubleClickListener(callback));
  }

  public static void installKeyboardDefaults(final KeyBoardWindow window) {
    SwingCallback callback = new SwingCallback(window);
    installKeyboardDefaults(
        createFocusListener(callback), createMouseDoubleClickListener(callback));
  }

  private static FocusListener createFocusListener(EventCallback c) {
    return new FocusListener() {
      @Override
      public void focusLost(FocusEvent e) {
        c.call(null, false);
      }

      @Override
      public void focusGained(FocusEvent e) {
        c.call(e.getComponent(), true);
      }
    };
  }

  private static MouseListener createMouseDoubleClickListener(EventCallback c) {
    return new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          c.call(e.getComponent(), true);
        }
      }
    };
  }

  static class SwingCallback implements EventCallback {

    private final KeyBoardWindow window;
    private Component initPosition;
    private Transition transition;

    SwingCallback(KeyBoardWindow window) {
      this.window = window;
    }

    @Override
    public void call(final Component comp, final boolean show) {
      window
          .getKeyBoardPopup()
          .ifPresentOrElse(popup -> call(popup, comp, show), () -> initPosition = comp);
    }

    private void call(final KeyBoardPopup popup, final Component comp, final boolean show) {
      Component location;

      if (show && comp == null) {
        // recover cached component
        location = initPosition;
      } else {
        location = comp;
      }
      Platform.runLater(() -> callUI(popup, show, location));
    }

    private void callUI(final KeyBoardPopup popup, final boolean show, Component location) {
      if (show && location != null) {
        popup.setX(location.getLocationOnScreen().getX());
        popup.setY(location.getLocationOnScreen().getY() + location.getHeight());

        if (location instanceof JTextComponent textComponent) {
          Object type = textComponent.getDocument().getProperty(VkProperties.VK_TYPE);
          if (type != null) {
            popup.getKeyBoard().setKeyboardType((String) type);
          } else {
            popup.getKeyBoard().setKeyboardType(KeyboardType.TEXT);
          }
          Object locale = textComponent.getDocument().getProperty(VkProperties.VK_LOCALE);
          if (locale != null) {
            popup.getKeyBoard().switchLocale(new Locale((String) locale));
          }
        }
      }

      if (transition == null) {
        transition = new FadeTransition(Duration.seconds(0.1), popup.getKeyBoard());
      }
      updateTransitionUI(popup, show);
    }

    private void updateTransitionUI(final KeyBoardPopup popup, final boolean show) {
      if (show) {
        if (popup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
          return;
        }
        transition.setOnFinished(
            evt -> { // ignore
            });
        transition.stop();

        popup.getKeyBoard().setOpacity(0.0);
        popup.setVisible(true);
        ((FadeTransition) transition).setFromValue(0.0f);
        ((FadeTransition) transition).setToValue(1.0f);
        transition.play();

      } else {
        if (!popup.isVisible() && transition.getStatus() == Animation.Status.STOPPED) {
          return;
        }
        transition.stop();
        transition.setOnFinished(evt -> popup.setVisible(false));

        ((FadeTransition) transition).setFromValue(1.0f);
        ((FadeTransition) transition).setToValue(0.0f);
        transition.play();
      }
    }
  }
}
