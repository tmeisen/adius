package org.adiusframework.processmanager;

/**
 * The StandardQueryStatusEvent implements members to save the data which should
 * be returned by the methods which are defined in the QueryStatusEvent
 * interface.
 */
public class StandardQueryStatusEvent implements QueryStatusEvent {

	/**
	 * Stores the internal identifier of the related Query.
	 */
	private String externalId;

	/**
	 * Stores the external identifier of the related Query.
	 */
	private String internalId;

	/**
	 * Stores the attachment of the event.
	 */
	private String attachment;

	/**
	 * Stores the (new) QueryStatus of the related Query.
	 */
	private QueryStatus status;

	/**
	 * Create a new StandardQueryStatusEvent without an attachment.
	 * 
	 * @param internalId
	 *            The internal identifier of the related Query.
	 * @param externalId
	 *            The external identifier of the related Query.
	 * @param status
	 *            The (new) QueryStatus of the related Query.
	 */
	public StandardQueryStatusEvent(String internalId, String externalId, QueryStatus status) {
		setInternalId(internalId);
		setExternalId(externalId);
		setAttachment(null);
		setStatus(status);
	}

	/**
	 * Create a new StandardQueryStatusEvent with the given attachment.
	 * 
	 * @param internalId
	 *            The internal identifier of the related Query.
	 * @param externalId
	 *            The external identifier of the related Query.
	 * @param attachment
	 *            The given attachment.
	 * @param status
	 *            The (new) QueryStatus of the related Query.
	 */
	public StandardQueryStatusEvent(String internalId, String externalId, String attachment, QueryStatus status) {
		setInternalId(internalId);
		setExternalId(externalId);
		setAttachment(attachment);
		setStatus(status);
	}

	@Override
	public String getExternalId() {
		return externalId;
	}

	/**
	 * Sets a new external identifier.
	 * 
	 * @param externalId
	 *            The new external identifier.
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public QueryStatus getStatus() {
		return status;
	}

	/**
	 * Sets a new QueryStatus.
	 * 
	 * @param status
	 *            The new QueryStatus.
	 */
	public void setStatus(QueryStatus status) {
		this.status = status;
	}

	@Override
	public String getInternalId() {
		return internalId;
	}

	/**
	 * Sets a new internal identifier.
	 * 
	 * @param internalId
	 *            The new internal identifier.
	 */
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	@Override
	public String getAttachment() {
		return attachment;
	}

	/**
	 * Sets a new attachment.
	 * 
	 * @param attachment
	 *            The new attachment.
	 */
	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
