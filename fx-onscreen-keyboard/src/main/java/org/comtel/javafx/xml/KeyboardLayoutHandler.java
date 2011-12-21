/*
 * Copyright (c)omtel 2011.
 */

package org.comtel.javafx.xml;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.comtel.javafx.xml.layout.Keyboard;

public class KeyboardLayoutHandler {

	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public KeyboardLayoutHandler() {
		try {
			context = JAXBContext.newInstance(new Class[] { Keyboard.class });
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * "resources/xml/kb-layout.xml"
	 * 
	 * @param file
	 * @return
	 */
	public Keyboard getLayout(String filename) throws IllegalStateException {

		InputStream is = KeyboardLayoutHandler.class.getResourceAsStream(filename);

		if (is == null) {
			throw new IllegalStateException("file: " + filename + " can not be found");
		}

		Object obj = null;
		try {
			obj = unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new IllegalStateException("file: " + filename + " can not be read", e);
		}
		if (obj != null && obj instanceof Keyboard) {
			return (Keyboard) obj;
		}
		return null;
	}
}
