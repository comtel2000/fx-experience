/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.StandardKeyCode.ARROW_DOWN;
import static org.comtel2000.keyboard.control.StandardKeyCode.ARROW_LEFT;
import static org.comtel2000.keyboard.control.StandardKeyCode.ARROW_RIGHT;
import static org.comtel2000.keyboard.control.StandardKeyCode.ARROW_UP;
import static org.comtel2000.keyboard.control.StandardKeyCode.BACK_SPACE;
import static org.comtel2000.keyboard.control.StandardKeyCode.CAPS_LOCK;
import static org.comtel2000.keyboard.control.StandardKeyCode.CLOSE;
import static org.comtel2000.keyboard.control.StandardKeyCode.CTRL_DOWN;
import static org.comtel2000.keyboard.control.StandardKeyCode.DELETE;
import static org.comtel2000.keyboard.control.StandardKeyCode.EMAIL_TYPE;
import static org.comtel2000.keyboard.control.StandardKeyCode.END;
import static org.comtel2000.keyboard.control.StandardKeyCode.ENTER;
import static org.comtel2000.keyboard.control.StandardKeyCode.F1;
import static org.comtel2000.keyboard.control.StandardKeyCode.F10;
import static org.comtel2000.keyboard.control.StandardKeyCode.F11;
import static org.comtel2000.keyboard.control.StandardKeyCode.F12;
import static org.comtel2000.keyboard.control.StandardKeyCode.F2;
import static org.comtel2000.keyboard.control.StandardKeyCode.F3;
import static org.comtel2000.keyboard.control.StandardKeyCode.F4;
import static org.comtel2000.keyboard.control.StandardKeyCode.F5;
import static org.comtel2000.keyboard.control.StandardKeyCode.F6;
import static org.comtel2000.keyboard.control.StandardKeyCode.F7;
import static org.comtel2000.keyboard.control.StandardKeyCode.F8;
import static org.comtel2000.keyboard.control.StandardKeyCode.F9;
import static org.comtel2000.keyboard.control.StandardKeyCode.HELP;
import static org.comtel2000.keyboard.control.StandardKeyCode.HOME;
import static org.comtel2000.keyboard.control.StandardKeyCode.LOCALE_SWITCH;
import static org.comtel2000.keyboard.control.StandardKeyCode.NUMERIC_TYPE;
import static org.comtel2000.keyboard.control.StandardKeyCode.PAGE_DOWN;
import static org.comtel2000.keyboard.control.StandardKeyCode.PAGE_UP;
import static org.comtel2000.keyboard.control.StandardKeyCode.PRINTSCREEN;
import static org.comtel2000.keyboard.control.StandardKeyCode.REDO;
import static org.comtel2000.keyboard.control.StandardKeyCode.SHIFT_DOWN;
import static org.comtel2000.keyboard.control.StandardKeyCode.SYMBOL_DOWN;
import static org.comtel2000.keyboard.control.StandardKeyCode.TAB;
import static org.comtel2000.keyboard.control.StandardKeyCode.UNDO;
import static org.comtel2000.keyboard.control.StandardKeyCode.URL_TYPE;

import java.util.Locale;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.comtel2000.keyboard.event.KeyButtonEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyButtonEventHandler implements EventHandler<KeyButtonEvent> {

  private static final Logger logger = LoggerFactory.getLogger(KeyButtonEventHandler.class);
  private final KeyboardPane keyboard;

  KeyButtonEventHandler(KeyboardPane keyboard) {
    this.keyboard = keyboard;
  }

  @Override
  public void handle(KeyButtonEvent event) {
    if (!event.getEventType().equals(KeyButtonEvent.SHORT_PRESSED)) {
      logger.warn("ignore non short pressed events");
      return;
    }
    event.consume();
    KeyButton kb = (KeyButton) event.getSource();
    switch (kb.getKeyCode()) {
      case SHIFT_DOWN:
        // switch shifted
        keyboard.setKeyboardType(keyboard.isControl(), !keyboard.isShift(), keyboard.isSymbol());
        break;
      case SYMBOL_DOWN:
        // switch sym / qwerty
        keyboard.setKeyboardType(keyboard.isControl(), keyboard.isShift(), !keyboard.isSymbol());
        break;
      case CLOSE:
        keyboard.fireCloseEvent(event);
        break;
      case TAB:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_TAB, true);
        break;
      case BACK_SPACE:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_BACK_SPACE, true);
        break;
      case DELETE:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_DELETE, true);
        break;
      case CTRL_DOWN:
        // switch ctrl
        keyboard.setControl(!keyboard.isControl());
        keyboard.setKeyboardType(keyboard.isControl(), keyboard.isShift(), keyboard.isSymbol());
        break;
      case LOCALE_SWITCH:
        keyboard.switchLocale(Locale.forLanguageTag(kb.getText()));
        break;
      case ENTER:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_ENTER, true);
        break;
      case ARROW_UP:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_UP, true);
        break;
      case ARROW_DOWN:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_DOWN, true);
        break;
      case ARROW_LEFT:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_LEFT, true);
        break;
      case ARROW_RIGHT:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_RIGHT, true);
        break;
      case UNDO:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_Z, true);
        break;
      case REDO:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_Y, true);
        break;
      case HOME:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_HOME, true);
        break;
      case END:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_END, true);
        break;
      case PAGE_UP:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_UP, true);
        break;
      case PAGE_DOWN:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_PAGE_DOWN, true);
        break;
      case HELP:
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_HELP, true);
        break;
      case NUMERIC_TYPE:
        keyboard.setKeyboardType(KeyboardType.NUMERIC);
        break;
      case EMAIL_TYPE:
        keyboard.setKeyboardType(KeyboardType.EMAIL);
        break;
      case URL_TYPE:
        keyboard.setKeyboardType(KeyboardType.URL);
        break;
      case CAPS_LOCK:
        keyboard.setCapsLock(!keyboard.isCapsLock());
        break;
      case PRINTSCREEN:
        double opacity = keyboard.getScene().getWindow().getOpacity();
        keyboard.getScene().getWindow().setOpacity(0.0);
        keyboard.sendToComponent((char) java.awt.event.KeyEvent.VK_PRINTSCREEN, true);
        Timeline timeline = new Timeline();
        timeline.setDelay(Duration.millis(1000));
        timeline
            .getKeyFrames()
            .add(
                new KeyFrame(
                    Duration.millis(500),
                    new KeyValue(keyboard.getScene().getWindow().opacityProperty(), opacity)));
        timeline.play();
        break;
      case F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12:
        keyboard.sendToComponent((char) Math.abs(kb.getKeyCode()), true);
        break;
      default:
        if (kb.getKeyText() != null) {
          for (int i = 0; i < kb.getKeyText().length(); i++) {
            keyboard.sendToComponent(kb.getKeyText().charAt(i), keyboard.isControl());
          }
        } else if (kb.getKeyCode() > -1) {
          keyboard.sendToComponent((char) kb.getKeyCode(), keyboard.isControl());
        } else {
          logger.debug("unknown key code: {}", kb.getKeyCode());
          keyboard.sendToComponent((char) kb.getKeyCode(), true);
        }
        if (!keyboard.isCapsLock() && keyboard.isShift()) {
          keyboard.setKeyboardType(keyboard.isControl(), !keyboard.isShift(), keyboard.isSymbol());
        }
        break;
    }
  }
}
