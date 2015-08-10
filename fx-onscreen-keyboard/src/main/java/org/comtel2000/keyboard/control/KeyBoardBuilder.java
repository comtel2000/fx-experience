package org.comtel2000.keyboard.control;

/*
 * #%L
 * fx-onscreen-keyboard
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.comtel2000.keyboard.robot.IRobot;

import javafx.util.Builder;

public class KeyBoardBuilder implements Builder<KeyboardPane> {

	private Path layerPath;

	private DefaultLayer defaultLayer;

	private Locale initLocale;

	private List<IRobot> iRobots;

	private String style;

	private double initScale = 0.0;

	protected KeyBoardBuilder() {
		iRobots = new ArrayList<>(2);
	}

	public static KeyBoardBuilder create() {
		return new KeyBoardBuilder();
	}

	public KeyBoardBuilder layerPath(Path path) {
		layerPath = path;
		return this;
	}

	public KeyBoardBuilder layer(DefaultLayer layer) {
		defaultLayer = layer;
		return this;
	}

	public KeyBoardBuilder style(String css) {
		style = css;
		return this;
	}

	public KeyBoardBuilder initLocale(Locale locale) {
		initLocale = locale;
		return this;
	}

	public KeyBoardBuilder initScale(double scale) {
		initScale = scale;
		return this;
	}

	public KeyBoardBuilder addIRobot(IRobot robot) {
		iRobots.add(robot);
		return this;
	}

	@Override
	public KeyboardPane build() {

		KeyboardPane kb = new KeyboardPane();

		if (style != null) {
			kb.setKeyBoardStyle(style);
		}
		if (initLocale != null) {
			kb.setLocale(initLocale);
		}
		if (layerPath != null) {
			kb.setLayerPath(layerPath);
		}

		if (defaultLayer != null) {
			kb.setLayer(defaultLayer);
		}
		if (initScale > 0.0) {
			kb.setScale(initScale);
		}
		for (IRobot robot : iRobots) {
			kb.addRobotHandler(robot);
		}
		iRobots.clear();
		iRobots = null;

		try {
			kb.load();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return kb;
	}

}
