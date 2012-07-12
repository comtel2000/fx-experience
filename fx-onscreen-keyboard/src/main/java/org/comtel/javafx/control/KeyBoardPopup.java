package org.comtel.javafx.control;

import java.nio.file.Path;

import javafx.stage.Popup;

import org.comtel.javafx.robot.IRobot;

public class KeyBoardPopup extends Popup {

	private final KeyBoard keyboard;

	public final KeyBoard getKeyBoard() {
		return keyboard;
	}

	public KeyBoardPopup(Path layerpath, IRobot robot) {
		this(new KeyBoard(layerpath, robot));
	}

	public KeyBoardPopup(KeyBoard panel) {
		keyboard = panel;
		super.getContent().add(panel);
	}
}
