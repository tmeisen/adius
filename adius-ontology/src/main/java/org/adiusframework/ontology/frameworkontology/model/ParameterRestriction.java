package org.adiusframework.ontology.frameworkontology.model;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

/**
 * This class represents a parameter restriction of an OWL class, usually a
 * feature class.
 * 
 */
public class ParameterRestriction implements Comparable<ParameterRestriction> {

	/**
	 * The restricted property.
	 */
	private OWLObjectPropertyExpression property;

	/**
	 * The class expression the property is restricted to.
	 */
	private OWLClassExpression classExpression;

	/**
	 * The constructor.
	 * 
	 * @param property
	 *            the restricted property
	 * @param classExpression
	 *            class expression the property is restricted to
	 */
	public ParameterRestriction(OWLObjectPropertyExpression property, OWLClassExpression classExpression) {
		this.property = property;
		this.classExpression = classExpression;
	}

	@Override
	public int compareTo(ParameterRestriction o) {
		return this.property.toString().compareTo(o.property.toString());
	}

	public OWLObjectPropertyExpression getProperty() {
		return this.property;
	}

	public OWLClassExpression getClassExpression() {
		return this.classExpression;
	}
}