package org.adiusframework.processmanager.exception;

import org.adiusframework.query.Query;

/**
 * The QueryRelatedException is thrown when an exception occurs, that is related
 * to a special Query.
 */
public abstract class QueryRelatedException extends ProcessManagerException implements IsQueryRelated {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -4837346936640729279L;

	/**
	 * Stores the Query that is related to the exception.
	 */
	private Query query;

	/**
	 * Creates a new QueryRelatedException with a message that contains special
	 * information about this exception and the Query that is related to it.
	 * 
	 * @param message
	 *            The special information.
	 * @param query
	 *            The related Query.
	 */
	public QueryRelatedException(String message, Query query) {
		super(message);
		setQuery(query);
	}

	@Override
	public Query getQuery() {
		return query;
	}

	/**
	 * Sets a new Query that is related to this exception.
	 * 
	 * @param query
	 *            The new Query.
	 */
	public void setQuery(Query query) {
		this.query = query;
	}
}
