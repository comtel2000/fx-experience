package org.comtel.javafx.control;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.effect.BoxBlur;
import javafx.stage.WindowEvent;

public class MultiKeyButton extends KeyButton {

	private ObservableList<KeyButton> extKeyCodes;
	private MultiKeyPopup context;

	public ObservableList<KeyButton> getExtKeyCodes() {
		if (extKeyCodes == null) {
			extKeyCodes = FXCollections.observableArrayList();
			extKeyCodes.addListener(new ListChangeListener<KeyButton>() {

				public void onChanged(Change<? extends KeyButton> c) {
					while (c.next()) {
						for (KeyButton button : c.getAddedSubList()) {
							getContext().getButtons().add(button);
						}
					}
				}
			});
		}
		return extKeyCodes;
	}

	public MultiKeyPopup getContext() {
		if (context == null) {

			context = new MultiKeyPopup();
			context.setOnHidden(new EventHandler<WindowEvent>() {

				public void handle(WindowEvent event) {
					getParent().getParent().setEffect(null);
					getParent().getParent().setDisable(false);

				}
			});
			setOnLongPressed(new EventHandler<Event>() {

				public void handle(Event event) {
					context.getButtonPane().setScaleX(((Node) event.getSource()).getParent().getParent().getScaleX());
					context.getButtonPane().setScaleY(((Node) event.getSource()).getParent().getParent().getScaleY());
					getParent().getParent().setEffect(new BoxBlur());
					getParent().getParent().setDisable(true);
					setFocused(false);
					context.show((Node) event.getSource(), Side.TOP, -getPrefWidth(), -getPrefHeight());

				}
			});

		}
		return context;
	}

	public void addExtKeyCode(int extKeyCode) {
		addExtKeyCode(extKeyCode, null, null);
	}

	public void addExtKeyCode(int extKeyCode, String label, String style) {
		ShortPressKeyButton button = new ShortPressKeyButton(Character.toString((char) extKeyCode));
		if (style != null && style.startsWith(".")) {
			button.getStyleClass().add(style.substring(1));
		}
		if (label != null){
			button.setText(label);
		}
		button.setFocusTraversable(false);
		button.setCache(true);
		
		//TODO: add to css style
		button.setPrefWidth(40);
		button.setPrefHeight(40);
		
		button.setKeyCode(extKeyCode);
		button.setOnShortPressed(getOnShortPressed());
		
		getExtKeyCodes().add(button);
	}
	
	public boolean isContextAvailable() {
		return context != null && extKeyCodes != null;
	}

}
