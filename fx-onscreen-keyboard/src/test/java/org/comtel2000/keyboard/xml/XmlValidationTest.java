package org.comtel2000.keyboard.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

public class XmlValidationTest {

  private final Path mainParent = Paths.get("src", "main", "resources", "xml");
  private final Path testParent = Paths.get("src", "test", "resources", "xml");
  private final Path xsd = Paths.get("src", "test", "resources", "kb-layout.xsd");

  @Test
  public void validateXmlLayouts() throws Exception {
    Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
    v.setSchemaSource(new StreamSource(xsd.toFile()));
    Files.walk(mainParent).filter(p -> !p.toFile().isDirectory() && p.getFileName().toString().endsWith(".xml")).forEach(p -> {
      System.out.println("test file: " + p);
      ValidationResult r = v.validateInstance(new StreamSource(p.toFile()));
      assertTrue(r.isValid());
    });
  }

  @Test
  public void validateFail() throws Exception {
    Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
    v.setSchemaSource(new StreamSource(xsd.toFile()));

    ValidationResult r = v
        .validateInstance(new StreamSource(testParent.resolve("kb-layout_fails.xml").toFile()));
    assertFalse(r.isValid());
    r.getProblems().forEach(System.out::println);

  }

}
