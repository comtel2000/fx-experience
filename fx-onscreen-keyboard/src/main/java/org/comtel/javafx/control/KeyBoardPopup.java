package org.comtel.javafx.control;

import javafx.scene.Scene;
import javafx.stage.Popup;

public class KeyBoardPopup extends Popup {

	private final KeyboardPane keyboard;

	private Scene owner = null;
	
	public final KeyboardPane getKeyBoard() {
		return keyboard;
	}

	public KeyBoardPopup(KeyboardPane panel) {
		keyboard = panel;
		getContent().add(panel);
	}

	public boolean isVisible() {
		return isShowing();
	}

	public void setVisible(boolean b) {
		if (b) {
			if (owner != null){
				super.show(owner.getWindow());
			}else{
				super.show(this.getOwnerWindow());
			}
		} else {
			hide();
		}

	}

	public Scene getOwner() {
		return owner;
	}

	public void setOwner(Scene owner) {
		this.owner = owner;
	}
}
