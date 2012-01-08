package org.comtel.javafx;

import javafx.stage.PopupWindow;

public class KeyboardPopup extends PopupWindow {

	private final KeyBoardPanel keyboard;

	public final KeyBoardPanel getKeyboard() {
		return keyboard;
	}

	public KeyboardPopup(String layerpath, boolean awtRobot) {
		this(new KeyBoardPanel(layerpath, awtRobot));
	}

	public KeyboardPopup(KeyBoardPanel panel) {
		keyboard = panel;
		setAutoFix(true);
		getScene().setRoot(keyboard);
	}
}
