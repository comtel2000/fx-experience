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

	private ObservableList<Integer> extKeyCodes;
	private MultiKeyPopup context;

	public ObservableList<Integer> getExtKeyCodes() {
		if (extKeyCodes == null) {
			extKeyCodes = FXCollections.observableArrayList();
			extKeyCodes.addListener(new ListChangeListener<Integer>() {

				public void onChanged(Change<? extends Integer> c) {
					while (c.next()) {
						for (int code : c.getAddedSubList()) {
							KeyButton button = new KeyButton(Character.toString((char) code));
							// button.setPrefSize(getPrefWidth(),
							// getPrefHeight());
							button.setPrefWidth(40);
							button.setPrefHeight(30);
							button.setKeyCode(code);
							button.setOnShortPressed(getOnShortPressed());
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
					getParent().setEffect(null);
					getParent().setDisable(false);

				}
			});
			setOnLongPressed(new EventHandler<Event>() {

				public void handle(Event event) {
					context.getButtonPane().setScaleX(((Node) event.getSource()).getParent().getParent().getScaleX());
					context.getButtonPane().setScaleY(((Node) event.getSource()).getParent().getParent().getScaleY());
					getParent().setEffect(new BoxBlur());
					getParent().setDisable(true);
					setFocused(false);
					context.show((Node) event.getSource(), Side.TOP, -getPrefWidth(), -getPrefHeight());

				}
			});

		}
		return context;
	}

	public void addExtKeyCode(int extKeyCode) {
		getExtKeyCodes().add(extKeyCode);
	}

	public boolean isContextAvailable() {
		return context != null && extKeyCodes != null;
	}

}
