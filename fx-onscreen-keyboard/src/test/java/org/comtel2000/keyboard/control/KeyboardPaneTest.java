package org.comtel2000.keyboard.control;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.NodeQueryUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.*;

public class KeyboardPaneTest extends ApplicationTest {

    private static final int SHORT_PRESS_DELAY = 20;
    private static final int LONG_PRESS_DELAY = 450;

    private static final Function<Integer, Predicate<Node>> KEY_CODE_FUNC = keyCode -> {
        return node -> ((node instanceof KeyButton)
                && keyCode == KeyButton.class.cast(node).getKeyCode());
    };

    private IKeyboardType type = new IKeyboardType() {
        @Override
        public int getKeyboardTypeNumber() {
            return 10;
        }

        @Override
        public boolean getControl() {
            return false;
        }

        @Override
        public boolean getShift() {
            return false;
        }

        @Override
        public boolean getSymbol() {
            return false;
        }
    };

    @Override
    public void start(Stage stage) {
        KeyboardPane keyboard = KeyBoardBuilder.create().setExternalKeyboardTypeFinder(() -> Collections.singletonList(new IExternalKeyboardTypeFinder.KeyboardTypeBean(type, "c:\\app", "ip-keyboard.xml"))).layer(DefaultLayer.DEFAULT)
                .initLocale(Locale.ENGLISH).build();
        keyboard.setActiveType(type);
        Scene scene = new Scene(keyboard, keyboard.getPrefWidth(), keyboard.getPrefHeight());
        stage.setScene(scene);
        stage.show();
    }

    private void pressShort(String query) {
        pressKey(query, SHORT_PRESS_DELAY);
    }

    private void pressLong(String query) {
        pressKey(query, LONG_PRESS_DELAY);
    }

    private void pressKey(String query, int millis) {
        moveTo(point(query)).press(MouseButton.PRIMARY).sleep(millis, TimeUnit.MILLISECONDS)
                .release(MouseButton.PRIMARY);
    }

    private void pressShort(int keyCode) {
        clickOn(KEY_CODE_FUNC.apply(keyCode), MouseButton.PRIMARY).sleep(SHORT_PRESS_DELAY,
                TimeUnit.MILLISECONDS);
    }

    @Test
    public void toggleShift() {
        verifyThat("a", isNotNull());
        verifyThat("A", isNull());
        pressShort(StandardKeyCode.SHIFT_DOWN);
        verifyThat("a", isNull());
        verifyThat("A", isNotNull());
        pressShort(StandardKeyCode.SHIFT_DOWN);
        verifyThat("a", isNotNull());
        verifyThat("A", isNull());
    }

    @Test
    public void toggleControl() {
        verifyThat("a", isNotNull());
        verifyThat("F1", isNull());
        pressShort(StandardKeyCode.CTRL_DOWN);
        verifyThat("a", isNull());
        verifyThat("F1", isNotNull());
        pressShort(StandardKeyCode.CTRL_DOWN);
        verifyThat("a", isNotNull());
        verifyThat("F1", isNull());
    }

    @Test
    public void toggleSymbol() {
        verifyThat("a", isNotNull());
        verifyThat("(", isNull());
        pressShort(StandardKeyCode.SYMBOL_DOWN);
        verifyThat("a", isNull());
        verifyThat("(", isNotNull());
        pressShort(StandardKeyCode.SYMBOL_DOWN);
        verifyThat("a", isNotNull());
        verifyThat("(", isNull());
    }

    @Test
    public void toggleSymbolShift() {
        verifyThat("a", isNotNull());
        verifyThat("[", isNull());
        pressShort(StandardKeyCode.SHIFT_DOWN);
        pressShort(StandardKeyCode.SYMBOL_DOWN);
        verifyThat("a", isNull());
        verifyThat("[", isNotNull());
        pressShort(StandardKeyCode.SHIFT_DOWN);
        pressShort(StandardKeyCode.SYMBOL_DOWN);
        verifyThat("a", isNotNull());
        verifyThat("[", isNull());
    }

    @Test
    public void shortEnter() {
        clickOn(point("ENTER"), MouseButton.PRIMARY);
    }

    @Test
    public void shortPressO() {
        pressShort("o");
        verifyThat(".key-background", isEnabled());
        clickOn(MouseButton.PRIMARY);
        verifyThat(".key-background", isEnabled());
    }

    @Test
    public void longPressO() {
        pressLong("o");
        verifyThat(".key-background", isDisabled());
        clickOn(MouseButton.PRIMARY);
        verifyThat(".key-background", isEnabled());
    }

    @Test
    public void longPressA() {
        pressLong("a");
        verifyThat(".key-background", isDisabled());
        clickOn(MouseButton.PRIMARY);
        verifyThat(".key-background", isEnabled());
    }

    @Test
    public void longPressUandAbort() {
        pressLong("u");
        verifyThat(".key-background", isDisabled());
        moveTo(NodeQueryUtils.hasText("a")).clickOn(MouseButton.PRIMARY);
        verifyThat(".key-background", isEnabled());
    }
}
