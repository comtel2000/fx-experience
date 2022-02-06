package org.comtel2000.swing.control;

import javafx.embed.swing.JFXPanel;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import org.comtel2000.keyboard.control.KeyBoardPopup;

import javax.swing.*;
import java.io.Serial;
import java.util.Optional;


/**
 * Swing window wrapper class for {@link KeyBoardPopup}
 *
 * @author comtel
 */
public class KeyBoardWindow extends JWindow {

    public final static EventHandler<? super Event> DEFAULT_CLOSE_HANDLER = (event) -> {
        if (event.getSource() instanceof Node) {
            ((Node) event.getSource()).getScene().getWindow().hide();
        }
    };
    @Serial
    private static final long serialVersionUID = -6573658828549744809L;
    private final JFXPanel jfxPanel;
    private KeyBoardPopup popup;

    protected KeyBoardWindow() {
        super();
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setFocusable(false);
        setBackground(null);

        getContentPane().add(jfxPanel = new JFXPanel());
        jfxPanel.setFocusable(false);
        jfxPanel.setOpaque(false);
    }

    /**
     * must run in FxApplicationThread
     *
     * @param popup Keyboard popup
     */
    protected void createScene(final KeyBoardPopup popup) {
        this.popup = popup;
        Scene scene = new Scene(new Group(), 0, 0);
        jfxPanel.setScene(scene);
        popup.registerScene(scene);
    }

    public Optional<KeyBoardPopup> getKeyBoardPopup() {
        return Optional.ofNullable(popup);
    }

}
