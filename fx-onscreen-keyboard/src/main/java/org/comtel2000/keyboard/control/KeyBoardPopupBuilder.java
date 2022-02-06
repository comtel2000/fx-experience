package org.comtel2000.keyboard.control;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.util.Builder;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;
import org.comtel2000.keyboard.robot.IRobot;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;

public class KeyBoardPopupBuilder implements Builder<KeyBoardPopup> {

    private final KeyBoardBuilder kb;
    private double offset = -1;
    private EventHandler<? super Event> closeEventHandler;

    protected KeyBoardPopupBuilder() {
        kb = KeyBoardBuilder.create();
    }

    public static KeyBoardPopupBuilder create() {
        return new KeyBoardPopupBuilder();
    }

    public KeyBoardPopupBuilder layerPath(Path path) {
        kb.layerPath(path);
        return this;
    }

    public KeyBoardPopupBuilder initLocale(Locale locale) {
        kb.initLocale(locale);
        return this;
    }

    public KeyBoardPopupBuilder initScale(double scale) {
        kb.initScale(scale);
        return this;
    }

    public KeyBoardPopupBuilder addIRobot(IRobot robot) {
        kb.addIRobot(robot);
        return this;
    }

    public KeyBoardPopupBuilder layer(DefaultLayer l) {
        kb.layer(l);
        return this;
    }

    public KeyBoardPopupBuilder style(String css) {
        kb.style(css);
        return this;
    }

    public KeyBoardPopupBuilder offset(double offset) {
        this.offset = offset;
        return this;
    }

    public KeyBoardPopupBuilder onKeyboardCloseButton(EventHandler<? super Event> handler) {
        closeEventHandler = handler;
        return this;
    }

    @Override
    public KeyBoardPopup build() {
        KeyBoardPopup popup = new KeyBoardPopup(kb.build());
        if (offset > -1) {
            popup.setOffset(offset);
        }
        popup.setOnKeyboardCloseButton(Objects.requireNonNullElseGet(closeEventHandler, () -> e -> popup.setVisible(Visibility.HIDE)));
        return popup;
    }

}
