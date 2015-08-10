package org.comtel2000.keyboard;

/*
 * #%L
 * fx-onscreen-keyboard
 * %%
 * Copyright (C) 2014 - 2015 comtel2000
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the comtel2000 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.comtel2000.keyboard.xml.KeyboardLayoutHandler;
import org.comtel2000.keyboard.xml.layout.Keyboard;
import org.junit.AfterClass;
import org.junit.Assert;
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
		Keyboard kb = handler.getLayout("/xml/default/kb-layout.xml");
		Assert.assertNotNull(kb);
		Assert.assertFalse(kb.getRow().isEmpty());

		for (Keyboard.Row row : kb.getRow()) {
			System.err.println("\nRow " + row.getRowEdgeFlags());
			for (Keyboard.Row.Key key : row.getKey()) {
				System.err.println(key.getCodes() + "\t" + (key.getKeyLabel() != null ? key.getKeyLabel() : key.getKeyIconStyle()));
			}
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
					System.err.println(l);
				}
			});
		}
		Assert.assertNotNull(url);

	}
}
