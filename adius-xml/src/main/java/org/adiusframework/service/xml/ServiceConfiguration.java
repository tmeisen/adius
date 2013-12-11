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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.adiusframework.util.GenericToStringExtender;

/**
 * <p>
 * Java class for serviceConfiguration complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="serviceConfiguration">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="activator" type="{http://www.adiusframework.org/service/xml}activator"/>
 *         &lt;element name="parameter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceConfiguration", propOrder = { "activator", "parameter" })
public class ServiceConfiguration extends GenericToStringExtender implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected Activator activator;
	protected String parameter;

	/**
	 * Gets the value of the activator property.
	 * 
	 * @return possible object is {@link Activator }
	 * 
	 */
	public Activator getActivator() {
		return activator;
	}

	/**
	 * Sets the value of the activator property.
	 * 
	 * @param value
	 *            allowed object is {@link Activator }
	 * 
	 */
	public void setActivator(Activator value) {
		this.activator = value;
	}

	/**
	 * Gets the value of the parameter property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * Sets the value of the parameter property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setParameter(String value) {
		this.parameter = value;
	}

}