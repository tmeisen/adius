package org.adiusframework.serviceregistry;

import org.adiusframework.util.console.SpringApplicationConsole;

/**
 * The ConsoleServiceRegistry basically initializes it's super-class
 * SpringApplicationConsole with the base-configuration for the ServiceRegistry.
 */
public class ConsoleServiceRegistry extends SpringApplicationConsole {

	/**
	 * The base-configuration-files.
	 */
	private static final String[] BASE_CONFIG = { "classpath:adius-serviceregistry-core-context.xml",
			"classpath:adius-serviceregistry-message-context.xml" };

	/**
	 * Creates a new SpringApplicationConsole with the defined (default)
	 * base-configuration, running a ServiceRegistry.
	 */
	public ConsoleServiceRegistry() {
		super(BASE_CONFIG);
	}

	/**
	 * Creates a new ConsoelServiceRegistry and starts it with the given
	 * configuration.
	 * 
	 * @param args
	 *            The given configuration.
	 */
	public static void main(String[] args) {

		// check args
		if (args.length < 1) {
			System.err.println("Missing arguments: ConsoleServiceRegistry [context-configuration] expected");
			return;
		}
		new ConsoleServiceRegistry().init(args);
	}

}
