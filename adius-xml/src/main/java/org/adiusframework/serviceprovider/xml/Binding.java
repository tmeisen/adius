//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.22 at 03:12:59 PM MEZ 
//

package org.adiusframework.serviceprovider.xml;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.adiusframework.util.GenericToStringExtender;

/**
 * <p>
 * Java class for binding complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="binding">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="protocol" type="{http://www.adiusframework.org/serviceprovider/xml}protocol"/>
 *         &lt;element name="data" type="{http://www.adiusframework.org/serviceprovider/xml}protocolData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "binding", propOrder = { "protocol", "data" })
public class Binding extends GenericToStringExtender implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected Protocol protocol;
	@XmlElement(required = true)
	protected ProtocolData data;

	/**
	 * Gets the value of the protocol property.
	 * 
	 * @return possible object is {@link Protocol }
	 * 
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Sets the value of the protocol property.
	 * 
	 * @param value
	 *            allowed object is {@link Protocol }
	 * 
	 */
	public void setProtocol(Protocol value) {
		this.protocol = value;
	}

	/**
	 * Gets the value of the data property.
	 * 
	 * @return possible object is {@link ProtocolData }
	 * 
	 */
	public ProtocolData getData() {
		return data;
	}

	/**
	 * Sets the value of the data property.
	 * 
	 * @param value
	 *            allowed object is {@link ProtocolData }
	 * 
	 */
	public void setData(ProtocolData value) {
		this.data = value;
	}

}
