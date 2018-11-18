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

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FXOK {

  private static KeyBoardPopup popup;

  private FXOK() {
  }

  public static void registerPopup(KeyBoardPopup p) {
    popup = p;
  }

  public static void setVisible(final Visibility visible, final TextInputControl textNode) {
    if (popup == null) {
      return;
    }
    popup.setVisible(visible, textNode);
  }

  public static Map<String, String> getVkProperties(Node node) {
    if (node.hasProperties()) {
      Map<String, String> vkProps = new HashMap<>();
      node.getProperties().forEach((key, value) -> {
        if (key.toString().startsWith("vk")) {

          vkProps.put(key.toString(), String.valueOf(value));
        }
      });
      return vkProps;
    }
    if (node.getParent() != null && node.getParent().hasProperties()) {
      Map<String, String> vkProps = new HashMap<>();
      node.getParent().getProperties().forEach((key, value) -> {
        if (key.toString().startsWith("vk")) {
          vkProps.put(key.toString(), String.valueOf(value));
        }
      });
      return vkProps;
    }
    return Collections.emptyMap();

  }

  public static KeyBoardPopup getPopup() {
    return popup;
  }

}
