//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.22 at 03:13:00 PM MEZ 
//

package org.adiusframework.service.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.adiusframework.util.GenericToStringExtender;

/**
 * <p>
 * Java class for resourceRequirementList complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="resourceRequirementList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resourceRequirement" type="{http://www.adiusframework.org/service/xml}resourceRequirement" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resourceRequirementList", propOrder = { "resourceRequirement" })
public class ResourceRequirementList extends GenericToStringExtender implements Serializable {

	private final static long serialVersionUID = 1L;
	protected List<ResourceRequirement> resourceRequirement;

	/**
	 * Gets the value of the resourceRequirement property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the resourceRequirement property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getResourceRequirement().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ResourceRequirement }
	 * 
	 * 
	 */
	public List<ResourceRequirement> getResourceRequirement() {
		if (resourceRequirement == null) {
			resourceRequirement = new ArrayList<ResourceRequirement>();
		}
		return this.resourceRequirement;
	}

}