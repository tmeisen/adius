package org.adiusframework.ontology.frameworkontology;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.ontology.ClassPersistenceInfo;
import org.adiusframework.ontology.OntologyManager;
import org.adiusframework.ontology.PropertyPersistenceInfo;
import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.model.Application;
import org.adiusframework.ontology.frameworkontology.model.Constant;
import org.adiusframework.ontology.frameworkontology.model.Feature;
import org.adiusframework.ontology.frameworkontology.model.ObjectInstance;
import org.adiusframework.ontology.frameworkontology.model.ParameterAssertion;
import org.adiusframework.ontology.frameworkontology.model.Transformation;
import org.adiusframework.resource.state.LogicalAssertion.LogicalConnectiveType;
import org.adiusframework.resource.state.QuantifierAssertion.QuantifierType;
import org.adiusframework.util.datastructures.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

public class FrameworkOntologyManager extends OntologyManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FrameworkOntologyManager.class);

	/**
	 * Map of loaded classes
	 */
	private ConcurrentMap<String, OWLClass> owlClasses;

	/**
	 * Map of loaded object properties
	 */
	private ConcurrentMap<String, OWLObjectProperty> owlObjectProperties;

	/**
	 * Map of loaded annotations
	 */
	private ConcurrentMap<String, OWLAnnotationProperty> owlAnnotationProperties;

	/**
	 * Map of loaded data properties
	 */
	private ConcurrentMap<String, OWLDataProperty> owlDataProperties;

	/**
	 * A set of axioms in the ontology.
	 */
	private Set<Feature> axioms;

	/**
	 * A set of features in the ontology.
	 */
	private Set<Feature> features;

	/**
	 * A set of transformations in the ontology.
	 */
	private Set<Transformation> transformations;

	/**
	 * A set of applications in the ontology.
	 */
	private Set<Application> applications;

	/**
	 * Set of constants defined in the ontology
	 */
	private Set<Constant> constants;

	/**
	 * The constructor of the ontology content manager.
	 * 
	 * @param ontologyFileName
	 *            file containing the ontology information
	 * @throws OWLEntityNotFoundException
	 * @throws OWLOntologyCreationException
	 */
	public FrameworkOntologyManager(String ontologyFileName) throws OWLEntityNotFoundException,
			OWLOntologyCreationException {
		super(ontologyFileName);

		// load standard entities
		owlClasses = new ConcurrentHashMap<String, OWLClass>();
		loadOwlClasses(FrameworkOntologyConstants.CLASS_APPLICATION, FrameworkOntologyConstants.CLASS_AXIOM,
				FrameworkOntologyConstants.CLASS_COLLECTION, FrameworkOntologyConstants.CLASS_CONSTANT,
				FrameworkOntologyConstants.CLASS_FEATURE, FrameworkOntologyConstants.CLASS_LOGIC_AND,
				FrameworkOntologyConstants.CLASS_LOGIC_EXISTS, FrameworkOntologyConstants.CLASS_LOGIC_FORALL,
				FrameworkOntologyConstants.CLASS_LOGIC_NOT, FrameworkOntologyConstants.CLASS_LOGIC_OR,
				FrameworkOntologyConstants.CLASS_OBJECT, FrameworkOntologyConstants.CLASS_TRANSFORMATION,
				FrameworkOntologyConstants.CLASS_VALUETYPE);

		// load annotations
		owlAnnotationProperties = new ConcurrentHashMap<String, OWLAnnotationProperty>();
		loadAnnotations(FrameworkOntologyConstants.ANNOTATION_TABLE, FrameworkOntologyConstants.ANNOTATION_IDCOLUMN,
				FrameworkOntologyConstants.ANNOTATION_NAMECOLUMN, FrameworkOntologyConstants.ANNOTATION_DBNAME,
				FrameworkOntologyConstants.ANNOTATION_FOREIGNKEY,
				FrameworkOntologyConstants.ANNOTATION_INVERSEFOREIGNKEY,
				FrameworkOntologyConstants.ANNOTATION_ATTRIBUTE, FrameworkOntologyConstants.ANNOTATION_DATAPROPERTY,
				FrameworkOntologyConstants.ANNOTATION_AGGREGATIONOBJECTPROPERTY,
				FrameworkOntologyConstants.ANNOTATION_TRANSFORMATIONPROPERTYNAME,
				FrameworkOntologyConstants.ANNOTATION_SINGLETON);

		// properties
		owlDataProperties = new ConcurrentHashMap<String, OWLDataProperty>();
		loadDataProperties(FrameworkOntologyConstants.DATAPROPERTY_AGGREGATION,
				FrameworkOntologyConstants.DATAPROPERTY_MINIMUM, FrameworkOntologyConstants.DATAPROPERTY_MAXIMUM,
				FrameworkOntologyConstants.DATAPROPERTY_AVERAGE, FrameworkOntologyConstants.DATAPROPERTY_COUNT,
				FrameworkOntologyConstants.DATAPROPERTY_SUM);

		// properties representing operations of transformations and
		// applications
		owlObjectProperties = new ConcurrentHashMap<String, OWLObjectProperty>();
		loadObjectProperties(FrameworkOntologyConstants.OBJECTPROPERTY_OPERAND,
				FrameworkOntologyConstants.OBJECTPROPERTY_PARAMETER,
				FrameworkOntologyConstants.OBJECTPROPERTY_POSITIVEEFFECT,
				FrameworkOntologyConstants.OBJECTPROPERTY_NEGATIVEEFFECT,
				FrameworkOntologyConstants.OBJECTPROPERTY_POSITIVEPRECONDITION,
				FrameworkOntologyConstants.OBJECTPROPERTY_NEGATIVEPRECONDITION,
				FrameworkOntologyConstants.OBJECTPROPERTY_VALUETYPE);
	}

	protected void loadDataProperties(String... properties) throws OWLEntityNotFoundException {
		for (String p : properties)
			owlDataProperties.put(p, loadDataProperty(p));
	}

	public OWLDataProperty getDataProperty(String propertyName) {
		OWLDataProperty property = owlDataProperties.get(propertyName);
		if (property == null)
			LOGGER.error("No data property " + propertyName + " found");
		return property;
	}

	protected void loadObjectProperties(String... properties) throws OWLEntityNotFoundException {
		for (String p : properties)
			owlObjectProperties.put(p, loadObjectProperty(p));
	}

	public OWLObjectProperty getObjectProperty(String propertyName) {
		OWLObjectProperty property = owlObjectProperties.get(propertyName);
		if (property == null)
			LOGGER.error("No object property " + propertyName + " found");
		return property;
	}

	protected void loadAnnotations(String... annotations) {
		for (String a : annotations)
			owlAnnotationProperties.put(a, loadAnnotationProperty(a));
	}

	public OWLAnnotationProperty getAnnotationProperty(String annotationName) {
		OWLAnnotationProperty property = owlAnnotationProperties.get(annotationName);
		if (property == null)
			LOGGER.error("No annotation property " + annotationName + " found");
		return property;
	}

	protected void loadOwlClasses(String... classes) throws OWLEntityNotFoundException {
		for (String c : classes)
			owlClasses.put(c, loadClass(c));
	}

	public OWLClass getOwlClass(String className) {
		OWLClass owlClass = owlClasses.get(className);
		if (owlClass == null)
			LOGGER.error("No owl class " + className + " found");
		return owlClass;
	}

	/**
	 * Sets the context properties for the ontology.
	 * 
	 * @param properties
	 *            the property map
	 * @throws OWLEntityNotFoundException
	 */
	public void setContext(Properties properties) throws OWLEntityNotFoundException {

		LOGGER.info("Setting ontology context...");

		// load the context individual from the ontology
		OWLNamedIndividual context = loadIndividual("Context");

		// loop through all properties in the map
		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);

			try {
				OWLDataProperty dataProperty = loadDataProperty(key);
				setDataPropertyAssertion(context, dataProperty, value, true);
			} catch (OWLEntityNotFoundException e) {
				// No problem if no fitting data property was found
			}
		}

		// flush the changes to the reasoner
		LOGGER.info("Updating reasoner...");
		getReasoner().flush();

		// the list of transformations and features are now invalid
		this.transformations = null;
		this.axioms = null;
		this.features = null;
		this.applications = null;
	}

	/**
	 * Returns all object classes.
	 * 
	 * @return a NodeSet with all object classes
	 */
	public NodeSet<OWLClass> getObjectClasses() {
		return getReasoner().getSubClasses(getOwlClass(FrameworkOntologyConstants.CLASS_OBJECT), false);
	}

	/**
	 * Returns all transformations in the ontology.
	 * 
	 * @return set of transformations
	 * @throws OWLInvalidContentException
	 */
	public Set<Transformation> getTransformations() throws OWLInvalidContentException {
		if (transformations == null) {
			OWLClass transformationClass = getOwlClass(FrameworkOntologyConstants.CLASS_TRANSFORMATION);
			transformations = new HashSet<Transformation>();
			for (Node<OWLNamedIndividual> node : getReasoner().getInstances(transformationClass, false))
				transformations.add(new Transformation(this, node));
		}
		return transformations;
	}

	public List<ParameterAssertion> getParameterAssertion(OWLNamedIndividual individual) {
		List<ParameterAssertion> parameterAssertions = new Vector<ParameterAssertion>();

		// retrieve a list of all parameter properties used in the ontology
		NodeSet<OWLObjectPropertyExpression> parameterProperties = getParameterObjectProperties();

		// assemble the list of parameter assertions
		for (Node<OWLObjectPropertyExpression> property : parameterProperties)
			for (Node<OWLNamedIndividual> assertion : getReasoner().getObjectPropertyValues(individual,
					property.getRepresentativeElement()))
				parameterAssertions.add(new ParameterAssertion(this, property, new ObjectInstance(this, assertion)));
		return parameterAssertions;
	}

	/**
	 * Returns all constants in the ontology
	 * 
	 * @return
	 * @throws OWLInvalidContentException
	 */
	public Set<Constant> getConstants() throws OWLInvalidContentException {
		if (constants == null) {
			OWLClass constantClass = getOwlClass(FrameworkOntologyConstants.CLASS_CONSTANT);
			constants = new HashSet<Constant>();
			for (Node<OWLNamedIndividual> node : getReasoner().getInstances(constantClass, false))
				constants.add(new Constant(this, node));
		}
		return constants;
	}

	public Set<Constant> getConstantsOfClass(OWLClassExpression expression) throws OWLInvalidContentException {
		Set<Constant> result = new HashSet<Constant>();
		OWLClass constantClass = getOwlClass(FrameworkOntologyConstants.CLASS_CONSTANT);
		for (Node<OWLNamedIndividual> node : getReasoner().getInstances(constantClass, false)) {
			Set<OWLClass> classes = getDirectClassesOfIndividualNode(node);
			if (filterClass(classes, expression.asOWLClass()) != null)
				result.add(new Constant(this, node));
		}
		return result;
	}

	/**
	 * Returns all simulations in the ontology.
	 * 
	 * @return set of simulations
	 * @throws OWLInvalidContentException
	 */
	public Set<Application> getApplications() throws OWLInvalidContentException {
		if (applications == null) {
			OWLClass applicationClass = getOwlClass(FrameworkOntologyConstants.CLASS_APPLICATION);
			applications = new HashSet<Application>();
			for (Node<OWLNamedIndividual> node : getReasoner().getInstances(applicationClass, false))
				applications.add(new Application(this, node));
		}
		return applications;
	}

	/**
	 * Returns all axioms in the ontology.
	 * 
	 * @throws OWLInvalidContentException
	 */
	public Set<Feature> getAxioms() throws OWLInvalidContentException {
		if (this.axioms == null) {
			OWLClass axiomClass = getOwlClass(FrameworkOntologyConstants.CLASS_AXIOM);
			axioms = new HashSet<Feature>();
			for (Node<OWLClass> node : getReasoner().getSubClasses(axiomClass, false))
				axioms.add(new Feature(this, node));
		}
		return axioms;
	}

	/**
	 * Returns all features in the ontology.
	 * 
	 * @throws OWLInvalidContentException
	 */
	public Set<Feature> getFeatures() throws OWLInvalidContentException {
		if (features == null) {
			OWLClass featureClass = getOwlClass(FrameworkOntologyConstants.CLASS_FEATURE);
			features = new HashSet<Feature>();
			for (Node<OWLClass> node : getReasoner().getSubClasses(featureClass, false))
				features.add(new Feature(this, node));
		}
		return features;
	}

	/**
	 * Checks if a class expressions is a collection class.
	 * 
	 * @param expression
	 *            the class expression
	 * @return returns true if the class expression is a subclass or equivalent
	 *         class of "Collection"
	 */
	public boolean isCollectionClass(OWLClassExpression expression) {
		OWLClass collectionClass = getOwlClass(FrameworkOntologyConstants.CLASS_COLLECTION);
		return (getReasoner().getSuperClasses(expression, false).containsEntity(collectionClass) || getReasoner()
				.getEquivalentClasses(expression).contains(collectionClass));
	}

	/**
	 * Checks if a class expression is an axiom class.
	 * 
	 * @param expression
	 *            the class expression
	 * @return returns true if the class expression is a subclass or equivalent
	 *         class of "Axiom"
	 */
	public boolean isAxiomClass(OWLClassExpression expression) {
		OWLClass axiomClass = getOwlClass(FrameworkOntologyConstants.CLASS_AXIOM);
		return (getReasoner().getSuperClasses(expression, false).containsEntity(axiomClass) || getReasoner()
				.getEquivalentClasses(expression).contains(axiomClass));
	}

	/**
	 * Checks if a class expression is a feature class.
	 * 
	 * @param expression
	 *            the class expression
	 * @return returns true if the class expression is a subclass or equivalent
	 *         class of "Feature"
	 */
	public boolean isFeatureClass(OWLClassExpression expression) {
		OWLClass featureClass = getOwlClass(FrameworkOntologyConstants.CLASS_FEATURE);
		return (getReasoner().getSuperClasses(expression, false).containsEntity(featureClass) || getReasoner()
				.getEquivalentClasses(expression).contains(featureClass));
	}

	/**
	 * Determines the type of quantifier of a predicate instance.
	 * 
	 * @param predicate
	 *            the predicate instance
	 * @return the quantifier type or null if it is no quantifier
	 */
	public QuantifierType getQuantifierType(OWLNamedIndividual predicate) {
		OWLClass existsClass = getOwlClass(FrameworkOntologyConstants.CLASS_LOGIC_EXISTS);
		OWLClass forallClass = getOwlClass(FrameworkOntologyConstants.CLASS_LOGIC_FORALL);
		if (doesIndividualBelongToClass(predicate, existsClass))
			return QuantifierType.EXISTS;
		else if (doesIndividualBelongToClass(predicate, forallClass))
			return QuantifierType.FORALL;
		else
			return null;
	}

	/**
	 * Determines the type of logical connective of a predicate instance.
	 * 
	 * @param predicate
	 *            the predicate instance
	 * @return the logical connective type or null if it is no logical
	 *         connective
	 */
	public LogicalConnectiveType getLogicalConnectiveType(OWLNamedIndividual predicate) {
		OWLClass orClass = getOwlClass(FrameworkOntologyConstants.CLASS_LOGIC_OR);
		OWLClass andClass = getOwlClass(FrameworkOntologyConstants.CLASS_LOGIC_AND);
		OWLClass notClass = getOwlClass(FrameworkOntologyConstants.CLASS_LOGIC_NOT);
		if (doesIndividualBelongToClass(predicate, notClass))
			return LogicalConnectiveType.NOT;
		else if (doesIndividualBelongToClass(predicate, andClass))
			return LogicalConnectiveType.AND;
		else if (doesIndividualBelongToClass(predicate, orClass))
			return LogicalConnectiveType.OR;
		else
			return null;
	}

	/**
	 * Checks if a class expression is a value type class.
	 * 
	 * @param expression
	 *            the class expression
	 * @return returns true if the class expression is a value type class
	 */
	public boolean isValueTypeClass(OWLClassExpression expression) {
		OWLClass valueTypeClass = getOwlClass(FrameworkOntologyConstants.CLASS_VALUETYPE);
		return (getEquivalentClasses(expression).contains(valueTypeClass) || getAllSuperClasses(expression).contains(
				valueTypeClass));
	}

	/**
	 * Returns all operand assertions (usually used by logical connective
	 * individuals)
	 * 
	 * @param individual
	 *            the individuals
	 * @return a set of asserted individuals
	 */
	public Set<OWLNamedIndividual> getOperandAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_OPERAND)).getFlattened();
	}

	/**
	 * Returns all subproperties of "hasParameter".
	 * 
	 * @return set of "hasInput" subproperties
	 */
	public NodeSet<OWLObjectPropertyExpression> getParameterObjectProperties() {
		return getReasoner().getSubObjectProperties(
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_PARAMETER), false);
	}

	/**
	 * Returns all parameter assertions (used by quantifier individuals)
	 * 
	 * @param individual
	 *            the individuals
	 * @return a set of asserted individuals
	 */
	public NodeSet<OWLNamedIndividual> getParameterAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_PARAMETER));
	}

	// Set of Positive Precondition Assertions
	public Set<OWLNamedIndividual> getPositivePreconditionAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_POSITIVEPRECONDITION)).getFlattened();
	}

	// Set of Negative Precondition Assertions
	public Set<OWLNamedIndividual> getNegativePreconditionAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_NEGATIVEPRECONDITION)).getFlattened();
	}

	// Set of Positive Effect Assertions
	public Set<OWLNamedIndividual> getPositiveEffectAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_POSITIVEEFFECT)).getFlattened();
	}

	// Set of Negative Effect Assertions
	public Set<OWLNamedIndividual> getNegativeEffectAssertions(OWLNamedIndividual individual) {
		return getReasoner().getObjectPropertyValues(individual,
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_NEGATIVEEFFECT)).getFlattened();
	}

	/**
	 * Returns a named class this individual belongs to.
	 * 
	 * @param individual
	 *            the individual
	 * @return a named class or null if none is found
	 */
	public OWLClass getNamedClass(OWLNamedIndividual individual) {
		for (OWLClassExpression expression : individual.getTypes(getOntologies()))
			if (expression.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
				return expression.asOWLClass();
		return null;
	}

	public Set<OWLQuantifiedObjectRestriction> getObjectPropertyRestrictions(Node<OWLClass> featureClassNode) {
		Set<OWLQuantifiedObjectRestriction> result = new HashSet<OWLQuantifiedObjectRestriction>();

		// collect all superclass and equivalent class expressions
		Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();
		for (OWLClass featureClass : featureClassNode) {
			expressions.addAll(featureClass.getSuperClasses(getOntologies()));
			expressions.addAll(featureClass.getEquivalentClasses(getOntologies()));
		}
		LOGGER.debug("Getting property restrictions of " + featureClassNode);
		LOGGER.debug("Found expressions " + expressions);

		for (OWLClassExpression expr : expressions)
			if ((expr.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM)
					|| (expr.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM))
				result.add((OWLQuantifiedObjectRestriction) expr);
		return result;
	}

	public boolean isParameterProperty(OWLObjectPropertyExpression property) {
		OWLObjectProperty parameterProperty = getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_PARAMETER);
		return (getReasoner().getEquivalentObjectProperties(property).contains(parameterProperty) || getReasoner()
				.getSuperObjectProperties(property, false).containsEntity(parameterProperty));
	}

	/**
	 * Returns all type assertions of an individual combined into a single class
	 * expression.
	 * 
	 * @param individual
	 *            the individual
	 * @return the class expression describing the type(s) of the individual
	 */
	public OWLClassExpression getTypeExpression(OWLNamedIndividual individual) {
		NodeSet<OWLClass> types = getReasoner().getTypes(individual, true);
		if (types.isSingleton())
			return types.getNodes().iterator().next().getRepresentativeElement();
		return getDataFactory().getOWLObjectIntersectionOf(types.getFlattened());
	}

	/**
	 * Returns the value of the annotation "transformationPropertyName" of an
	 * object property node.
	 * 
	 * @param objectPropertyNode
	 *            the property node
	 * @return the annotation value
	 * @throws OWLInvalidContentException
	 */
	public String getTransformationPropertyNameAnnotation(Node<OWLObjectPropertyExpression> objectPropertyNode)
			throws OWLInvalidContentException {
		OWLAnnotationProperty annotationProperty = getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_TRANSFORMATIONPROPERTYNAME);

		// loop for a PropertyName annotation
		for (OWLObjectPropertyExpression prop : objectPropertyNode)
			if (!prop.isAnonymous()) {
				String propertyName = getSingleAnnotationString(prop.asOWLObjectProperty(), annotationProperty);
				if (propertyName != null)
					return propertyName;
			}
		return null;
	}

	/**
	 * Returns the persistence information for an OWL class. If none is found
	 * for this specific class the function returns null. Function is for
	 * internal use only.
	 * 
	 * @param expression
	 *            class whose persistence information must be retrieved
	 * @return persistence information of the class
	 * @throws OWLInvalidContentException
	 */
	private ClassPersistenceInfo getClassPersistenceInfoDirect(OWLClass expression) throws OWLInvalidContentException {
		String dbTable = getSingleAnnotationString(expression,
				getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_TABLE));
		String dbPrimaryKey = getSingleAnnotationString(expression,
				getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_IDCOLUMN));
		String dbNameColumn = getSingleAnnotationString(expression,
				getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_NAMECOLUMN));

		if (dbTable != null || dbPrimaryKey != null || dbNameColumn != null) {
			if (dbTable == null || dbPrimaryKey == null)
				throw new OWLInvalidContentException("Incomplete class persistence information", expression);
			return new ClassPersistenceInfo(dbTable, dbPrimaryKey, dbNameColumn);
		}
		return null;
	}

	/**
	 * Returns the persistence information for an OWL class expression. Also
	 * checks equivalent and super classes. If nothing is found, an exception is
	 * thrown.
	 * 
	 * @param expression
	 *            class expression to retrieve the information for
	 * @return persistence information for the class expression
	 * @throws OWLInvalidContentException
	 */
	public ClassPersistenceInfo getClassPersistenceInfo(OWLClassExpression expression)
			throws OWLInvalidContentException {

		// build list of all equivalent or super classes
		Set<OWLClass> classSet = new HashSet<OWLClass>();
		classSet.addAll(getEquivalentClasses(expression));
		classSet.addAll(getAllSuperClasses(expression));

		// try to find persistence information of equivalent classes
		ClassPersistenceInfo result = null;

		for (OWLClass cls : classSet) {
			ClassPersistenceInfo res = getClassPersistenceInfoDirect(cls);
			if (res != null) {
				if (result != null)
					throw new OWLInvalidContentException("class persistance information ambiguous for "
							+ expression.toString());
				result = res;
			}
		}

		// check if a result was found and return it
		if (result == null)
			throw new OWLInvalidContentException("class persistance information not found", expression);
		return result;
	}

	/**
	 * Collects the persistence information of a property. It returns null if no
	 * information was found.
	 * 
	 * @param property
	 *            the property
	 * @return persistence information of the property
	 * @throws OWLInvalidContentException
	 */
	public PropertyPersistenceInfo getPropertyPersistenceInfo(OWLObjectProperty property)
			throws OWLInvalidContentException {

		// try directly
		String dbForeignKey = getSingleAnnotationString(property,
				getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_FOREIGNKEY));
		String dbInverseForeignKey = getSingleAnnotationString(property,
				getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_INVERSEFOREIGNKEY));
		if ((dbForeignKey != null))
			return new PropertyPersistenceInfo(dbForeignKey, dbInverseForeignKey);

		// try all inverses
		Set<OWLObjectPropertyExpression> inverses = getReasoner().getInverseObjectProperties(property).getEntities();
		for (OWLObjectPropertyExpression prop : inverses) {
			String dbForeignKey2 = getSingleAnnotationString(prop.asOWLObjectProperty(),
					getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_FOREIGNKEY));
			if (dbForeignKey2 != null) {
				if (dbInverseForeignKey != null)
					throw new OWLInvalidContentException("inverse object property annotation ambigous", property);
				dbInverseForeignKey = dbForeignKey2;
			}
		}
		if (dbInverseForeignKey != null)
			return new PropertyPersistenceInfo(dbForeignKey, dbInverseForeignKey);
		return null;
	}

	/**
	 * Returns the database name annotation for an individual node. If no
	 * information was found, an exception is thrown.
	 * 
	 * @param individualNode
	 *            the individual
	 * @return persistence information for the individual
	 * @throws OWLInvalidContentException
	 */
	public String getIndividualDatabaseName(Node<OWLNamedIndividual> individualNode) throws OWLInvalidContentException {

		String result = null;
		for (OWLNamedIndividual ind : individualNode) {
			String dbName = getSingleAnnotationString(ind,
					getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_DBNAME));
			if (dbName != null) {
				if (result != null)
					throw new OWLInvalidContentException("individual persistance ambiguous: " + individualNode);
				result = dbName;
			}
		}

		if (result == null)
			throw new OWLInvalidContentException("individual persistance information not found: " + individualNode);
		return result;
	}

	/**
	 * Returns the database name annotation for an individual. It no information
	 * was found, an exception is thrown.
	 * 
	 * @param individual
	 *            the individual
	 * @return persistence information for the individual
	 * @throws OWLInvalidContentException
	 */
	public String getIndividualDatabaseName(OWLNamedIndividual individual) throws OWLInvalidContentException {

		return getIndividualDatabaseName(getReasoner().getSameIndividuals(individual));
	}

	/**
	 * This function finds a class expression consisting only of named classes
	 * (and intersections of them) that describes the given expression. It is
	 * used to remove property restrictions from the expression.
	 * 
	 * @param expression
	 *            the class expression to find the alternative expression for
	 * @return the alternative expression for the given class expression
	 */
	public OWLClassExpression getNamedClassExpression(OWLClassExpression expression) {
		// if the expression already is a class, directly return it
		if (expression.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
			return expression;

		// try to find an equivalent class
		Node<OWLClass> equivalentClasses = getReasoner().getEquivalentClasses(expression);
		if (equivalentClasses.getSize() > 0)
			return equivalentClasses.getRepresentativeElement();

		// return a superclass / the intersection of the superclasses
		Set<OWLClass> superClasses = getDirectSuperClasses(expression);
		if (superClasses.size() == 1)
			return superClasses.iterator().next();
		else if (superClasses.size() > 1)
			return getDataFactory().getOWLObjectIntersectionOf(superClasses);

		// failure
		return null;
	}

	/**
	 * Checks if a given individual is an instance of a specific class.
	 * 
	 * @param individual
	 *            the individual
	 * @param classExpression
	 *            the class expression
	 * @return true if the individual is an instance of the class expression
	 */
	public boolean doesIndividualBelongToClass(OWLNamedIndividual individual, OWLClassExpression classExpression) {
		OWLClassAssertionAxiom assertion = getDataFactory().getOWLClassAssertionAxiom(classExpression, individual);

		return getReasoner().isEntailed(assertion);
	}

	/**
	 * Checks if a property is a value type property.
	 * 
	 * @param property
	 *            the property
	 * @return returns true if it is a value type property
	 */
	public boolean isValueTypeProperty(OWLObjectProperty property) {
		return (getReasoner().getEquivalentObjectProperties(property).contains(
				getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_VALUETYPE)) || getReasoner()
				.getSuperObjectProperties(property, false).containsEntity(
						getObjectProperty(FrameworkOntologyConstants.OBJECTPROPERTY_VALUETYPE)));
	}

	/**
	 * @return set of owl classes that are annotated with the singleton
	 *         annotation property
	 */
	public Set<OWLClass> getSingletonConcepts() {
		Set<OWLClass> singletons = new HashSet<OWLClass>();
		OWLAnnotationProperty annotation = getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_SINGLETON);
		Set<OWLClass> candidates = getAllSubClasses(getOwlClass(FrameworkOntologyConstants.CLASS_OBJECT));
		for (OWLClass candidate : candidates) {
			if (isAnnotatedWith(candidate, annotation))
				singletons.add(candidate);
		}
		return singletons;
	}

	public static void main(String[] args) throws OWLEntityNotFoundException, OWLOntologyCreationException,
			OWLInvalidContentException {
		FrameworkOntologyManager ocm = new FrameworkOntologyManager(
				"D:/Repositories/adius/_workspace/feis-serviceprovider-windows/fem-0.2.owl");
		List<String> singletons = new Vector<String>();
		for (OWLClass singleton : ocm.getSingletonConcepts())
			singletons.add(ocm.getClassName(singleton));
		System.out.println(singletons);
	}

}
