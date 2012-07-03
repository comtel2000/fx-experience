package org.comtel.javafx.xml;

import java.io.IOException;
import java.net.URL;

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

	public Keyboard getLayout(String file) throws IOException {
		return getLayout(KeyboardLayoutHandler.class.getResource(file));
	}
	
	/**
	 * "resources/xml/kb-layout.xml"
	 * 
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public Keyboard getLayout(URL url) throws IOException {

		Object obj = null;
		try {
			obj = unmarshaller.unmarshal(url);
		} catch (JAXBException e) {
			throw new IOException("file: " + url + " can not be read", e);
		}
		if (obj != null && obj instanceof Keyboard) {
			return (Keyboard) obj;
		}
		return null;
	}
}
