package org.comtel2000.keyboard;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FXOK {

    private static KeyBoardPopup popup;

    private FXOK() {
    }

    public static void registerPopup(KeyBoardPopup p) {
        popup = p;
    }

    public static void setVisible(final Visibility visible, final TextInputControl textNode) {
        if (popup == null) {
            return;
        }
        popup.setVisible(visible, textNode);
    }

    public static Map<String, String> getVkProperties(Node node) {
        if (node.hasProperties()) {
            Map<String, String> vkProps = new HashMap<>();
            node.getProperties().forEach((key, value) -> {
                if (key.toString().startsWith("vk")) {

                    vkProps.put(key.toString(), String.valueOf(value));
                }
            });
            return vkProps;
        }
        if (node.getParent() != null && node.getParent().hasProperties()) {
            Map<String, String> vkProps = new HashMap<>();
            node.getParent().getProperties().forEach((key, value) -> {
                if (key.toString().startsWith("vk")) {
                    vkProps.put(key.toString(), String.valueOf(value));
                }
            });
            return vkProps;
        }
        return Collections.emptyMap();

    }

    public static KeyBoardPopup getPopup() {
        return popup;
    }

}
