package org.adiusframework.ontology.frameworkontology.model;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

/**
 * A class describing a transformation defined in the ontology.
 * 
 * @author Alex Tenbrock
 * @author Tobias Meisen
 * 
 */
public class Transformation extends OWLIndividualNodeEntity {

	/**
	 * An ordered list of all parameter assertions.
	 */
	private List<ParameterAssertion> parameterAssertions;

	/**
	 * The constructor
	 * 
	 * @param contentManager
	 * @param individualNode
	 */
	public Transformation(FrameworkOntologyManager contentManager, Node<OWLNamedIndividual> individualNode) {
		super(contentManager, individualNode);
		this.parameterAssertions = new Vector<ParameterAssertion>();

		// retrieve a list of all parameter properties used in the ontology
		this.parameterAssertions = getContentManager().getParameterAssertion(individualNode.getRepresentativeElement());

		// sort the list
		Collections.sort(this.parameterAssertions);
	}

	/**
	 * Checks if one of the equivalent individuals representing this
	 * transformation has the given name. Case does not matter.
	 * 
	 * @param name
	 *            the name to check
	 * @return true if the feature has that name
	 */
	public boolean hasName(String name) {
		for (OWLNamedIndividual cls : getIndividualNode())
			if (getContentManager().getEntityName(cls).equalsIgnoreCase(name))
				return true;
		return false;
	}

	/**
	 * Returns the parameter assertions for this transformation.
	 * 
	 * @return the list of parameter assertions
	 */
	public List<ParameterAssertion> getParameterAssertions() {
		return this.parameterAssertions;
	}

	public Set<OWLNamedIndividual> getPositivePreconditionAssertions() {
		return getContentManager().getPositivePreconditionAssertions(getIndividualNode().getRepresentativeElement());
	}

	public Set<OWLNamedIndividual> getNegativePreconditionAssertions() {
		return getContentManager().getNegativePreconditionAssertions(getIndividualNode().getRepresentativeElement());
	}

	public Set<OWLNamedIndividual> getPositiveEffectAssertions() {
		return getContentManager().getPositiveEffectAssertions(getIndividualNode().getRepresentativeElement());
	}

	public Set<OWLNamedIndividual> getNegativeEffectAssertions() {
		return getContentManager().getNegativeEffectAssertions(getIndividualNode().getRepresentativeElement());
	}
}
