package org.adiusframework.util;

/**
 * The StoppableThread is a general interface for a Thread which can be stopped
 * correctly.
 * 
 * @see AbstractStoppableThread
 */
public interface StoppableThread {

	/**
	 * Marks the Thread to be stopped.
	 */
	public abstract void stopThread();

	/**
	 * Starts the Thread.
	 */
	public abstract void start();

	/**
	 * Marks the Thread as a Daemon- or a User-Thread.
	 * 
	 * @param value
	 *            True if it should be a Daemon-, false otherwise User-Thread.
	 * @see Thread#setDaemon(boolean)
	 */
	public abstract void setDaemon(boolean value);

}