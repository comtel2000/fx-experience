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

import java.util.Collection;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.transform.Scale;

public class MultiKeyButton extends KeyButton {

	private ObservableList<KeyButton> extKeyCodes;

	private MultiKeyPopup context;

	private final DoubleProperty scaleProperty;

	private final Collection<String> styles;

	public MultiKeyButton(DoubleProperty scaleProperty, Collection<String> styles) {
		this.scaleProperty = scaleProperty;
		this.styles = styles;
	}

	public ObservableList<KeyButton> getExtKeyCodes() {
		if (extKeyCodes == null) {
			extKeyCodes = FXCollections.observableArrayList();
			extKeyCodes.addListener((ListChangeListener<KeyButton>) c -> {
				while (c.next()) {
					for (KeyButton button : c.getAddedSubList()) {
						getContext().getButtons().add(button);
					}
				}
			});
		}
		return extKeyCodes;
	}

	public MultiKeyPopup getContext() {
		if (context == null) {

			context = new MultiKeyPopup();
			context.getStylesheets().addAll(styles);

			context.setOnHidden(event -> {
				getParent().getParent().setEffect(null);
				getParent().getParent().setDisable(false);

			});
			setOnLongPressed(event -> {
				context.getButtonPane().getTransforms().setAll(new Scale(scaleProperty.get(), scaleProperty.get(), 1, 0, 0, 0));

				// getParent().getParent().setEffect(new BoxBlur());
				getParent().getParent().setDisable(true);
				setFocused(false);
				context.show((Node) event.getSource(), Side.TOP, -getPrefWidth(), -getPrefHeight());
			});

		}
		return context;
	}

	public void addExtKeyCode(int extKeyCode) {
		addExtKeyCode(extKeyCode, null, null);
	}

	public void addExtKeyCode(int extKeyCode, String label, ObservableList<String> styleClasses) {
		ShortPressKeyButton button = new ShortPressKeyButton(Character.toString((char) extKeyCode));

		if (styleClasses != null) {
			button.getStyleClass().addAll(styleClasses);
		} else {
			button.setId("key-context-button");
		}
		if (label != null) {
			button.setText(label);
		}
		button.setFocusTraversable(false);

		// TODO: add to css style
		button.setPrefWidth(this.getPrefWidth());
		button.setPrefHeight(this.getPrefHeight());

		button.setKeyCode(extKeyCode);
		button.setOnShortPressed(getOnShortPressed());

		getExtKeyCodes().add(button);
	}

	public boolean isContextAvailable() {
		return context != null && extKeyCodes != null;
	}

}
