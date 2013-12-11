package org.adiusframework.util.xml;

public class XmlParseException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 4480839901263336789L;

	public XmlParseException() {
		super();
	}

	public XmlParseException(String message) {
		super(message);
	}

	public XmlParseException(Throwable cause) {
		super(cause);
	}

	public XmlParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
