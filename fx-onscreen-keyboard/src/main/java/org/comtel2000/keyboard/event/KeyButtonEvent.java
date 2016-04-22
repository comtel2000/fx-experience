package org.comtel2000.keyboard.event;

import org.comtel2000.keyboard.control.KeyButton;

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

import javafx.event.Event;
import javafx.event.EventType;

public class KeyButtonEvent extends Event {

  private static final long serialVersionUID = 647301812232489628L;

  public static final EventType<Event> ANY;

  public static final EventType<Event> LONG_PRESSED;

  public static final EventType<Event> SHORT_PRESSED;

  public KeyButtonEvent(EventType<Event> type) {
    super(type);
  }

  public KeyButtonEvent(KeyButton button, EventType<Event> type) {
    super(button, button, type);
  }

  @Override
  public String toString() {
    return new StringBuilder("KeyButtonEvent [")
            .append("source = ").append(getSource())
            .append(", target = ").append(getTarget())
            .append(", eventType = ").append(getEventType())
            .append(", consumed = ").append(isConsumed()).append("]").toString();
  }

  static {
    ANY = new EventType<Event>(Event.ANY, "KB_PRESSED");
    LONG_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_LONG");
    SHORT_PRESSED = new EventType<Event>(ANY, "KB_PRESSED_SHORT");
  }

}
