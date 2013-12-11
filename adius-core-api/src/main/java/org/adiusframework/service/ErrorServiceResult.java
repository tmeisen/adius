package org.adiusframework.service;

public class ErrorServiceResult implements ServiceResult {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -8542542966582347371L;

	private String errorMessage;

	public ErrorServiceResult(String errorMessage) {
		setErrorMessage(errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	private void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
