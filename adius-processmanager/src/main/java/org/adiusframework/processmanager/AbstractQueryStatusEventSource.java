package org.adiusframework.processmanager;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends the QueryStatusEventSource with basic functionality. Therefore it
 * implements a simple List for managing registered QueryStatusListener and a
 * method for publishing an event to all registered listeners.
 * 
 * @see #fireEvent(QueryStatusEvent)
 */
public class AbstractQueryStatusEventSource implements QueryStatusEventSource {

	private List<QueryStatusListener> listeners;

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueryStatusEventSource.class);

	public AbstractQueryStatusEventSource() {
		setListeners(new Vector<QueryStatusListener>());
	}

	protected List<QueryStatusListener> getListeners() {
		return listeners;
	}

	protected void setListeners(List<QueryStatusListener> listeners) {
		this.listeners = listeners;
	}

	@Override
	public void registerListener(QueryStatusListener listener) {
		if (!getListeners().contains(listener))
			getListeners().add(listener);
	}

	@Override
	public void unregisterListener(QueryStatusListener listener) {
		getListeners().remove(listener);
	}

	/**
	 * Publishes an event to all registered listeners.
	 * 
	 * @param event
	 *            The event which should be published
	 */
	public void fireEvent(QueryStatusEvent event) {
		LOGGER.debug("Publishing query status event " + event.getStatus() + " of " + event.getInternalId());
		for (QueryStatusListener listener : getListeners()) {
			LOGGER.debug("Publishing to " + listener);
			listener.queryStatusChanged(event);
		}
		LOGGER.debug("Event successfully fired...");
	}

}
