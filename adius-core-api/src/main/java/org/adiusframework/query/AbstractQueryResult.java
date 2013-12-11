package org.adiusframework.query;

public abstract class AbstractQueryResult implements QueryResult {

	private static final long serialVersionUID = -1628455930514833889L;

	private String queryId;

	@Override
	public String getQueryId() {
		return queryId;
	}

	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
}
