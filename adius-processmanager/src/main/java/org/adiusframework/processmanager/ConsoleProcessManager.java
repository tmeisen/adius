package org.adiusframework.processmanager;

import org.adiusframework.util.console.SpringApplicationConsole;

/**
 * The ConsoleProcessManager basically initializes it's super-class
 * SpringApplicationConsole with the base-configuration for the ProcessManager.
 */
public class ConsoleProcessManager extends SpringApplicationConsole {

	/**
	 * The (default) base-configuration-files.
	 */
	private static final String[] BASE_CONFIG = { "classpath:adius-processmanager-core-context.xml",
			"classpath:adius-processmanager-database-context.xml", "classpath:adius-processmanager-message-context.xml" };

	/**
	 * Creates a new SpringApplicationConsole with the defined (default)
	 * base-configuration, running a ProcessManager.
	 */
	public ConsoleProcessManager() {
		super(BASE_CONFIG);
	}

	/**
	 * Creates a new ConsoleProcessManager and starts it with the given
	 * configuration.
	 * 
	 * @param args
	 *            The given configuration.
	 */
	public static void main(String[] args) {

		// check args
		if (args.length < 1) {
			System.err.println("Missing arguments: ConsoleResourceManager [context-configuration] expected");
			return;
		}
		new ConsoleProcessManager().init(args);
	}

}