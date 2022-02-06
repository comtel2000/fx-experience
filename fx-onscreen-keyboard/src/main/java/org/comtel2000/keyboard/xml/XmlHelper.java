package org.comtel2000.keyboard.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Optional;

public class XmlHelper {

    public static final String KEYBOARD = "Keyboard";
    public static final String ROW = "Row";
    public static final String KEY = "Key";

    public static final String ATTR_KEY_WIDTH = "keyWidth";
    public static final String ATTR_KEY_HEIGHT = "keyHeight";
    public static final String ATTR_H_GAP = "horizontalGap";
    public static final String ATTR_V_GAP = "verticalGap";
    public static final String ATTR_ROW_EDGE_FLAGS = "rowEdgeFlags";
    public static final String ATTR_KEY_EDGE_FLAGS = "keyEdgeFlags";

    public static final String ATTR_KEY_LABEL = "keyLabel";
    public static final String ATTR_KEY_LABEL_STYLE = "keyLabelStyle";
    public static final String ATTR_KEY_ICON_STYLE = "keyIconStyle";
    public static final String ATTR_KEY_OUTPUT_TEXT = "keyOutputText";

    public static final String ATTR_CODES = "codes";
    public static final String ATTR_MOVABLE = "movable";
    public static final String ATTR_REPEATABLE = "repeatable";
    public static final String ATTR_STICKY = "sticky";

    public static final String FLAG_RIGHT = "right";
    public static final String FLAG_LEFT = "left";

    private XmlHelper() {
    }

    public static Optional<String> readAttribute(XMLStreamReader reader, String attr) {
        return Optional.ofNullable(reader.getAttributeValue(null, attr));
    }

    public static Optional<Integer> readIntAttribute(XMLStreamReader reader, String attr) {
        return Optional.ofNullable(reader.getAttributeValue(null, attr)).flatMap(s -> {
            try {
                return Optional.of(Integer.valueOf(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }

    public static Optional<Double> readDoubleAttribute(XMLStreamReader reader, String attr) {
        return Optional.ofNullable(reader.getAttributeValue(null, attr)).flatMap(s -> {
            try {
                return Optional.of(Double.valueOf(s));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        });
    }

    public static int readIntAttribute(XMLStreamReader reader, String attr, int defaultValue) {
        String a = reader.getAttributeValue(null, attr);
        if (a == null || a.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(a);
        } catch (NumberFormatException e) {
            return defaultValue;
        }

    }

    public static double readDoubleAttribute(XMLStreamReader reader, String attr, double defaultValue) {
        String a = reader.getAttributeValue(null, attr);
        if (a == null || a.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(a);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean readBooleanAttribute(XMLStreamReader reader, String attr,
                                               boolean defaultValue) {
        String a = reader.getAttributeValue(null, attr);
        if (a == null || a.isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(a);
    }

    public static int parseInt(String i) {
        return i.startsWith("0x") ? Integer.parseInt(i.substring(2), 16) : Integer.parseInt(i);
    }

    public static void close(XMLStreamReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                // nothing
            }
        }
    }

}
