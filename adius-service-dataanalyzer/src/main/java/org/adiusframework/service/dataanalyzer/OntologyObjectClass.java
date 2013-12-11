package org.adiusframework.service.dataanalyzer;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.state.StateBuilder;
import org.adiusframework.util.db.hibernate.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;

/**
 * This class describes a object class in the ontology that can be analyzed.
 * 
 * It is possibly an element in a collection or the top of a collection
 * hierarchy. Such a hierarchy has the user-delivered object class at the top
 * and all object classes that are connected to that class at the bottom.
 * 
 * @author Alexander Tenbrock
 * 
 */
public class OntologyObjectClass {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OntologyObjectClass.class);

	/**
	 * The ontology helper object.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The object id (delivered by the user). This is only valid for the top
	 * object in a collection hierarchy.
	 */
	private String objectID;

	/**
	 * The OWL class for this object
	 */
	private OWLClass objectClass;

	/**
	 * The parent in the hierarchy.
	 */
	private OntologyObjectClass collectionParent;

	/**
	 * The property that connects the parent with this object.
	 */
	private OWLObjectProperty collectionParentProperty;

	/**
	 * Constructor for the top object of a collection hierarchy.
	 * 
	 * @param objectClass
	 *            the class that is represented by this object.
	 * @param objectID
	 *            the object id that identifies this object in the database
	 * @param contentManager
	 *            the analyzer helper
	 */
	public OntologyObjectClass(OWLClass objectClass, String objectID, FrameworkOntologyManager contentManager) {
		this.collectionParent = null;
		this.collectionParentProperty = null;
		this.contentManager = contentManager;
		this.objectID = objectID;
		this.objectClass = objectClass;
	}

	/**
	 * Constructor for a branch in the collection hierarchy.
	 * 
	 * @param objectClass
	 *            the class that is represented by this object.
	 * @param parent
	 *            the parent object class in the hierarchy
	 * @param collectionParentProperty
	 *            the property that connects the parent with this object class
	 */
	public OntologyObjectClass(OWLClass objectClass, OntologyObjectClass parent,
			OWLObjectProperty collectionParentProperty) {
		this.objectClass = objectClass;
		this.collectionParent = parent;
		this.collectionParentProperty = collectionParentProperty;

		this.contentManager = parent.contentManager;
		this.objectID = null;
	}

	/**
	 * Returns the OWLClass that belongs to this object.
	 * 
	 * @return the OWLClass object
	 */
	public OWLClass getObjectClass() {
		return this.objectClass;
	}

	/**
	 * Checks if this is the top of a collection hierarchy
	 * 
	 * @return returns true if this is the top object
	 */
	public boolean isTopObject() {
		return (this.collectionParent == null);
	}

	/**
	 * This method analyzes this object class. A query is generated for every
	 * feature that this object class can be a parameter for. If this object
	 * class is a collection, the object classes that are connected to this
	 * class through a SomeValuesFrom or AllValuesFrom property restriction are
	 * also analyzed.
	 * 
	 * @param features
	 * @param stateBuilder
	 * @param sessionFactory
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	public void analyze(Set<FeatureAnalyzer> features, StateBuilder stateBuilder, SessionFactory sessionFactory)
			throws OWLInvalidContentException, UnsupportedExpressionException {

		// check all features if one or more of them are compatible with this
		// object class (= if this object class can be a parameter of the
		// feature)
		Iterator<FeatureAnalyzer> featureAnalyzeIterator = features.iterator();
		while (featureAnalyzeIterator.hasNext()) {
			FeatureAnalyzer featureAnalyzer = featureAnalyzeIterator.next();
			if (featureAnalyzer.getCompatibleParameterExpressions(objectClass).size() > 0) {
				LOGGER.info("Analyzing feature \"" + featureAnalyzer.getFeature().getRepresentativeName()
						+ "\" for class \"" + contentManager.getEntityName(this.objectClass) + "\"");

				// build the query, part one:
				// build the part of the query that only depends on the feature
				// description in the ontology
				SqlQuery query = featureAnalyzer.getQuery();

				// part two: add the where-clauses that are imposed through the
				// user-delivered object id and the connected objects.
				addObjectRestrictionToQuery(featureAnalyzer, query);

				// build the single SQL string
				String queryString = query.getQueryString();
				LOGGER.debug("Created query: " + queryString);

				// execute query and analyze the result
				List<Object> list = HibernateUtil.nativeSQLListQuery(queryString, sessionFactory);
				featureAnalyzer.analyzeResult(list, stateBuilder);
				// analyzed Featured don't have to be analyzed again
				featureAnalyzeIterator.remove();

			} else
				LOGGER.info("Skipping feature \"" + featureAnalyzer.getFeature().getRepresentativeName() + "\"");
		}

		// check if this is a collection and analyze its sub object classes
		// (i.e. the classes that are connected to this class through property
		// restrictions
		if (contentManager.isCollectionClass(objectClass))
			analyzeCollection(features, stateBuilder, sessionFactory);
	}

	/**
	 * This method adds the restrictions imposed through this object /
	 * collection to a query.
	 * 
	 * @param featureAnalyzer
	 *            the feature that is being analyzed
	 * @param query
	 *            the query
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	public void addObjectRestrictionToQuery(FeatureAnalyzer featureAnalyzer, SqlQuery query)
			throws OWLInvalidContentException, UnsupportedExpressionException {
		for (OWLClassExpression classExpression : featureAnalyzer.getCompatibleParameterExpressions(this.objectClass)) {
			if (isTopObject())
				query.addAlternativeObjectIDRestriction(classExpression, this.objectID);
			else
				query.addAlternativeRestriction(query.describeObjectPropertyConnection(
						this.collectionParent.getObjectClass(), this.collectionParentProperty, classExpression, false));
		}

		// call the function of the next(upper) class in the hierarchy (if this
		// is not the top class)
		if (!isTopObject())
			this.collectionParent.addObjectRestrictionToQuery(query);
	}

	/**
	 * This method adds the restrictions imposed through this object /
	 * collection to a query. For internal use only.
	 * 
	 * @param query
	 *            the query
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	private void addObjectRestrictionToQuery(SqlQuery query) throws OWLInvalidContentException,
			UnsupportedExpressionException {
		// if this is the top object of a collection, verify that it has the
		// correct id
		if (isTopObject())
			query.addManadatoryRestriction(query.getIDColumnString(this.objectClass) + " = " + this.objectID);

		// if it is not the top object, add the property expression and call the
		// parent's function
		else {
			query.addManadatoryRestriction(query.describeObjectPropertyConnection(
					this.collectionParent.getObjectClass(), this.collectionParentProperty, this.objectClass, false));
			this.collectionParent.addObjectRestrictionToQuery(query);
		}
	}

	/**
	 * Analyzes all classes that are connected to this class through a
	 * SomeValuesFrom or AllValuesFrom property restriction. This method should
	 * only be called if this class is a collection.
	 * 
	 * @param features
	 *            a list of all features that should be included in the analysis
	 *            process
	 * @param state
	 *            the state
	 * @param sessionFactory
	 *            the session factory to use
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	private void analyzeCollection(Set<FeatureAnalyzer> features, StateBuilder stateBuilder,
			SessionFactory sessionFactory) throws OWLInvalidContentException, UnsupportedExpressionException {
		LOGGER.info("Analyzing collection \"" + contentManager.getEntityName(this.objectClass) + "\"");

		// get list of all property restrictions for this class
		Set<OWLQuantifiedObjectRestriction> propertyRestrictions = contentManager
				.getObjectPropertyRestrictions(contentManager.getReasoner().getEquivalentClasses(objectClass));

		// now analyze all classes that are connected to this class through a
		// restriction
		for (OWLQuantifiedObjectRestriction restriction : propertyRestrictions) {
			if (restriction.getFiller().getClassExpressionType() != ClassExpressionType.OWL_CLASS)
				throw new UnsupportedExpressionException("Only named classes are allowed for collection expressions");

			// create a subObjectClass for the restricted property and class and
			// analyze that
			OntologyObjectClass subObjectClass = new OntologyObjectClass(restriction.getFiller().asOWLClass(), this,
					restriction.getProperty().asOWLObjectProperty());
			subObjectClass.analyze(features, stateBuilder, sessionFactory);
		}
	}

}
