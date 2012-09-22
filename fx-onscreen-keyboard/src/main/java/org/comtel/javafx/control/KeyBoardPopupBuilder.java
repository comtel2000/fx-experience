package org.comtel.javafx.control;

import java.nio.file.Path;
import java.util.Locale;

import org.comtel.javafx.robot.IRobot;

import javafx.util.Builder;

public class KeyBoardPopupBuilder implements Builder<KeyBoardPopup> {

	private final KeyBoardBuilder kb;

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

	@Override
	public KeyBoardPopup build() {
		KeyBoardPopup popup = new KeyBoardPopup(kb.build());
		return popup;
	}

}
