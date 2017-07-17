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

package org.comtel2000.swing.control;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import org.comtel2000.keyboard.control.KeyboardBuilder;
import org.comtel2000.keyboard.control.KeyboardLayer;
import org.comtel2000.keyboard.control.KeyboardPopup;
import org.comtel2000.keyboard.robot.IRobot;
import org.comtel2000.swing.robot.AWTRobotHandler;

import javafx.application.Platform;
import javafx.util.Builder;

/**
 * Swing {@link KeyboardWindow} builder
 *
 * @author comtel
 *
 */
public class KeyboardWindowBuilder implements Builder<KeyboardWindow> {

  private final KeyboardBuilder kb;

  KeyboardWindowBuilder() {
    kb = KeyboardBuilder.create();
  }

  /**
   * create new instance
   *
   * @return this
   */
  public static KeyboardWindowBuilder create() {
    return new KeyboardWindowBuilder();
  }

  /**
   * The own layout location
   *
   * @param path the layer location
   * @return this
   */
  public KeyboardWindowBuilder layerPath(Path path) {
    kb.layerPath(path);
    return this;
  }

  /**
   * The initial {@link Locale}
   *
   * @param locale initial locale
   * @return this
   */
  public KeyboardWindowBuilder initLocale(Locale locale) {
    kb.initLocale(locale);
    return this;
  }

  /**
   * The initial keyboard scale
   *
   * @param scale initial size
   * @return this
   */
  public KeyboardWindowBuilder initScale(double scale) {
    kb.initScale(scale);
    return this;
  }

  /**
   * The robot adapter default {@link AWTRobotHandler}
   *
   * @param robot default {@link AWTRobotHandler}
   * @return this
   */
  public KeyboardWindowBuilder addIRobot(IRobot robot) {
    kb.addIRobot(robot);
    return this;
  }

  /**
   * The embedded layout
   *
   * @param layer embedded layout layer
   * @return this
   */
  public KeyboardWindowBuilder layer(KeyboardLayer layer) {
    kb.layer(layer);
    return this;
  }

  /**
   * The style location and name
   *
   * @param css style sheet
   * @return this
   */
  public KeyboardWindowBuilder style(String css) {
    kb.style(css);
    return this;
  }

  /**
   * build and wait for finalize FX instantiation
   *
   * @return KeyboardWindow window
   */
  @Override
  public KeyboardWindow build() {
    final KeyboardWindow window = new KeyboardWindow();
    final CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      KeyboardPopup popup = new KeyboardPopup(kb.build());
      window.createScene(popup);
      latch.countDown();
    });
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return window;
  }

}
