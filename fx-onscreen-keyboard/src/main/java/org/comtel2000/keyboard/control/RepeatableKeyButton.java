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

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;

class RepeatableKeyButton extends KeyButton {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RepeatableKeyButton.class);

  // key repeat rate (cps)
  private static double KEY_REPEAT_RATE = 25;
  private static final double KEY_REPEAT_RATE_MIN = 2;
  private static final double KEY_REPEAT_RATE_MAX = 50;

  private final long REPEAT_DELAY = 40;

  private Timeline repeatDelay;

  static {
    AccessController.doPrivileged((PrivilegedAction<Void>) () -> {

      String s = System.getProperty("org.comtel2000.keyboard.repeatRate");
      if (s != null) {
        Double rate = Double.valueOf(s);
        if (rate <= 0) {
          // disable key repeat
          KEY_REPEAT_RATE = 0;
        } else {
          KEY_REPEAT_RATE = Math.min(Math.max(rate, KEY_REPEAT_RATE_MIN), KEY_REPEAT_RATE_MAX);
        }
      }
      return null;
    });
  }

  RepeatableKeyButton() {
    super();
    getStyleClass().add("repeatable-button");
  }

  @Override
  public boolean isRepeatable() {
      return true;
  }

    @Override
    protected void initEventListener(double delay) {

        if (KEY_REPEAT_RATE > 0) {
            buttonDelay = new Timeline(new KeyFrame(Duration.millis(delay), event -> {
                fireShortPressed();
                // buttonDelay.playFrom(buttonDelay.getCycleDuration().subtract(Duration.millis(REPEAT_DELAY)));
                repeatDelay.playFromStart();
            }));
            repeatDelay = new Timeline(
                    new KeyFrame(Duration.millis(1000.0 / KEY_REPEAT_RATE), event -> fireShortPressed()));
            repeatDelay.setCycleCount(Animation.INDEFINITE);
            setOnDragDetected(e -> {
                logger.trace("{} drag detected", getKeyCode());
                buttonDelay.stop();
                e.consume();
            });

            setOnMousePressed(e -> {
                logger.trace("{} pressed", getKeyCode());
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    if (!isMovable()) {
                        fireShortPressed();
                    }
                    buttonDelay.playFromStart();
                }
                e.consume();
            });

            setOnMouseReleased(e -> {
                logger.trace("{} released", getKeyCode());
                if (isMovable() && buttonDelay.getStatus() == Status.RUNNING) {
                    fireShortPressed();
                }
                buttonDelay.stop();
                repeatDelay.stop();
                setFocused(false);
                e.consume();
            });
        }
    }

}
