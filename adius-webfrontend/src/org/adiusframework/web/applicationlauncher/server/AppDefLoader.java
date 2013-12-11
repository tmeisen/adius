package org.adiusframework.web.applicationlauncher.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

/**
 * The interface should be implemented when is required to load
 * configuration properties from the server, it would be received
 * as an input stream.
 * 
 * @author Tobias Meisen
 */
public interface AppDefLoader {

	/**
	 * Loads the application definitions from the specified configuration
	 * resource.
	 * 
	 * @param stream
	 *        The stream containing the configuration data.
	 * @return Map of loaded application definitions (name of the application is
	 *         used as key).
	 * @throws IOException
	 *         If the stream could not be accessed.
	 */
	public Map<String, ApplicationDefinition> load(InputStream stream) throws IOException;

}