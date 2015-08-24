package org.comtel2000.samples.fx;

import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.swing.robot.NativeAsciiRobotHandler;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StandAloneApp extends Application {

	private int posX = 0, posY = 0;

	@Override
	public void start(Stage stage) {

		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);

		KeyboardPane kb = new KeyboardPane();
		kb.setLayer(DefaultLayer.NUMBLOCK);
		kb.addRobotHandler(new NativeAsciiRobotHandler());
		kb.setOnKeyboardCloseButton(e -> System.exit(0));

		Map<String, String> params = getParameters().getNamed();
		if (params.isEmpty() && !getParameters().getRaw().isEmpty()) {
			showHelp();
		}
		try {
			if (params.containsKey("help")) {
				showHelp();
			}
			if (params.containsKey("scale")) {
				kb.setScale(Double.valueOf(params.get("scale")));
			}
			if (params.containsKey("locale")) {
				kb.setLocale(parseLocale(params.get("locale")));
			}
			if (params.containsKey("pos")) {
				parsePosition(params.get("pos"));
			}
			if (params.containsKey("layout")) {
				kb.setLayerPath((Paths.get(this.getClass().getResource(params.get("layout")).toURI())));
			}
			if (params.containsKey("type")) {
				kb.setKeyboardType(KeyboardType.valueOf(params.get("type").toUpperCase()));
			}

			kb.load();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			showHelp();
		}

		KeyBoardPopup popup = new KeyBoardPopup(kb);
		popup.setX(posX);
		popup.setY(posY);

		Scene scene = new Scene(new Group(), 0.1, 0.1);
		stage.setScene(scene);
		stage.show();

		popup.registerScene(scene);
		popup.setVisible(true);
		// popup.show(stage);

	}

	private void showHelp() {
		System.out.println();
		System.out.println("\t--scale=<double>\tset the intial scale");
		System.out.println("\t--lang=<locale>\t\tsetting keyboard language (en,de,ru,..)");
		System.out.println("\t--layout=<path>\t\tpath to custom layout xml");
		System.out.println("\t--pos=<x,y>\t\tinitial keyboard position");
		System.out.println("\t--type=<type>\t\tvkType like numeric, email, url, text(default)");
		System.out.println("\t--help\t\t\tthis help screen");
		System.exit(0);
	}

	private Locale parseLocale(String l) throws Exception {
		if (l == null || l.isEmpty()) {
			throw new ParseException("invalid locale", 0);
		}
		String[] lang = l.split("_");
		if (lang.length == 2) {
			return new Locale(lang[0], lang[1]);
		}
		return Locale.forLanguageTag(l);
	}

	private void parsePosition(String p) throws Exception {
		if (p == null || p.isEmpty()) {
			throw new Exception("invalid position: " + String.valueOf(p));
		}

		String[] pos = p.split(",");
		if (pos.length == 2) {
			posX = Integer.valueOf(pos[0]);
			posY = Integer.valueOf(pos[1]);
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
