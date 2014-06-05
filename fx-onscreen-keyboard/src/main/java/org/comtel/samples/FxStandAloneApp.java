package org.comtel.samples;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.comtel.javafx.control.KeyBoardPopup;
import org.comtel.javafx.control.KeyBoardPopupBuilder;
import org.comtel.javafx.robot.RobotFactory;
import org.slf4j.LoggerFactory;

public class FxStandAloneApp extends Application {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(FxStandAloneApp.class);

	private int posX = 0;
	private int posY = 0;

	@Override
	public void start(Stage stage) {

		stage.setTitle("FX Keyboard (" + System.getProperty("javafx.runtime.version") + ")");
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);

		Path path = null;
		try {
			path = Paths.get(FxStandAloneApp.class.getResource("/xml/numblock").toURI());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		String fontUrl = FxStandAloneApp.class.getResource("/font/FontKeyboardFX.ttf").toExternalForm();
		Font.loadFont(fontUrl, -1);

		final KeyBoardPopup popup = KeyBoardPopupBuilder.create().initScale(1.0).initLocale(Locale.ENGLISH).addIRobot(RobotFactory.createNativeAsciiRobot()).layerPath(path).build();

		Scene scene = new Scene(new Group(), 1, 1);
		popup.setOwner(scene);

		scene.getStylesheets().add(FxStandAloneApp.class.getResource("/css/KeyboardButtonStyle.css").toExternalForm());

		stage.setOnCloseRequest(e -> System.exit(0));

		stage.setScene(scene);
		stage.show();

		popup.getScene().getWindow().setX(posX);
		popup.getScene().getWindow().setY(posY);

		popup.show(stage);

	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
