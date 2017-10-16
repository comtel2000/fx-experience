package org.comtel2000.keyboard.control;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Locale;

/**
 * Created by Guglielmo Moretti - CEIA SpA
 * Date: 16/10/2017.
 */
public class FakeApp extends Application {

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
        keyboard.setKeyboardType(type);
        Scene scene = new Scene(keyboard, keyboard.getPrefWidth(), keyboard.getPrefHeight());
        stage.setScene(scene);
        stage.show();
    }
}
