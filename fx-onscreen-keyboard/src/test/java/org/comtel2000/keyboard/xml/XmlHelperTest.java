package org.comtel2000.keyboard.xml;

import org.junit.jupiter.api.Test;

import javax.xml.stream.*;
import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class XmlHelperTest {

  @Test
  public void readAttribute() throws XMLStreamException, FactoryConfigurationError, IOException {
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
  public void readIntAttribute() throws XMLStreamException, FactoryConfigurationError, IOException {
    URL url = XmlHelperTest.class.getResource("/xml/default/kb-layout.xml");
    XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(url.openStream());
    reader.next();
    reader.require(XMLStreamConstants.START_ELEMENT, null, XmlHelper.KEYBOARD);

    assertEquals(Integer.valueOf(40),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_WIDTH).orElse(-1));
    assertEquals(Integer.valueOf(30),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_KEY_HEIGHT).orElse(-1));
    assertEquals(Integer.valueOf(0),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_H_GAP).orElse(-1));
    assertEquals(Integer.valueOf(0),
        XmlHelper.readIntAttribute(reader, XmlHelper.ATTR_V_GAP).orElse(-1));
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
  public void readBooleanAttribute()
      throws XMLStreamException, FactoryConfigurationError, IOException {
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
}