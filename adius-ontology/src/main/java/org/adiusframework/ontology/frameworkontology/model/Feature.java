package org.adiusframework.ontology.frameworkontology.model;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.Node;

/**
 * Class representing a feature class or axiom class in the ontology.
 * 
 */
public class Feature {

	/**
	 * The content Manager object.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The OWL class node representing this feature or axiom.
	 */
	private Node<OWLClass> classNode;

	/**
	 * The parameter restrictions for this feature or axiom.
	 */
	private Vector<ParameterRestriction> parameterRestrictions = new Vector<ParameterRestriction>();

	/**
	 * Constructor.
	 */
	public Feature(FrameworkOntologyManager contentManager, Node<OWLClass> classNode) {
		this.contentManager = contentManager;
		this.classNode = classNode;

		// get list of all object property restrictions of this class
		Set<OWLQuantifiedObjectRestriction> restrictions = contentManager.getObjectPropertyRestrictions(classNode);

		// build list of parameter restrictions
		for (OWLQuantifiedObjectRestriction restriction : restrictions)
			if (contentManager.isParameterProperty(restriction.getProperty()))
				parameterRestrictions.add(new ParameterRestriction(restriction.getProperty(), restriction.getFiller()));

		Collections.sort(parameterRestrictions);
	}

	/**
	 * Returns the name of this feature.
	 * 
	 * @return the name of this feature
	 */
	public String getRepresentativeName() {
		return contentManager.getShortestNodeLabel(this.classNode);
	}

	/**
	 * Checks if one of the equivalent individuals representing this feature has
	 * the given name. Case does not matter.
	 * 
	 * @param name
	 *            the name to check
	 * @return true if the feature has that name
	 */
	public boolean hasName(String name) {
		for (OWLClass cls : classNode)
			if (contentManager.getEntityName(cls).equalsIgnoreCase(name))
				return true;
		return false;
	}

	/**
	 * Checks if this feature is satisfiable (= not Owl:Nothing).
	 * 
	 * @return true if this feature is satisfiable
	 */
	public boolean isSatisfiable() {
		return contentManager.getReasoner().isSatisfiable(this.classNode.getRepresentativeElement());
	}

	/**
	 * Returns the list of parameter restrictions.
	 * 
	 * @return vector with parameter restrictions
	 */
	public Vector<ParameterRestriction> getParameterRestrictions() {
		return parameterRestrictions;
	}

	/**
	 * Checks if the given individual belongs to this feature class.
	 * 
	 * @param individual
	 *            the individual
	 * @return true if the individual belongs to this feature class
	 */
	public boolean hasInstance(OWLNamedIndividual individual) {
		return contentManager.doesIndividualBelongToClass(individual, classNode.getRepresentativeElement());
	}

}
