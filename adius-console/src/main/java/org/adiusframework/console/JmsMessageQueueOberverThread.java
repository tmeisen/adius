package org.adiusframework.console;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.adiusframework.query.QueryResult;
import org.adiusframework.util.AbstractStoppableThread;
import org.adiusframework.util.jms.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;

public class JmsMessageQueueOberverThread extends AbstractStoppableThread {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessageQueueOberverThread.class);

	private static final long TIMEOUT = 1000L;

	private JmsTemplate jmsTemplate;

	private Destination destination;

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	@Override
	protected void runInLoop() {
		Message msg = MessageUtils.waitForMessage(getJmsTemplate(), destination, TIMEOUT);
		if (msg != null) {
			try {
				LOGGER.debug("Message received");
				if (QueryResult.class.isInstance(((ObjectMessage) msg).getObject())) {
					LOGGER.debug("Query id: " + QueryResult.class.cast(((ObjectMessage) msg).getObject()).getQueryId()
							+ " " + ((ObjectMessage) msg).getObject().getClass());
				}

			} catch (JMSException e) {
				System.out.println("Error occured during accessing message data " + e.getMessage());
			}
		}
	}

}
