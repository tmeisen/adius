package org.adiusframework.ontology.frameworkontology.model;

import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

/**
 * The class represents an application defined in the ontology.
 * 
 * @author Alexander Tenbrock
 * 
 */
public class Application extends Transformation {

	public Application(FrameworkOntologyManager contentManager, Node<OWLNamedIndividual> individualNode) {
		super(contentManager, individualNode);
	}

}
