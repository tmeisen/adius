package org.adiusframework.service.dataanalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.adiusframework.ontology.ClassPersistenceInfo;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.ontology.frameworkontology.model.Constant;
import org.adiusframework.ontology.frameworkontology.model.Feature;
import org.adiusframework.ontology.frameworkontology.model.ParameterRestriction;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.StateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for analyzing a feature. It is used to generate the SQL query string
 * and to interpret the results of the query.
 * 
 * @author Alexander Tenbrock
 */
public class FeatureAnalyzer {

	private static final Logger LOGGER = LoggerFactory.getLogger(FeatureAnalyzer.class);

	/**
	 * The content Manager object.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The feature object.
	 */
	private Feature feature;

	/**
	 * Constructor.
	 * 
	 * @param contentManager
	 *            content manager used to access the ontology
	 * @param feature
	 *            OWL class representation of the feature
	 * @throws UnsupportedExpressionException
	 */
	public FeatureAnalyzer(FrameworkOntologyManager contentManager, Feature feature)
			throws UnsupportedExpressionException {
		this.contentManager = contentManager;
		this.feature = feature;
	}

	/**
	 * Returns the feature.
	 * 
	 * @return the feature
	 */
	public Feature getFeature() {
		return this.feature;
	}

	/**
	 * This method checks if this feature is applicable for the given class and
	 * returns the parameters that the class is compatible to.
	 * 
	 * @param cls
	 *            the class whose compatibility to this feature should be
	 *            checked
	 * @return set of parameter restrictions compatible with the expression
	 */
	public Set<OWLClassExpression> getCompatibleParameterExpressions(OWLClass cls) {
		LOGGER.info("analyse Feature: " + feature.getRepresentativeName());
		// create the result set
		Set<OWLClassExpression> result = new HashSet<OWLClassExpression>();

		// check all parameter restriction expressions
		for (ParameterRestriction restriction : this.feature.getParameterRestrictions()) {

			// create a set of all compatible classes for this parameter
			Set<OWLClass> compatibleClasses = new HashSet<OWLClass>();

			// add all equivalent classes to the list
			compatibleClasses.addAll(this.contentManager.getEquivalentClasses(restriction.getClassExpression()));

			// if the restriciton is a named class, add all subclasses
			if (restriction.getClassExpression().getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
				compatibleClasses.addAll(this.contentManager.getAllSubClasses(restriction.getClassExpression()));
				// LOGGER.info("restriction is named class");
			}
			// else, add the sub classes of the restriction's super classes

			else {
				// LOGGER.info("restriction isn't named class");
				Set<OWLClass> superClasses = this.contentManager
						.getDirectSuperClasses(restriction.getClassExpression());
				compatibleClasses.addAll(superClasses);
				for (OWLClass superClass : superClasses) {
					// Search here
					compatibleClasses.addAll(this.contentManager.getAllSubClasses(superClass));
					if (compatibleClasses.contains(cls)) {
						LOGGER.info("This superclass: " + superClass.toString());
					} else {
						LOGGER.info("NOT this superclass: " + superClass.toString());
					}
				}
			}
			// check if the class is in the list of comaptible classes
			if (compatibleClasses.contains(cls))
				result.add(restriction.getClassExpression());
		}

		// return the (possibly empty) set
		return result;
	}

	/**
	 * Creates the query object for this feature and returns it.
	 * 
	 * @return the query object
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public SqlQuery getQuery() throws UnsupportedExpressionException, OWLInvalidContentException {

		// create the query object
		SqlQuery query = new SqlQuery(null, QueryType.Standard, feature.getParameterRestrictions().size(),
				contentManager);

		// set all result columns
		int i = 0;
		for (ParameterRestriction restriction : feature.getParameterRestrictions()) {
			OWLClassExpression classExpression = restriction.getClassExpression();

			if (contentManager.isValueTypeClass(classExpression))
				query.setResultColumnClass(i, classExpression);
			else {

				// if there is a name column for this class, retrieve that
				// column. Else, ask for the ID column
				ClassPersistenceInfo restriction_pi = contentManager.getClassPersistenceInfo(classExpression);
				if (restriction_pi.getDbNameColumn() != null)
					query.setResultColumn(i, classExpression, restriction_pi, restriction_pi.getDbNameColumn());
				else
					query.setResultColumn(i, classExpression, restriction_pi, restriction_pi.getDbIDColumn());
			}
			i++;
		}

		// add all parameter restrictions to the query
		for (ParameterRestriction restriction : feature.getParameterRestrictions())
			if (restriction.getClassExpression().getClassExpressionType() != ClassExpressionType.OWL_CLASS)
				query.addExpression(restriction.getClassExpression(), false);

		return query;
	}

	/**
	 * Analyzes the results of the SQL query.
	 * 
	 * @param objectList
	 *            result of the database query
	 * @throws OWLInvalidContentException
	 */
	public void analyzeResult(List<Object> objectList, StateBuilder stateBuilder) throws OWLInvalidContentException {
		LOGGER.debug("Analyzing result of query, containing " + objectList.size() + " elements");
		for (Object object : objectList) {

			// check whether an array of objects is given or one object
			Object[] objects = null;
			if (object.getClass().isArray())
				objects = (Object[]) object;
			else {

				// create array of object, to enable similar handling in the
				// next parts of the code
				objects = new Object[1];
				objects[0] = object;
			}

			List<AssertionEntity> arguments = new Vector<AssertionEntity>();
			for (int i = 0; i < objects.length; i++) {

				// get the class name of this restriction
				OWLClassExpression e = feature.getParameterRestrictions().get(i).getClassExpression();
				String className = contentManager.getClassName(e);

				// now we have to identify if the result represents a constant
				// that have to be mapped to the ontology, this can only be
				// happen if the class expression is anonymous
				Constant match = null;
				if (!e.isAnonymous()) {

					// first lets identify all defined constants of the given
					// type
					Set<Constant> constants = contentManager.getConstantsOfClass(e);

					// now lets check if the result is represented by one of the
					// constants
					for (Constant constant : constants) {
						if (constant.getValue().equals(objects[i].toString()))
							match = constant;
					}
				}

				// set the result
				if (match != null)
					arguments.add(new AssertionObject(className, match.getRepresentativeName(), true));
				else {

					// if no constant found, we have to build the representative
					// name
					String representativeName = new StringBuilder().append(className).append("_")
							.append(objects[i].toString().replaceAll(" ", "_")).toString();
					arguments.add(new AssertionObject(className, representativeName, false));
				}
			}

			// add the state of this feature
			stateBuilder.addAssertion(new Predicate(feature.getRepresentativeName(), arguments));
		}
		LOGGER.debug("Analysis finished");
	}
}
