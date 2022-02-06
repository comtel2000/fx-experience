package org.comtel2000.swing.control;

import javafx.application.Platform;
import javafx.util.Builder;
import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardBuilder;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.robot.IRobot;
import org.comtel2000.swing.robot.AWTRobotHandler;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


/**
 * Swing {@link KeyBoardWindow} builder
 *
 * @author comtel
 */
public class KeyBoardWindowBuilder implements Builder<KeyBoardWindow> {

    private final CountDownLatch latch = new CountDownLatch(1);

    private final KeyBoardBuilder kb;

    KeyBoardWindowBuilder() {
        kb = KeyBoardBuilder.create();
    }

    /**
     * create new instance
     *
     * @return this
     */
    public static KeyBoardWindowBuilder create() {
        return new KeyBoardWindowBuilder();
    }

    /**
     * The own layout location
     *
     * @param path the layer location
     * @return this
     */
    public KeyBoardWindowBuilder layerPath(Path path) {
        kb.layerPath(path);
        return this;
    }

    /**
     * The initial {@link Locale}
     *
     * @param locale initial locale
     * @return this
     */
    public KeyBoardWindowBuilder initLocale(Locale locale) {
        kb.initLocale(locale);
        return this;
    }

    /**
     * The initial keyboard scale
     *
     * @param scale initial size
     * @return this
     */
    public KeyBoardWindowBuilder initScale(double scale) {
        kb.initScale(scale);
        return this;
    }

    /**
     * The robot adapter default {@link AWTRobotHandler}
     *
     * @param robot default {@link AWTRobotHandler}
     * @return this
     */
    public KeyBoardWindowBuilder addIRobot(IRobot robot) {
        kb.addIRobot(robot);
        return this;
    }

    /**
     * The embedded layout
     *
     * @param layer embedded layout layer
     * @return this
     */
    public KeyBoardWindowBuilder layer(DefaultLayer layer) {
        kb.layer(layer);
        return this;
    }

    /**
     * The style location and name
     *
     * @param css style sheet
     * @return this
     */
    public KeyBoardWindowBuilder style(String css) {
        kb.style(css);
        return this;
    }

    /**
     * build and wait for finalize FX instantiation
     *
     * @return KeyBoardWindow window
     */
    @Override
    public KeyBoardWindow build() {
        final KeyBoardWindow window = new KeyBoardWindow();
        Platform.runLater(() -> {
            KeyBoardPopup popup = new KeyBoardPopup(kb.build());
            window.createScene(popup);
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return window;
    }

}
