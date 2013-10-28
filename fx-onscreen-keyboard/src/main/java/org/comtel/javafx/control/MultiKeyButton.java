package org.comtel.javafx.control;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.stage.WindowEvent;

public class MultiKeyButton extends KeyButton {

	private ObservableList<KeyButton> extKeyCodes;
	private MultiKeyPopup context;

	private final SimpleDoubleProperty scaleProperty;
	
	public MultiKeyButton(SimpleDoubleProperty scaleProperty) {
		this.scaleProperty = scaleProperty;
	}
	
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

					context.getButtonPane().getTransforms().setAll(new Scale(scaleProperty.get(),scaleProperty.get(),1,0,0,0));
					
					//getParent().getParent().setEffect(new BoxBlur());
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

	public void addExtKeyCode(int extKeyCode, String label, ObservableList<String> styleClasses) {
		ShortPressKeyButton button = new ShortPressKeyButton(Character.toString((char) extKeyCode));
		
		if (styleClasses != null) {
			button.getStyleClass().addAll(styleClasses);
		}else{
			button.setId("key-context-button");
		}
		if (label != null){
			button.setText(label);
		}
		button.setFocusTraversable(false);
		
		//TODO: add to css style
		button.setPrefWidth(this.getPrefWidth());
		button.setPrefHeight(this.getPrefHeight());

		
		button.setKeyCode(extKeyCode);
		button.setOnShortPressed(getOnShortPressed());
		
		getExtKeyCodes().add(button);
	}
	
	public boolean isContextAvailable() {
		return context != null && extKeyCodes != null;
	}

}
