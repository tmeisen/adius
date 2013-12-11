package org.adiusframework.processmanager.exception;

import org.adiusframework.query.Query;

/**
 * The IsQueryRelated interface is used to show that something is related to a
 * special Query. Moreover it is possible to access this special Query.
 */
public interface IsQueryRelated {

	/**
	 * Return the Query that relates to this object.
	 * 
	 * @return The related Query.
	 */
	public Query getQuery();

}
