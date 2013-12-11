package org.adiusframework.ontology.frameworkontology;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.Node;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.model.Constant;
import org.adiusframework.ontology.frameworkontology.model.FeatureInstance;
import org.adiusframework.ontology.frameworkontology.model.ObjectInstance;
import org.adiusframework.ontology.frameworkontology.model.ParameterAssertion;
import org.adiusframework.ontology.frameworkontology.model.Transformation;
import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.AssertionVariable;
import org.adiusframework.resource.state.LogicalAssertion;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.QuantifierAssertion;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateBuilder;
import org.adiusframework.resource.state.LogicalAssertion.LogicalConnectiveType;
import org.adiusframework.resource.state.QuantifierAssertion.QuantifierType;

/**
 * A state builder that builds State objects based on the preconditions or
 * effects of transformations and applications.
 * 
 * @author Tobias Meisen
 * 
 */
public class FrameworkOntologyBasedStateBuilder extends StateBuilder {

	/**
	 * The content manager.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * The constructor.
	 * 
	 * @param contentManager
	 *            the content manager to use
	 */
	public FrameworkOntologyBasedStateBuilder(FrameworkOntologyManager contentManager, StateTemplate template) {
		super(template);
		this.contentManager = contentManager;
	}

	protected Assertion buildAssertion(OWLNamedIndividual individual) throws OWLInvalidContentException,
			UnsupportedExpressionException {

		// check if it is a axiom state:
		if (contentManager.isAxiomClass(contentManager.getTypeExpression(individual)))
			return buildPredicate(individual);

		// check if it is a feature state:
		if (contentManager.isFeatureClass(contentManager.getTypeExpression(individual)))
			return buildPredicate(individual);

		// check if it is a logical connective
		LogicalConnectiveType connectiveType = contentManager.getLogicalConnectiveType(individual);
		if (connectiveType != null)
			return buildLogicalAssertion(individual, connectiveType);

		// check if it is a quantifier
		QuantifierType quantifierType = contentManager.getQuantifierType(individual);
		if (quantifierType != null)
			return buildQuanfifierAssertion(individual, quantifierType);

		throw new UnsupportedExpressionException("Could not get predicate state for individual", individual);
	}

	protected Predicate buildPredicate(OWLNamedIndividual individual) throws OWLInvalidContentException {

		FeatureInstance featureInstance = new FeatureInstance(contentManager, individual);

		// collect the parameter objects of the feature
		List<AssertionEntity> arguments = new Vector<AssertionEntity>();
		for (ObjectInstance featureParameter : featureInstance.getParameterAssertions()) {

			// try to find a corresponding variable, if none is found we try to
			// locate a matching object if both fails, we have to create a new
			// object
			AssertionVariable variable = findVariable(featureParameter.getRepresentativeName());
			AssertionObject object = null;
			if (variable != null)
				arguments.add(variable);
			else {
				object = findObject(featureParameter.getRepresentativeName());

				// check whether it already exists or if one has to be created
				if (object != null)
					arguments.add(object);
				else {

					// lets identify if a constant exists
					Set<Constant> constants = contentManager.getConstantsOfClass(featureParameter.getIndividualClass());
					boolean isConstant = false;
					for (Constant constant : constants) {
						if (constant.getRepresentativeName().equals(featureParameter.getRepresentativeName())) {
							isConstant = true;
							break;
						}
					}
					arguments.add(new AssertionObject(featureParameter.getTypeName(), featureParameter
							.getRepresentativeName(), isConstant));
				}
			}
		}

		// return a newly created feature state object
		return new Predicate(featureInstance.getFeature().getRepresentativeName(), arguments);
	}

	protected LogicalAssertion buildLogicalAssertion(OWLNamedIndividual individual, LogicalConnectiveType connectiveType)
			throws UnsupportedExpressionException, OWLInvalidContentException {

		// collect the operands
		List<Assertion> arguments = buildAssertions(contentManager.getOperandAssertions(individual), null);

		// return the new connective state object
		return new LogicalAssertion(connectiveType, arguments);
	}

	protected QuantifierAssertion buildQuanfifierAssertion(OWLNamedIndividual individual, QuantifierType quantifierType)
			throws UnsupportedExpressionException, OWLInvalidContentException {

		// build a list with the parameters of this quantifier
		List<AssertionVariable> parameters = new Vector<AssertionVariable>();
		for (Node<OWLNamedIndividual> individualNode : contentManager.getParameterAssertions(individual)) {
			ObjectInstance object = new ObjectInstance(contentManager, individualNode);
			parameters.add(new AssertionVariable(object.getTypeName(), object.getRepresentativeName()));
		}

		// collect preconditions and effects
		List<Assertion> preconditions = buildAssertions(contentManager.getPositivePreconditionAssertions(individual),
				contentManager.getNegativePreconditionAssertions(individual));
		List<Assertion> effects = buildAssertions(contentManager.getPositiveEffectAssertions(individual),
				contentManager.getNegativeEffectAssertions(individual));

		// return new quantifier state
		return new QuantifierAssertion(quantifierType, parameters, preconditions, effects);
	}

	/**
	 * Build this state from the preconditions of a transformation (or a
	 * application).
	 * 
	 * @param transformation
	 *            the transformation/application individual
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public State buildFromPreconditions(Transformation transformation) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		// remove old data
		reset();

		// set up the list of variables
		for (ParameterAssertion param : transformation.getParameterAssertions())
			addVariable(new AssertionVariable(param.getIndividual().getTypeName(), param.getIndividual()
					.getRepresentativeName()));

		// add the positive predicates
		List<Assertion> assertions = buildAssertions(transformation.getPositivePreconditionAssertions(),
				transformation.getNegativePreconditionAssertions());
		for (Assertion assertion : assertions)
			addAssertion(assertion);
		return build(true);
	}

	/**
	 * Build this state from the effects of a transformation (or a application).
	 * 
	 * @param transformation
	 *            the transformation/application individual
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public State buildFromEffects(Transformation transformation) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		// remove old data
		reset();

		// set up the list of variables
		for (ParameterAssertion param : transformation.getParameterAssertions())
			addVariable(new AssertionVariable(param.getIndividual().getTypeName(), param.getIndividual()
					.getRepresentativeName()));

		// add the positive predicates
		List<Assertion> assertions = buildAssertions(transformation.getPositiveEffectAssertions(),
				transformation.getNegativeEffectAssertions());
		for (Assertion assertion : assertions)
			addAssertion(assertion);
		return build(true);
	}

	protected List<Assertion> buildAssertions(Set<OWLNamedIndividual> positiveAssertions,
			Set<OWLNamedIndividual> negativeAssertions) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		// add the positive assertions
		List<Assertion> assertions = new Vector<Assertion>();
		if (positiveAssertions != null)
			for (OWLNamedIndividual ind : positiveAssertions)
				assertions.add(buildAssertion(ind));

		// add the negative assertions (by wrapping it into a logical assertion)
		if (negativeAssertions != null)
			for (OWLNamedIndividual individual : negativeAssertions) {
				List<Assertion> arguments = new Vector<Assertion>();
				arguments.add(buildAssertion(individual));
				assertions.add(new LogicalAssertion(LogicalConnectiveType.NOT, arguments));
			}
		return assertions;
	}
}
