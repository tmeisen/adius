package org.adiusframework.ontology.frameworkontology.model;

import java.util.Set;

import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyConstants;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;

public class Constant extends OWLIndividualNodeEntity {

	private OWLClass individualClass;

	private String value;

	/**
	 * Constructor of a constant using the passed information to initialize the
	 * constant.
	 * 
	 * @param contentManager
	 *            ContentManager representing the ontology the constant is
	 *            defined by.
	 * @param individualNode
	 *            Node within the information model representing this constant
	 * @throws OWLInvalidContentException
	 */
	public Constant(FrameworkOntologyManager contentManager, Node<OWLNamedIndividual> individualNode)
			throws OWLInvalidContentException {
		super(contentManager, individualNode);

		// we have to find the type of the constant that is a direct subclass of
		// object, hence first we find all direct classes of the individual
		OWLClass constantClass = getContentManager().getOwlClass(FrameworkOntologyConstants.CLASS_CONSTANT);
		Set<OWLClass> classes = getContentManager()
				.getDirectClassesOfIndividualNode(getIndividualNode(), constantClass);

		// second we try to find the class that satisfies the subclass axiom
		OWLClass objectClass = getContentManager().getOwlClass(FrameworkOntologyConstants.CLASS_OBJECT);
		setIndividualClass(getContentManager().filterClass(classes, objectClass));

		// third the value has to be identified
		setValue(getContentManager().getIndividualDatabaseName(individualNode));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getRepresentativeName();
	}

	public OWLClass getIndividualClass() {
		return individualClass;
	}

	public void setIndividualClass(OWLClass individualClass) {
		this.individualClass = individualClass;
	}

	public String getRepresentativeClassName() {
		return getContentManager().getClassName(getIndividualClass());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((individualClass == null) ? 0 : individualClass.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Constant other = (Constant) obj;
		if (individualClass == null) {
			if (other.individualClass != null)
				return false;
		} else if (!individualClass.equals(other.individualClass))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
