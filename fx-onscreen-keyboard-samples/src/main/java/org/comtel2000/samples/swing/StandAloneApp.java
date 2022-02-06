package org.comtel2000.samples.swing;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import org.comtel2000.keyboard.control.DefaultLayer;
import org.comtel2000.keyboard.control.KeyBoardPopup;
import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.keyboard.control.KeyboardType;
import org.comtel2000.swing.robot.NativeAsciiRobotHandler;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class StandAloneApp extends JFrame {

  @Serial
  private static final long serialVersionUID = 291529300404728337L;
  private static String[] arguments;
  private int posX, posY;

  public StandAloneApp() {
  }

  public static void main(String[] args) {
    arguments = args;
    SwingUtilities.invokeLater(() -> {
      StandAloneApp app = new StandAloneApp();
      app.init();
    });
  }

  public static Map<String, String> getParameters() {
    if (arguments.length == 0) {
      return Collections.emptyMap();
    }
    Map<String, String> parameters = new HashMap<>();
    for (String arg : arguments) {
      String[] data = arg.split("=");
      if (data.length == 2) {
        parameters.put(data[0].replace("--", "").trim(), data[1].trim());
      }
    }
    return parameters;
  }

  public void init() {

    setLayout(new BorderLayout());

    // create javafx panel
    final JFXPanel javafxPanel = new JFXPanel();
    javafxPanel.setFocusable(false);
    javafxPanel.setOpaque(false);

    add(javafxPanel, BorderLayout.CENTER);

    JWindow fxKeyboard = new JWindow();
    fxKeyboard.setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
    fxKeyboard.getContentPane().add(javafxPanel);
    fxKeyboard.setFocusable(false);
    fxKeyboard.setBackground(null);

    fxKeyboard.pack();
    fxKeyboard.setLocationByPlatform(true);

    // create JavaFX scene
    Platform.runLater(() -> createScene(javafxPanel));

  }

  public void createScene(JFXPanel javafxPanel) {

    Scene scene = new Scene(new Group(), 0, 0);
    javafxPanel.setScene(scene);
    KeyboardPane kb = new KeyboardPane();
    kb.setLayer(DefaultLayer.NUMBLOCK);
    kb.addRobotHandler(new NativeAsciiRobotHandler());
    kb.setOnKeyboardCloseButton(e -> System.exit(0));

    Map<String, String> params = getParameters();
    if (params.isEmpty() && arguments.length != 0) {
      showHelp();
    }
    try {
      if (params.containsKey("help")) {
        showHelp();
      }
      if (params.containsKey("scale")) {
        kb.setScale(Double.parseDouble(params.get("scale")));
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

    popup.show(scene.getWindow());
  }

  private void showHelp() {
    System.out.println();
    System.out.println("\t--scale=<double>\tset the initial scale");
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
      throw new Exception("invalid position: " + p);
    }

    String[] pos = p.split(",");
    if (pos.length == 2) {
      posX = Integer.parseInt(pos[0]);
      posY = Integer.parseInt(pos[1]);
    }
  }
}
