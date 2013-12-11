package org.adiusframework.service.dataanalyzer;

import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLObjectPropertyAtom;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.adiusframework.ontology.ClassPersistenceInfo;
import org.adiusframework.ontology.PropertyPersistenceInfo;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyConstants;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

/**
 * Enumeration that determines the type of a SQL query.
 * 
 * @author Alexander Tenbrock
 */
enum QueryType {
	Standard, Count, Min, Max, Sum, Avg;
}

/**
 * Base class for SQL expressions. It consists of supporting functions that are
 * used internally by the Query class.
 * 
 * @author Alexander Tenbrock
 */
public class SqlQueryBase {

	/**
	 * An entry in the query table.
	 */
	protected class QueryTableEntry {
		private OWLClassExpression expression;
		private String table;
		private String alias;

		public QueryTableEntry(OWLClassExpression expression, String table, String alias) {
			this.expression = expression;
			this.table = table;
			this.alias = alias;
		}

		public OWLClassExpression getExpression() {
			return this.expression;
		}

		public String getTable() {
			return this.table;
		}

		public String getAlias() {
			return this.alias;
		}
	}

	/**
	 * The parent query (if this is a subquery).
	 */
	private SqlQueryBase parent = null;

	/**
	 * The content manager.
	 */
	protected FrameworkOntologyManager contentManager;

	/**
	 * List of all tables and their aliases used in this (sub-)query.
	 */
	protected Vector<QueryTableEntry> tables = new Vector<QueryTableEntry>();

	/**
	 * List of all columns that are returned by this query.
	 */
	protected Vector<String> resultColumns = new Vector<String>();

	/**
	 * List of all the result columns' classes.
	 */
	private Vector<OWLClassExpression> resultColumnClasses = new Vector<OWLClassExpression>();

	/**
	 * List of restrictions that must be fulfilled. They are connected by "AND".
	 */
	protected Vector<String> mandatoryRestrictions = new Vector<String>();

	/**
	 * List of alternative restrictions. Only one of them has to be fulfilled.
	 * They are connected by "OR".
	 */
	protected Vector<String> alternativeRestrictions = new Vector<String>();

	/**
	 * Type of this query.
	 */
	protected QueryType queryType = QueryType.Standard;

	/**
	 * Number of tables. Used for generating the number of the next table alias.
	 * Usually only the tableCount of the top query is used to avoid duplicate
	 * table aliases.
	 */
	private int tableCount = 0;

	/**
	 * Constructor of the QueryBase class.
	 * 
	 * @param parent
	 *            parent Query
	 * @param queryType
	 *            type of the query
	 * @param contentManager
	 *            the analyzer helper
	 * @throws OWLInvalidContentException
	 */
	protected SqlQueryBase(SqlQueryBase parent, QueryType queryType, int numResultColumns,
			FrameworkOntologyManager contentManager) throws OWLInvalidContentException {
		this.parent = parent;
		this.queryType = queryType;
		this.contentManager = contentManager;
		this.resultColumns.setSize(numResultColumns);
		this.resultColumnClasses.setSize(numResultColumns);
	}

	/**
	 * Return the root query in a query tree.
	 * 
	 * @return root query
	 */
	protected SqlQueryBase getRootQuery() {
		if (this.parent != null)
			return this.parent.getRootQuery();
		return this;
	}

	/**
	 * Returns the next index of a table alias. This function is usually only
	 * called on the root query in order to avoid double uses of the same table
	 * alias in a nested query.
	 * 
	 * @return table index
	 */
	protected int getNextTableIndex() {
		return this.tableCount++;
	}

	/**
	 * Finds the table alias of an expression in this query only. If it does not
	 * exist the function returns null.
	 * 
	 * @param expression
	 *            expression to find the alias for
	 * @return alias name or null
	 */
	private String findTableAlias(OWLClassExpression expression) {
		OWLClassExpression namedExpression = this.contentManager.getNamedClassExpression(expression);

		// check if the expression is in the table list
		for (QueryTableEntry entry : this.tables)
			if (contentManager.isEquivalent(entry.getExpression(), namedExpression)) {
				if (entry.getAlias() != null)
					return entry.getAlias();
				return entry.getTable();
			}
		return null;
	}

	/**
	 * Finds the table alias of an expression in this query and also in the
	 * parent queries. If it does not exist the function returns null.
	 * 
	 * @param expression
	 *            expression to find the alias for
	 * @return alias name or null
	 */
	private String findTableAliasGlobal(OWLClassExpression expression) {
		String result = findTableAlias(expression);
		if (result != null)
			return result;

		if (this.parent != null)
			return this.parent.findTableAliasGlobal(expression);
		return null;
	}

	/**
	 * Adds an alias and a table name for an expression to the table list of
	 * this query.
	 * 
	 * @param expression
	 *            class expression to be added to the list
	 * @param pi
	 *            persistence information of the class expression
	 * @return the generated table alias
	 */
	public String addTable(OWLClassExpression expression, ClassPersistenceInfo pi) {
		// check if the expression is already is the table list
		String alias = findTableAlias(expression);
		if (alias != null)
			return alias;

		// create a class expression that only consists of (intersections of)
		// named classes
		OWLClassExpression namedExpression = this.contentManager.getNamedClassExpression(expression);

		// create the table alias, and add the table entry
		alias = "table" + getRootQuery().getNextTableIndex();
		this.tables.add(new QueryTableEntry(namedExpression, pi.getDbTable(), alias));

		// return the alias
		return alias;
	}

	/**
	 * Sets a column that this query should retrieve.
	 * 
	 * @param index
	 *            index of the column to set
	 * @param expression
	 *            class expression that identifies the table the column belongs
	 *            to
	 * @param pi
	 *            persistence information of the class expression (may be null)
	 * @param columnName
	 *            name of the column to add - if set to null, the id column will
	 *            be used
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	public void setResultColumn(int index, OWLClassExpression expression, ClassPersistenceInfo pi, String columnName)
			throws UnsupportedExpressionException, OWLInvalidContentException {
		// get the persistence info of the expression
		ClassPersistenceInfo tmpPi = (pi != null ? pi : this.contentManager.getClassPersistenceInfo(expression));
		// decide what column name to use
		String tmpColumnName = (columnName != null ? columnName : tmpPi.getDbIDColumn());

		// set the column class and name
		this.resultColumnClasses.set(index, expression);
		this.resultColumns.set(index, getColumnString(expression, tmpPi, tmpColumnName, true));
	}

	/**
	 * Sets the class expression of a result column. This is used for value
	 * types. For value types, the actual table can only be deduced from the
	 * context inside a feature's descriptive class expression and not from the
	 * actual persistence information of the value type class. Therefore, only
	 * the value type class is set.
	 * 
	 * @param index
	 * @param expression
	 */
	public void setResultColumnClass(int index, OWLClassExpression expression) {
		// set the column class
		this.resultColumnClasses.set(index, expression);
	}

	/**
	 * Sets a result column for a value type.
	 * 
	 * @param subjectClass
	 *            the subject class of the property
	 * @param property
	 *            the property that defines the value of this column
	 * @param objectClass
	 *            the object class of the property (this is the value type
	 *            class)
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	public void setResultColumnToValueType(OWLClassExpression subjectClass, OWLObjectProperty property,
			OWLClassExpression objectClass) throws OWLInvalidContentException, UnsupportedExpressionException {

		// verify that the property is functional
		if (!contentManager.isPropertyFunctional(property))
			throw new UnsupportedExpressionException("Value type properties must be functional", property);

		// get the associated data property for this ValueType object property
		OWLAnnotationProperty annotation = contentManager
				.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_DATAPROPERTY);
		OWLDataProperty dataProperty = (OWLDataProperty) contentManager.getSingleAnnotationEntity(property, annotation);
		if (dataProperty == null)
			throw new OWLInvalidContentException(annotation.getIRI().getFragment()
					+ " annotation not found for property", property);

		// set all fitting columns
		for (int i = 0; i < this.resultColumnClasses.size(); i++)
			if (this.resultColumnClasses.get(i).equals(objectClass))
				this.resultColumns.set(i, describeDataProperty(dataProperty, subjectClass));
	}

	/**
	 * Returns a string that identifies a column, including the table alias.
	 * 
	 * @param expression
	 *            class expression that identifies the table
	 * @param pi
	 *            persistence information of the class expression
	 * @param column
	 *            name of the column
	 * @param forceLocalTable
	 *            set to true if tables from a parent query must not be used
	 * @return complete column name.
	 */
	private String getColumnString(OWLClassExpression expression, ClassPersistenceInfo pi, String column,
			boolean forceLocalTable) {
		String alias = null;
		if (!forceLocalTable)
			alias = findTableAliasGlobal(expression);
		if (alias == null)
			alias = addTable(expression, pi);
		return alias + "." + column;
	}

	/**
	 * Returns a string that identifies a column, including the table alias.
	 * 
	 * @param expression
	 *            class expression that identifies the table
	 * @param column
	 *            name of the column
	 * @return complete column name
	 * @throws OWLInvalidContentException
	 */
	protected String getColumnString(OWLClassExpression expression, String column) throws OWLInvalidContentException {
		ClassPersistenceInfo pi = this.contentManager.getClassPersistenceInfo(expression);
		return getColumnString(expression, pi, column, false);
	}

	/**
	 * Returns a string that identifies the primary key of the expression,
	 * including the table alias
	 * 
	 * @param expression
	 *            class expression
	 * @return complete column name
	 * @throws OWLInvalidContentException
	 */
	protected String getIDColumnString(OWLClassExpression expression) throws OWLInvalidContentException {
		ClassPersistenceInfo pi = this.contentManager.getClassPersistenceInfo(expression);
		return getColumnString(expression, pi, pi.getDbIDColumn(), false);
	}

	/**
	 * Returns a string that identifies the name column of the expression,
	 * including the table alias
	 * 
	 * @param expression
	 *            class expression
	 * @return complete column name
	 * @throws OWLInvalidContentException
	 */
	protected String getNameColumnString(OWLClassExpression expression) throws OWLInvalidContentException {
		ClassPersistenceInfo pi = this.contentManager.getClassPersistenceInfo(expression);
		return getColumnString(expression, pi, pi.getDbNameColumn(), false);
	}

	/**
	 * Adds a mandatory restriction to this query.
	 * 
	 * @param restriction
	 *            the restriction string to add
	 * @throws OWLInvalidContentException
	 */
	protected void addManadatoryRestriction(String restriction) throws OWLInvalidContentException {
		this.mandatoryRestrictions.add(restriction);
	}

	/**
	 * Adds a alternative restriction to this query. The primary key of the
	 * object class is used for the comparison.
	 * 
	 * @param objectClass
	 *            class expression that identifies the database table
	 * @param objectID
	 *            value that the primary key is compared to
	 * @throws OWLInvalidContentException
	 */
	protected void addAlternativeObjectIDRestriction(OWLClassExpression objectClass, String objectID)
			throws OWLInvalidContentException {
		this.alternativeRestrictions.add(getIDColumnString(objectClass) + " = " + objectID);
	}

	/**
	 * Adds an alternative restriction to this query.
	 * 
	 * @param restriction
	 *            the restriction to be added
	 * @throws OWLInvalidContentException
	 */
	protected void addAlternativeRestriction(String restriction) {
		this.alternativeRestrictions.add(restriction);
	}

	/**
	 * This method generates a SQL term that describes a data property.
	 * 
	 * @param property
	 *            the data property to be described
	 * @param parentExpression
	 *            the parent expression the property is used with
	 * @return the SQL term for this property
	 * @throws OWLInvalidContentException
	 * @throws UnsupportedExpressionException
	 */
	protected String describeDataProperty(OWLDataProperty property, OWLClassExpression parentExpression)
			throws OWLInvalidContentException, UnsupportedExpressionException {

		Set<OWLDataProperty> superProperties = contentManager.getReasoner().getSuperDataProperties(property, false)
				.getFlattened();

		if (superProperties.contains(contentManager
				.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_AGGREGATION))) {

			// it is an aggregation data property
			QueryType subQueryType = null;
			if (superProperties.contains(contentManager
					.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_MINIMUM)))
				subQueryType = QueryType.Min;
			else if (superProperties.contains(contentManager
					.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_MAXIMUM)))
				subQueryType = QueryType.Max;
			else if (superProperties.contains(contentManager
					.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_SUM)))
				subQueryType = QueryType.Sum;
			else if (superProperties.contains(contentManager
					.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_AVERAGE)))
				subQueryType = QueryType.Avg;
			else if (superProperties.contains(contentManager
					.getDataProperty(FrameworkOntologyConstants.DATAPROPERTY_COUNT)))
				subQueryType = QueryType.Count;

			// get the associated data property and object property
			OWLObjectProperty objectProperty = (OWLObjectProperty) contentManager.getSingleAnnotationEntity(property,
					contentManager
							.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_AGGREGATIONOBJECTPROPERTY));
			OWLDataProperty dataProperty = (OWLDataProperty) contentManager.getSingleAnnotationEntity(property,
					contentManager.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_DATAPROPERTY));
			String dbAttribute = contentManager.getSingleAnnotationString(dataProperty,
					contentManager.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_ATTRIBUTE));

			// check for errors
			if ((objectProperty == null) || (dataProperty == null))
				throw new OWLInvalidContentException("aggregation property annotations not found", property);
			if (contentManager.isPropertyFunctional(objectProperty))
				throw new UnsupportedExpressionException("Only nonfunctional properties supported for aggregations",
						property);

			// get the target class expression of the object property
			NodeSet<OWLClass> ranges = contentManager.getReasoner().getObjectPropertyRanges(objectProperty, false);
			OWLClassExpression propertyTargetType = ranges.iterator().next().getRepresentativeElement();
			if (!ranges.isSingleton())
				propertyTargetType = contentManager.getDataFactory().getOWLObjectIntersectionOf(ranges.getFlattened());

			// execute query
			SqlQuery subCondition = new SqlQuery(this, subQueryType, 1, this.contentManager);
			subCondition.setResultColumn(0, propertyTargetType, null, dbAttribute);
			subCondition.addExpression(propertyTargetType, false);
			subCondition.addManadatoryRestriction(subCondition.describeObjectPropertyConnection(parentExpression,
					objectProperty, propertyTargetType, false));

			return "(" + subCondition.getQueryString() + ")";
		}

		// if it is not an aggregation property, it must be a native data
		// property
		String dbAttribute = contentManager.getSingleAnnotationString(property,
				contentManager.getAnnotationProperty(FrameworkOntologyConstants.ANNOTATION_ATTRIBUTE));
		if (dbAttribute == null)
			throw new OWLInvalidContentException("Error: attribute annotation not found for property \""
					+ property.getIRI().getFragment() + "\"");

		return getColumnString(parentExpression, dbAttribute);
	}

	/**
	 * Describes in SQL terms how two classes are connected through an object
	 * property. Super and equivalent properties are also included in the
	 * description.
	 * 
	 * @param subjectClass
	 *            the object class for this property
	 * @param property
	 *            the property
	 * @param objectClass
	 *            the subject class of this property
	 * @param negate
	 *            set to true if the term should be negated
	 * @return the description of this property
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	protected String describeObjectPropertyConnection(OWLClassExpression subjectClass, OWLObjectProperty property,
			OWLClassExpression objectClass, boolean negate) throws UnsupportedExpressionException,
			OWLInvalidContentException {

		Vector<String> expressions = new Vector<String>();

		// get the description of the equivalent properties (should include the
		// original property)
		Set<OWLObjectPropertyExpression> equivalentProperties = contentManager.getReasoner()
				.getEquivalentObjectProperties(property).getEntities();
		for (OWLObjectPropertyExpression equivalentProperty : equivalentProperties)
			getObjectPropertyDescriptionDirect(subjectClass, equivalentProperty.asOWLObjectProperty(), objectClass,
					negate, expressions);

		// get the description of the super properties
		Set<OWLObjectPropertyExpression> superProperties = contentManager.getReasoner()
				.getSuperObjectProperties(property, false).getFlattened();
		for (OWLObjectPropertyExpression superProperty : superProperties)
			if (!superProperty.isTopEntity())
				getObjectPropertyDescriptionDirect(subjectClass, superProperty.asOWLObjectProperty(), objectClass,
						negate, expressions);

		// if there are no expressions, the property was not properly defined
		if (expressions.size() == 0)
			throw new OWLInvalidContentException("The property was not defined through annotations or SWRL rules",
					property);

		// connect descriptions and return them
		return SqlQueryHelper.connectStrings(expressions, " AND ");
	}

	/**
	 * Adds the property description for a specific object property to a list of
	 * expressions. Super and equivalent properties are ignored.
	 * 
	 * @param subjectClass
	 *            the object class for this property
	 * @param property
	 *            the property
	 * @param objectClass
	 *            the subject class of this property
	 * @param negate
	 *            set to true if the term should be negated
	 * @param expressions
	 *            the list of expressions
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	private void getObjectPropertyDescriptionDirect(OWLClassExpression subjectClass, OWLObjectProperty property,
			OWLClassExpression objectClass, boolean negate, Vector<String> expressions)
			throws UnsupportedExpressionException, OWLInvalidContentException {

		// check if this property is part of a swrl rule consequent
		for (OWLOntology ontology : contentManager.getOntologies())
			for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE, true)) {
				SWRLIArgument objectArgument = null;
				SWRLIArgument subjectArgument = null;
				for (SWRLAtom headAtom : rule.getHead()) {
					if (headAtom instanceof SWRLObjectPropertyAtom) {
						SWRLObjectPropertyAtom propertyAtom = (SWRLObjectPropertyAtom) headAtom;
						if (property == propertyAtom.getPredicate().getNamedProperty()) {
							objectArgument = propertyAtom.getFirstArgument();
							subjectArgument = propertyAtom.getSecondArgument();
						}
					}
				}
				if (objectArgument != null) {
					SWRLRuleExpression ruleExpression = new SWRLRuleExpression(this, rule, objectArgument,
							subjectArgument, subjectClass, objectClass, contentManager);
					expressions.add(ruleExpression.evaluate(negate));
				}
			}

		// try to find property persistance information
		PropertyPersistenceInfo ppi = contentManager.getPropertyPersistenceInfo(property);
		if (ppi != null) {
			String comparator = SqlQueryHelper.getComparator(negate);

			if (contentManager.isPropertyFunctional(property)) {

				// if the property is functional, write
				// "subject.ForeignKey = object.IDColumn"
				expressions.add(getColumnString(subjectClass, ppi.getDbForeignKey()) + " " + comparator + " "
						+ getIDColumnString(objectClass));
			} else {

				// if the property is not functional, write
				// "subject.IDColumn = object.ForeignKeyColumn"
				expressions.add(getIDColumnString(subjectClass) + " " + comparator + " "
						+ getColumnString(objectClass, ppi.getDbForeignKeyInverse()));
			}
		}
	}
}
