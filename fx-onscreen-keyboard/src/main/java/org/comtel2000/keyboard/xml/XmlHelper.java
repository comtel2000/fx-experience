/*******************************************************************************
 * Copyright (c) 2017 comtel2000
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 *
 * 3. Neither the name of the comtel2000 nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

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
