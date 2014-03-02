package org.comtel.javafx.control;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import org.slf4j.LoggerFactory;

public class ShortPressKeyButton extends KeyButton {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ShortPressKeyButton.class);

	public ShortPressKeyButton(String key) {
		super(key);
	}

	@Override
	protected void initEventListener() {
		setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
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
