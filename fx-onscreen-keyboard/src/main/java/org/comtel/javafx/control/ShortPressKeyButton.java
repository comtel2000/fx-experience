package org.comtel.javafx.control;

import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;


public class ShortPressKeyButton extends KeyButton {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ShortPressKeyButton.class);
	
	public ShortPressKeyButton(String key) {
		super(key);
	}

	protected void initEventListener() {
		setOnMousePressed(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				logger.trace("{} pressed", getKeyCode());
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					fireShortPressed();
				}
				setFocused(false);
				event.consume();
			}
		});
	}

}
