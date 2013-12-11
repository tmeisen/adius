package org.adiusframework.util.xml;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlLoader {
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public static Document loadDocument(File xmlFile) throws XmlParseException {
		try {
			return XmlLoader.factory.newDocumentBuilder().parse(xmlFile);
		} catch (Exception e) {
			throw new XmlParseException(e.getMessage());
		}
	}

	public static Document loadDocument(InputStream xmlStream) throws XmlParseException {
		try {
			return XmlLoader.factory.newDocumentBuilder().parse(xmlStream);
		} catch (Exception e) {
			throw new XmlParseException(e.getMessage());
		}
	}

	public static Node loadRoot(File xmlFile) throws XmlParseException {
		return XmlLoader.loadDocument(xmlFile).getDocumentElement();
	}

	public static Node loadRoot(String xmlFilePath) throws XmlParseException {
		return XmlLoader.loadDocument(xmlFilePath).getDocumentElement();
	}

	public static Node loadRoot(InputStream xmlStream) throws XmlParseException {
		return XmlLoader.loadDocument(xmlStream).getDocumentElement();
	}

	public static Document loadDocument(String xmlFilePath) throws XmlParseException {
		File xmlFile = new File(xmlFilePath);
		return XmlLoader.loadDocument(xmlFile);
	}
}