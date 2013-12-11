package org.adiusframework.ontology.frameworkontology.model;

import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.reasoner.Node;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

/**
 * Class representing a parameter assertion for an OWL individual (usually a
 * transformation or application).
 */
public class ParameterAssertion implements Comparable<ParameterAssertion> {
	/**
	 * The content manager to use.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The asserted property.
	 */
	private Node<OWLObjectPropertyExpression> property;

	/**
	 * The asserted individual.
	 */
	private ObjectInstance individual;

	/**
	 * The constructor.
	 * 
	 * @param contentManager
	 *            the content manager to use
	 * @param property
	 *            the asserted property
	 * @param individual
	 *            the asserted individual
	 */
	public ParameterAssertion(FrameworkOntologyManager contentManager, Node<OWLObjectPropertyExpression> property,
			ObjectInstance individual) {
		this.contentManager = contentManager;
		this.property = property;
		this.individual = individual;
	}

	@Override
	public int compareTo(ParameterAssertion o) {
		return this.property.toString().compareTo(o.property.toString());
	}

	public Node<OWLObjectPropertyExpression> getProperty() {
		return this.property;
	}

	public ObjectInstance getIndividual() {
		return this.individual;
	}

	public String getPropertyName() throws OWLInvalidContentException {
		return contentManager.getTransformationPropertyNameAnnotation(this.property);
	}
}