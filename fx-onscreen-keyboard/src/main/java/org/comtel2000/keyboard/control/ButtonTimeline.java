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

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.util.Duration;

abstract class ButtonTimeline {

  protected final Timeline timeline;
  private Node owner;
  protected Consumer<ActionEvent> handler;

  protected ButtonTimeline(double delay) {
    timeline = !Double.isNaN(delay) && delay > 0.0d ? new Timeline(new KeyFrame(Duration.millis(delay), this::handle)) : new Timeline();
  }

  ButtonTimeline withOwner(Node owner, Consumer<ActionEvent> handler) {
    if (!isOwner(owner)) {
      timeline.stop();
      this.owner = owner;
    }
    this.handler = handler;
    return this;
  }

  boolean isOwner(Node node) {
    return owner != null && node == owner;
  }

  boolean isEnabled() {
    return !timeline.getKeyFrames().isEmpty();
  }

  void playFromStart() {
    timeline.playFromStart();
  }

  void stop() {
    timeline.stop();
  }

  Status getStatus() {
    return timeline.getStatus();
  }

  double getCurrentRate() {
    return timeline.getCurrentRate();
  }

  void handle(ActionEvent event) {
    if (handler != null) {
      handler.accept(event);
    }
  }
}
