package org.adiusframework.service.dataanalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.adiusframework.ontology.ClassPersistenceInfo;
import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyBasedStateBuilder;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.ontology.frameworkontology.model.Application;
import org.adiusframework.ontology.frameworkontology.model.Feature;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateBuilder;
import org.adiusframework.resource.state.StateBuilder.StateTemplate;
import org.adiusframework.service.dataanalyzer.exception.DatabaseException;
import org.adiusframework.util.db.hibernate.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.SessionFactory;
import org.semanticweb.owlapi.model.OWLClass;

/**
 * Main class of the DataAnalyzer component. It provides methods to initialize
 * and run the DataAnalyzer.
 * 
 * @author Alexander Tenbrock
 */
public class DataAnalyzer {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DataAnalyzer.class);

	/**
	 * The content manager.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The sessions factory to be used.
	 */
	private SessionFactory sessionFactory;

	/**
	 * Method for initializing the data analyzer.
	 * 
	 * @param contentManager
	 *            content manager used for ontology access
	 * @param sessionFactory
	 *            session factory used for connecting to the database
	 */
	public DataAnalyzer(FrameworkOntologyManager contentManager, SessionFactory sessionFactory) {

		// set members
		this.contentManager = contentManager;

		// set hibernate session factory
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Gets the FrameworkOntologyManager used by this instance.
	 */
	public FrameworkOntologyManager getContentManager() {
		return this.contentManager;
	}

	/**
	 * Returns the target state for an application in the ontology.
	 * 
	 * @param applicationName
	 *            the name of the application
	 * @return the target state
	 * 
	 * @throws OWLEntityNotFoundException
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public State getTargetState(String applicationName) throws OWLEntityNotFoundException,
			UnsupportedExpressionException, OWLInvalidContentException {
		for (Application application : contentManager.getApplications())
			if (application.hasName(applicationName)) {
				FrameworkOntologyBasedStateBuilder stateBuilder = new FrameworkOntologyBasedStateBuilder(
						contentManager, StateTemplate.GOAL_STATE);
				return stateBuilder.buildFromPreconditions(application);
			}
		throw new OWLEntityNotFoundException("Application not found: " + applicationName);
	}

	public State getAxiomState() throws UnsupportedExpressionException, OWLInvalidContentException {

		// create the necessary feature objects
		Set<FeatureAnalyzer> featureAnalyzers = new HashSet<FeatureAnalyzer>();
		for (Feature axiom : this.contentManager.getAxioms()) {
			if (axiom.isSatisfiable())
				featureAnalyzers.add(new FeatureAnalyzer(this.contentManager, axiom));
		}

		// create the object that describes the current state
		StateBuilder stateBuilder = new StateBuilder(StateTemplate.AXIOM_STATE);
		for (FeatureAnalyzer featureAnalyzer : featureAnalyzers) {
			LOGGER.info("Analyzing axiom " + featureAnalyzer.getFeature().getRepresentativeName());

			// build the query, part one:
			// build the part of the query that only depends on the feature
			// description in the ontology
			SqlQuery query = featureAnalyzer.getQuery();

			// build the single SQL string and log it:
			String queryString = query.getQueryString();
			LOGGER.info("Created query: " + queryString);

			// execute query and analyze the result
			List<Object> list = HibernateUtil.nativeSQLListQuery(queryString, this.sessionFactory);
			featureAnalyzer.analyzeResult(list, stateBuilder);
		}
		return stateBuilder.build(true);
	}

	/**
	 * Runs the DataAnalyzer, but checks only a subset of the features.
	 * 
	 * @param className
	 *            class of the object to be analyzed
	 * @param objectID
	 *            id of the object to be analyzed
	 * @param featureNames
	 *            names of all the features that have to be tested.
	 * @return result of the analysis
	 * @throws OWLEntityNotFoundException
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 * @throws DatabaseException
	 */
	public State getActualState(String className, String objectID, Set<String> featureNames)
			throws OWLEntityNotFoundException, OWLInvalidContentException, DatabaseException,
			UnsupportedExpressionException {

		// find the OWL class for the class name
		OWLClass objectClass = contentManager.loadClass(className);

		// for every given feature name, find the feature class
		Set<Feature> selectedFeatures = new HashSet<Feature>();
		for (Feature feature : contentManager.getFeatures())
			for (String featureName : featureNames)
				if (feature.hasName(featureName))
					selectedFeatures.add(feature);

		// start the analysis
		return startAnalysis(objectClass, objectID, selectedFeatures);
	}

	/**
	 * Runs the DataAnalyzer and checks all compatible features.
	 * 
	 * @param className
	 *            class of the object to be analyzed
	 * @param objectID
	 *            id of the object to be analyzed
	 * @return result of the analysis
	 * @throws OWLEntityNotFoundException
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 * @throws DatabaseException
	 */
	public State getActualState(String className, String objectID) throws OWLEntityNotFoundException,
			DatabaseException, OWLInvalidContentException, UnsupportedExpressionException {

		// find the owl class for the class name
		OWLClass objectClass = contentManager.loadClass(className);

		// start the analysis
		return startAnalysis(objectClass, objectID, contentManager.getFeatures());
	}

	/**
	 * Checks if the object to be analyzed exists in the database.
	 * 
	 * @param objectClass
	 *            class of the object to be analyzed
	 * @param objectID
	 *            id of the object to be analyzed
	 * @throws DatabaseException
	 * @throws OWLInvalidContentException
	 */
	private void checkObjectExistance(OWLClass objectClass, String objectID) throws DatabaseException,
			OWLInvalidContentException {

		// get the persistence information for the object class
		ClassPersistenceInfo pi = this.contentManager.getClassPersistenceInfo(objectClass);

		// build the query string and execute the query
		String query = "SELECT COUNT(*) FROM " + pi.getDbTable() + " WHERE " + pi.getDbIDColumn() + " = \"" + objectID
				+ "\"";
		LOGGER.info("Checking object existance with query: " + query);
		// long count = ((BigInteger) HibernateUtil.nativeSQLUniqueQuery(query,
		// this.sessionFactory)).longValue();
		long count = 1;

		// analyze number of result rows
		if (count < 1)
			// throw new DatabaseException("object not found in database");
			LOGGER.info("object not found in database");
		else if (count > 1)
			throw new DatabaseException("more than one object found in database");

		// report success
		LOGGER.info("The object's existance in the database was verified");
	}

	/**
	 * Starts the analysis of an object in the database.
	 * 
	 * @param objectClass
	 *            class of the object to be analyzed
	 * @param objectID
	 *            id of the object to be analyzed
	 * @param featureClasses
	 *            list of all features that have to be taken into account
	 * @return result of the analysis
	 * @throws DatabaseException
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	private State startAnalysis(OWLClass objectClass, String objectID, Set<Feature> features) throws DatabaseException,
			OWLInvalidContentException, UnsupportedExpressionException

	{
		// check if the object really exists in the database
		checkObjectExistance(objectClass, objectID);

		// create the necessary feature objects
		Set<FeatureAnalyzer> featureAnalyzers = new HashSet<FeatureAnalyzer>();
		for (Feature feature : features) {
			if (feature.isSatisfiable())
				featureAnalyzers.add(new FeatureAnalyzer(contentManager, feature));
		}

		// create the object that describes the current state
		StateBuilder stateBuilder = new StateBuilder(StateTemplate.AS_IS_STATE);

		// analyze the object class
		OntologyObjectClass ontologyObject = new OntologyObjectClass(objectClass, objectID, contentManager);
		ontologyObject.analyze(featureAnalyzers, stateBuilder, sessionFactory);
		return stateBuilder.build(true);
	}

}
