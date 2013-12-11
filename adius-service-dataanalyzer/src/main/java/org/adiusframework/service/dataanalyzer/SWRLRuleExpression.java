package org.adiusframework.service.dataanalyzer;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLBuiltInAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLDataPropertyAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import org.adiusframework.ontology.ClassPersistenceInfo;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

/**
 * Class for generating a SQL-term that is defined by a SWRL rule expression.
 * 
 * @author Alexander Tenbrock
 */
public class SWRLRuleExpression {
	/**
	 * The query this rule expression belongs to.
	 */
	private SqlQueryBase parentQuery;

	/**
	 * The SWRL rule object.
	 */
	private SWRLRule rule;

	/**
	 * The object argument. Used when the rule defines a class or when it
	 * defines a property.
	 */
	private SWRLIArgument objectArgument;

	/**
	 * The subject argument. Used when the rule defines a object property.
	 */
	private SWRLIArgument subjectArgument;

	/**
	 * The OWL class of the object argument.
	 */
	private OWLClassExpression objectClass;

	/**
	 * The OWL class of the subject argument.
	 */
	private OWLClassExpression subjectClass;

	/**
	 * This vector collects all the generated SQL terms. Later they are collect
	 * with " AND ".
	 */
	private Vector<String> restrictions = new Vector<String>();

	/**
	 * The content manager.
	 */
	private FrameworkOntologyManager contentManager;

	/**
	 * Constructor. Used when the SWRL rule is describing a OWL class.
	 * 
	 * @param parentQuery
	 *            parent SQL query this SWRL query belongs to
	 * @param rule
	 *            the SWRL rule
	 * @param subjectArgument
	 *            the argument describing the class restricted by this rule
	 * @param subjectClass
	 *            the OWL class of the object argument
	 * @param contentManager
	 *            the used content manager
	 */
	public SWRLRuleExpression(SqlQueryBase parentQuery, SWRLRule rule, SWRLIArgument subjectArgument,
			OWLClassExpression subjectClass, FrameworkOntologyManager contentManager) {
		this.parentQuery = parentQuery;
		this.subjectArgument = subjectArgument;
		this.subjectClass = subjectClass;
		this.rule = rule;
		this.contentManager = contentManager;
	}

	/**
	 * Constructor. Used when the SWRL rule is describing an object property.
	 * 
	 * @param parentQuery
	 *            parent SQL query this SWRL query belongs to
	 * @param rule
	 *            the SWRL rule
	 * @param subjectArgument
	 *            the subject argument of the property restricted by this rule
	 *            expression
	 * @param objectArgument
	 *            the object argument of the property restricted by this rule
	 * @param subjectClass
	 *            the OWL class of the object argument
	 * @param objectClass
	 *            the OWL class of the subject argument
	 * @param contentManager
	 *            the analyzer helper to use
	 */
	public SWRLRuleExpression(SqlQueryBase parentQuery, SWRLRule rule, SWRLIArgument subjectArgument,
			SWRLIArgument objectArgument, OWLClassExpression subjectClass, OWLClassExpression objectClass,
			FrameworkOntologyManager contentManager) {
		this.parentQuery = parentQuery;
		this.subjectArgument = subjectArgument;
		this.objectArgument = objectArgument;
		this.subjectClass = subjectClass;
		this.objectClass = objectClass;
		this.rule = rule;
		this.contentManager = contentManager;
	}

	/**
	 * This function collects all class expressions belonging to objectArgument
	 * in the rule and combines it into a single expression. This expression
	 * then can be interpreted as the class this objectArgument belongs to.
	 * 
	 * @param objectArgument
	 *            argument whose class is to be determined
	 * @return the class expression that describes the argument
	 */
	private OWLClassExpression collectSWRLObjectClassExpression(SWRLIArgument objectArgument) {
		Set<OWLClassExpression> expressions = new HashSet<OWLClassExpression>();

		// build set of all head and body atoms of the rule
		Set<SWRLAtom> atoms = new HashSet<SWRLAtom>();
		atoms.addAll(this.rule.getHead());
		atoms.addAll(this.rule.getBody());

		for (SWRLAtom atom : atoms) {
			// if this atom is a class definition atom
			if (atom instanceof SWRLClassAtom) {
				SWRLClassAtom classAtom = (SWRLClassAtom) atom;
				if (classAtom.getArgument().equals(objectArgument))
					expressions.add(classAtom.getPredicate());
			}
			// if this atom is a data property atom
			else if (atom instanceof SWRLDataPropertyAtom) {
				SWRLDataPropertyAtom propertyAtom = (SWRLDataPropertyAtom) atom;
				if (propertyAtom.getFirstArgument().equals(objectArgument))
					expressions.addAll(propertyAtom.getPredicate().getDomains(contentManager.getOntologies()));
			}
			// if this atom is an object property atom
			else if (atom instanceof SWRLObjectPropertyAtom) {
				SWRLObjectPropertyAtom propertyAtom = (SWRLObjectPropertyAtom) atom;
				if (propertyAtom.getFirstArgument().equals(objectArgument))
					expressions.addAll(propertyAtom.getPredicate().getDomains(contentManager.getOntologies()));
				else if (propertyAtom.getSecondArgument().equals(objectArgument))
					expressions.addAll(propertyAtom.getPredicate().getRanges(contentManager.getOntologies()));
			}
		}

		// check if it is the object or subject argument for this rule
		if (objectArgument.equals(this.objectArgument))
			expressions.add(this.objectClass);
		if (objectArgument.equals(this.subjectArgument))
			expressions.add(this.subjectClass);

		// build complex expression and return it (or null of no expression was
		// found)
		if (expressions.size() == 0)
			return null;
		if (expressions.size() == 1)
			return expressions.iterator().next();
		return contentManager.getDataFactory().getOWLObjectIntersectionOf(expressions);
	}

	/**
	 * Resolves the value of a data argument. If successful, the function
	 * returns a SQL string that can be evaluated to a numerical value.
	 * 
	 * @param argument
	 *            data argument whose value is be resolved
	 * @return string that contains the value of the argument
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String resolveSWRLDataArgument(SWRLDArgument argument) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		// if the argument is a literal value, directly convert it to a string
		// and return it
		if (argument instanceof SWRLLiteralArgument)
			return ((SWRLLiteralArgument) argument).getLiteral().getLiteral();

		// if the argument is no literal, find properties or builtins that will
		// deliver its value
		Vector<String> results = new Vector<String>();

		for (SWRLAtom bodyAtom : this.rule.getBody()) {

			// result of a built in math function?
			if (bodyAtom instanceof SWRLBuiltInAtom) {
				SWRLBuiltInAtom builtInAtom = (SWRLBuiltInAtom) bodyAtom;
				if (builtInAtom.getArguments().get(0).equals(argument)) {

					// SWRL math function or SQWRL aggregation?
					SWRLBuiltInsVocabulary mathFunction = SqlQueryHelper.getSWRLMathFunction(builtInAtom);

					if (mathFunction != null) {
						// resolve the arguments of this function
						String leftSide = resolveSWRLDataArgument(builtInAtom.getArguments().get(1));
						String rightSide = resolveSWRLDataArgument(builtInAtom.getArguments().get(2));

						// build the resulting term
						switch (mathFunction) {
						case ADD:
							results.add(leftSide + " + " + rightSide);
							break;
						case SUBTRACT:
							results.add(leftSide + " - (" + rightSide + ")");
							break;
						case MULTIPLY:
							results.add("(" + leftSide + ") * (" + rightSide + ")");
							break;
						case DIVIDE:
							results.add("(" + leftSide + ") / (" + rightSide + ")");
							break;
						case MOD:
							results.add("MOD (" + leftSide + ", " + rightSide + ")");
							break;
						default:
							// not supported
							break;
						}
					}
				}
			}

			// data property?
			else if (bodyAtom instanceof SWRLDataPropertyAtom) {
				SWRLDataPropertyAtom propertyAtom = (SWRLDataPropertyAtom) bodyAtom;
				if (propertyAtom.getSecondArgument().equals(argument)) {
					// process the restriction for the first parameter (the
					// individual)
					SWRLIArgument objectArgument = propertyAtom.getFirstArgument();
					processSWRLInvididualArgument(objectArgument);

					// return the expression string of this property
					OWLClassExpression objectArgumentClass = collectSWRLObjectClassExpression(objectArgument);
					OWLDataProperty property = propertyAtom.getPredicate().asOWLDataProperty();
					results.add(this.parentQuery.describeDataProperty(property, objectArgumentClass));
				}
			}
		}

		// check if this argument could not be resolved
		if (results.isEmpty())
			throw new UnsupportedExpressionException("could not resolve value of SWRL argument \"" + argument + "\"");
		return SqlQueryHelper.connectStrings(results, " AND ");
	}

	/**
	 * Processes an individual argument. This means that it adds a restriction
	 * that defines this individual.
	 * 
	 * @param argument
	 *            the argument to be processed
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	private void processSWRLInvididualArgument(SWRLIArgument argument) throws OWLInvalidContentException,
			UnsupportedExpressionException {

		// this variable is set to "true" as soon as the individual argument
		// could be resolved.
		boolean resolved = false;

		// check if the argument is an individual
		if (argument instanceof SWRLIndividualArgument) {
			SWRLIndividualArgument individual = (SWRLIndividualArgument) argument;
			OWLClassExpression argumentClass = collectSWRLObjectClassExpression(argument);

			String individualDbName = this.contentManager.getIndividualDatabaseName((OWLNamedIndividual) individual
					.getIndividual());
			ClassPersistenceInfo argumentClassPersistanceInfo = this.contentManager
					.getClassPersistenceInfo(argumentClass);

			this.parentQuery.getRootQuery().addTable(argumentClass, argumentClassPersistanceInfo);
			this.restrictions.add(this.parentQuery.getNameColumnString(argumentClass) + " = \"" + individualDbName
					+ "\"");
			resolved = true;
		}

		// find object properties that will deliver its value
		for (SWRLAtom bodyAtom : this.rule.getBody()) {
			if (bodyAtom instanceof SWRLObjectPropertyAtom) {
				SWRLObjectPropertyAtom propertyAtom = (SWRLObjectPropertyAtom) bodyAtom;
				if (propertyAtom.getSecondArgument().equals(argument)) {
					// process the restriction for the first parameter (the
					// individual)
					OWLClassExpression firstArgumentClass = collectSWRLObjectClassExpression(propertyAtom
							.getFirstArgument());
					processSWRLInvididualArgument(propertyAtom.getFirstArgument());

					// collect information about the class of the argument and
					// the property
					OWLClassExpression argumentClass = collectSWRLObjectClassExpression(argument);
					OWLObjectProperty property = propertyAtom.getPredicate().getNamedProperty();

					this.restrictions.add(this.parentQuery.describeObjectPropertyConnection(firstArgumentClass,
							property, argumentClass, false));
					resolved = true;
				}

			}
		}

		// throw an exception if this argument could not be resolved
		if (!resolved && !argument.equals(this.objectArgument) && !argument.equals(this.subjectArgument))
			throw new UnsupportedExpressionException("could not resolve value of SWRL argument \"" + argument + "\"");
	}

	/**
	 * This function processes a SWRL comparison. It resolves the arguments and
	 * combines them with the comparison operator.
	 * 
	 * @param negate
	 *            indicates if the comparison should be negated
	 * @param atom
	 *            the atom that represents the comparison
	 * @param comparison
	 *            the comparison operator
	 * @return the SQL equivalent of this comparison
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String processSWRLComparison(boolean negate, SWRLBuiltInAtom atom, SWRLBuiltInsVocabulary comparison)
			throws UnsupportedExpressionException, OWLInvalidContentException {
		String result = resolveSWRLDataArgument(atom.getArguments().get(0));
		result += " " + SqlQueryHelper.getComparator(comparison, negate) + " ";
		result += resolveSWRLDataArgument(atom.getArguments().get(1));
		return result;
	}

	/**
	 * Processes the SWRL rule and returns the query.
	 * 
	 * @param negate
	 *            indicates if the query should be negated
	 * @return the query string
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public String evaluate(boolean negate) throws UnsupportedExpressionException, OWLInvalidContentException {

		// clear all restrictions
		this.restrictions.clear();

		// look for comparisons and evaluate them
		for (SWRLAtom bodyAtom : this.rule.getBody()) {
			if (bodyAtom instanceof SWRLBuiltInAtom) {
				SWRLBuiltInAtom builtInAtom = (SWRLBuiltInAtom) bodyAtom;
				SWRLBuiltInsVocabulary comparison = null;
				if (builtInAtom.isCoreBuiltIn()) {
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.EQUAL.getIRI()))
						comparison = SWRLBuiltInsVocabulary.EQUAL;
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.NOT_EQUAL.getIRI()))
						comparison = SWRLBuiltInsVocabulary.NOT_EQUAL;
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.LESS_THAN.getIRI()))
						comparison = SWRLBuiltInsVocabulary.LESS_THAN;
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL.getIRI()))
						comparison = SWRLBuiltInsVocabulary.LESS_THAN_OR_EQUAL;
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.GREATER_THAN.getIRI()))
						comparison = SWRLBuiltInsVocabulary.GREATER_THAN;
					if (builtInAtom.getPredicate().equals(SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL.getIRI()))
						comparison = SWRLBuiltInsVocabulary.GREATER_THAN_OR_EQUAL;
				}
				// if a comparison was found, process it and add the result to
				// the restrictions
				if (comparison != null)
					this.restrictions.add(processSWRLComparison(negate, builtInAtom, comparison));
			}
		}

		// return the connected restrictions
		return SqlQueryHelper.connectStrings(this.restrictions, " AND ");
	}
}
