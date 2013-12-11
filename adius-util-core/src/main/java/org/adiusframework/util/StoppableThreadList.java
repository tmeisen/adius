package org.adiusframework.util;

/**
 * The StoppableThreadList interface defines functions to start and stop all
 * Threads with are manages in a special List.
 */
public interface StoppableThreadList {

	/**
	 * Start all Threads in the List.
	 */
	public abstract void startList();

	/**
	 * Stops all Threads in the List.
	 */
	public abstract void stopList();

}