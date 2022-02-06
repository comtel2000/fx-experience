package org.comtel2000.keyboard.control.skin;

import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;
import org.comtel2000.keyboard.FXOK;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;

class KeyboardTextFieldSkin extends TextFieldSkin {

    public KeyboardTextFieldSkin(TextField textInput) {
        super(textInput);
        addFocusListener(textInput);
    }

    private void addFocusListener(TextField textInput) {
        textInput.focusedProperty().addListener(
                (l, a, b) -> FXOK.setVisible(b ? Visibility.SHOW : Visibility.HIDE, textInput));

    }

}
