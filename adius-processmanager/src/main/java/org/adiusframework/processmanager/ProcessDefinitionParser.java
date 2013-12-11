package org.adiusframework.processmanager;

import java.io.File;
import java.io.InputStream;

import org.adiusframework.processmanager.xml.ProcessDefinition;

/**
 * The ProcessDefinitionParser defines an interface for creating a
 * ProcessDefinition out of several data-sources.
 */
public interface ProcessDefinitionParser {

	/**
	 * Creates a ProcessDefinition out of an InputStream.
	 * 
	 * @param stream
	 *            The data-source as an InputStream
	 * @return The ProcessDefinition
	 */
	public abstract ProcessDefinition parse(InputStream stream);

	/**
	 * Creates a ProcessDefinition out of a String.
	 * 
	 * @param string
	 *            The data-source as a String
	 * @return The ProcessDefinition
	 */
	public abstract ProcessDefinition parse(String string);

	/**
	 * Creates a ProcessDefinition out of the content of a File.
	 * 
	 * @param file
	 *            The data-source
	 * @return The ProcessDefinition
	 */
	public abstract ProcessDefinition parse(File file);

}