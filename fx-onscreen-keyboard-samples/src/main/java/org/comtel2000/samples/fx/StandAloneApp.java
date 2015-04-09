package org.comtel2000.samples.fx;

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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Locale;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.swing.robot.NativeAsciiRobotHandler;

public class StandAloneApp extends Application {

	private int posX = 0;

	private int posY = 0;

	@Override
	public void start(Stage stage) throws MalformedURLException, IOException, URISyntaxException {

		stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);

		//KeyBoardPopup popup = KeyBoardPopupBuilder.create().initScale(1.0).initLocale(Locale.ENGLISH).addIRobot(new NativeAsciiRobotHandler()).layer(DefaultLayer.NUMBLOCK).build();

		KeyboardPane kb = new KeyboardPane();

		kb.setScale(1.0);
		kb.setLocale(Locale.ENGLISH);
		kb.setLayer(DefaultLayer.NUMBLOCK);
		kb.addRobotHandler(new NativeAsciiRobotHandler());
		kb.load();
		
		KeyBoardPopup popup = new KeyBoardPopup(kb);

		Scene scene = new Scene(new Group(), 1, 1);
		stage.setOnCloseRequest(e -> System.exit(0));

		stage.setScene(scene);
		stage.show();

		popup.getScene().getWindow().setX(posX);
		popup.getScene().getWindow().setY(posY);

		popup.show(stage);

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
