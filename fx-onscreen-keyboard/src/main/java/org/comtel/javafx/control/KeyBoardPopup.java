package org.comtel.javafx.control;

import javafx.stage.Popup;

public class KeyBoardPopup extends Popup {

	private final KeyBoard keyboard;

	public final KeyBoard getKeyBoard() {
		return keyboard;
	}

	public KeyBoardPopup(KeyBoard panel) {
		keyboard = panel;
		getContent().add(panel);
	}
}
