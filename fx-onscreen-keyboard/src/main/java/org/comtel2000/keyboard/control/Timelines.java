/*******************************************************************************
 * Copyright (c) 2025 comtel2000
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

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.Node;

class Timelines {

  private RepeatableButtonTimeline repeatableButtonTimeline;
  private LongPressDelayTimeline longPressDelayTimeline;

  ButtonTimeline repeatableButtonTimeline() {
    if (repeatableButtonTimeline == null) {
      repeatableButtonTimeline = new RepeatableButtonTimeline();
    }
    return repeatableButtonTimeline;
  }

  ButtonTimeline longPressDelayTimeline() {
    if (longPressDelayTimeline == null) {
      longPressDelayTimeline = new LongPressDelayTimeline();
    }
    return longPressDelayTimeline;
  }
  
  ButtonTimeline repeatableButtonTimeline(Node owner, Consumer<ActionEvent> handler) {
    return repeatableButtonTimeline().withOwner(owner, handler);
  }

  ButtonTimeline longPressDelayTimeline(Node owner, Consumer<ActionEvent> handler) {
    return longPressDelayTimeline().withOwner(owner, handler);
  }

  void stopRepeatableButtonTimeline() {
    if (repeatableButtonTimeline != null) {
      repeatableButtonTimeline.stop();
    }
  }

  void stopLongPressDelayTimeline() {
    if (longPressDelayTimeline != null) {
      longPressDelayTimeline.stop();
    }
  }

  void stop() {
    stopRepeatableButtonTimeline();
    stopLongPressDelayTimeline();
  }
}
