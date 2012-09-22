package org.comtel.javafx.builder;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;

public class KeyboardTextFieldBuilder extends TextFieldBuilder {

		@Override
		public TextField build() {
	        final TextField tf = new TextField();
	        applyTo(tf);
	       
			tf.focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (newValue) {
						Point2D point = new Point2D(tf.getScene().getWindow().getX() + tf.getLayoutX(), tf.getScene()
								.getWindow().getY()
								+ tf.getLayoutY() + 40);
						System.out.println("focus on:"  +point);
					} else {
						System.out.println("focus off");
					}
				}
			});
			return tf;
		}
		
		
}
