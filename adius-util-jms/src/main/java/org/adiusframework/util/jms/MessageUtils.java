package org.adiusframework.util.jms;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * The MessageUtils class provides several method to simplify the sending and
 * receiving of messages with jms.
 */
public class MessageUtils {

	/** The time how long the program waits for a message. */
	private static final long MESSAGE_TIMEOUT = 250;

	/**
	 * Removes all messages from a given destination that can be received with a
	 * given JmsTemplate.
	 * 
	 * @param destination
	 *            The name of the destination to clean up.
	 * @param jmsTemplate
	 *            The JmsTemplate to receive the messages.
	 */
	public static void cleanUp(String destination, JmsTemplate jmsTemplate) {
		MessageUtils.cleanUp(new ActiveMQQueue(destination), jmsTemplate);
	}

	/**
	 * Removes all messages from a given Destination that can be received with a
	 * given JmsTemplate.
	 * 
	 * @param destination
	 *            The Destination to clean up.
	 * @param jmsTemplate
	 *            The JmsTemplate to receive the messages.
	 */
	public static void cleanUp(Destination destination, JmsTemplate jmsTemplate) {

		// cleaning up the message result queue
		Message message = null;
		do {
			message = MessageUtils.waitForMessage(jmsTemplate, destination, MESSAGE_TIMEOUT);
		} while (message != null);
	}

	/**
	 * Receives a message with the given JmsTemplate from a the given
	 * destination. It can be specified how long the timeout for receiving
	 * should be.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to receive the message.
	 * @param destination
	 *            The name of the destination to listen to.
	 * @param timeout
	 *            The timeout or null if no timeout is wanted.
	 * @return The received message, or null if no message could be received.
	 */
	public static ObjectMessage waitForMessage(JmsTemplate jmsTemplate, String destination, Long timeout) {
		return waitForMessage(jmsTemplate, new ActiveMQQueue(destination), timeout);
	}

	/**
	 * Receives a message with the given JmsTemplate from a the given
	 * Destination. It can be specified how long the timeout for receiving
	 * should be.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to receive the message.
	 * @param destination
	 *            The Destination to listen to.
	 * @param timeout
	 *            The timeout or null if no timeout is wanted.
	 * @return The received message, or null if no message could be received.
	 */
	public static ObjectMessage waitForMessage(JmsTemplate jmsTemplate, Destination destination, Long timeout) {
		if (timeout != null)
			jmsTemplate.setReceiveTimeout(timeout);
		Message message = jmsTemplate.receive(destination);
		if (message == null)
			return null;
		jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_INDEFINITE_WAIT);
		return (ObjectMessage) message;
	}

	/**
	 * Sends a object-message with the given parameters using the given
	 * JmsTemplate.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to send the message.
	 * @param destination
	 *            The name of the destination for the message.
	 * @param corellationId
	 *            The identifier for the responded request.
	 * @param replyTo
	 *            The name of the destination to which the recipient should
	 *            answer.
	 * @param object
	 *            The object to be send.
	 */
	public static void sendObjectMessage(JmsTemplate jmsTemplate, String destination, final String corellationId,
			final String replyTo, final Serializable object) {
		sendObjectMessage(jmsTemplate, new ActiveMQQueue(destination), corellationId, new ActiveMQQueue(replyTo),
				object);
	}

	/**
	 * Sends a object-message with the given parameters using the given
	 * JmsTemplate. Moreover it can be specified that additional header-data are
	 * attached to this message.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to send the message.
	 * @param destination
	 *            The name of the destination for the message.
	 * @param corellationId
	 *            The identifier for the responded request.
	 * @param replyTo
	 *            The name of the destination to which the recipient should
	 *            answer.
	 * @param object
	 *            The object to be send.
	 * @param header
	 *            A Map containing the additional header-data as a set of key
	 *            and value pairs.
	 */
	public static void sendObjectMessage(JmsTemplate jmsTemplate, String destination, final String corellationId,
			final String replyTo, final Serializable object, final Map<String, Object> header) {
		sendObjectMessage(jmsTemplate, new ActiveMQQueue(destination), corellationId, new ActiveMQQueue(replyTo),
				object, header);
	}

	/**
	 * Sends a object-message with the given parameters using the given
	 * JmsTemplate. Moreover it can be specified that additional header-data are
	 * attached to this message.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to send the message.
	 * @param destination
	 *            The name of the destination for the message.
	 * @param corellationId
	 *            The identifier for the responded request.
	 * @param replyTo
	 *            The Destination to which the recipient should answer.
	 * @param object
	 *            The object to be send.
	 * @param header
	 *            A Map containing the additional header-data as a set of key
	 *            and value pairs.
	 */
	public static void sendObjectMessage(JmsTemplate jmsTemplate, String destination, final String corellationId,
			final Destination replyTo, final Serializable object, final Map<String, Object> header) {
		sendObjectMessage(jmsTemplate, new ActiveMQQueue(destination), corellationId, replyTo, object, header);
	}

	/**
	 * Sends a object-message with the given parameters using the given
	 * JmsTemplate.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to send the message.
	 * @param destination
	 *            The Destination for the message.
	 * @param corellationId
	 *            The identifier for the responded request.
	 * @param replyTo
	 *            The name of the destination to which the recipient should
	 *            answer.
	 * @param object
	 *            The object to be send.
	 */
	public static void sendObjectMessage(JmsTemplate jmsTemplate, Destination destination, final String corellationId,
			final Destination replyTo, final Serializable object) {

		jmsTemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(object);
				message.setJMSCorrelationID(corellationId);
				message.setJMSReplyTo(replyTo);
				return message;
			}
		});
	}

	/**
	 * Sends a object-message with the given parameters using the given
	 * JmsTemplate. Moreover it can be specified that additional header-data are
	 * attached to this message.
	 * 
	 * @param jmsTemplate
	 *            The JmsTemplate to send the message.
	 * @param destination
	 *            The Destination for the message.
	 * @param corellationId
	 *            The identifier for the responded request.
	 * @param replyTo
	 *            The Destination to which the recipient should answer.
	 * @param object
	 *            The object to be send.
	 * @param header
	 *            A Map containing the additional header-data as a set of key
	 *            and value pairs.
	 */
	public static void sendObjectMessage(JmsTemplate jmsTemplate, Destination destination, final String corellationId,
			final Destination replyTo, final Serializable object, final Map<String, Object> header) {

		jmsTemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage(object);
				message.setJMSCorrelationID(corellationId);
				message.setJMSReplyTo(replyTo);
				for (Entry<String, Object> e : header.entrySet())
					message.setObjectProperty(e.getKey(), e.getValue());
				return message;
			}
		});
	}
}
