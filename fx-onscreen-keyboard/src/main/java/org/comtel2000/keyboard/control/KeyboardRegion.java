/* Copyright (c) 2025 comtel2000 */
package org.comtel2000.keyboard.control;

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
import static org.comtel2000.keyboard.xml.XmlHelper.readDoubleAttribute;

import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
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
import javafx.scene.layout.RowConstraints;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class KeyboardRegion extends GridPane {

  private final XMLInputFactory factory = XMLInputFactory.newDefaultFactory();

  private static final Logger logger = LoggerFactory.getLogger(KeyboardRegion.class);
  private final KeyboardPane keyboard;

  KeyboardRegion(KeyboardPane keyboard, URL layout) {
    this.keyboard = keyboard;
    build(layout);
  }

  private void build(URL layout) {

    setAlignment(Pos.CENTER);
    getStyleClass().add("key-background-row");

    double keyWidth = 10;
    double keyHeight = 35;
    double horizontalGap = 5;
    double verticalGap = 5;
    int colIndex = -1;
    int rowIndex = -1;
    double rowWidth = 0;
    double minRowWidth = -1;
    double maxRowWidth = -1;

    GridPane colPane = null;
    XMLStreamReader reader = null;
    try {
      reader = factory.createXMLStreamReader(layout.openStream());
      while (reader.hasNext()) {
        reader.next();
        switch (reader.getEventType()) {
          case XMLStreamConstants.START_ELEMENT:
            switch (reader.getLocalName()) {
              case KEYBOARD:
                verticalGap = readDoubleAttribute(reader, ATTR_V_GAP, verticalGap);
                setVgap(verticalGap);
                horizontalGap = readDoubleAttribute(reader, ATTR_H_GAP, horizontalGap);

                keyWidth = readDoubleAttribute(reader, ATTR_KEY_WIDTH, keyWidth);
                keyHeight = readDoubleAttribute(reader, ATTR_KEY_HEIGHT, keyHeight);
                break;
              case ROW:
                rowIndex++;
                colIndex = -1;
                rowWidth = 0;
                colPane = new GridPane();
                colPane.setHgap(horizontalGap);
                colPane.getStyleClass().add("key-background-column");
                add(colPane, 0, rowIndex);
                RowConstraints rc = new RowConstraints();
                rc.setPrefHeight(keyHeight);
                colPane.getRowConstraints().add(rc);
                readAttribute(reader, ATTR_ROW_EDGE_FLAGS)
                    .ifPresent(flag -> rc.setValignment(VPos.valueOf(flag.toUpperCase())));
                break;

              case KEY:
                colIndex++;
                ColumnConstraints cc = new ColumnConstraints();
                cc.setHgrow(Priority.SOMETIMES);
                cc.setFillWidth(true);
                cc.setPrefWidth(readDoubleAttribute(reader, ATTR_KEY_WIDTH, keyWidth));
                String code = reader.getAttributeValue(null, ATTR_CODES);
                if (code == null || code.isEmpty()) {
                  Pane placeholder = new Pane();
                  colPane.add(placeholder, colIndex, 0);
                  colPane.getColumnConstraints().add(cc);
                  rowWidth += cc.getPrefWidth();
                  continue;
                }

                String[] codes = code.split(",");
                final KeyButton button;
                if (codes.length > 1
                    || code.equals(Integer.toString(StandardKeyCode.LOCALE_SWITCH))) {
                  button = new MultiKeyButton(this, getStylesheets());
                } else if (readBooleanAttribute(reader, ATTR_REPEATABLE, false)) {
                  button = new RepeatableKeyButton();
                } else {
                  button = new ShortPressKeyButton();
                }

                button.setFocusTraversable(false);
                button.setPickOnBounds(
                    true); // Ensures the button reacts to clicks/taps on the entire area, including
                // label/icon (fixes #92)
                button.setOnShortPressed(keyboard);

                button.setMinHeight(1);
                button.setPrefHeight(keyHeight);
                button.setPrefWidth(keyWidth);
                button.setMaxWidth(Double.MAX_VALUE);

                button.setMovable(readBooleanAttribute(reader, ATTR_MOVABLE, false));

                if (button.isMovable()) {
                  keyboard.installMoveHandler(button);
                  button.getStyleClass().add("movable-style");
                }
                button.setSticky(readBooleanAttribute(reader, ATTR_STICKY, false));
                if (button.isSticky()) {
                  button.getStyleClass().add("sticky-style");
                }
                readAttribute(reader, ATTR_KEY_LABEL_STYLE)
                    .ifPresent(
                        s -> {
                          if (s.charAt(0) == '.') {
                            for (var style : s.split(";")) {
                              button.getStyleClass().add(style.substring(1));
                            }
                          }
                        });

                if (codes.length > 0 && !codes[0].isEmpty()) {
                  button.setKeyCode(parseInt(codes[0]));
                }
                if (codes.length > 1) {
                  for (var i = 1; i < codes.length; i++) {
                    int keyCode = parseInt(codes[i]);
                    button.addExtKeyCode(keyCode, Character.toString((char) keyCode));
                  }
                }

                if (button.getKeyCode() == StandardKeyCode.LOCALE_SWITCH) {
                  for (var l : keyboard.getAvailableLocales().keySet()) {
                    button.addExtKeyCode(
                        StandardKeyCode.LOCALE_SWITCH, l.getLanguage().toUpperCase(Locale.ENGLISH));
                  }
                }

                readAttribute(reader, ATTR_KEY_ICON_STYLE)
                    .ifPresent(
                        s -> {
                          if (s.charAt(0) == '.') {
                            logger.trace("Load css style: {}", s);
                            var icon = new Label();
                            for (var style : s.split(";")) {
                              icon.getStyleClass().add(style.substring(1));
                            }
                            icon.setMaxSize(40, 40);
                            button.setContentDisplay(ContentDisplay.CENTER);
                            icon.setMouseTransparent(true);
                            button.setGraphic(icon);
                          } else if (s.charAt(0) == '@') {
                            try (InputStream is =
                                KeyboardPane.class.getResourceAsStream(
                                    s.replace('@', '/') + ".png")) {
                              Image image = new Image(is);
                              if (!image.isError()) {
                                var imageView = new ImageView(image);
                                imageView.setMouseTransparent(true);
                                button.setGraphic(imageView);
                              } else {
                                logger.error("Image: {} not found", s);
                              }
                            } catch (Exception e) {
                              logger.error(e.getMessage(), e);
                            }
                          }
                        });

                var label = reader.getAttributeValue(null, ATTR_KEY_LABEL);
                button.setText(
                    label != null ? label : Character.toString((char) button.getKeyCode()));
                readAttribute(reader, ATTR_KEY_OUTPUT_TEXT).ifPresent(button::setKeyText);

                cc.setHalignment(HPos.CENTER);
                button.setAlignment(Pos.BASELINE_CENTER);

                readAttribute(reader, ATTR_KEY_EDGE_FLAGS)
                    .ifPresent(
                        flag -> {
                          switch (flag) {
                            case FLAG_RIGHT:
                              cc.setHalignment(HPos.RIGHT);
                              button.setAlignment(Pos.BASELINE_RIGHT);
                              break;
                            case FLAG_LEFT:
                              cc.setHalignment(HPos.LEFT);
                              button.setAlignment(Pos.BASELINE_LEFT);
                              break;
                            default:
                              cc.setHalignment(HPos.CENTER);
                              break;
                          }
                        });

                switch (button.getKeyCode()) {
                  case java.awt.event.KeyEvent.VK_SPACE:
                    keyboard.installMoveHandler(button);
                    break;
                  case StandardKeyCode.BACK_SPACE, StandardKeyCode.DELETE:
                    if (!button.isRepeatable()) {
                      button.setOnLongPressed(
                          e -> {
                            keyboard.sendToComponent((char) 97, true);
                            keyboard.sendToComponent(
                                (char) java.awt.event.KeyEvent.VK_DELETE, keyboard.isControl());
                          });
                    }
                    break;
                  default:
                    break;
                }
                colPane.add(button, colIndex, 0);
                colPane.getColumnConstraints().add(cc);
                rowWidth += colPane.getHgap() + cc.getPrefWidth();
                break;
              default:
                break;
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            if (reader.getLocalName().equals(ROW)) {
              maxRowWidth = Math.max(maxRowWidth, rowWidth);
              minRowWidth = minRowWidth == -1.0 ? rowWidth : Math.min(minRowWidth, rowWidth);
              logger.trace("{} - [{}/{}] url: {}", rowWidth, rowIndex, colIndex, layout.getPath());
            }
            break;
          case XMLStreamConstants.END_DOCUMENT:
            // setMinWidth(minRowWidth);
            // setMaxWidth(maxRowWidth);
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    } finally {
      close(reader);
    }
  }
}
