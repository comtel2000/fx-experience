package org.comtel2000.keyboard.control;

/*
 * #%L
 * fx-onscreen-keyboard
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
		buttons.addListener((ListChangeListener<ButtonBase>) c -> {
			while (c.next()) {
				for (ButtonBase button : c.getAddedSubList()) {
					button.setFocusTraversable(false);
					button.setOnAction(event -> hide());
					buttonPane.getChildren().add(button);
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

	public ObservableList<String> getStylesheets() {
		return buttonPane.getStylesheets();
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
		if (!buttonPane.getChildren().isEmpty()) {
			HPos hpos = side != Side.LEFT ? side != Side.RIGHT ? HPos.CENTER : HPos.RIGHT : HPos.LEFT;
			VPos vpos = side != Side.TOP ? side != Side.BOTTOM ? VPos.CENTER : VPos.BOTTOM : VPos.TOP;
			// Point2D point2d = Utils.pointRelativeTo(node,
			// computePrefWidth(-1D), computePrefHeight(-1D), hpos, vpos, d,
			// d1, true);
			Point2D point2d = Utils.pointRelativeTo(node, buttonPane.getPrefWidth(), buttonPane.getPrefHeight(), hpos, vpos, d, d1, true);
			super.show(node, point2d.getX(), point2d.getY());
		}
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
