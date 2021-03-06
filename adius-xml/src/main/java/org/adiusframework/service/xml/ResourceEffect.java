//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.22 at 03:13:00 PM MEZ 
//

package org.adiusframework.service.xml;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import org.adiusframework.util.GenericToStringExtender;

/**
 * <p>
 * Java class for resourceEffect complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="resourceEffect">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="capability" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="protocol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceEffect")
public class ResourceEffect extends GenericToStringExtender implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlAttribute
	protected String capability;
	@XmlAttribute
	protected String type;
	@XmlAttribute
	protected String protocol;

	/**
	 * Gets the value of the capability property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCapability() {
		return capability;
	}

	/**
	 * Sets the value of the capability property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCapability(String value) {
		this.capability = value;
	}

	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

	/**
	 * Gets the value of the protocol property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets the value of the protocol property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setProtocol(String value) {
		this.protocol = value;
	}

}
