package org.comtel2000.keyboard.control;

import javafx.util.Builder;
import org.comtel2000.keyboard.robot.IRobot;

import java.nio.file.Path;
import java.util.Locale;

public class KeyBoardBuilder implements Builder<KeyboardPane> {

    private final KeyboardPane kb;

    protected KeyBoardBuilder() {
        kb = new KeyboardPane();
    }

    public static KeyBoardBuilder create() {
        return new KeyBoardBuilder();
    }

    public void layerPath(Path path) {
        kb.setLayerPath(path);
    }

    public void layer(DefaultLayer layer) {
        kb.setLayer(layer);
    }

    public void style(String css) {
        kb.setStyle(css);
    }

    public void initLocale(Locale locale) {
        kb.setLocale(locale);
    }

    public void initScale(double scale) {
        kb.setScale(scale);
    }

    public void addIRobot(IRobot robot) {
        kb.addRobotHandler(robot);
    }

    @Override
    public KeyboardPane build() {
        try {
            kb.load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return kb;
    }

}
