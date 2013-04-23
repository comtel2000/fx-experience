package org.comtel.mt.msc.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	private AutoCloseable controller;

	public static void main(String[] args) throws IOException {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("MSC FX GUI (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(true);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("view/MscImageView.fxml"));
		Parent root = (Parent) fxmlLoader.load();
		controller = fxmlLoader.getController();
		Scene scene = new Scene(root);
		String css = this.getClass().getResource("main.css").toExternalForm();
		scene.getStylesheets().add(css);
		
		stage.setScene(scene);
		stage.show();

	}

	@Override
	public void stop() throws Exception {
		if (controller != null) {
			controller.close();
		}
		super.stop();
	}
}
