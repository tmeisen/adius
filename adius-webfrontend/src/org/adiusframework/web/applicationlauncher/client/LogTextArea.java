package org.adiusframework.web.applicationlauncher.client;

import com.google.gwt.user.client.ui.TextArea; 

/**
 * UI custom component to show the output of a loaded application.
 * 
 * @author Tobias Meisen
 */
public class LogTextArea extends TextArea {
	
	/** The value is used to store the number of text lines kept in memory. */
	private int capacity;
	/** It stores the capacity of the log text area. */
	private int fillLevel;

	/** Number of text lines set by default. */
	public static final int DEFAULT_CAPACITY = 50;

	/**
	 * Default constructor, setting the default capacity.
	 */
	public LogTextArea() {
		this(DEFAULT_CAPACITY);
	} // end : constructor

	/**
	 * Constructor of the log text area with definable capacity.
	 * 
	 * @param capacity 
	 * 		  Number of text lines that are kept in memory.
	 */
	public LogTextArea(int capacity) {
		this.fillLevel = 0;
		this.capacity = capacity;		
	} // end : constructor
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Convenient method to add a new line to the text area. The method
	 * automatically adds a newline, before the text.
	 * 
	 * @param log 
	 *        Text that have to be added to the text area.
	 */
	public void addLog(final String log) {

		String prefix = "";
		
		final String text;
		final boolean isEmpty;		
		
		// Check the current fill level of the area
		if (fillLevel + 1 > capacity) {
			// Remove the oldest entry
			text = getText();
			prefix = text.substring(text.indexOf("\n") + 1);
		} else {
			prefix = getText();
			fillLevel++;
		}

		// Now add the text
		isEmpty = getText().isEmpty();
		
		if (!isEmpty) {
			prefix = prefix + "\n";
		}
		
		setText(prefix + log);
		setCursorPos(getText().length());
	} // end : addLog method 

}