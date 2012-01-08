package org.comtel.javafx.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Menu;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.stage.Popup;

import com.sun.javafx.Utils;

public class MultiKeyPopup extends Popup {

	private final ObservableList<ButtonBase> buttons;
	private final TilePane buttonPane;

	public static final String DEFAULT_STYLE_CLASS = "key-context-background";

	public MultiKeyPopup() {
		buttonPane = new TilePane();
		buttonPane.setId(DEFAULT_STYLE_CLASS);
		buttonPane.setPrefColumns(3);
		buttonPane.setFocusTraversable(false);

		buttons = FXCollections.observableArrayList();
		buttons.addListener(new ListChangeListener<ButtonBase>() {

			public void onChanged(Change<? extends ButtonBase> c) {
				while (c.next()) {
					for (ButtonBase button : c.getAddedSubList()) {
						button.setFocusTraversable(false);
						button.setOnAction(new EventHandler<ActionEvent>() {

							public void handle(ActionEvent event) {
								hide();
							}
						});
						buttonPane.getChildren().add(button);
					}
				}
			}

		});

		getContent().add(buttonPane);
		setAutoFix(true);
		setAutoHide(true);
	}

	public Pane getButtonPane() {
		return buttonPane;
	}

	public void setPaneStyle(String value) {
		buttonPane.setStyle(value);
	}

	public ObservableList<ButtonBase> getButtons() {
		return buttons;
	}

	public void show(Node node, Side side, double d, double d1) {
		if (node == null) {
			return;
		}

		Event.fireEvent(this, new Event(Menu.ON_SHOWING));
		if (buttonPane.getChildren().size() == 0) {
			return;
		} else {
			HPos hpos = side != Side.LEFT ? side != Side.RIGHT ? HPos.CENTER : HPos.RIGHT : HPos.LEFT;
			VPos vpos = side != Side.TOP ? side != Side.BOTTOM ? VPos.CENTER : VPos.BOTTOM : VPos.TOP;
			// Point2D point2d = Utils.pointRelativeTo(node,
			// computePrefWidth(-1D), computePrefHeight(-1D), hpos, vpos, d,
			// d1, true);
			Point2D point2d = Utils.pointRelativeTo(node, buttonPane.getPrefWidth(), buttonPane.getPrefHeight(), hpos,
					vpos, d, d1, true);
			super.show(node, point2d.getX(), point2d.getY());
			return;
		}
	}

	public final void setOnAction(EventHandler<ActionEvent> eventhandler) {
		onActionProperty().set(eventhandler);
	}

	public final EventHandler<ActionEvent> getOnAction() {
		return (EventHandler<ActionEvent>) onActionProperty().get();
	}

	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return onAction;
	}

	public void hide() {
		if (!isShowing()) {
			return;
		} else {
			Event.fireEvent(this, new Event(Menu.ON_HIDING));
			super.hide();
			Event.fireEvent(this, new Event(Menu.ON_HIDDEN));
			return;
		}
	}

	private ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<EventHandler<ActionEvent>>() {

		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, (EventHandler<? super ActionEvent>) get());
		}

	};

}
