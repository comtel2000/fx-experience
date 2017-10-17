package org.comtel2000.keyboard.control;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isNotNull;
import static org.testfx.matcher.base.NodeMatchers.isNull;

public class ExternalKeyboardPaneTest extends ApplicationTest {

    private IKeyboardType externalTestKeyboardSize = new IKeyboardType() {
        @Override
        public int getKeyboardId() {
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

        KeyboardPane keyboard = KeyBoardBuilder.create()
                .setExternalKeyboardTypeFinder(() -> Collections.singletonList(new IExternalKeyboardTypeFinder.KeyboardTypeBean(externalTestKeyboardSize, "/xml", "ip-keyboard.xml"))).layer(DefaultLayer.DEFAULT)
                .initLocale(Locale.ENGLISH).build();

        keyboard.setKeyboardType(externalTestKeyboardSize);

        Scene scene = new Scene(keyboard, keyboard.getPrefWidth(), keyboard.getPrefHeight());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void verifyIPKeyboard() {
        verifyThat("a", isNull());
        verifyThat("A", isNull());
        verifyThat("1", isNotNull());
        verifyThat("2", isNotNull());
        verifyThat("3", isNotNull());
        verifyThat("4", isNotNull());
        verifyThat("5", isNotNull());
        verifyThat("6", isNotNull());
        verifyThat("7", isNotNull());
        verifyThat("8", isNotNull());
        verifyThat("@", isNotNull());

    }
}
