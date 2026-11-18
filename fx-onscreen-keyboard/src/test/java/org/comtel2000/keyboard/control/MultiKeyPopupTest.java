/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.control;

import static org.comtel2000.keyboard.control.FxTestSupport.runFx;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MultiKeyPopupTest {

  private Stage stage;

  @BeforeAll
  static void initToolkit() {
    FxTestSupport.startToolkit();
  }

  @AfterEach
  void closeStage() {
    if (stage != null) {
      runFx(stage::close);
    }
  }

  private static TilePane pane(MultiKeyPopup popup) {
    return (TilePane) popup.getContent().get(0);
  }

  @Test
  void layoutGrowsWithButtons() {
    runFx(
        () -> {
          var popup = new MultiKeyPopup();
          var pane = pane(popup);
          assertTrue(pane.getStyleClass().contains(MultiKeyPopup.DEFAULT_STYLE_CLASS));
          assertTrue(popup.isAutoHide());
          assertTrue(popup.isAutoFix());

          int[][] expected = {
            {1, 1}, {2, 1}, {3, 1}, {4, 1}, {5, 1}, {3, 2}, {4, 2}, {4, 2}, {5, 2}, {5, 2}
          };
          for (int i = 0; i < expected.length; i++) {
            var button = new Button(String.valueOf(i));
            button.setFocusTraversable(true);
            popup.addButton(button);
            assertFalse(button.isFocusTraversable());
            assertEquals(expected[i][0], pane.getPrefColumns(), "columns for " + (i + 1));
            assertEquals(expected[i][1], pane.getPrefRows(), "rows for " + (i + 1));
          }

          popup.setPaneStyle("-fx-background-color: red;");
          assertEquals("-fx-background-color: red;", pane.getStyle());
        });
  }

  @Test
  void onActionProperty() {
    runFx(
        () -> {
          var popup = new MultiKeyPopup();
          var count = new AtomicInteger();
          popup.setOnAction(e -> count.incrementAndGet());
          assertSame(popup.onActionProperty().get(), popup.getOnAction());
          popup.fireEvent(new ActionEvent());
          assertEquals(1, count.get());
        });
  }

  @Test
  void showAndHide() {
    runFx(
        () -> {
          var popup = new MultiKeyPopup();
          var events = new AtomicInteger();
          popup.addEventHandler(Menu.ON_SHOWING, e -> events.incrementAndGet());
          popup.addEventHandler(Menu.ON_HIDDEN, e -> events.incrementAndGet());

          popup.show(null, 1.0);
          assertEquals(0, events.get());
          popup.hide(); // not showing: no events
          assertEquals(0, events.get());

          var anchor = new Button("x");
          stage = new Stage();
          stage.setScene(new Scene(new StackPane(anchor), 200, 200));
          stage.show();

          popup.show(anchor, 1.0);
          assertEquals(1, events.get(), "no buttons: nothing to show");
          assertFalse(popup.isShowing());

          var button = new Button("a");
          popup.addButton(button);
          popup.show(anchor, 1.5);
          assertTrue(popup.isShowing());
          assertEquals(1.5, pane(popup).getScaleX());

          button.fire(); // buttons close the popup
          assertFalse(popup.isShowing());
          assertEquals(3, events.get());
        });
  }
}
