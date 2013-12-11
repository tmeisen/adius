package org.adiusframework.util.jms.apachemq;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.adiusframework.util.jms.JmsCallback;
import org.adiusframework.util.net.Callback;
import org.adiusframework.util.net.CallbackExecutor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ActiveMqCallbackExecutor offers the possibility to execute a callback
 * which is accessed over a ActiveMQ broker and JMS-Data.
 */
public class ActiveMqCallbackExecutor implements CallbackExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActiveMqCallbackExecutor.class);

	@Override
	public boolean execute(Callback callback, Serializable data) {
		if (!JmsCallback.class.isInstance(callback))
			throw new UnsupportedOperationException("Callback of class " + callback.getClass() + " not supported");
		JmsCallback jmsCallback = JmsCallback.class.cast(callback);
		Connection connection = null;
		try {

			// lets create a connection factory and send the message
			ConnectionFactory cf = new ActiveMQConnectionFactory(jmsCallback.getConnectionUrl());
			connection = cf.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = null;
			if (jmsCallback.getDestinationType().equals(JmsCallback.DESTINATION_TYPE_TOPIC))
				destination = session.createTopic(jmsCallback.getDestinationName());
			else
				destination = session.createQueue(jmsCallback.getDestinationName());
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			ObjectMessage message = session.createObjectMessage(data);
			message.setJMSCorrelationID(jmsCallback.getCorrelationId());
			producer.send(message);
			return true;
		} catch (JMSException e) {
			LOGGER.error("Execution of jms callback failed, reason: " + e.getMessage());
			return false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (JMSException e) {
					LOGGER.error("Connection to broker could not be closed: " + e.getMessage());
				}
			}
		}
	}

}
