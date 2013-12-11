package org.adiusframework.processmanager;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * The SpringJmsRoutingServiceExecutor is used to execute service-processes by
 * sending a representative message over JMS to the service.
 */
public class SpringJmsRoutingServiceExecutor implements ServiceExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringJmsRoutingServiceExecutor.class);

	/**
	 * The JmsTemplate is used to send messages to the services which should be
	 * executed.
	 */
	private JmsTemplate jmsTemplate;

	/**
	 * The services should answer to this Destination when the finished their
	 * work.
	 */
	private Destination replyTo;

	/**
	 * Return the JmsTemplate which is currently used by the
	 * SpringJmsRoutingServiceExecutor.
	 * 
	 * @return The current JmsTemplate.
	 */
	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	/**
	 * Sets a new JmsTemplate.
	 * 
	 * @param jmsTemplate
	 *            The new JmsTemplate.
	 */
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	/**
	 * Return the Destination which is currently used by the
	 * SpringJmsRoutingServiceExecutor.
	 * 
	 * @return The current Destination.
	 */
	public Destination getReplyTo() {
		return replyTo;
	}

	/**
	 * Sets a new Destination.
	 * 
	 * @param replyTo
	 *            The new Destination.
	 */
	public void setReplyTo(Destination replyTo) {
		this.replyTo = replyTo;
	}

	@Override
	public void execute(final String correlationId, final ServiceRegistration sr, final ServiceInput input) {

		// lets just build the message using spring integration message builder
		LOGGER.debug("Creating new JMS message:");
		LOGGER.debug("   ReplyTo: " + getReplyTo().toString());
		LOGGER.debug("   Correlation id: " + correlationId);
		jmsTemplate.send(sr.getProviderDefinition().getBinding().getData().getJmsData().getQueue(),
				new MessageCreator() {

					@Override
					public Message createMessage(Session session) throws JMSException {
						ObjectMessage msg = session.createObjectMessage();
						msg.setJMSCorrelationID(correlationId);
						msg.setJMSReplyTo(getReplyTo());
						msg.setStringProperty("route", sr.getServiceDefinition().getBinding().getRoute());
						msg.setObject(input);
						return msg;
					}
				});
		LOGGER.debug("Message successfully send...");
	}

}
