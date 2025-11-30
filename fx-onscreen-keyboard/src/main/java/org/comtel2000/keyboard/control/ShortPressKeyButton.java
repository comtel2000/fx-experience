/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import javafx.scene.input.MouseButton;
import org.slf4j.LoggerFactory;

class ShortPressKeyButton extends KeyButton {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ShortPressKeyButton.class);

  ShortPressKeyButton() {
    super();
  }

  @Override
  protected void initEventListener(Timelines timelines) {
    setOnMousePressed(
        event -> {
          logger.trace("{} pressed", getKeyCode());
          timelines.stop();
          if (event.getButton().equals(MouseButton.PRIMARY)) {
            fireShortPressed();
          }
          setFocused(false);
          event.consume();
        });
  }
}
