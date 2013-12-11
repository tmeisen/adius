package org.adiusframework.ontology.resourceontology.model;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ResourceType extends ResourceOntologyEntity {

	private String name;

	public ResourceType(String name, OWLNamedIndividual individual) {
		super(individual);
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getName()).append(" (").append(getIndividual()).append(")").toString();
	}

}
