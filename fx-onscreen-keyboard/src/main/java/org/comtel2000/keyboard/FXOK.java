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
package org.comtel2000.keyboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.keyboard.control.VkProperties;

import javafx.animation.FadeTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.stage.Screen;
import javafx.util.Duration;

public class FXOK implements VkProperties {

  public enum Visiblity {
    /** Set position and visible true */
    SHOW,

    /** Set visible false */
    HIDE,

    /** Set positioning only if visible true */
    POS
  }


  private static KeyBoardPopup popup;


  private static FadeTransition animation;

  private FXOK() {}

  public static void registerPopup(KeyBoardPopup p) {
    popup = p;
  }

  public static void setVisible(Visiblity visible) {
    setVisible(visible, null);
  }

  public static void setVisible(final Visiblity visible, final TextInputControl textNode) {
    if (popup == null) {
      return;
    }
    if ((visible == Visiblity.POS || visible == Visiblity.SHOW) && textNode != null) {
      Map<String, String> vkProps = getVkProperties(textNode);
      if (vkProps.isEmpty()) {
        popup.getKeyBoard().setKeyboardType(KeyboardType.TEXT);
      } else {
        popup.getKeyBoard().setKeyboardType(vkProps.getOrDefault(VK_TYPE, VK_TYPE_TEXT));
        if (vkProps.containsKey(VK_LOCALE)) {
          popup.getKeyBoard().switchLocale(new Locale(vkProps.get(VK_LOCALE)));
        }
      }

      Bounds textNodeBounds = textNode.localToScreen(textNode.getBoundsInLocal());
      Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
      if (textNodeBounds.getMinX() + popup.getWidth() > screenBounds.getMaxX()) {
        popup.setX(screenBounds.getMaxX() - popup.getWidth());
      } else {
        popup.setX(textNodeBounds.getMinX());
      }
      if (textNodeBounds.getMaxY() + popup.getHeight() > screenBounds.getMaxY()) {
        popup.setY(textNodeBounds.getMinY() - popup.getHeight() - popup.getOffset());
      } else {
        popup.setY(textNodeBounds.getMaxY() + popup.getOffset());
      }
    }

    if (visible == Visiblity.POS || visible == Visiblity.HIDE && !popup.isShowing()) {
      return;
    }
    if (animation != null) {
      animation.stop();
    } else {
      animation = new FadeTransition(Duration.millis(100), popup.getKeyBoard());
      animation.setOnFinished(e -> {
        if (animation.toValueProperty().get() == 0.0) {
          popup.hide();
        }
      });
    }
    animation.setFromValue(visible == Visiblity.SHOW ? 0.0 : 1.0);
    animation.setToValue(visible == Visiblity.SHOW ? 1.0 : 0.0);

    if (visible == Visiblity.SHOW && !popup.isShowing()) {
      // initial start
      popup.show(textNode.getScene().getWindow());
    }
    animation.playFromStart();
  }

  public static Map<String, String> getVkProperties(Node node) {
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

  public static void updateVisibilty(Scene scene, TextInputControl textInput) {
    if (textInput.isEditable() && textInput.isFocused()) {
      setVisible(Visiblity.SHOW, textInput);
    } else if (scene == null || scene.getWindow() == null || !scene.getWindow().isFocused()
        || !(scene.getFocusOwner() instanceof TextInputControl && ((TextInputControl) scene.getFocusOwner()).isEditable())) {
      setVisible(Visiblity.HIDE, textInput);
    } else {
      setVisible(Visiblity.POS, textInput);
    }
  }

}
