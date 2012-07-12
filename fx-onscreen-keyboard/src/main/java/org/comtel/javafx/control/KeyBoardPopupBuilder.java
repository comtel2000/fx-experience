package org.comtel.javafx.control;

import java.nio.file.Path;

import javafx.util.Builder;

public class KeyBoardPopupBuilder implements Builder<KeyBoardPopup> {

	private KeyBoard panel;
	private Path path;
	
	public KeyBoardPopupBuilder layerPath(Path path){
		this.path = path;
		return this;
	}
	
	public KeyBoardPopupBuilder keyBoardPanel(KeyBoard panel){
		this.panel = panel;
		return this;
	}

	@Override
	public KeyBoardPopup build() {
		if (panel == null){
			panel = new KeyBoard(path);
		}
		KeyBoardPopup popup = new KeyBoardPopup(panel);
		return popup;
	}

}
