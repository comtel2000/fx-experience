package org.comtel2000.swing.control;

/*
 * #%L
 * fx-onscreen-keyboard-swing
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */


import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardBuilder;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.robot.IRobot;
import org.comtel2000.swing.robot.AWTRobotHandler;

import javafx.application.Platform;
import javafx.util.Builder;

public class KeyBoardWindowBuilder implements Builder<KeyBoardWindow> {

	private final CountDownLatch latch = new CountDownLatch(1);

	private final KeyBoardBuilder kb;

	KeyBoardWindowBuilder() {
		kb = KeyBoardBuilder.create();
	}

	/**
	 * create instance 
	 * @return this
	 */
	public static KeyBoardWindowBuilder create() {
		return new KeyBoardWindowBuilder();
	}

	/**
	 * @see {@link KeyBoardBuilder#layerPath(Path)}
	 * @param path
	 * @return this
	 */
	public KeyBoardWindowBuilder layerPath(Path path) {
		kb.layerPath(path);
		return this;
	}

	/**
	 * @see {@link KeyBoardBuilder#initLocale(Locale)}
	 * @param locale
	 * @return this
	 */
	public KeyBoardWindowBuilder initLocale(Locale locale) {
		kb.initLocale(locale);
		return this;
	}

	/**
	 * @see {@link KeyBoardBuilder#initScale(double)}
	 * @param scale
	 * @return this
	 */
	public KeyBoardWindowBuilder initScale(double scale) {
		kb.initScale(scale);
		return this;
	}

	/**
	 * @see {@link KeyBoardBuilder#addIRobot(IRobot)}
	 * @param robot default {@link AWTRobotHandler}
	 * @return this
	 */
	public KeyBoardWindowBuilder addIRobot(IRobot robot) {
		kb.addIRobot(robot);
		return this;
	}

	/**
	 * @see {@link KeyBoardBuilder#layer(DefaultLayer)}
	 * @param layer
	 * @return this
	 */
	public KeyBoardWindowBuilder layer(DefaultLayer layer) {
		kb.layer(layer);
		return this;
	}

	/**
	 * @see {@link KeyBoardBuilder#style(String)}
	 * @param css Style
	 * @return this
	 */
	public KeyBoardWindowBuilder style(String css) {
		kb.style(css);
		return this;
	}

	/**
	 * build and wait for finalize FX instantiation
	 * 
	 * @return KeyBoardWindow window
	 */
	@Override
	public KeyBoardWindow build() {
		final KeyBoardWindow window = new KeyBoardWindow();
		Platform.runLater(() -> {
			KeyBoardPopup popup = new KeyBoardPopup(kb.build());
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
