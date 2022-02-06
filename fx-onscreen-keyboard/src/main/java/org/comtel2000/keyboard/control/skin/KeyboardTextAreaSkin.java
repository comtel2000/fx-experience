package org.comtel2000.keyboard.control.skin;

import javafx.scene.control.TextArea;
import javafx.scene.control.skin.TextAreaSkin;
import org.comtel2000.keyboard.FXOK;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;

class KeyboardTextAreaSkin extends TextAreaSkin {

    public KeyboardTextAreaSkin(TextArea textInput) {
        super(textInput);
        addFocusListener(textInput);
    }

    private void addFocusListener(TextArea textInput) {
        textInput.focusedProperty().addListener(
                (l, a, b) -> FXOK.setVisible(b ? Visibility.SHOW : Visibility.HIDE, textInput));
    }

}
