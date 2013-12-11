package org.adiusframework.ontology;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.IRI;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import org.semanticweb.owlapi.util.AutoIRIMapper;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

/**
 * Manager class for accessing an ontology. It provides methods that are not
 * specific for a single component but may be useful for all components that
 * make use of OWL ontologies.
 * 
 * @author Alexander Tenbrock
 * @author Tobias Meisen
 */
public class OntologyManager {

	/**
	 * The logger object to be used.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OntologyManager.class);

	/**
	 * The ontology manager used in this application.
	 */
	private OWLOntologyManager manager;

	/**
	 * The root ontology used in this application.
	 */
	private OWLOntology rootOntology;

	/**
	 * The Reasoner used in this application.
	 */
	private OWLReasoner reasoner;

	/**
	 * A short form provider. Used for resolving names to entities.
	 */
	private BidirectionalShortFormProvider shortFormProvider;

	/**
	 * This method initialized the static OntologyHelper class. The ontology
	 * with the specified filename and all its imported ontology files that are
	 * in the same directory are loaded and the pellet reasoner is prepared.
	 * 
	 * @param ontologyFileName
	 *            Filename of the ontology to load
	 * @throws OWLOntologyCreationException
	 */
	public OntologyManager(String ontologyFileName) throws OWLOntologyCreationException {

		// create OWLOntologyManager
		setManager(OWLManager.createOWLOntologyManager());

		// add IRI mapper
		File parentdirectory = new File(ontologyFileName).getAbsoluteFile().getParentFile();
		getManager().addIRIMapper(new AutoIRIMapper(parentdirectory, false));

		// load ontologies
		rootOntology = getManager().loadOntology(IRI.create(new File(ontologyFileName)));

		if (getManager().getOntologies() != null) {
			LOGGER.info("Ontologies loaded: ");
			for (OWLOntology onto : getManager().getOntologies()) {
				if (onto.getOntologyID().getVersionIRI() != null)
					LOGGER.info(onto.getOntologyID().getVersionIRI().toString());
				else
					LOGGER.info(onto.getOntologyID().getOntologyIRI().toString());
				if (getManager().getOntologyDocumentIRI(onto) != null)
					LOGGER.info("stored in " + getManager().getOntologyDocumentIRI(onto));
			}
		} else
			LOGGER.info("No ontologies were loaded");

		// create short form provider
		shortFormProvider = new BidirectionalShortFormProviderAdapter(getManager().getOntologies(),
				new SimpleShortFormProvider());

		// create reasoner
		reasoner = PelletReasonerFactory.getInstance().createReasoner(rootOntology);
	}

	protected OWLOntologyManager getManager() {
		return manager;
	}

	protected void setManager(OWLOntologyManager manager) {
		this.manager = manager;
	}

	protected void flush() {
		LOGGER.debug("Flushing managed ontologies");
		shortFormProvider = new BidirectionalShortFormProviderAdapter(getManager().getOntologies(),
				new SimpleShortFormProvider());
		getReasoner().flush();
	}

	/**
	 * Returns the loaded ontologies.
	 * 
	 * @return Set of loaded ontologies
	 */
	public Set<OWLOntology> getOntologies() {
		return getManager().getOntologies();
	}

	/**
	 * Returns the data factory of the ontology manager.
	 * 
	 * @return The data factory
	 */
	public OWLDataFactory getDataFactory() {
		return getManager().getOWLDataFactory();
	}

	/**
	 * Returns the reasoner.
	 * 
	 * @return The reasoner
	 */
	public OWLReasoner getReasoner() {
		return reasoner;
	}

	/**
	 * Sets a data property assertion.
	 * 
	 * @param individual
	 *            the individual to set the property value for
	 * @param dataProperty
	 *            the data property
	 * @param value
	 *            the value to set
	 * @param removeAssertionsIfNecessary
	 *            if this is true and the property is functional, existing
	 *            axioms are removed from all loaded ontologies
	 */
	public void setDataPropertyAssertion(OWLIndividual individual, OWLDataProperty dataProperty, String value,
			boolean removeAssertionsIfNecessary) {
		// remove the old assertion if the property is functional
		if (removeAssertionsIfNecessary && isPropertyFunctional(dataProperty)) {
			for (OWLOntology ontology : getOntologies())
				for (OWLDataPropertyAssertionAxiom assertion : ontology.getDataPropertyAssertionAxioms(individual))
					if (assertion.getProperty().equals(dataProperty))
						getManager().removeAxiom(ontology, assertion);
		}

		// add the new assertion axiom to the ontology
		getManager().addAxiom(rootOntology,
				getDataFactory().getOWLDataPropertyAssertionAxiom(dataProperty, individual, value));
	}

	/**
	 * Returns the class from the ontology with the specified name.
	 * 
	 * @param className
	 *            name of the class (short form)
	 * @return class object
	 * @throws OWLEntityNotFoundException
	 */
	public OWLClass loadClass(String className) throws OWLEntityNotFoundException {
		Set<OWLEntity> entities = shortFormProvider.getEntities(className);
		for (OWLEntity entity : entities)
			if (entity.isOWLClass())
				return entity.asOWLClass();

		throw new OWLEntityNotFoundException("OWL Class \"" + className + "\" not found");
	}

	/**
	 * Returns the individual from the ontology with the specified name.
	 * 
	 * @param individualName
	 *            name of the individual (short form)
	 * @return individual object
	 * @throws OWLEntityNotFoundException
	 */
	public OWLNamedIndividual loadIndividual(String individualName) throws OWLEntityNotFoundException {
		Set<OWLEntity> entities = shortFormProvider.getEntities(individualName);
		for (OWLEntity entity : entities)
			if (entity.isOWLNamedIndividual())
				return entity.asOWLNamedIndividual();
		throw new OWLEntityNotFoundException("OWL Individual \"" + individualName + "\" not found");
	}

	/**
	 * Returns the object property from the ontology with the specified name.
	 * 
	 * @param propertyName
	 *            name of the property (short form)
	 * @return property object
	 * @throws OWLEntityNotFoundException
	 */
	public OWLObjectProperty loadObjectProperty(String propertyName) throws OWLEntityNotFoundException {
		Set<OWLEntity> entities = shortFormProvider.getEntities(propertyName);
		for (OWLEntity entity : entities)
			if (entity.isOWLObjectProperty())
				return entity.asOWLObjectProperty();

		throw new OWLEntityNotFoundException("OWL Object Property \"" + propertyName + "\" not found");
	}

	/**
	 * Returns the data property from the ontology with the specified name.
	 * 
	 * @param propertyName
	 *            name of the property (short form)
	 * @return property object
	 * @throws OWLEntityNotFoundException
	 */
	public OWLDataProperty loadDataProperty(String propertyName) throws OWLEntityNotFoundException {
		Set<OWLEntity> entities = shortFormProvider.getEntities(propertyName);
		for (OWLEntity entity : entities)
			if (entity.isOWLDataProperty())
				return entity.asOWLDataProperty();

		throw new OWLEntityNotFoundException("OWL Data Property \"" + propertyName + "\" not found");
	}

	/**
	 * Returns the annotation property from the ontology with the specified
	 * name.
	 * 
	 * @param annotationPropertyName
	 *            name of the annotation property
	 * @return property object
	 */
	public OWLAnnotationProperty loadAnnotationProperty(String annotationPropertyName) {
		Set<OWLEntity> entities = shortFormProvider.getEntities(annotationPropertyName);
		for (OWLEntity entity : entities)
			if (entity.isOWLAnnotationProperty())
				return entity.asOWLAnnotationProperty();

		LOGGER.warn("OWL Annotation Property \"" + annotationPropertyName + "\" not found");
		return null;
	}

	/**
	 * Returns all equivalent classes of a class expression.
	 * 
	 * @param cls
	 *            the class expression
	 * @return the set of equivalent classes
	 */
	public Set<OWLClass> getEquivalentClasses(OWLClassExpression cls) {
		return reasoner.getEquivalentClasses(cls).getEntities();
	}

	/**
	 * Returns all super classes of a class expression.
	 * 
	 * @param cls
	 *            the class expression
	 * @return the set of super classes of the expression
	 */
	public Set<OWLClass> getAllSuperClasses(OWLClassExpression cls) {
		return reasoner.getSuperClasses(cls, false).getFlattened();
	}

	/**
	 * Returns all direct super classes of a class expression.
	 * 
	 * @param cls
	 *            the class expression
	 * @return the set of direct super classes of the expression
	 */
	public Set<OWLClass> getDirectSuperClasses(OWLClassExpression cls) {
		return reasoner.getSuperClasses(cls, true).getFlattened();
	}

	/**
	 * Returns all subclasses of a class expression.
	 * 
	 * @param cls
	 *            the class expression
	 * @return the set of sub classes of the expression
	 */
	public Set<OWLClass> getAllSubClasses(OWLClassExpression cls) {
		return reasoner.getSubClasses(cls, false).getFlattened();
	}

	/**
	 * This method return the annotation value of an entity for the given
	 * annotation property. If there is no annotation, it returns null. If there
	 * is more than one annotation for this property, an exception is thrown.
	 * 
	 * @param entity
	 *            the entity
	 * @param property
	 *            the annotation property
	 * @return the annotation value
	 * @throws OWLInvalidContentException
	 */
	public OWLAnnotationValue getSingleAnnotationValue(OWLEntity entity, OWLAnnotationProperty property)
			throws OWLInvalidContentException {
		if (entity == null || property == null)
			return null;
		Set<OWLAnnotation> annotations = entity.getAnnotations(rootOntology, property);
		if (annotations.size() == 0)
			return null;
		if (annotations.size() > 1)
			throw new OWLInvalidContentException("more than one annotation \"" + property.getIRI().getFragment()
					+ "\" for entity \"" + entity.getIRI().getFragment() + "\"");
		return annotations.iterator().next().getValue();
	}

	/**
	 * Gets an annotation string for the specified entity in the ontology. The
	 * annotation value may be a string or an IRI. If there is more than one
	 * annotation associated with the entity through the specified annotation
	 * property, an exception is thrown.
	 * 
	 * @param entity
	 *            entity object for which the annotation is to be retrieved
	 * @param property
	 *            the annotation property
	 * @return string value of the annotation
	 * @throws OWLInvalidContentException
	 */
	public String getSingleAnnotationString(OWLEntity entity, OWLAnnotationProperty property)
			throws OWLInvalidContentException {
		OWLAnnotationValue value = getSingleAnnotationValue(entity, property);
		if (value == null)
			return null;

		if (value instanceof OWLLiteral)
			return ((OWLLiteral) value).getLiteral();
		else if (value instanceof IRI) {
			Set<OWLEntity> entities = rootOntology.getEntitiesInSignature((IRI) value, true);
			if (entities.size() == 1)
				return getEntityName(entities.iterator().next());
			throw new OWLInvalidContentException("annotation individual not found: " + value.toString());
		} else
			throw new OWLInvalidContentException("unsupported annotation value: " + value.toString());
	}

	/**
	 * Gets an annotated entity of the specified entity in the ontology. The
	 * annotation value must be an IRI describing a named individual. If there
	 * is more than one annotation associated with the entity through the
	 * specified annotation property, an exception is thrown.
	 * 
	 * @param entity
	 *            entity for which the annotation is to be retrieved
	 * @param property
	 *            the annotation property
	 * @return the entity value of the annotation
	 * @throws OWLInvalidContentException
	 */
	public OWLEntity getSingleAnnotationEntity(OWLEntity entity, OWLAnnotationProperty property)
			throws OWLInvalidContentException {
		OWLAnnotationValue value = getSingleAnnotationValue(entity, property);
		if (value == null)
			return null;

		if (value instanceof IRI) {
			Set<OWLEntity> entities = rootOntology.getEntitiesInSignature((IRI) value, true);

			if (entities.size() == 1)
				return entities.iterator().next();
			throw new OWLInvalidContentException("annotation individual not found: " + value.toString());
		}
		throw new OWLInvalidContentException("unsupported annotation value: " + value.toString());
	}

	/**
	 * Returns the name of an entity. If the entity has the annotation
	 * "rdfs:label" it is used as the name. Else, the fraction of the entity's
	 * IRI is returned.
	 * 
	 * @param entity
	 *            the entity whose name is to be returned
	 * @return the entity's name
	 */
	public String getEntityName(OWLEntity entity) {
		try {
			String label = getSingleAnnotationString(entity, this.getDataFactory().getRDFSLabel());
			if (label != null)
				return label;
		} catch (OWLInvalidContentException e) {
			// do nothing
		}
		return entity.getIRI().getFragment();
	}

	/**
	 * Returns all direct classes representing the type of an individual node.
	 * If a class has to be filtered the parameter filter can be used.
	 * 
	 * @param individualNode
	 *            the individual node that have to be scanned.
	 * @param filter
	 *            the class that should be skipped.
	 * @return set containing all OWLClasses that represent a type of the
	 *         individual node.
	 */
	public Set<OWLClass> getDirectClassesOfIndividualNode(Node<OWLNamedIndividual> individualNode, OWLClass filter) {
		Set<OWLClass> types = new HashSet<OWLClass>();
		for (OWLNamedIndividual individual : individualNode.getEntities()) {
			for (OWLClassExpression expression : individual.getTypes(getOntologies()))
				if (expression.getClassExpressionType() == ClassExpressionType.OWL_CLASS
						&& (filter == null || !filter.equals(expression.asOWLClass())))
					types.add(expression.asOWLClass());
		}
		return types;
	}

	/**
	 * Returns all types of an individual node.
	 * 
	 * @param individualNode
	 *            the individual node that have to be scanned.
	 * @return set containing all OWLClasses that represent a type of the
	 *         individual node.
	 */
	public Set<OWLClass> getDirectClassesOfIndividualNode(Node<OWLNamedIndividual> individualNode) {
		return getDirectClassesOfIndividualNode(individualNode, null);
	}

	/**
	 * Returns the shortest label annotation of set of entities. If no label
	 * annotations are found, the shortest IRI is returned.
	 * 
	 * @param entitySet
	 *            the entity set
	 * @return the shortest label of all classes of the set
	 */
	public <CLASS extends OWLEntity> String getShortestLabel(Set<CLASS> entitySet) {
		String result = null;

		// try to find the shortest label annotation
		for (OWLEntity entity : entitySet) {
			try {
				String name = getSingleAnnotationString(entity, getDataFactory().getRDFSLabel());
				if (name != null)
					if ((result == null) || (name.length() < result.length()))
						result = name;
			} catch (OWLInvalidContentException e) {
				// do nothing
			}

		}
		if (result != null)
			return result;

		// find the shortest IRI fragment
		for (OWLEntity entity : entitySet) {
			String name = entity.getIRI().getFragment();
			if (name != null)
				if ((result == null) || (name.length() < result.length()))
					result = name;
		}
		return result;
	}

	/**
	 * Returns the shortest label annotation of a node. If no label annotations
	 * are found, the shortest IRI is returned.
	 * 
	 * @param node
	 *            the class node
	 * @return the shortest label of all classes of the node
	 */
	public <CLASS extends OWLEntity> String getShortestNodeLabel(Node<CLASS> node) {
		return getShortestLabel(node.getEntities());
	}

	/**
	 * This method tries to find a non-ambiguous class name for the class
	 * expression. The function returns null on failure.
	 * 
	 * @param expression
	 *            The expression to find a class name for
	 * @return The class name
	 */
	public String getClassName(OWLClassExpression expression) {
		// try equivalent classes
		Node<OWLClass> equivalentNode = reasoner.getEquivalentClasses(expression);
		if (equivalentNode.getSize() != 0)
			return getShortestNodeLabel(equivalentNode);

		// try a direct super class
		NodeSet<OWLClass> superClasses = reasoner.getSuperClasses(expression, true);
		if (!superClasses.isEmpty())
			return getShortestLabel(superClasses.getFlattened());

		// try all super class
		superClasses = reasoner.getSuperClasses(expression, false);
		if (!superClasses.isEmpty())
			return getShortestLabel(superClasses.getFlattened());

		return null;
	}

	/**
	 * Checks if two class expressions are known to be equivalent.
	 * 
	 * @param expression1
	 *            the first expression
	 * @param expression2
	 *            the second expression
	 * @return true if the expressions are equivalent
	 */
	public boolean isEquivalent(OWLClassExpression expression1, OWLClassExpression expression2) {
		return reasoner.isEntailed(getManager().getOWLDataFactory().getOWLEquivalentClassesAxiom(expression1,
				expression2));
	}

	/**
	 * Checks if a property is known to be functional.
	 * 
	 * @param property
	 *            the property to be checked
	 * @return true if the property is functional
	 */
	public boolean isPropertyFunctional(OWLDataProperty property) {
		return property.isFunctional(getManager().getOntologies());
	}

	/**
	 * Checks if a property is known to be functional.
	 * 
	 * @param property
	 *            the property to be checked
	 * @return true if the property is functional
	 */
	public boolean isPropertyFunctional(OWLObjectProperty property) {
		return property.isFunctional(getManager().getOntologies());
	}

	/**
	 * Searches for a class within the given set that represents a subclass of
	 * the given class.
	 * 
	 * @param classes
	 *            the set of classes
	 * @param searchClass
	 *            class search within the set
	 * @return null if no class has been found, otherwise the last matching
	 *         class is returned.
	 */
	public OWLClass filterClass(Set<OWLClass> classes, OWLClass searchClass) {
		OWLClass result = null;
		for (OWLClass c : classes) {
			if (isClassSubClassOfClass(c, searchClass)) {
				if (result != null)
					LOGGER.warn("More than one super class found, satisfying critera " + searchClass + ".");
				result = c;
			}
		}
		return result;
	}

	/**
	 * Checks if the given subClass is a real subclass of the given superClass
	 * class.
	 * 
	 * @param subClass
	 *            Class that have to be checked.
	 * @param superClass
	 *            Super class.
	 * @return true if and only if the given subClass is a real subclass of the
	 *         superClass.
	 */
	public boolean isClassSubClassOfClass(OWLClass subClass, OWLClass superClass) {
		OWLAxiom axiom = getDataFactory().getOWLSubClassOfAxiom(subClass, superClass);
		return getReasoner().isEntailed(axiom);
	}

	/**
	 * Checks if the given class has an instance of the given annotation
	 * 
	 * @param owlClass
	 *            class that have to be checked
	 * @param annotation
	 *            annotation that has to be checked
	 * @return true, if and only if an instance of the annotation is attached to
	 *         the class, otherwise false.
	 */
	public boolean isAnnotatedWith(OWLClass owlClass, OWLAnnotationProperty annotation) {
		return owlClass.getAnnotations(rootOntology, annotation).size() > 0;
	}

}
