/*******************************************************************************
 * Copyright (c) 2017 comtel2000
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

package org.comtel2000.samples.fx;

import java.util.Collections;
import java.util.Locale;

import org.comtel2000.keyboard.FXOK;
import org.comtel2000.keyboard.control.KeyboardLayer;
import org.comtel2000.keyboard.control.KeyboardPopup;
import org.comtel2000.keyboard.control.KeyboardPopupBuilder;
import org.comtel2000.keyboard.control.VkProperties;
import org.comtel2000.keyboard.control.table.KeyboardTableCell;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class MainDemo extends Application {

  @Override
  public void start(Stage stage) {
    stage.setTitle("FX FXOK (" + System.getProperty("javafx.runtime.version") + ")");
    stage.setResizable(true);

    KeyboardPopup popup = KeyboardPopupBuilder.create().initLocale(Locale.ENGLISH).build();
    FXOK.registerPopup(popup);

    BorderPane pane = new BorderPane();
    pane.setTop(buildHeaderPane(popup));

    TabPane tab = new TabPane();
    tab.getTabs().add(new Tab("Fields", buildTextPane()));
    tab.getTabs().add(new Tab("Table", buildTablePane()));
    pane.setCenter(tab);

    Scene scene = new Scene(pane, 600, 800);

    stage.setOnCloseRequest(e -> System.exit(0));
    stage.setScene(scene);

    FXOK.addDoubleClickEventFilter(stage);
    // FXOK.addFocusListener(scene);
    FXOK.addGlobalFocusListener();

    stage.show();

  }

  private Node buildHeaderPane(KeyboardPopup popup) {
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
    popup.getKeyboard().spaceKeyMoveProperty().bindBidirectional(spaceKeyMove.selectedProperty());

    CheckBox capsLock = new CheckBox("CapsLock");
    capsLock.setSelected(true);
    popup.getKeyboard().capsLockProperty().bindBidirectional(capsLock.selectedProperty());

    CheckBox numblock = new CheckBox("NumBlock");
    numblock.setSelected(false);
    numblock.selectedProperty().addListener((l, a, b) -> popup.getKeyboard().switchLayer(b ? KeyboardLayer.NUMBLOCK : KeyboardLayer.DEFAULT));
    return new ToolBar(okButton, cancelButton, popupButton, spaceKeyMove, capsLock, numblock);
  }

  private Pane buildTextPane() {

    VBox pane = new VBox(10);

    pane.getChildren().add(new Label("Text0"));
    TextField tf0 = new TextField();
    tf0.setPromptText("text");
    pane.getChildren().add(tf0);

    pane.getChildren().add(new Label("Text1 (numeric)"));
    TextField tf1 = new TextField();
    tf1.setPromptText("0-9");
    // Currently, the vkType property supports the following values:
    // numeric, url, email, and text
    tf1.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
    pane.getChildren().add(tf1);

    pane.getChildren().add(new Label("Text2 (locale 'de')"));
    TextField tf2 = new TextField();
    tf2.setPromptText("switch locale to 'DE'");
    tf2.getProperties().put(VkProperties.VK_LOCALE, "de");
    pane.getChildren().add(tf2);

    pane.getChildren().add(new Label("Text3 (email)"));
    TextField tf3 = new TextField();
    tf3.setPromptText("email");
    tf3.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_EMAIL);
    pane.getChildren().add(tf3);

    pane.getChildren().add(new Label("Text4 (url)"));
    TextField tf4 = new TextField();
    tf4.setPromptText("url");
    tf4.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_URL);
    pane.getChildren().add(tf4);

    pane.getChildren().add(new Label("Text5 (control)"));
    TextField tf5 = new TextField();
    tf5.setPromptText("control");
    tf5.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_CTRL);
    pane.getChildren().add(tf5);

    ComboBox<String> combo = new ComboBox<>();
    combo.setEditable(true);
    combo.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
    pane.getChildren().add(combo);

    pane.getChildren().add(new TextArea());
    pane.getChildren().add(new Label("Password"));
    pane.getChildren().add(new PasswordField());
    pane.getChildren().add(new Separator());
    return pane;
  }

  private Pane buildTablePane() {

    TableView<Person> table = new TableView<>();
    table.setEditable(true);
    TableColumn<Person, String> nameCol = new TableColumn<>("Name");
    nameCol.setMinWidth(100);
    nameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
    nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
    TableColumn<Person, Integer> ageCol = new TableColumn<>("age");
    ageCol.setMinWidth(100);
    ageCol.setCellValueFactory(new PropertyValueFactory<Person, Integer>("age"));
    ageCol.setCellFactory(
        KeyboardTableCell.forTableColumn(new IntegerStringConverter(), Collections.singletonMap(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC)));
    TableColumn<Person, String> emailCol = new TableColumn<>("Email");
    emailCol.setMinWidth(200);
    emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
    emailCol.setCellFactory(KeyboardTableCell.forTableColumn(Collections.singletonMap(VkProperties.VK_TYPE, VkProperties.VK_TYPE_EMAIL)));
    table.getColumns().add(nameCol);
    table.getColumns().add(ageCol);
    table.getColumns().add(emailCol);

    table.setItems(FXCollections.observableArrayList(new Person("Jacob", 44, "jacob.smith@example.com"),
        new Person("Isabella", 23, "isabella.johnson@example.com"), new Person("Ethan", 45, "ethan.williams@example.com"),
        new Person("Emma", 19, "emma.jones@example.com"), new Person("Michael", 33, "michael.brown@example.com")));

    return new BorderPane(table);
  }

  public static class Person {

    private final StringProperty name;
    private final IntegerProperty age;
    private final StringProperty email;

    private Person(String name, int age, String email) {
      this.name = new SimpleStringProperty(name);
      this.age = new SimpleIntegerProperty(age);
      this.email = new SimpleStringProperty(email);
    }

    public String getName() {
      return name.get();
    }

    public void setName(String name) {
      this.name.set(name);
    }

    public int getAge() {
      return age.get();
    }

    public void setAge(int age) {
      this.age.set(age);
    }

    public String getEmail() {
      return email.get();
    }

    public void setEmail(String email) {
      this.email.set(email);
    }
  }

  public static void main(String[] args) {
    Application.launch(args);
  }

}
