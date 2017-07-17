package org.comtel2000.keyboard.xml;

import static org.comtel2000.keyboard.control.button.SymbolCode.BACK_SPACE;
import static org.comtel2000.keyboard.control.button.SymbolCode.DELETE;
import static org.comtel2000.keyboard.control.button.SymbolCode.LOCALE_SWITCH;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_CODES;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_H_GAP;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_EDGE_FLAGS;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_HEIGHT;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_ICON_STYLE;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_LABEL;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_LABEL_STYLE;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_OUTPUT_TEXT;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_KEY_WIDTH;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_MOVABLE;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_REPEATABLE;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_ROW_EDGE_FLAGS;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_STICKY;
import static org.comtel2000.keyboard.xml.XmlHelper.ATTR_V_GAP;
import static org.comtel2000.keyboard.xml.XmlHelper.FLAG_LEFT;
import static org.comtel2000.keyboard.xml.XmlHelper.FLAG_RIGHT;
import static org.comtel2000.keyboard.xml.XmlHelper.KEY;
import static org.comtel2000.keyboard.xml.XmlHelper.KEYBOARD;
import static org.comtel2000.keyboard.xml.XmlHelper.ROW;
import static org.comtel2000.keyboard.xml.XmlHelper.close;
import static org.comtel2000.keyboard.xml.XmlHelper.parseInt;
import static org.comtel2000.keyboard.xml.XmlHelper.readAttribute;
import static org.comtel2000.keyboard.xml.XmlHelper.readBooleanAttribute;
import static org.comtel2000.keyboard.xml.XmlHelper.readIntAttribute;

import java.io.InputStream;
import java.net.URI;
import java.util.Locale;
import java.util.stream.IntStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.comtel2000.keyboard.control.KeyboardPane;
import org.comtel2000.keyboard.control.button.KeyButton;
import org.comtel2000.keyboard.control.button.MultiKeyButton;
import org.comtel2000.keyboard.control.button.RepeatableKeyButton;
import org.comtel2000.keyboard.control.button.ShortPressKeyButton;
import org.slf4j.LoggerFactory;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

public class LayoutReader {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LayoutReader.class);

  private final XMLInputFactory factory = XMLInputFactory.newInstance();

  private final KeyboardPane pane;

  private int keyWidth;
  private int keyHeight;
  private int horizontalGap;
  private int rowWidth;
  private int minRowWidth;
  private int maxRowWidth;
  private int colIndex;
  private int rowIndex;

  public LayoutReader(KeyboardPane pane) {
    this.pane = pane;
  }

  public Region getLayout(URI layout) {

    resetValues();

    final GridPane rowPane = new GridPane();
    rowPane.setAlignment(Pos.CENTER);
    rowPane.getStyleClass().add("key-background-row");
    GridPane colPane = null;
    XMLStreamReader reader = null;
    try {
      reader = factory.createXMLStreamReader(layout.toURL().openStream());
      while (reader.hasNext()) {
        if (reader.next() == XMLStreamConstants.START_ELEMENT) {
          switch (reader.getLocalName()) {
          case KEYBOARD:
            initKeyboard(reader, rowPane);
            break;
          case ROW:
            colPane = addRow(reader, rowPane);
            break;
          case KEY:
            addKey(reader, colPane);
            break;
          default:
            break;
          }
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      close(reader);
    }
    return rowPane;
  }

  private void resetValues() {
    keyWidth = 10;
    keyHeight = 35;
    horizontalGap = 10;
    rowWidth = 0;
    minRowWidth = Integer.MAX_VALUE;
    maxRowWidth = 0;
    colIndex = -1;
    rowIndex = -1;
  }

  private void addKey(final XMLStreamReader reader, final GridPane colPane) {
    colIndex++;
    final ColumnConstraints cc = new ColumnConstraints();
    cc.setHgrow(Priority.SOMETIMES);
    cc.setFillWidth(true);
    cc.setPrefWidth(readIntAttribute(reader, ATTR_KEY_WIDTH, keyWidth));
    String code = reader.getAttributeValue(null, ATTR_CODES);
    if (code == null || code.isEmpty()) {
      Pane placeholder = new Pane();
      colPane.add(placeholder, colIndex, 0);
      colPane.getColumnConstraints().add(cc);
      rowWidth += cc.getPrefWidth();
      return;
    }

    final KeyButton button = createKeyButton(reader, code);

    cc.setHalignment(HPos.CENTER);
    button.setAlignment(Pos.BASELINE_CENTER);

    readAttribute(reader, ATTR_KEY_EDGE_FLAGS).ifPresent(flag -> {
      if (flag.equals(FLAG_RIGHT)) {
        cc.setHalignment(HPos.RIGHT);
        button.setAlignment(Pos.BASELINE_RIGHT);
      } else if (flag.equals(FLAG_LEFT)) {
        cc.setHalignment(HPos.LEFT);
        button.setAlignment(Pos.BASELINE_LEFT);
      } else {
        cc.setHalignment(HPos.CENTER);
      }
    });

    colPane.add(button, colIndex, 0);
    colPane.getColumnConstraints().add(cc);

    rowWidth += cc.getPrefWidth();
  }

  private KeyButton createKeyButton(final XMLStreamReader reader, String code) {

    String[] codes = code.split(",");
    final KeyButton button;
    if (codes.length > 1 || code.equals(Integer.toString(LOCALE_SWITCH))) {
      button = new MultiKeyButton(pane, pane.getStylesheets());
    } else if (readBooleanAttribute(reader, ATTR_REPEATABLE, false)) {
      button = new RepeatableKeyButton();
    } else {
      button = new ShortPressKeyButton();
    }

    updateStyle(reader, button);

    if (codes.length > 0 && !codes[0].isEmpty()) {
      button.setKeyCode(parseInt(codes[0]));
    }
    if (codes.length > 1) {
      IntStream.range(1, codes.length).map(i -> parseInt(codes[i]))
          .forEach(key -> button.addExtKeyCode(key, Character.toString((char) key)));
    }
    if (button.getKeyCode() == LOCALE_SWITCH) {
      pane.getLocales().forEach(
          l -> button.addExtKeyCode(LOCALE_SWITCH, l.getLanguage().toUpperCase(Locale.ENGLISH)));
    }
    String label = reader.getAttributeValue(null, ATTR_KEY_LABEL);
    button.setText(label != null ? label : Character.toString((char) button.getKeyCode()));
    readAttribute(reader, ATTR_KEY_OUTPUT_TEXT).ifPresent(button::setKeyText);

    if (!button.isRepeatable()
        && (button.getKeyCode() == BACK_SPACE || button.getKeyCode() == DELETE)) {
      button.setOnLongPressed(e -> pane.clearAll());
    }

    return button;
  }

  private void updateStyle(final XMLStreamReader reader, final KeyButton button) {
    button.setFocusTraversable(false);
    button.setOnShortPressed(pane);

    button.setMinHeight(1);
    button.setPrefHeight(keyHeight);
    button.setPrefWidth(keyWidth);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setMovable(readBooleanAttribute(reader, ATTR_MOVABLE, false));

    if (button.isMovable()) {
      pane.installMoveHandler(button);
      button.getStyleClass().add("movable-style");
    }
    button.setSticky(readBooleanAttribute(reader, ATTR_STICKY, false));
    if (button.isSticky()) {
      button.getStyleClass().add("sticky-style");
    }
    readAttribute(reader, ATTR_KEY_LABEL_STYLE).ifPresent(s -> {
      if (s.charAt(0) == '.') {
        for (String style : s.split(";")) {
          button.getStyleClass().add(style.substring(1));
        }
      }
    });
    readAttribute(reader, ATTR_KEY_ICON_STYLE).ifPresent(s -> {
      if (s.charAt(0) == '.') {
        loadStyle(button, s);
      } else if (s.charAt(0) == '@') {
        loadImage(button, s);
      }
    });
  }

  private GridPane addRow(final XMLStreamReader reader, final GridPane rowPane) {
    rowIndex++;
    colIndex = -1;
    maxRowWidth = Math.max(maxRowWidth, rowWidth);
    if (rowWidth > 0) {
      minRowWidth = Math.min(minRowWidth, rowWidth);
    }
    logger.trace("{} - [{}/{}]", rowWidth, minRowWidth, maxRowWidth);
    rowWidth = 0;
    final GridPane colPane = new GridPane();
    colPane.getStyleClass().add("key-background-column");
    rowPane.add(colPane, 0, rowIndex);
    RowConstraints rc = new RowConstraints();
    rc.setPrefHeight(keyHeight);
    colPane.getRowConstraints().add(rc);
    readAttribute(reader, ATTR_ROW_EDGE_FLAGS)
        .ifPresent(flag -> rc.setValignment(VPos.valueOf(flag.toUpperCase())));
    return colPane;
  }

  private void initKeyboard(final XMLStreamReader reader, final GridPane rowPane) {
    readIntAttribute(reader, ATTR_V_GAP).ifPresent(rowPane::setVgap);
    horizontalGap = readIntAttribute(reader, ATTR_H_GAP, horizontalGap);
    keyWidth = readIntAttribute(reader, ATTR_KEY_WIDTH, keyWidth);
    keyHeight = readIntAttribute(reader, ATTR_KEY_HEIGHT, keyHeight);
  }

  private void loadImage(final KeyButton button, final String img) {
    try (InputStream is = KeyboardPane.class.getResourceAsStream(img.replace('@', '/') + ".png")) {
      final Image image = new Image(is);
      if (!image.isError()) {
        logger.error("Image: {} not found", img);
        return;
      }
      button.setGraphic(new ImageView(image));
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  private void loadStyle(final KeyButton button, final String style) {
    logger.trace("Load css style: {}", style);
    final Label icon = new Label();
    for (String s : style.split(";")) {
      icon.getStyleClass().add(s.substring(1));
    }
    icon.setMaxSize(40, 40);
    button.setContentDisplay(ContentDisplay.CENTER);
    button.setGraphic(icon);
  }

}
