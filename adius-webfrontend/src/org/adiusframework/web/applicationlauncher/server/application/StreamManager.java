package org.adiusframework.web.applicationlauncher.server.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will handle all the buffers, input and output streams of an
 * application process.
 * 
 * @author Tobias Meisen
 */
public class StreamManager extends Thread {
	static final Logger LOGGER = LoggerFactory.getLogger(StreamManager.class);

	/** Determines if the stream manager is closed. */
	private volatile boolean closed;

	/** It contains all the positions with their input streams associated. */
	private Map<Integer, InputStream> inputStreams;

	/** It contains all the positions with their output streams associated. */
	private Map<Integer, OutputStream> outputStreams;

	/** This collection have all the buffer reader objects. */
	private Map<Integer, BufferedReaderThread> buffered;

	/**
	 * Default constructor, it will initialize all the collections.
	 */
	public StreamManager() {
		this.inputStreams = new HashMap<Integer, InputStream>();
		this.outputStreams = new HashMap<Integer, OutputStream>();
		this.buffered = new HashMap<Integer, BufferedReaderThread>();
	} // end : constructor

	// PUBLIC METHODS
	// _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * It will check if there is a next piece of data using the specified
	 * channel in the buffer.
	 * 
	 * @param channel
	 *            Specified channel to do the checking in the proper buffer
	 *            reader thread.
	 * @return True, if there exists more data in the buffer, false otherwise.
	 */
	public boolean hasNext(int channel) {
		final BufferedReaderThread brt = buffered.get(channel);
		if (brt == null) {
			return false;
		}
		return brt.hasNext();
	} // end : hasNext method

	/**
	 * It will obtain the next piece of data using the specified channel in the
	 * buffer.
	 * 
	 * @param channel
	 *            Specified channel to request the data form the proper buffer
	 *            reader thread.
	 * @return Null, if there is not more information, or, the next data
	 *         contained in the buffer.
	 */
	public String getNext(int channel) {
		if (hasNext(channel)) {
			return buffered.get(channel).next();
		}
		return null;
	} // end : getNext method

	/**
	 * The assignment to the input stream collection will be done by this
	 * method, it will use a number as a key, related with the stream content.
	 * 
	 * @param iStream
	 *            Input stream to be registered in the stream manager.
	 * @param number
	 *            Index for store the stream in the correct collection position.
	 */
	public void register(InputStream iStream, int number) {
		this.inputStreams.put(number, iStream);
	} // end : register method

	public Map<Integer, InputStream> getInputStreams() {
		return this.inputStreams;
	}

	/**
	 * The assignment to the output stream collection will be done by this
	 * method, it will use a number as a key, related with the stream content.
	 * 
	 * @param oStream
	 *            Output stream to be registered in the stream manager.
	 * @param number
	 *            Index for store the stream in the correct collection position.
	 */
	public void register(OutputStream oStream, int number) {
		this.outputStreams.put(number, oStream);
	} // end : register method

	/**
	 * Useful for writing some data, to a specific position in the output
	 * stream.
	 * 
	 * @param data
	 *            Selected data to be stored in the stream.
	 * @param number
	 *            Index to store in the correct collection position.
	 */
	public synchronized void writeToStream(String data, int number) {
		if (outputStreams.containsKey(number)) {
			OutputStream os = outputStreams.get(number);
			PrintWriter pw = new PrintWriter(new PrintStream(os));
			pw.print(data);
			pw.close();
		}
	} // end : writeToStream method

	/** It will close the stream manager for further operations. */
	public void close() {
		this.closed = true;
	} // end : close method

	/**
	 * Process executed in a new thread, where components in the buffered
	 * collection will be started as a daemon threads, and, after the stream
	 * manager closes it will close the buffered reader threads.
	 */
	@Override
	public void run() {

		// Clean and prepare the inputStreams
		this.buffered.clear();
		Set<Map.Entry<Integer, InputStream>> entries = this.inputStreams.entrySet();

		// Validation for the elements in the input stream
		if ((entries == null) || (entries.isEmpty())) {
			System.out.println("Entries in the input stream map are null/empty");
			return;
		}

		// Iterating an starting a BufferedReaderThread for each entry
		for (Entry<Integer, InputStream> entry : entries) {

			BufferedReaderThread brt = new BufferedReaderThread(entry.getKey(), new BufferedReader(
					new InputStreamReader(entry.getValue())));

			this.buffered.put(entry.getKey(), brt);
			brt.setDaemon(true);
			brt.start();
		}

		// Execute
		while (!this.closed) {
			try {
				Thread.sleep(500/* mls */);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		// Clean up and finish
		if (this.closed) {
			for (BufferedReaderThread bufReader : this.buffered.values()) {
				bufReader.terminate();
			}
		}
	} // end : run method

	// ************************************** NESTED CLASS
	// ************************************** \\

	/**
	 * According to a buffer it will continue reading the next line, handling
	 * buffer overflows and the locks for the multithread enviroment.
	 * 
	 * @author Tobias Meisen
	 */
	private class BufferedReaderThread extends Thread {

		/** Number that has been assigned to this thread for identification */
		private int number;

		/** Flag useful for indicating the thread termination. */
		private volatile boolean close;

		/** The buffered reader used to read the stream */
		private BufferedReader reader;

		/** The buffer used by this thread to store read data */
		private LinkedList<String> buffer;

		/** Lock to make this class multi-threading compatible */
		final Lock lock = new ReentrantLock();

		/** Maximum constant buffer size. */
		private static final int MAX_BUFFER_SIZE = 100;

		/**
		 * Constructor to initialize the number and the reader buffer.
		 * 
		 * @param number
		 *            number used to identify the reader thread
		 * @param reader
		 *            the reader used to access the underlying data stream
		 */
		public BufferedReaderThread(int number, BufferedReader reader) {
			this.number = number;
			this.reader = reader;
			this.buffer = new LinkedList<String>();
		} // end : constructor

		/**
		 * Starting the buffering and monitoring of the data that is passed by
		 * the reader.
		 */
		@Override
		public void run() {

			String line = null; // Container for the next line in the buffer

			try {
				while (close || ((line = this.reader.readLine()) != null)) {

					this.lock.lock();
					this.buffer.addLast(line);

					// Cleanup the buffer
					if (this.buffer.size() > MAX_BUFFER_SIZE) {
						int elementToRemove = this.buffer.size() - MAX_BUFFER_SIZE;
						for (int i = 0; i < elementToRemove; i++) {

							// lets make sure that there is an element (should
							// always be the case, because we have the lock)
							if (hasNext()) {
								buffer.pollLast();
							}
						} // end : for

					} // end : if

					this.lock.unlock();
				} // end : while

				this.reader.close();

			} catch (IOException ioe) {
				LOGGER.error("Reading of line failed in channel: " + number);
				this.lock.unlock();
			} catch (Exception e) {
				LOGGER.error("Exception occured in stream manager during data acquisition in channel: " + number);
				this.lock.unlock();
			}
		} // end : run method

		/**
		 * Checks whether there is more string data available within the queue
		 * buffer.
		 * 
		 * @return true if more strings are available otherwise false
		 */
		public boolean hasNext() {

			this.lock.lock();
			try {
				return !this.buffer.isEmpty();
			} finally {
				this.lock.unlock();
			}
		} // end : hasNext method

		/**
		 * If there is more data within the buffer this data is returned, if no
		 * data is available null is returned.
		 * 
		 * @return the next string data within the queue
		 */
		public String next() {

			this.lock.lock();

			try {
				if (hasNext()) {
					return this.buffer.pollFirst();
				}
				return null;
			} finally {
				this.lock.unlock();
			}
		} // end : next method

		/**
		 * Informs the thread about the termination and to stop the monitoring
		 * and buffering of the reader. When this method is called the run
		 * method can do further work till the shutdown is handled.
		 */
		public void terminate() {

			this.lock.lock();

			try {
				this.buffer.clear();
				this.close = true;
			} finally {
				this.lock.unlock();
			}
		} // end : close method

	} // END : BufferedReaderThread class

} // END : StreamManager Class