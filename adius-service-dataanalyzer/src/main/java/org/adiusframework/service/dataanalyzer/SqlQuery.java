package org.adiusframework.service.dataanalyzer;

import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLQuantifiedDataRestriction;
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLClassAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.DataRangeType;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataHasValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyConstants;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

/**
 * Class used for building SQL queries based on OWL class expressions. A query
 * can either be a root query or a subquery. The class provides methods for
 * evaluating different types OWL class expressions. These functions return
 * strings that are the SQL equivalent of that class expression.
 * 
 * @author Alexander Tenbrock
 */
public class SqlQuery extends SqlQueryBase {

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            parent query if this is a subquery
	 * @param queryType
	 *            type of the query
	 * @param contentManager
	 *            the content manager to use
	 * @throws OWLInvalidContentException
	 */
	public SqlQuery(SqlQueryBase parent, QueryType queryType, int numResultColumns,
			FrameworkOntologyManager contentManager) throws OWLInvalidContentException {
		super(parent, queryType, numResultColumns, contentManager);
	}

	/**
	 * This method evaluates an OWL ObjectHasValue expression and returns the
	 * corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateHasValue(OWLObjectHasValue expression, boolean negate, OWLClassExpression parentExpression)
			throws OWLInvalidContentException, UnsupportedExpressionException

	{
		// get the restricted property
		OWLObjectProperty property = expression.getProperty().getNamedProperty();

		// verify that the individual is not anonymous
		if (expression.getValue().isAnonymous())
			throw new UnsupportedExpressionException("Error: anonymous individual not supported in HasValue expression");

		// get the individual and its name in the database
		OWLNamedIndividual value = expression.getValue().asOWLNamedIndividual();
		String valueDbName = this.contentManager.getIndividualDatabaseName(value);

		// check if it is a value type property
		if (this.contentManager.isValueTypeProperty(property)) {
			// get the associated data property for this ValueType object
			// property
			OWLAnnotationProperty annotation = contentManager
					.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_DATAPROPERTY);
			OWLDataProperty dataProperty = (OWLDataProperty) contentManager.getSingleAnnotationEntity(property,
					annotation);
			if (dataProperty == null)
				throw new OWLInvalidContentException(annotation.getIRI().getFragment()
						+ " annotation not found for property", property);

			return describeDataProperty(dataProperty, parentExpression) + " = \"" + valueDbName + "\"";
		}

		// get the class of the invidiual
		OWLClassExpression valueClass = this.contentManager.getTypeExpression(value);

		// compare the parent expression's foreign key column to the individual
		// class's id column
		String result = describeObjectPropertyConnection(parentExpression, property, valueClass, negate);

		// and compare the individual class's name column name to the
		// individual's database name
		result += " AND " + getNameColumnString(valueClass) + " = \"" + valueDbName + "\"";
		return result;
	}

	/**
	 * This method evaluates an OWL ObjectSomeValuesFrom or ObjectAllValuesFrom
	 * expression and returns the corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateQuantifiedObjectRestriction(OWLQuantifiedObjectRestriction expression, boolean negate,
			OWLClassExpression parentExpression) throws OWLInvalidContentException, UnsupportedExpressionException {
		// get the restricted property
		OWLObjectProperty property = expression.getProperty().getNamedProperty();

		if (contentManager.isValueTypeClass(expression.getFiller())) {

			// in case it is a value type property, just set the query's result
			// column and don't return any expression
			getRootQuery().setResultColumnToValueType(parentExpression, property, expression.getFiller());
			return null;

		} else if (contentManager.isPropertyFunctional(property)) {

			// if the property is functional, there is no need to create a
			// subquery
			String result = describeObjectPropertyConnection(parentExpression, property, expression.getFiller(), negate);
			String fillerResult = evaluateExpression(expression.getFiller(), false, parentExpression);
			if (fillerResult != null)
				result += " AND " + fillerResult;
			return result;

		} else {

			// if the property is not functional, we need to create a subquery
			SqlQuery subCondition = new SqlQuery(this, QueryType.Standard, 1, this.contentManager);

			// query for the parent expression's id column
			subCondition.setResultColumn(0, parentExpression, null, null);

			// the restricted property defines the WHERE clause in the subquery
			String propertyExpression = subCondition.describeObjectPropertyConnection(parentExpression, property,
					expression.getFiller(), negate);
			subCondition.addManadatoryRestriction(propertyExpression);

			if (expression.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
				// if the restriction type is SomeValuesFrom, just create the
				// subquery in a straightforward way
				subCondition.addExpression(expression.getFiller(), false);
				return getIDColumnString(parentExpression) + " IN (" + subCondition.getQueryString() + ")";
			} else if (expression.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
				// if the restriction type is AllValuesFrom, create a negated
				// subquery and check if parent expression is NOT in it
				subCondition.addExpression(expression.getFiller(), true);
				return getIDColumnString(parentExpression) + " NOT IN (" + subCondition.getQueryString() + ")";
			} else
				// throw Exception. Should'nt happen
				throw new UnsupportedExpressionException("Unsupported quantified restriction", expression);
		}
	}

	/**
	 * This method evaluates an OWL DataHasValue expression and returns the
	 * corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateDataHasValue(OWLDataHasValue expression, boolean negate, OWLClassExpression parentExpression)
			throws UnsupportedExpressionException, OWLInvalidContentException, OWLInvalidContentException {
		// check if the restricted property is functional
		OWLDataProperty property = expression.getProperty().asOWLDataProperty();
		if (!contentManager.isPropertyFunctional(property))
			throw new UnsupportedExpressionException("only functional data properties are allowed", property);

		// return the comparison expressions
		return describeDataProperty(property, parentExpression) + " " + SqlQueryHelper.getComparator(negate) + " "
				+ expression.getValue().getLiteral();
	}

	/**
	 * This method evaluates an OWL DataSomeValuesFrom or DataAllValuesFrom
	 * expression and returns the corresponding SQL term. (For data properties,
	 * these restrictions are equivalent because all data properties must
	 * functional)
	 * 
	 * @param expression
	 *            OWL expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateDataQuantifiedRestriction(OWLQuantifiedDataRestriction expression, boolean negate,
			OWLClassExpression parentExpression) throws UnsupportedExpressionException, OWLInvalidContentException,
			OWLInvalidContentException

	{
		// check if the restricted property is functional
		OWLDataProperty property = expression.getProperty().asOWLDataProperty();
		if (!contentManager.isPropertyFunctional(property))
			throw new UnsupportedExpressionException("only functional data properties are allowed", property);

		// get the restriction object
		OWLDataRange range = expression.getFiller();
		OWLDatatypeRestriction restriction = null;
		if (range.getDataRangeType() == DataRangeType.DATATYPE_RESTRICTION)
			restriction = (OWLDatatypeRestriction) range;
		else
			throw new UnsupportedExpressionException("unsupported data range type");

		// create a list of all restrictions for this property
		Vector<String> facetRestrictions = new Vector<String>();

		for (OWLFacetRestriction facetRestriction : restriction.getFacetRestrictions()) {
			// get the comparator
			String comparator = SqlQueryHelper.getComparator(facetRestriction.getFacet(), negate);

			// build the comparison
			String comparison = describeDataProperty(property, parentExpression) + " " + comparator + " "
					+ facetRestriction.getFacetValue().getLiteral();
			facetRestrictions.add(comparison);
		}
		return SqlQueryHelper.connectStrings(facetRestrictions, " AND ");
	}

	/**
	 * This method evaluates an OWL class and returns the corresponding SQL
	 * term.
	 * 
	 * @param expression
	 *            OWL class to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateClass(OWLClass expression, boolean negate) throws UnsupportedExpressionException,
			OWLInvalidContentException, OWLInvalidContentException {
		// the list of all SQL expressions that are generated
		Vector<String> expressions = new Vector<String>();

		// check if this class is part of a swrl rule consequent
		for (OWLOntology ontology : contentManager.getOntologies())
			for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE, true)) {
				SWRLIArgument globalRuleArgument = null;
				for (SWRLAtom headAtom : rule.getHead()) {
					if (headAtom instanceof SWRLClassAtom) {
						SWRLClassAtom classAtom = (SWRLClassAtom) headAtom;
						if (expression == classAtom.getPredicate())
							globalRuleArgument = classAtom.getArgument();
					}
				}

				// if a fitting SWRL rule is found, evaluate it and add the
				// resulting SQL string to this function's resulting expression
				// list
				if (globalRuleArgument != null) {
					SWRLRuleExpression ruleExpression = new SWRLRuleExpression(this, rule, globalRuleArgument,
							expression, this.contentManager);
					expressions.add(ruleExpression.evaluate(negate));
				}
			}
		return SqlQueryHelper.connectStrings(expressions, " AND ");
	}

	/**
	 * This method evaluates an OWL intersection expression and returns the
	 * corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL intersection expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateIntersection(OWLObjectIntersectionOf expression, boolean negate)
			throws UnsupportedExpressionException, OWLInvalidContentException {
		Set<OWLClassExpression> subExpressions = expression.asConjunctSet();
		if (subExpressions.contains(expression))
			throw new UnsupportedExpressionException("Error: expression is no conjunct set");

		String result = null;

		for (OWLClassExpression expr : subExpressions) {
			String expr_result = evaluateExpression(expr, negate, expression);
			if (expr_result != null) {
				if (result == null)
					result = expr_result;
				else {
					if (!negate)
						result += " AND " + expr_result;
					else
						result += " OR " + expr_result;
				}
			}
		}
		if (result != null)
			return "(" + result + ")";
		return null;
	}

	/**
	 * This method evaluates an OWL union expression and returns the
	 * corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL union expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateUnion(OWLObjectUnionOf expression, boolean negate, OWLClassExpression parentExpression)
			throws UnsupportedExpressionException, OWLInvalidContentException {
		Set<OWLClassExpression> subExpressions = expression.asDisjunctSet();
		if (subExpressions.contains(expression))
			throw new UnsupportedExpressionException("Error: expression is no disjunct set");

		String result = null;

		for (OWLClassExpression expr : subExpressions) {
			String expr_result = evaluateExpression(expr, negate, expression);

			if (expr_result != null) {
				if (result == null)
					result = expr_result;
				else {
					if (!negate)
						result += " OR " + expr_result;
					else
						result += " AND " + expr_result;
				}
			}
		}
		if (result != null)
			return "(" + result + ")";
		return null;
	}

	/**
	 * This method evaluates an generic OWL expression and returns the
	 * corresponding SQL term.
	 * 
	 * @param expression
	 *            OWL expression to be evaluated
	 * @param negate
	 *            if true, the expression is negated
	 * @param parentExpression
	 *            parent expression
	 * @return the generated SQL term
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private String evaluateExpression(OWLClassExpression expression, boolean negate, OWLClassExpression parentExpression)
			throws UnsupportedExpressionException, OWLInvalidContentException {
		switch (expression.getClassExpressionType()) {
		case OBJECT_INTERSECTION_OF:
			return evaluateIntersection((OWLObjectIntersectionOf) expression, negate);
		case OBJECT_UNION_OF:
			return evaluateUnion((OWLObjectUnionOf) expression, negate, parentExpression);
		case OBJECT_COMPLEMENT_OF:
			return evaluateExpression(((OWLObjectComplementOf) expression).getOperand(), !negate, parentExpression);
		case OBJECT_HAS_VALUE:
			return evaluateHasValue((OWLObjectHasValue) expression, negate, parentExpression);
		case DATA_HAS_VALUE:
			return evaluateDataHasValue((OWLDataHasValue) expression, negate, parentExpression);
		case OBJECT_ALL_VALUES_FROM:
		case OBJECT_SOME_VALUES_FROM:
			return evaluateQuantifiedObjectRestriction((OWLQuantifiedObjectRestriction) expression, negate,
					parentExpression);
		case DATA_ALL_VALUES_FROM:
		case DATA_SOME_VALUES_FROM:
			return evaluateDataQuantifiedRestriction((OWLQuantifiedDataRestriction) expression, negate,
					parentExpression);
		case OWL_CLASS:
			return evaluateClass((OWLClass) expression, negate);
		default:
			throw new UnsupportedExpressionException("unknown class expression: " + expression.toString());
		}
	}

	/**
	 * Evaluates an expression and adds its restrictions to the query.
	 * 
	 * @param expression
	 *            the expression to query
	 * @param negate
	 *            indicates if the query has to be negated
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public void addExpression(OWLClassExpression expression, boolean negate) throws UnsupportedExpressionException,
			OWLInvalidContentException {
		String expressionWhereClause = evaluateExpression(expression, negate, null);
		if (expressionWhereClause != null)
			this.mandatoryRestrictions.add(expressionWhereClause);
	}

	/**
	 * This method builds and returns the complete SQL string of this query.
	 * 
	 * @return string that contains the SQL query
	 * @throws UnsupportedExpressionException
	 */
	public String getQueryString() throws UnsupportedExpressionException {
		if (this.queryType != QueryType.Standard && this.resultColumns.size() > 1)
			throw new UnsupportedExpressionException("only one target column allowed for aggregate functions");

		String result = null;

		switch (this.queryType) {
		case Min:
			result = "SELECT MIN(" + this.resultColumns.get(0) + ")";
			break;
		case Max:
			result = "SELECT MAX(" + this.resultColumns.get(0) + ")";
			break;
		case Sum:
			result = "SELECT SUM(" + this.resultColumns.get(0) + ")";
			break;
		case Avg:
			result = "SELECT AVG(" + this.resultColumns.get(0) + ")";
			break;
		case Count:
			result = "SELECT COUNT(" + this.resultColumns.get(0) + ")";
			break;
		case Standard:
			result = "SELECT DISTINCT " + SqlQueryHelper.connectStrings(this.resultColumns, ", ");
		}

		result += " FROM ";
		for (int i = 0; i < this.tables.size(); i++) {
			if (i > 0)
				result += ", ";
			result += this.tables.get(i).getTable();
			if (this.tables.get(i).getAlias() != null)
				result += " AS " + this.tables.get(i).getAlias();
		}

		Vector<String> whereClauses = new Vector<String>(this.mandatoryRestrictions);

		// add all alternative restrictions, connected with OR
		if (this.alternativeRestrictions.size() > 0)
			whereClauses.add("(" + SqlQueryHelper.connectStrings(this.alternativeRestrictions, " OR ") + ")");

		// connect all WHERE clauses with AND
		String connectedString = SqlQueryHelper.connectStrings(whereClauses, " AND ");
		if (connectedString != null)
			result += " WHERE " + connectedString;

		// return the result
		return result;
	}
}
