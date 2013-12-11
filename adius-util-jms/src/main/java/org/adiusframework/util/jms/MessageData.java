package org.adiusframework.util.jms;

/**
 * Stores basic data which are connected to a specific message.
 */
public class MessageData {

	/** An identifier for the request. */
	private String correlationId;

	/** The name of the destination where the message comes from. */
	private String replyTo;

	/**
	 * Creates a new MessageData object with the given parameters.
	 * 
	 * @param correlationId
	 *            The identifier for the request.
	 * @param replyTo
	 *            The name of the destination where the message comes from.
	 */
	public MessageData(String correlationId, String replyTo) {
		setCorrelationId(correlationId);
		setReplyTo(replyTo);
	}

	/**
	 * Returns the identifier for the request.
	 * 
	 * @return The identifier.
	 */
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

	/**
	 * Returns the name of the destination where the message comes from.
	 * 
	 * @return The name of the destination.
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * Sets a new name for the destination where the message comes from.
	 * 
	 * @param replyTo
	 *            The new name.
	 */
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public String toString() {
		return getCorrelationId() + " " + getReplyTo();
	}

}
