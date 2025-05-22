package org.comtel2000.keyboard.robot;

import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;

public class MockRobot implements IRobot {

    public static class KeyPress {
        public final char character;
        public final int code; // Can store this if int variant is used
        public final String text; // Can store this if String variant is used
        public final boolean isCtrlPressed;

        public KeyPress(char character, boolean isCtrlPressed) {
            this.character = character;
            this.code = -1; // Not used by this constructor
            this.text = null; // Not used by this constructor
            this.isCtrlPressed = isCtrlPressed;
        }

        public KeyPress(int code, boolean isCtrlPressed) {
            this.character = (char) code; // Store as char as well for convenience
            this.code = code;
            this.text = null; // Not used by this constructor
            this.isCtrlPressed = isCtrlPressed;
        }
        
        public KeyPress(String text) {
            this.character = 0; // Not applicable directly
            this.code = -1; // Not applicable directly
            this.text = text;
            this.isCtrlPressed = false; // Typically not used with string send
        }

        @Override
        public String toString() {
            return "KeyPress{" +
                    "character=" + character +
                    ", code=" + code +
                    ", text='" + text + '\'' +
                    ", isCtrlPressed=" + isCtrlPressed +
                    '}';
        }
    }

    public List<KeyPress> receivedKeyPresses = new ArrayList<>();
    public Node lastSender;

    @Override
    public void sendToComponent(Node sender, char ch, boolean ctrl) {
        receivedKeyPresses.add(new KeyPress(ch, ctrl));
        this.lastSender = sender;
    }

    @Override
    public void sendToComponent(Node sender, int ch, boolean ctrl) {
        // This method is often used for special keys (like Enter, Tab) but can also be for chars.
        // For simplicity, let's store it and also convert to char if it's a printable char.
        receivedKeyPresses.add(new KeyPress(ch, ctrl));
        this.lastSender = sender;
    }

    @Override
    public void sendToComponent(Node sender, String text) {
        receivedKeyPresses.add(new KeyPress(text));
        this.lastSender = sender;
    }

    // Other IRobot methods if any (based on the actual interface definition)
    // For example, if there are methods like keyPress, keyRelease for virtual keys:
    // public void keyPress(int keycode) { /* store or log */ }
    // public void keyRelease(int keycode) { /* store or log */ }
    
    public void clear() {
        receivedKeyPresses.clear();
        lastSender = null;
    }
}
