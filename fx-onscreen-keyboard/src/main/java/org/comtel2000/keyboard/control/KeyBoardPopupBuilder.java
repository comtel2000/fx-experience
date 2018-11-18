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

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.util.Builder;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;
import org.comtel2000.keyboard.robot.IRobot;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public class KeyBoardPopupBuilder implements Builder<KeyBoardPopup> {

  private final KeyBoardBuilder kb;
  private double offset = -1;
  private EventHandler<? super Event> closeEventHandler;

  protected KeyBoardPopupBuilder() {
    kb = KeyBoardBuilder.create();
  }

  public static KeyBoardPopupBuilder create() {
    return new KeyBoardPopupBuilder();
  }

  public KeyBoardPopupBuilder layerPath(Path path) {
    kb.layerPath(path);
    return this;
  }

  public KeyBoardPopupBuilder initLocale(Locale locale) {
    kb.initLocale(locale);
    return this;
  }

  public KeyBoardPopupBuilder initScale(double scale) {
    kb.initScale(scale);
    return this;
  }

  public KeyBoardPopupBuilder addIRobot(IRobot robot) {
    kb.addIRobot(robot);
    return this;
  }

  public KeyBoardPopupBuilder layer(DefaultLayer l) {
    kb.layer(l);
    return this;
  }

  public KeyBoardPopupBuilder style(String css) {
    kb.style(css);
    return this;
  }

  public KeyBoardPopupBuilder offset(double offset) {
    this.offset = offset;
    return this;
  }

  public KeyBoardPopupBuilder onKeyboardCloseButton(EventHandler<? super Event> handler) {
    closeEventHandler = handler;
    return this;
  }

  @Override
  public KeyBoardPopup build() {
    KeyBoardPopup popup = new KeyBoardPopup(kb.build());
    if (offset > -1) {
      popup.setOffset(offset);
    }
    popup.setOnKeyboardCloseButton(Objects.requireNonNullElseGet(closeEventHandler, () -> e -> popup.setVisible(Visibility.HIDE)));
    return popup;
  }

}
