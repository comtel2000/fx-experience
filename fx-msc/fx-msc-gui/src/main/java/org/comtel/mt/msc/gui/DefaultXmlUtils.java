package org.comtel.mt.msc.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DefaultXmlUtils {

	/**
	 * create new DOM XML Document
	 * 
	 * @param rootName
	 * @param version
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createXmlDocument(String rootName, String version) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document doc = impl.createDocument(null, null, null);
		Element root = doc.createElement(rootName);
		root.setAttribute("version", version);

		doc.appendChild(root);

		return doc;
	}

	/**
	 * create new DOM XML Document with comment
	 * 
	 * @param rootName
	 * @param version
	 * @param date
	 * @return
	 * @throws ParserConfigurationException
	 */
	public static Document createXmlDocument(String rootName, String version, Date date) throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		Document doc = impl.createDocument(null, null, null);
		doc.appendChild(doc.createComment("author: " + ""));
		if (date != null)
			doc.appendChild(doc.createComment("created: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)));

		Element root = doc.createElement(rootName);
		root.setAttribute("version", version);

		doc.appendChild(root);

		return doc;
	}

	public static Element getElement(Document doc, String tagName, int index) {
		// given an XML document and a tag
		// return an Element at a given index
		NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
		return (Element) rows.item(index);
	}

	public static int getSize(Document doc, String tagName) {
		// given an XML document and a tag name
		// return the number of ocurances
		NodeList rows = doc.getDocumentElement().getElementsByTagName(tagName);
		return rows.getLength();
	}

	public static String getValue(Element e, String tagName) {
		try {
			// get node lists of a tag name from a Element
			NodeList elements = e.getElementsByTagName(tagName);

			Node node = elements.item(0);
			NodeList nodes = node.getChildNodes();

			// find a value whose value is non-whitespace
			String s;
			for (int i = 0; i < nodes.getLength(); i++) {
				s = ((Node) nodes.item(i)).getNodeValue().trim();
				if (s.equals("") || s.equals("\r")) {
					continue;
				} else
					return s;
			}

		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}

		return null;

	}

	public static String docToString(Document doc) {
		return toString(doc);
	}

	public static String toString(Node node) {

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
		// "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		// transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		// transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StringWriter sw = new StringWriter();
		StreamResult sr = new StreamResult(sw);

		DOMSource domSource = new DOMSource(node);
		try {
			transformer.transform(domSource, sr);
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
		return sw.toString();
	}

	public static byte[] doc2bytes(Document doc, boolean file) {
		try {
			doc.normalizeDocument();
			Source source = new DOMSource(doc);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Result result = new StreamResult(out);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			if (file) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");// linefeed
																		// formatting
			} else {
				transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");// remove
																						// xml
																						// header
			}
			transformer.transform(source, result);
			return out.toByteArray();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Document bytes2doc(byte[] xml) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new ByteArrayInputStream(xml));
	}

	public static Document file2doc(File file) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = factory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
		return doc;
	}

	public static XMLGregorianCalendar getXMLGregorianCalendar() throws DatatypeConfigurationException {
		DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
		return dataTypeFactory.newXMLGregorianCalendar(new GregorianCalendar());
	}
}
