package org.adiusframework.util.jms;

import org.adiusframework.util.net.RemoteCallback;

/**
 * The JmsCallback class stores the information which are necessary to execute a
 * RemoteCallback with jms-data.
 */
public class JmsCallback implements RemoteCallback {

	/**
	 * UID for serialization.
	 */
	private static final long serialVersionUID = -4587772514288343173L;

	/** Indicates that the destination of the jms-packet is a topic. */
	public static final String DESTINATION_TYPE_TOPIC = "topic";

	/** Indicates that the destination of the jms-packet is a queue. */
	public static final String DESTINATION_TYPE_QUEUE = "queue";

	/** Stores the url for the connection. */
	private String connectionUrl;

	/** Stores the name of the destination. */
	private String destinationName;

	/**
	 * Stores the type of the destination.
	 * 
	 * @see #DESTINATION_TYPE_QUEUE
	 * @see #DESTINATION_TYPE_TOPIC
	 */
	private String destinationType;

	/** Stores the id which identifies the responded request. */
	private String correlationId;

	/**
	 * Returns the url for the connection.
	 * 
	 * @return The connection-url.
	 */
	public String getConnectionUrl() {
		return connectionUrl;
	}

	/**
	 * Sets a new url for the connection.
	 * 
	 * @param connectionUrl
	 *            The new connection-url.
	 */
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	/**
	 * Returns the name of the destination.
	 * 
	 * @return The destination-name.
	 */
	public String getDestinationName() {
		return destinationName;
	}

	/**
	 * Sets a new name of the destination.
	 * 
	 * @param destinationName
	 *            The new destination-name.
	 */
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	/**
	 * Returns the type of the destination.
	 * 
	 * @return The destination-type.
	 */
	public String getDestinationType() {
		return destinationType;
	}

	/**
	 * Sets a new type of the destination.
	 * 
	 * @param destinationType
	 *            The new destination-type.
	 * @see #DESTINATION_TYPE_QUEUE
	 * @see #DESTINATION_TYPE_TOPIC
	 */
	public void setDestinationType(String destinationType) {
		this.destinationType = destinationType;
	}

	/**
	 * Return the identifier of the responded request.
	 * 
	 * @return The identifier.
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Sets a new identifier of the responded request.
	 * 
	 * @param correlationId
	 *            The new identifier.
	 */
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

}
