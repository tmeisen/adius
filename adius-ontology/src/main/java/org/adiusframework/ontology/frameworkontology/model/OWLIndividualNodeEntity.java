package org.adiusframework.ontology.frameworkontology.model;

import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

public abstract class OWLIndividualNodeEntity {

	/**
	 * The content manager.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The individual node representing this owl entity.
	 */
	private Node<OWLNamedIndividual> individualNode;

	/**
	 * Constructor of a constant using the passed information to initialize the
	 * constant.
	 * 
	 * @param contentManager
	 *            ContentManager representing the ontology the constant is
	 *            defined by.
	 * @param individualNode
	 *            Node within the information model representing this constant
	 */
	public OWLIndividualNodeEntity(FrameworkOntologyManager contentManager, Node<OWLNamedIndividual> individualNode) {
		setContentManager(contentManager);
		setIndividualNode(individualNode);
	}

	public FrameworkOntologyManager getContentManager() {
		return contentManager;
	}

	public void setContentManager(FrameworkOntologyManager contentManager) {
		this.contentManager = contentManager;
	}

	public Node<OWLNamedIndividual> getIndividualNode() {
		return individualNode;
	}

	public void setIndividualNode(Node<OWLNamedIndividual> individualNode) {
		this.individualNode = individualNode;
	}

	/**
	 * Return the a representative name for this individual node.
	 * 
	 * @return the name
	 */
	public String getRepresentativeName() {
		return getContentManager().getShortestNodeLabel(getIndividualNode());
	}

}
