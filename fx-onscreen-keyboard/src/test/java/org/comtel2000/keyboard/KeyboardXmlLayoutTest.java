package org.comtel2000.keyboard;

/*******************************************************************************
 * Copyright (c) 2016 comtel2000
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.comtel2000.keyboard.xml.KeyboardLayoutHandler;
import org.comtel2000.keyboard.xml.layout.Keyboard;
import org.comtel2000.keyboard.xml.layout.ObjectFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

public class KeyboardXmlLayoutTest {

  private final Path parent = Paths.get("src", "main", "resources");

  private static KeyboardLayoutHandler handler;

  @BeforeClass
  public static void open() {
    handler = new KeyboardLayoutHandler();
  }

  @AfterClass
  public static void close() {

  }

  @Test
  public void testGetLayout() throws IOException {
    Keyboard kb = handler.getLayout("/xml/default/kb-layout.xml");
    Assert.assertNotNull(kb);
    Assert.assertFalse(kb.getRow().isEmpty());

    for (Keyboard.Row row : kb.getRow()) {
      System.out.println(String.format("row size: %s (%s) ", row.getKey().size(),row.getRowEdgeFlags()));
      String rows = row.getKey().stream().map(key -> String.format("%s[%s]", key.getCodes(),(key.getKeyLabel() != null ? key.getKeyLabel() : key.getKeyIconStyle()))).collect(Collectors.joining(";"));
      System.out.println(rows);
    }
  }

  @Test
  public void validateLayouts() throws IOException {
    String[] layouts = new String[] { "kb-layout", "kb-layout-shift", "kb-layout-sym", "kb-layout-sym-shift", "kb-layout-ctrl" };
    for (String layout : layouts) {
      Keyboard kb = handler.getLayout("/xml/default/" + layout + ".xml");
      Assert.assertNotNull(kb);
      Assert.assertFalse(kb.getRow().isEmpty());
    }
  }

  @Test
  public void validateUrls() throws IOException, URISyntaxException {

    URL url = KeyboardLayoutHandler.class.getResource("/xml/default");
    Path defaultPath = Paths.get(url.toURI());
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(defaultPath)) {
      stream.forEach(p -> {
        if (Files.isDirectory(p)) {
          Locale l = new Locale(p.getFileName().toString());
          System.out.println("custom locale: " + l);
        }
      });
    }
    Assert.assertNotNull(url);

  }

  @Test
  public void validateXmls() throws IOException, URISyntaxException {

    Files.walkFileTree(parent.resolve("xml"), new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (!file.getFileName().toString().endsWith(".xml")) {
          return FileVisitResult.CONTINUE;
        }
        try (InputStream is = Files.newInputStream(file)) {
          System.out.println("validate file: " + file);
          validate(is);
        } catch (JAXBException | SAXException | IOException e) {
          e.printStackTrace();
          Assert.fail(e.getMessage());
          return FileVisitResult.TERMINATE;
        }
        return FileVisitResult.CONTINUE;
      }
    });

  }

  private void validate(InputStream stream) throws JAXBException, SAXException, IOException {
    SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = sf.newSchema(parent.resolve("kb-layout.xsd").toUri().toURL());

    JAXBContext jc = JAXBContext.newInstance(Keyboard.class);

    Unmarshaller unmarshaller = jc.createUnmarshaller();
    unmarshaller.setSchema(schema);
    unmarshaller.setEventHandler((event) -> {
      System.out.println("\nEVENT");
      System.out.println("SEVERITY:  " + event.getSeverity());
      System.out.println("MESSAGE:  " + event.getMessage());
      System.out.println("LINKED EXCEPTION:  " + event.getLinkedException());
      System.out.println("LOCATOR");
      System.out.println("    LINE NUMBER:  " + event.getLocator().getLineNumber());
      System.out.println("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
      System.out.println("    OFFSET:  " + event.getLocator().getOffset());
      System.out.println("    OBJECT:  " + event.getLocator().getObject());
      System.out.println("    NODE:  " + event.getLocator().getNode());
      System.out.println("    URL:  " + event.getLocator().getURL());
      Assert.fail(event.getMessage());
      return true;
    });
    unmarshaller.unmarshal(stream);
    stream.close();
  }
}
