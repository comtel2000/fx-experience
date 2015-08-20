package org.comtel2000.keyboard.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Menu;
import javafx.scene.layout.TilePane;
import javafx.stage.Popup;

class MultiKeyPopup extends Popup {

	private final TilePane buttonPane;

	public static final String DEFAULT_STYLE_CLASS = "key-context-background";

	MultiKeyPopup() {
		// setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
		buttonPane = new TilePane();
		buttonPane.setId(DEFAULT_STYLE_CLASS);

		buttonPane.setFocusTraversable(false);

		buttonPane.getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next()) {
				c.getAddedSubList().forEach(node -> {
					node.setFocusTraversable(false);
					if (node instanceof ButtonBase) {
						((ButtonBase) node).setOnAction(event -> hide());
					}
				});
			}
		});

		getContent().add(buttonPane);
		setAutoFix(true);
		setAutoHide(true);
	}

	public void addButton(Button button) {

		buttonPane.getChildren().add(button);
		if (buttonPane.getChildren().size() > 8) {
			buttonPane.setPrefColumns(5);
			buttonPane.setPrefRows(2);
		} else if (buttonPane.getChildren().size() > 6) {
			buttonPane.setPrefColumns(4);
			buttonPane.setPrefRows(2);
		} else if (buttonPane.getChildren().size() > 4) {
			buttonPane.setPrefColumns(3);
			buttonPane.setPrefRows(2);
		} else if (buttonPane.getChildren().size() > 3) {
			buttonPane.setPrefColumns(2);
			buttonPane.setPrefRows(2);
		} else if (buttonPane.getChildren().size() > 1) {
			buttonPane.setPrefColumns(buttonPane.getChildren().size());
			buttonPane.setPrefRows(1);
		}
	}

	public ObservableList<String> getStylesheets() {
		return buttonPane.getStylesheets();
	}

	public void setPaneStyle(String value) {
		buttonPane.setStyle(value);
	}

	public void show(Node node, double scale) {
		if (node == null) {
			return;
		}

		Event.fireEvent(this, new Event(Menu.ON_SHOWING));
		if (!buttonPane.getChildren().isEmpty()) {

			if (buttonPane.getScaleX() != scale) {
				buttonPane.setScaleX(scale);
				buttonPane.setScaleY(scale);
			}

			double offsetX = getWidth() / 2;
			double offsetY = getHeight() / 2;

			Bounds bounds = node.localToScreen(node.getBoundsInLocal());
			super.show(node, bounds.getMinX() + (bounds.getWidth() / 2) - offsetX, bounds.getMinY() - offsetY);

			if (offsetX < 1 || offsetY < 1) {
				centerPosition();
			}
		}
	}

	private void centerPosition() {
		// fix position after size calculation
		setX(getX() - getWidth() / 2);
		setY(getY() - getHeight() / 2);
	}

	public final void setOnAction(EventHandler<ActionEvent> eventhandler) {
		onActionProperty().set(eventhandler);
	}

	public final EventHandler<ActionEvent> getOnAction() {
		return onActionProperty().get();
	}

	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return onAction;
	}

	@Override
	public void hide() {
		if (isShowing()) {
			Event.fireEvent(this, new Event(Menu.ON_HIDING));
			super.hide();
			Event.fireEvent(this, new Event(Menu.ON_HIDDEN));
		}
	}

	private ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<EventHandler<ActionEvent>>() {

		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, get());
		}

	};

}
