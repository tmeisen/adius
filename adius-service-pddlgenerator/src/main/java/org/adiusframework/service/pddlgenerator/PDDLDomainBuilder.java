package org.adiusframework.service.pddlgenerator;

import java.util.HashSet;
import java.util.Set;

import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyBasedStateBuilder;
import org.adiusframework.ontology.frameworkontology.model.Feature;
import org.adiusframework.ontology.frameworkontology.model.ParameterRestriction;
import org.adiusframework.ontology.frameworkontology.model.Transformation;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateBuilder.StateTemplate;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;

public class PDDLDomainBuilder {

	private Domain domain;

	/**
	 * Constructor.
	 * 
	 * @param contentManager
	 *            content manager for accessing the ontology
	 */
	public PDDLDomainBuilder(Domain domain) {
		setDomain(domain);
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	/**
	 * Write the list of used types into the domain file
	 * 
	 * @return a string of the type representation in PDDL
	 */
	private String buildTypes() {
		StringBuilder result = new StringBuilder();

		for (Node<OWLClass> owlClass : getDomain().getContentManager().getObjectClasses())
			if (getDomain().getContentManager().getReasoner().isSatisfiable(owlClass.getRepresentativeElement())) {

				// write the type name
				result.append("\t" + getDomain().getContentManager().getShortestNodeLabel(owlClass));

				// write the parent type
				for (Node<OWLClass> superClass : getDomain().getContentManager().getReasoner()
						.getSuperClasses(owlClass.getRepresentativeElement(), true))
					result.append(" - " + getDomain().getContentManager().getShortestNodeLabel(superClass) + "\n");
			}
		return result.toString();
	}

	/**
	 * Writes the list of all constants defined in the ontology.
	 * 
	 * @return a string containing the list of constants in pddl conform syntax.
	 * @throws OWLInvalidContentException
	 *             if the constants could not be identified successfully
	 */
	private String buildConstants() throws OWLInvalidContentException {
		StringBuilder result = new StringBuilder();
		for (AssertionObject constant : getDomain().getConstants()) {
			result.append("\t").append(constant.getName()).append(" - ").append(constant.getTypeName()).append("\n");
		}
		return result.toString();
	}

	/**
	 * Writes the list of all features and axioms into the domain file.
	 * 
	 * @return a string containing the list of features/axioms
	 * @throws OWLInvalidContentException
	 */
	private String buildFeaturesAndAxioms() throws OWLInvalidContentException {
		StringBuilder result = new StringBuilder();

		// collect a set of all features and axioms
		Set<Feature> featureAndAxiomSet = new HashSet<Feature>();
		featureAndAxiomSet.addAll(getDomain().getContentManager().getFeatures());
		featureAndAxiomSet.addAll(getDomain().getContentManager().getAxioms());

		for (Feature feature : featureAndAxiomSet)
			if (feature.isSatisfiable()) {
				// write the feature/axiom's name
				result.append("\t(" + feature.getRepresentativeName());

				// write all parameters
				int i = 0;
				for (ParameterRestriction param : feature.getParameterRestrictions())
					result.append(" ?param" + (i++) + " - "
							+ getDomain().getContentManager().getClassName(param.getClassExpression()));
				result.append(")\n");
			}

		return result.toString();
	}

	public void buildAction(Transformation transformation, StringBuilder result) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		// set up the before and after states
		FrameworkOntologyBasedStateBuilder stateBuilder = new FrameworkOntologyBasedStateBuilder(getDomain()
				.getContentManager(), StateTemplate.ACTION_STATE);
		State stateBefore = stateBuilder.buildFromPreconditions(transformation);
		State stateAfter = stateBuilder.buildFromEffects(transformation);

		result.append("(:action " + transformation.getRepresentativeName() + "\n");

		result.append(" :parameters (");
		PDDLStateBuilder.writeVariables(stateBefore.getVariables(), result);
		result.append(")\n");

		// write the precondition part of the action
		result.append(" :precondition\n\t(AND\n");
		PDDLStateBuilder.writeAssertions(stateBefore, result, null, 2);
		result.append("\t)\n");

		// write the effect part of the action
		result.append(" :effect\n\t(AND\n");
		PDDLStateBuilder.writeAssertions(stateAfter, result, null, 2);

		result.append("\t)\n");
		result.append(")\n");
	}

	/**
	 * Builds the description of this problem.
	 * 
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	public String buildPDDL() throws OWLInvalidContentException, UnsupportedExpressionException {

		// initialize the object
		StringBuilder result = new StringBuilder();

		// write domain definition
		result.append("(define (domain " + getDomain().getName() + ")\n\n");

		// write requirements
		result.append("(:requirements :strips :adl :typing)\n\n");

		// write type definitions
		result.append("(:types\n");
		result.append(buildTypes());
		result.append(")\n\n");

		// write constants
		result.append("(:constants\n");
		result.append(buildConstants());
		result.append(")\n\n");

		// write predicates
		result.append("(:predicates\n");
		result.append(buildFeaturesAndAxioms());
		result.append(")\n\n");

		// write actions
		for (Transformation transformation : getDomain().getContentManager().getTransformations())
			buildAction(transformation, result);

		// close domain file
		result.append(")");
		return result.toString();
	}

}
