package org.comtel2000.swing.control;

import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.comtel2000.keyboard.control.KeyBoardPopup;

import javax.swing.*;
import java.util.Optional;

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

/**
 * Swing window wrapper class for {@link KeyBoardPopup}
 *
 * @author comtel
 */
public class KeyBoardWindow extends JWindow {

  public final static EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = (event) -> {
    if (event.getSource() instanceof Node) {
      ((Node) event.getSource()).getScene().getWindow().hide();
    }
  };
  private static final long serialVersionUID = 1564988010984549166L;
  private final JFXPanel jfxPanel;
  private KeyBoardPopup popup;

  protected KeyBoardWindow() {
    super();
    setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    setFocusable(false);
    setBackground(null);

    getContentPane().add(jfxPanel = new JFXPanel());
    jfxPanel.setFocusable(false);
    jfxPanel.setOpaque(false);
  }

  /**
   * must run in FxApplicationThread
   *
   * @param popup Keyboard popup
   */
  protected void createScene(final KeyBoardPopup popup) {
    this.popup = popup;
    Scene scene = new Scene(new Group(), 0, 0);
    jfxPanel.setScene(scene);
    popup.registerScene(scene);
  }

  public Optional<KeyBoardPopup> getKeyBoardPopup() {
    return Optional.ofNullable(popup);
  }

}
