package org.adiusframework.ontology.resourceontology.model;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ResourceOntologyEntity {

	private OWLNamedIndividual individual;

	public ResourceOntologyEntity(OWLNamedIndividual individual) {
		setIndividual(individual);
	}

	public OWLNamedIndividual getIndividual() {
		return individual;
	}

	protected void setIndividual(OWLNamedIndividual individual) {
		this.individual = individual;
	}

}
