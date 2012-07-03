package org.comtel.javafx;

import java.io.IOException;

import junit.framework.Assert;

import org.comtel.javafx.xml.KeyboardLayoutHandler;
import org.comtel.javafx.xml.layout.Keyboard;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyboardXmlLayoutTest {

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
		Keyboard kb = handler.getLayout("/xml/kb-layout.xml");
		Assert.assertNotNull(kb);
		Assert.assertFalse(kb.getRow().isEmpty());

		for (Keyboard.Row row : kb.getRow()) {
			System.err.println("\nRow " + row.getRowEdgeFlags());
			for (Keyboard.Row.Key key : row.getKey()) {
				System.err.println(key.getCodes() + "\t"
						+ (key.getKeyLabel() != null ? key.getKeyLabel() : key.getKeyIconStyle()));
			}
		}
	}
}
