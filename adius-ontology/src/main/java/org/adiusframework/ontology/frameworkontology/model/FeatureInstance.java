package org.adiusframework.ontology.frameworkontology.model;

import java.util.List;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.ontology.frameworkontology.model.Feature;
import org.adiusframework.ontology.frameworkontology.model.ObjectInstance;
import org.adiusframework.ontology.frameworkontology.model.ParameterRestriction;

/**
 * This class represents a feature instance in the ontology. Since features and
 * axioms are equivalent (internally), this is also used to represent axiom
 * instances.
 * 
 */
public class FeatureInstance {

	/**
	 * The content manager.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The individual representing this feature instance.
	 */
	private OWLNamedIndividual individual;

	/**
	 * The feature (or axiom) this instance belongs to.
	 */
	private Feature feature;

	/**
	 * Constructor.
	 * 
	 * @param contentManager
	 *            the content manager to be used
	 * @param individual
	 *            the individual representing this feature instance.
	 * @throws OWLInvalidContentException
	 */
	public FeatureInstance(FrameworkOntologyManager contentManager, OWLNamedIndividual individual)
			throws OWLInvalidContentException {
		this.contentManager = contentManager;
		this.individual = individual;

		// find the feature
		for (Feature feat : contentManager.getFeatures())
			if (feat.hasInstance(individual)) {
				this.feature = feat;
				break;
			}

		// if no feature was found, search for an axiom
		if (this.feature == null)
			for (Feature feat : contentManager.getAxioms())
				if (feat.hasInstance(individual)) {
					this.feature = feat;
					break;
				}
	}

	/**
	 * Returns all parameter assertions for this feature instance.
	 * 
	 * @return list of parameter assertions.
	 */
	public List<ObjectInstance> getParameterAssertions() {
		Vector<ObjectInstance> result = new Vector<ObjectInstance>();
		for (ParameterRestriction restriction : this.getFeature().getParameterRestrictions()) {
			NodeSet<OWLNamedIndividual> values = this.contentManager.getReasoner().getObjectPropertyValues(
					this.individual, restriction.getProperty());
			if (values.isEmpty())
				result.add(null);
			else if (!values.isSingleton())
				result.add(null); // this shouldn't happen [throw exception?]
			else
				result.add(new ObjectInstance(this.contentManager, values.iterator().next()));
		}

		return result;
	}

	/**
	 * Returns the feature (or axiom) this is an instance of.
	 * 
	 * @return the Feature class
	 */
	public Feature getFeature() {
		return this.feature;
	}
}
