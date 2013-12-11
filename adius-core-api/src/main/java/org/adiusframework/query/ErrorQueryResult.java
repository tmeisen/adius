package org.adiusframework.query;

public class ErrorQueryResult extends AbstractQueryResult {

	private static final long serialVersionUID = -5108475829627886912L;

	private String message;

	public ErrorQueryResult() {
		setMessage("");
	}

	public ErrorQueryResult(String queryId, String message) {
		setQueryId(queryId);
		setMessage(message);
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
