package org.comtel.javafx.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.comtel.javafx.xml.layout.Keyboard;
import org.slf4j.LoggerFactory;

public class KeyboardLayoutHandler {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(KeyboardLayoutHandler.class);
	
	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public KeyboardLayoutHandler() {
		try {
			context = JAXBContext.newInstance(new Class[] { Keyboard.class });
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Keyboard getLayout(String file) throws IOException {
		URL url = KeyboardLayoutHandler.class.getResource(file);
		if (url != null){
			return getLayout(url);
		}
		InputStream is = KeyboardLayoutHandler.class.getResourceAsStream(file);
		if (is != null){
			return getLayout(is);
		}
		throw new IOException("layout not found on: " + file);
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
	
	public Keyboard getLayout(InputStream is) throws IOException {

		Object obj = null;
		try {
			obj = unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new IOException("stream can not be read", e);
		}
		if (obj != null && obj instanceof Keyboard) {
			return (Keyboard) obj;
		}
		return null;
	}
}
