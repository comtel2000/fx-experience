package org.comtel.javafx.control;

import javafx.fxml.JavaFXBuilderFactory;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class KeyBoardBuilderFactory implements BuilderFactory {

	private JavaFXBuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();
	
	@Override
	public Builder<?> getBuilder(Class<?> c) {
		if (c == KeyBoardPopup.class){
			return new KeyBoardPopupBuilder();
		}
		return defaultBuilderFactory.getBuilder(c);
	}

}
