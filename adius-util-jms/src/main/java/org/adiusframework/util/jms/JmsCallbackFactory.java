package org.adiusframework.util.jms;

/**
 * The JmsCallbackFactory can be used to create JmsCallbacks.
 */
public class JmsCallbackFactory {

	/** The url of the broker which is the destination of the callback. */
	private String brokerUrl;

	/** The default name of the destination. */
	private String defaultDestinationName;

	/**
	 * Return the url of the broker which is the destination of the callback.
	 * 
	 * @return The broker-url.
	 */
	public String getBrokerUrl() {
		return brokerUrl;
	}

	/**
	 * Sets a new url for the broker.
	 * 
	 * @param brokerUrl
	 *            The new broker-url.
	 */
	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	/**
	 * Return the default name for the destination.
	 * 
	 * @return The default-destination.
	 */
	public String getDefaultDestinationName() {
		return defaultDestinationName;
	}

	/**
	 * Sets a new default name for the destination.
	 * 
	 * @param defaultDestinationName
	 *            The new default-destination.
	 */
	public void setDefaultDestinationName(String defaultDestinationName) {
		this.defaultDestinationName = defaultDestinationName;
	}

	/**
	 * Creates a new JmsCallback with the configured and given parameters.
	 * 
	 * @param correlationId
	 *            The identifier for the responded request.
	 * @param destinationName
	 *            The name of the destination or null if the default one should
	 *            be used.
	 * @return The new JmsCallback.
	 */
	public JmsCallback create(String correlationId, String destinationName) {
		String dn = destinationName;
		if (dn == null)
			dn = getDefaultDestinationName();

		// create the concret callback
		JmsCallback jmsCallback = new JmsCallback();
		jmsCallback.setConnectionUrl(getBrokerUrl());
		jmsCallback.setCorrelationId(correlationId);
		jmsCallback.setDestinationName(dn);
		jmsCallback.setDestinationType(JmsCallback.DESTINATION_TYPE_QUEUE);
		return jmsCallback;
	}

}
