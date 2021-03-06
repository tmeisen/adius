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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.adiusframework.util.GenericToStringExtender;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="capability" type="{http://www.adiusframework.org/service/xml}serviceCapability"/>
 *         &lt;element name="informationmodel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="condition" type="{http://www.adiusframework.org/service/xml}resourceRequirementList"/>
 *         &lt;element name="effect" type="{http://www.adiusframework.org/service/xml}resourceEffectList"/>
 *         &lt;element name="binding" type="{http://www.adiusframework.org/service/xml}serviceBinding"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "capability", "informationmodel", "condition", "effect", "binding" })
@XmlRootElement(name = "serviceDefinition")
public class ServiceDefinition extends GenericToStringExtender implements Serializable {

	private final static long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected ServiceCapability capability;
	@XmlElement(required = true)
	protected String informationmodel;
	@XmlElement(required = true)
	protected ResourceRequirementList condition;
	@XmlElement(required = true)
	protected ResourceEffectList effect;
	@XmlElement(required = true)
	protected ServiceBinding binding;

	/**
	 * Gets the value of the capability property.
	 * 
	 * @return possible object is {@link ServiceCapability }
	 * 
	 */
	public ServiceCapability getCapability() {
		return capability;
	}

	/**
	 * Sets the value of the capability property.
	 * 
	 * @param value
	 *            allowed object is {@link ServiceCapability }
	 * 
	 */
	public void setCapability(ServiceCapability value) {
		this.capability = value;
	}

	/**
	 * Gets the value of the informationmodel property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getInformationmodel() {
		return informationmodel;
	}

	/**
	 * Sets the value of the informationmodel property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setInformationmodel(String value) {
		this.informationmodel = value;
	}

	/**
	 * Gets the value of the condition property.
	 * 
	 * @return possible object is {@link ResourceRequirementList }
	 * 
	 */
	public ResourceRequirementList getCondition() {
		return condition;
	}

	/**
	 * Sets the value of the condition property.
	 * 
	 * @param value
	 *            allowed object is {@link ResourceRequirementList }
	 * 
	 */
	public void setCondition(ResourceRequirementList value) {
		this.condition = value;
	}

	/**
	 * Gets the value of the effect property.
	 * 
	 * @return possible object is {@link ResourceEffectList }
	 * 
	 */
	public ResourceEffectList getEffect() {
		return effect;
	}

	/**
	 * Sets the value of the effect property.
	 * 
	 * @param value
	 *            allowed object is {@link ResourceEffectList }
	 * 
	 */
	public void setEffect(ResourceEffectList value) {
		this.effect = value;
	}

	/**
	 * Gets the value of the binding property.
	 * 
	 * @return possible object is {@link ServiceBinding }
	 * 
	 */
	public ServiceBinding getBinding() {
		return binding;
	}

	/**
	 * Sets the value of the binding property.
	 * 
	 * @param value
	 *            allowed object is {@link ServiceBinding }
	 * 
	 */
	public void setBinding(ServiceBinding value) {
		this.binding = value;
	}

}
