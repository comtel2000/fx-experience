package org.comtel2000.samples.fx;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

import java.util.Locale;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyBoardPopupBuilder;
import org.comtel2000.keyboard.control.VkProperties;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainDemo extends Application implements VkProperties {

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

    popup.addDoubleClickEventFilter(stage);
    // popup.addFocusListener(scene);
    popup.addGlobalFocusListener();

    stage.show();

  }

  public static void main(String[] args) {
    Application.launch(args);
  }

}
