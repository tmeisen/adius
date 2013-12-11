package org.adiusframework.processmanager;

import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceResult;

/**
 * The BasicServiceResultData provide a basic implementation of the
 * ServiceResultData interface by just adding a member attribute for the
 * identifier and the related ServiceResult object.
 */
public class BasicServiceResultData implements ServiceResultData {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 4489326935924438789L;

	private String correlationId;

	private ServiceResult result;

	public BasicServiceResultData() {
	}

	public BasicServiceResultData(String correlationId, ServiceResult result) {
		setCorrelationId(correlationId);
		setResult(result);
	}

	@Override
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Sets a new identifier.
	 * 
	 * @param correlationId
	 *            The new identifier.
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	@Override
	public boolean failed() {
		return ErrorServiceResult.class.isInstance(getResult());
	}

	@Override
	public ServiceResult getResult() {
		return result;
	}

	/**
	 * Sets a new ServiceResult which should be related to this
	 * ServiceResultData.
	 * 
	 * @param result
	 *            The new ServiceResult.
	 */
	public void setResult(ServiceResult result) {
		this.result = result;
	}

}
