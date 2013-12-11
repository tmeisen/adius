package org.adiusframework.util;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BasicStoppableThreadList manages several StoppableThreads and provides
 * method to start or stop every managed Thread.
 * 
 * @see StoppableThread
 */
public class BasicStoppableThreadList implements StoppableThreadList {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicStoppableThreadList.class);

	/**
	 * A List object which stores all managed Threads.
	 */
	private List<StoppableThread> list;

	/**
	 * Return the internal List where the Threads are stored.
	 * 
	 * @return The List of StoppableThreads.
	 */
	public List<StoppableThread> getList() {
		return list;
	}

	/**
	 * Sets a new List, which stores the manages Threads.
	 * 
	 * @param list
	 *            The new List.
	 */
	public void setList(List<StoppableThread> list) {
		this.list = list;
	}

	@Override
	public void startList() {
		for (StoppableThread t : getList()) {
			t.setDaemon(true);
			LOGGER.debug("Starting " + t);
			t.start();
		}
	}

	@Override
	public void stopList() {
		for (StoppableThread t : getList()) {
			LOGGER.debug("Sending stop signal to " + t);
			t.stopThread();
		}
	}

}
