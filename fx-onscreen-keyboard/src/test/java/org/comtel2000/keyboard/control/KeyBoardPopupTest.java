/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.callFx;
import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.comtel2000.keyboard.control.KeyBoardPopup.Visibility;
import org.comtel2000.keyboard.robot.RecordingRobot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyBoardPopupTest {

  private Stage stage;
  private TextField field;
  private Button button;
  private KeyBoardPopup popup;

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @BeforeEach
  void setUp() {
    runFx(
        () -> {
          field = new TextField();
          button = new Button("b");
          stage = new Stage();
          stage.setScene(new Scene(new VBox(field, button), 300, 200));
          stage.setX(50);
          stage.setY(50);
          stage.show();
          popup =
              KeyBoardPopupBuilder.create()
                  .initLocale(Locale.ENGLISH)
                  .addIRobot(new RecordingRobot())
                  .build();
        });
  }

  @AfterEach
  void tearDown() {
    runFx(
        () -> {
          popup.hide();
          stage.close();
        });
  }

  private boolean showing() {
    return callFx(popup::isShowing);
  }

  private void waitHidden() {
    assertTrue(FxTestSupport.waitFor(() -> !popup.isShowing(), 3000), "popup did not hide");
  }

  @Test
  void builderAppliesSettings() {
    runFx(
        () -> {
          var robot = new RecordingRobot();
          var closed = new AtomicInteger();
          var p =
              KeyBoardPopupBuilder.create()
                  .initLocale(Locale.GERMAN)
                  .initScale(1.5)
                  .layer(DefaultLayer.NUMBLOCK)
                  .layerPath(null)
                  .style("-fx-font-size: 10;")
                  .offset(12)
                  .addIRobot(robot)
                  .onKeyboardCloseButton(e -> closed.incrementAndGet())
                  .build();
          var kb = p.getKeyBoard();
          assertEquals(Locale.GERMAN, kb.getActiveLocale());
          assertEquals(1.5, kb.getScale());
          assertEquals(DefaultLayer.NUMBLOCK, kb.getLayer());
          assertEquals("-fx-font-size: 10;", kb.getStyle());
          assertEquals(12, p.getOffset());
          assertSame(robot, kb.getRobotHandler().get(0));
          assertTrue(p.getContent().contains(kb));
        });
  }

  @Test
  void keyBoardBuilder() {
    runFx(
        () -> {
          var b = KeyBoardBuilder.create();
          b.initLocale(Locale.ENGLISH);
          b.layer(DefaultLayer.DEFAULT);
          b.layerPath(null);
          b.style("-fx-background-color: red;");
          b.initScale(1.0);
          b.addIRobot(new RecordingRobot());
          var kb = b.build();
          assertEquals(KeyboardType.TEXT, kb.getActiveType());
        });
  }

  @Test
  void offsetProperty() {
    runFx(
        () -> {
          assertEquals(5, popup.getOffset());
          popup.setOffset(7);
          assertEquals(7, popup.offsetProperty().get());
          popup.setOffset(8);
          assertEquals(8, popup.getOffset());
        });
  }

  @Test
  void requiresKeyboardAndScene() {
    runFx(
        () -> {
          assertThrows(NullPointerException.class, () -> new KeyBoardPopup(null));
          assertThrows(NullPointerException.class, () -> popup.registerScene(null));
          assertTrue(KeyBoardPopup.getUserAgentStyleSheet().endsWith("KeyboardTextInputSkin.css"));
          assertNotNull(popup.getFocusChangeListener());
          assertNotNull(popup.getDoubleClickEventFilter());
        });
  }

  @Test
  void showBelowTextFieldAndHide() {
    runFx(() -> popup.setVisible(Visibility.SHOW, field));
    assertTrue(showing());
    runFx(
        () -> {
          assertTrue(popup.isVisible());
          var bounds = field.localToScreen(field.getBoundsInLocal());
          assertEquals(bounds.getMinX(), popup.getX(), 1.0);
          assertEquals(bounds.getMaxY() + popup.getOffset(), popup.getY(), 1.0);
          assertEquals(KeyboardType.TEXT, popup.getKeyBoard().getActiveType());
        });

    runFx(() -> popup.setVisible(false));
    waitHidden();
  }

  @Test
  void hideWhenNotShowingIsNoOp() {
    runFx(() -> popup.setVisible(Visibility.HIDE, field));
    assertFalse(showing());
  }

  @Test
  void positionOnlyDoesNotOpen() {
    runFx(() -> popup.setVisible(Visibility.POS, field));
    assertFalse(showing());
  }

  @Test
  void vkPropertiesSelectLayoutAndLocale() {
    runFx(
        () -> {
          field.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
          field.getProperties().put(VkProperties.VK_LOCALE, VkProperties.VK_LOCALE_DE);
          popup.setVisible(Visibility.SHOW, field);
          assertEquals(Locale.GERMAN, popup.getKeyBoard().getActiveLocale());
        });
    assertTrue(showing());
  }

  @Test
  void vkTypeIsApplied() {
    runFx(
        () -> {
          field.getProperties().put(VkProperties.VK_TYPE, VkProperties.VK_TYPE_NUMERIC);
          popup.setVisible(Visibility.SHOW, field);
          assertEquals(KeyboardType.NUMERIC, popup.getKeyBoard().getActiveType());
        });
  }

  @Test
  void vkStateOffHidesKeyboard() {
    runFx(() -> popup.setVisible(Visibility.SHOW, field));
    assertTrue(showing());
    runFx(
        () -> {
          field.getProperties().put(VkProperties.VK_STATE, VkProperties.VK_STATE_DISABLED);
          popup.setVisible(Visibility.SHOW, field);
        });
    waitHidden();
  }

  @Test
  void showWithoutTextNodeUsesRegisteredScene() {
    runFx(
        () -> {
          popup.registerScene(stage.getScene());
          assertSame(stage.getScene(), popup.getRegisteredScene());
          popup.setVisible(true);
        });
    assertTrue(showing());
    runFx(() -> assertSame(stage, popup.getOwnerWindow()));
  }

  @Test
  void focusListenerShowsAndHides() {
    runFx(
        () -> {
          button.requestFocus();
          popup.addFocusListener(stage.getScene());
          field.requestFocus();
        });
    assertTrue(FxTestSupport.waitFor(popup::isShowing, 3000));
    runFx(button::requestFocus);
    waitHidden();
  }

  @Test
  void focusListenerFindsNestedTextField() {
    runFx(
        () -> {
          var combo = new ComboBox<String>();
          combo.setEditable(true);
          ((VBox) stage.getScene().getRoot()).getChildren().add(combo);
          combo.applyCss();
          popup.getFocusChangeListener().changed(null, null, combo);
        });
    assertTrue(showing());
  }

  @Test
  void focusListenerDoNotOpen() {
    runFx(
        () -> {
          button.requestFocus();
          popup.addFocusListener(stage.getScene(), true);
          field.requestFocus();
        });
    FxTestSupport.waitForFxEvents();
    assertFalse(showing());
  }

  @Test
  void doubleClickOpensKeyboard() {
    runFx(
        () -> {
          popup.addDoubleClickEventFilter(stage);
          field.requestFocus();
          popup.getDoubleClickEventFilter().handle(click(stage, 1));
        });
    assertFalse(showing(), "single click does nothing");
    runFx(() -> popup.getDoubleClickEventFilter().handle(click(stage, 2)));
    assertTrue(showing());
  }

  @Test
  void globalListenersAreAddedToNewWindows() {
    var other = callFx(Stage::new);
    var otherField = callFx(TextField::new);
    runFx(
        () -> {
          popup.addGlobalFocusListener();
          popup.addGlobalDoubleClickEventFilter();
          other.setScene(new Scene(new VBox(otherField), 100, 100));
          other.show();
          otherField.requestFocus();
        });
    assertTrue(FxTestSupport.waitFor(popup::isShowing, 3000));
    runFx(other::close);
  }

  @Test
  void defaultCloseButtonHidesPopup() {
    runFx(() -> popup.setVisible(Visibility.SHOW, field));
    assertTrue(showing());
    runFx(() -> popup.getKeyBoard().fireCloseEvent(new Event(Event.ANY)));
    waitHidden();
  }

  @Test
  void defaultCloseHandlerFiresWindowCloseRequest() {
    runFx(
        () -> {
          var requests = new AtomicInteger();
          button.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e -> requests.incrementAndGet());
          KeyBoardPopup.DEFAULT_CLOSE_HANDLER.handle(new Event(button, button, Event.ANY));
          KeyBoardPopup.DEFAULT_CLOSE_HANDLER.handle(new Event("no node", null, Event.ANY));
          assertEquals(1, requests.get());
        });
  }

  private static MouseEvent click(Stage source, int clickCount) {
    return new MouseEvent(
        source,
        source,
        MouseEvent.MOUSE_CLICKED,
        0,
        0,
        0,
        0,
        MouseButton.PRIMARY,
        clickCount,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        null);
  }
}
