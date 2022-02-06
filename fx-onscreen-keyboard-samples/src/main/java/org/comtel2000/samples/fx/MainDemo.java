package org.comtel2000.samples.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;

import java.util.Locale;

import static org.comtel2000.keyboard.control.VkProperties.*;


public class MainDemo extends Application {

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("FX FXOK (" + System.getProperty("javafx.runtime.version") + ")");
        stage.setResizable(true);

        KeyBoardPopup popup = KeyBoardPopupBuilder.create().initLocale(Locale.ENGLISH).build();

        VBox pane = new VBox(20);

        Button okButton = new Button("Ok");
        okButton.setDefaultButton(true);

        Button cancelButton = new Button("Cancel");
        cancelButton.setCancelButton(true);

        Button popupButton = new Button("Popup");
        popupButton.setOnAction((a) -> {
            TextInputDialog dialog = new TextInputDialog("Popup");
            dialog.setTitle("Text Input Dialog");
            dialog.setContentText("Please enter your name:");
            dialog.showAndWait();

        });


        CheckBox spaceKeyMove = new CheckBox("Movable");
        spaceKeyMove.setSelected(true);
        popup.getKeyBoard().spaceKeyMoveProperty().bindBidirectional(spaceKeyMove.selectedProperty());

        CheckBox capsLock = new CheckBox("CapsLock");
        capsLock.setSelected(true);
        popup.getKeyBoard().capsLockProperty().bindBidirectional(capsLock.selectedProperty());

        CheckBox numblock = new CheckBox("NumBlock");
        numblock.setSelected(false);
        numblock.selectedProperty().addListener((l, a, b) -> popup.getKeyBoard().switchLayer(b ? DefaultLayer.NUMBLOCK : DefaultLayer.DEFAULT));

        pane.getChildren().add(new ToolBar(okButton, cancelButton, popupButton, spaceKeyMove, capsLock, numblock));

        pane.getChildren().add(new Label("Text0"));
        TextField tf0 = new TextField();
        tf0.setPromptText("text");
        pane.getChildren().add(tf0);

        pane.getChildren().add(new Label("Text1 (numeric)"));
        TextField tf1 = new TextField();
        tf1.setPromptText("0-9");
        // Currently, the vkType property supports the following values:
        // numeric, url, email, and text
        tf1.getProperties().put(VK_TYPE, "numeric");
        pane.getChildren().add(tf1);

        pane.getChildren().add(new Label("Text2 (locale 'de')"));
        TextField tf2 = new TextField();
        tf2.setPromptText("switch locale to 'DE'");
        tf2.getProperties().put(VK_LOCALE, "de");
        pane.getChildren().add(tf2);

        pane.getChildren().add(new Label("Text3 (email)"));
        TextField tf3 = new TextField();
        tf3.setPromptText("email");
        tf3.getProperties().put(VK_TYPE, VK_TYPE_EMAIL);
        pane.getChildren().add(tf3);

        pane.getChildren().add(new Label("Text4 (url)"));
        TextField tf4 = new TextField();
        tf4.setPromptText("url");
        tf4.getProperties().put(VK_TYPE, VK_TYPE_URL);
        pane.getChildren().add(tf4);

        pane.getChildren().add(new Label("Text5 (no keyboard)"));
        TextField tf5 = new TextField();
        tf5.setPromptText("no keyboard");
        tf5.getProperties().put(VK_STATE, VK_STATE_DISABLED);
        pane.getChildren().add(tf5);

        ComboBox<String> combo = new ComboBox<>();
        combo.setEditable(true);
        combo.getProperties().put(VK_TYPE, VK_TYPE_NUMERIC);
        pane.getChildren().add(combo);

        pane.getChildren().add(new TextArea());
        pane.getChildren().add(new Label("Password"));
        pane.getChildren().add(new PasswordField());
        pane.getChildren().add(new Separator());

        Scene scene = new Scene(pane, 600, 800);

        stage.setOnCloseRequest(e -> System.exit(0));
        stage.setScene(scene);

        popup.registerScene(scene);
        popup.addGlobalFocusListener();
        popup.addGlobalDoubleClickEventFilter();

        stage.show();

    }

}
