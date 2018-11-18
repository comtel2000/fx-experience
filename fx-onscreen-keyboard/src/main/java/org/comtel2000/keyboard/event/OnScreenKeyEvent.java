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
package org.comtel2000.keyboard.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;
import org.comtel2000.keyboard.control.KeyButton;

class OnScreenKeyEvent extends InputEvent {

  private static final EventType<? super Event> ANY;
  private static final EventType<? super Event> LONG_PRESSED;
  private static final EventType<? super Event> SHORT_PRESSED;
  private static final long serialVersionUID = 65116620766495525L;

  static {
    ANY = new EventType<>(Event.ANY, "KB_PRESSED");
    LONG_PRESSED = new EventType<>(ANY, "KB_PRESSED_LONG");
    SHORT_PRESSED = new EventType<>(ANY, "KB_PRESSED_SHORT");
  }

  public OnScreenKeyEvent(EventType<? extends InputEvent> type) {
    super(type);
  }

  public OnScreenKeyEvent(KeyButton button, EventType<? extends InputEvent> type) {
    super(button, button, type);

  }

  @Override
  public String toString() {
    return "KeyButtonEvent [" + "source = " + getSource() + ", target = " + getTarget()
        + ", eventType = " + getEventType() + ", consumed = " + isConsumed() + "]";
  }

}
