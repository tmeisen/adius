package org.adiusframework.ontology.frameworkontology.model;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyConstants;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

/**
 * This class represents instances of the ontology class "Object".
 * 
 * @author Alexander Tenbrock
 * 
 */
public class ObjectInstance extends OWLIndividualNodeEntity {

	private OWLClass individualClass;

	public ObjectInstance(FrameworkOntologyManager contentManager, Node<OWLNamedIndividual> individualNode) {
		super(contentManager, individualNode);

		// we have to identify the class of the individual that is a subclass of
		// object, so first we search for all classes that are represented by
		// this individual
		Set<OWLClass> classes = getContentManager().getDirectClassesOfIndividualNode(getIndividualNode());

		// second we try to find the class that satisfies the subclass axiom
		OWLClass objectClass = getContentManager().getOwlClass(FrameworkOntologyConstants.CLASS_OBJECT);
		setIndividualClass(getContentManager().filterClass(classes, objectClass));
	}

	/**
	 * Returns the database name for this individual.
	 * 
	 * @return the database name
	 * @throws OWLInvalidContentException
	 */
	public String getDatabaseName() throws OWLInvalidContentException {
		return getContentManager().getIndividualDatabaseName(getIndividualNode());
	}

	/**
	 * Returns the type name of this object. This is the shortest name of all
	 * its asserted classes.
	 * 
	 * @return the type name
	 */
	public String getTypeName() {
		return getContentManager().getClassName(getIndividualClass());
	}

	public OWLClass getIndividualClass() {
		return individualClass;
	}

	public void setIndividualClass(OWLClass individualClass) {
		this.individualClass = individualClass;
	}

}
