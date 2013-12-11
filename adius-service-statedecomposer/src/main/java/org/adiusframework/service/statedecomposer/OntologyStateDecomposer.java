package org.adiusframework.service.statedecomposer;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.AssertionVariable;
import org.adiusframework.resource.state.DefaultStateCollection;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateBuilder;
import org.adiusframework.resource.state.StateBuilder.StateTemplate;
import org.adiusframework.resource.state.StateCollection;
import org.adiusframework.service.decomposer.AssertionGraphRepresentation.MoreThanOnePathContainsAssertionEnitiyException;
import org.adiusframework.service.decomposer.AssertionGraphRepresentation.StateRelationGraphBuilder;
import org.adiusframework.service.statedecomposer.exception.InitializationException;
import org.adiusframework.service.statedecomposer.exception.StateDecomposerException;
import org.adiusframework.service.statedecomposer.exception.UnsupportedAssertionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyStateDecomposer implements StateDecomposer {

	private static Logger LOGGER = LoggerFactory.getLogger(OntologyStateDecomposer.class);

	private String ontology;

	public String getOntology() {
		return this.ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	@Override
	public StateCollection decompose(State baseState) throws StateDecomposerException {

		/*
		 * Is a TEST connects all assertion enities to figure out which have a
		 * relation with the staterepresentativ enties return in the end
		 * StateBuilders
		 */
		StateRelationGraphBuilder stateRelationGraphBuilder = new StateRelationGraphBuilder(baseState.getAssertions());
		// first lets create the ontology content manager
		FrameworkOntologyManager contentManager = null;
		try {
			contentManager = new FrameworkOntologyManager(getOntology());
		} catch (OWLEntityNotFoundException e) {
			throw new InitializationException(e.getMessage());
		} catch (OWLOntologyCreationException e) {
			throw new InitializationException(e.getMessage());
		}

		// second lets identify the concepts that can be decomposed and create a
		// data structure to facilitate the state decomposing
		List<String> singletons = new Vector<String>();
		for (OWLClass singleton : contentManager.getSingletonConcepts())
			singletons.add(contentManager.getClassName(singleton));
		for (AssertionObject object : baseState.getObjects()) {
			if (singletons.contains(object.getTypeName())) {
				stateRelationGraphBuilder.addStateRepresentiveAssertionEntry(object);
			}
		}

		// finally we can use the builders to create the final states
		// the getBuilder method builds the graphstructue and finds all the
		// assertions which belong to one representive and puts them into one
		// builder
		DefaultStateCollection dsc = new DefaultStateCollection();

		try {
			List<StateBuilder> builders = stateRelationGraphBuilder.getBuilders();
			for (StateBuilder builder : builders)
				dsc.addState(builder.build(true));
		} catch (MoreThanOnePathContainsAssertionEnitiyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dsc;
	}

	/*
	 * OLD ONE
	 * 
	 * @Override public StateCollection decompose(State baseState) throws
	 * StateDecomposerException {
	 * 
	 * 
	 * 
	 * StateRelationGraphBuilder stateRelationGraphBuilder = new
	 * StateRelationGraphBuilder();;
	 * 
	 * // first lets create the ontology content manager
	 * FrameworkOntologyManager contentManager = null; try { contentManager =
	 * new FrameworkOntologyManager(getOntology()); } catch
	 * (OWLEntityNotFoundException e) { throw new
	 * InitializationException(e.getMessage()); } catch
	 * (OWLOntologyCreationException e) { throw new
	 * InitializationException(e.getMessage()); }
	 * 
	 * // second lets identify the concepts that can be decomposed and create a
	 * // data structure to facilitate the state decomposing List<String>
	 * singletons = new Vector<String>(); for (OWLClass singleton :
	 * contentManager.getSingletonConcepts())
	 * singletons.add(contentManager.getClassName(singleton));
	 * StateRepresentativeList srl = new StateRepresentativeList( new
	 * StateRepresentativeTemplate(singletons)); for (AssertionObject object :
	 * baseState.getObjects()) { if (singletons.contains(object.getTypeName())){
	 * srl.addRepresentivePart(object.getTypeName(), object.getName());
	 * stateRelationGraphBuilder.addStateRepresentiveAssertionEntry(object); } }
	 * 
	 * stateRelationGraphBuilder.buildAssertionRelations(baseState.getAssertions(
	 * ));
	 * 
	 * 
	 * // now that we have the state representatives that define the //
	 * decomposition, we can proceed with the real decompose algorithm // we
	 * check for each assertion if one of the attached entities matches a //
	 * state part, if so the predicate is added to the state
	 * Map<StateRepresentative, StateBuilder> builders = new
	 * HashMap<StateRepresentative, StateBuilder>(); for (Assertion a :
	 * baseState.getAssertions()) { LOGGER.debug("Decomposing assertion " + a);
	 * decomposeAssertion(srl, a, builders); }
	 * 
	 * // finally we can use the builders to create the final states
	 * DefaultStateCollection dsc = new DefaultStateCollection(); for
	 * (StateBuilder builder : builders.values())
	 * dsc.addState(builder.build(true)); return dsc; }
	 */

	/**
	 * IS CURRENTY NOT IN USE Because it can't find relations which with a
	 * higher depth than 1
	 * 
	 * Method which combines all assertions which belong to the same
	 * StateRepresentative
	 * 
	 * @param srl
	 * @param a
	 * @param builders
	 * @throws UnsupportedAssertionException
	 */
	protected void decomposeAssertion(StateRepresentativeList srl, Assertion a,
			Map<StateRepresentative, StateBuilder> builders) throws UnsupportedAssertionException {

		// only predicates are supported, this should be the case, because
		// the base state should satisfy the current state template
		if (Predicate.class.isInstance(a)) {
			Predicate p = Predicate.class.cast(a);

			// for each entity used within the predicate, we have to find states
			// using this entity in a part
			boolean handled = false;
			for (AssertionEntity e : p.getArguments()) {

				// variables can be skipped, because the does not carry any part
				// information
				if (AssertionVariable.class.isInstance(e))
					continue;

				// now lets filter the state representatives by using the type
				// and name of the assertion as filter criteria
				List<StateRepresentative> fl = srl.getRepresentatives(e.getTypeName(), e.getName());

				for (StateRepresentative sr : fl) {

					// lets check if a builder already exists, otherwise we
					// create a new one
					if (!builders.containsKey(sr))
						builders.put(sr, new StateBuilder(StateTemplate.AS_IS_STATE));
					StateBuilder builder = builders.get(sr);
					builder.addAssertion(a);
					handled = true;
				}
			}

			// lets check if the assertion has been handled, if not i think this
			// is not a good sign
			if (!handled)
				LOGGER.warn("Assertion " + a
						+ " has not been handled by any state builder and will be removed from the problem space.");
		} else
			throw new UnsupportedAssertionException(a);
	}

}
