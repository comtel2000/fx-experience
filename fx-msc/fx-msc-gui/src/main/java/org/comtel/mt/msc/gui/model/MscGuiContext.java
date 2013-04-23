package org.comtel.mt.msc.gui.model;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.comtel.mt.msc.gui.model.jaxb.MSCGUI;
import org.slf4j.LoggerFactory;

public class MscGuiContext {

	private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MscGuiContext.class);

	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public MscGuiContext() {
		try {
			context = JAXBContext.newInstance(new Class[] { MSCGUI.class });
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public MSCGUI get(InputStream is) throws IOException {

		Object obj = null;
		try {
			obj = unmarshaller.unmarshal(is);
		} catch (JAXBException e) {
			throw new IOException("stream can not be read", e);
		}
		if (obj != null) {
			return (MSCGUI) obj;
		}
		return null;
	}

}