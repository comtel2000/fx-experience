/* Copyright (c) 2025-2000 comtel2000 (BSD 3-Clause) */
package org.comtel2000.keyboard.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.xml.stream.*;
import org.junit.jupiter.api.Test;

class XmlHelperTest {

  @Test
  void readAttribute() throws XMLStreamException, FactoryConfigurationError, IOException {
    URL url = XmlHelperTest.class.getResource("/xml/default/kb-layout.xml");
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(url.openStream());
    reader.next();
    reader.require(XMLStreamConstants.START_ELEMENT, null, XmlHelper.KEYBOARD);

    assertEquals("40", XmlHelper.readAttribute(reader, XmlHelper.ATTR_KEY_WIDTH).orElse(null));
    assertEquals("30", XmlHelper.readAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT).orElse(null));
    assertEquals("0", XmlHelper.readAttribute(reader, XmlHelper.ATTR_H_GAP).orElse(null));
    assertEquals("0", XmlHelper.readAttribute(reader, XmlHelper.ATTR_V_GAP).orElse(null));
    assertNull(XmlHelper.readAttribute(reader, "verticalGapX").orElse(null));
    assertNull(XmlHelper.readAttribute(reader, "").orElse(null));
    assertNull(XmlHelper.readAttribute(reader, null).orElse(null));
  }

  @Test
  void readIntAttribute() throws XMLStreamException, FactoryConfigurationError, IOException {
    URL url = XmlHelperTest.class.getResource("/xml/default/kb-layout.xml");
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(url.openStream());
    reader.next();
    reader.require(XMLStreamConstants.START_ELEMENT, null, XmlHelper.KEYBOARD);

    assertEquals(
        Integer.valueOf(40),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_WIDTH).orElse(-1));
    assertEquals(
        Integer.valueOf(30),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT).orElse(-1));
    assertEquals(
        Integer.valueOf(0), XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_H_GAP).orElse(-1));
    assertEquals(
        Integer.valueOf(0), XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_V_GAP).orElse(-1));
    assertNull(XmlHelper.readIntAttribute(reader, "verticalGapX").orElse(null));

    assertEquals(40, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_WIDTH, 1));
    assertEquals(30, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT, 1));
    assertEquals(0, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_H_GAP, 1));
    assertEquals(0, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_V_GAP, 1));
    assertEquals(1, XmlHelper.readIntAttribute(reader, "verticalGapX", 1));
    assertEquals(1, XmlHelper.readIntAttribute(reader, "", 1));
    assertEquals(1, XmlHelper.readIntAttribute(reader, null, 1));
  }

  @Test
  void readBooleanAttribute() throws XMLStreamException, FactoryConfigurationError, IOException {
    URL url = XmlHelperTest.class.getResource("/xml/default/kb-layout.xml");
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(url.openStream());
    reader.next();
    reader.require(XMLStreamConstants.START_ELEMENT, null, XmlHelper.KEYBOARD);

    assertFalse(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_REPEATABLE, false));
    assertFalse(XmlHelper.readBooleanAttribute(reader, "", false));

    while (reader.hasNext()) {
      reader.next();
      if (!reader.isStartElement() || !XmlHelper.KEY.equals(reader.getLocalName())) {
        continue;
      }
      if (32 == XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_CODES, -1)) {
        assertTrue(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_REPEATABLE, false));
        assertTrue(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_MOVABLE, false));
      }

      if (-1 == XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_CODES, 0)) {
        assertTrue(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_STICKY, false));
      }
    }
  }

  private static XMLStreamReader keyboard(String attributes) throws XMLStreamException {
    XMLStreamReader reader =
        XMLInputFactory.newInstance()
            .createXMLStreamReader(new StringReader("<Keyboard " + attributes + "/>"));
    reader.next();
    return reader;
  }

  @Test
  void readDoubleAttribute() throws XMLStreamException {
    XMLStreamReader reader = keyboard("keyWidth=\"40.5\" keyHeight=\"x\" verticalGap=\"\"");
    assertEquals(40.5, XmlHelper.readDoubleAttribute(reader, XmlHelper.ATTR_KEY_WIDTH).orElse(-1d));
    assertTrue(XmlHelper.readDoubleAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT).isEmpty());
    assertTrue(XmlHelper.readDoubleAttribute(reader, "missing").isEmpty());

    assertEquals(40.5, XmlHelper.readDoubleAttribute(reader, XmlHelper.ATTR_KEY_WIDTH, 1d));
    assertEquals(1d, XmlHelper.readDoubleAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT, 1d));
    assertEquals(1d, XmlHelper.readDoubleAttribute(reader, XmlHelper.ATTR_V_GAP, 1d));
    assertEquals(1d, XmlHelper.readDoubleAttribute(reader, "missing", 1d));
  }

  @Test
  void invalidIntAttributesUseDefault() throws XMLStreamException {
    XMLStreamReader reader = keyboard("keyWidth=\"4.5\" keyHeight=\"abc\"");
    assertTrue(XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_WIDTH).isEmpty());
    assertTrue(XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT).isEmpty());
    assertEquals(7, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_WIDTH, 7));
    assertEquals(7, XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT, 7));
  }

  @Test
  void booleanAttributeValues() throws XMLStreamException {
    XMLStreamReader reader = keyboard("sticky=\"TRUE\" movable=\"yes\" repeatable=\"false\"");
    assertTrue(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_STICKY, false));
    assertFalse(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_MOVABLE, true));
    assertFalse(XmlHelper.readBooleanAttribute(reader, XmlHelper.ATTR_REPEATABLE, true));
    assertTrue(XmlHelper.readBooleanAttribute(reader, "missing", true));
  }

  @Test
  void parseIntSupportsHex() {
    assertEquals(98, XmlHelper.parseInt("98"));
    assertEquals(-5, XmlHelper.parseInt("-5"));
    assertEquals(0x62, XmlHelper.parseInt("0x62"));
    assertEquals(0x20AC, XmlHelper.parseInt("0x20AC"));
    assertThrows(NumberFormatException.class, () -> XmlHelper.parseInt("x"));
  }

  @Test
  void closeIgnoresNullAndClosedReaders() throws XMLStreamException {
    XmlHelper.close(null);
    XMLStreamReader reader = keyboard("");
    XmlHelper.close(reader);
    XmlHelper.close(reader);
  }
}
