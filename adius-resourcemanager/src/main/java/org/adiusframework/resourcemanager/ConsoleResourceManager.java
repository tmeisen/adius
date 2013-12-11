package org.adiusframework.resourcemanager;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.console.SpringApplicationConsole;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The ConsoleResourceManager basically initializes it's super-class
 * SpringApplicationConsole with the base-configuration for the ResourceManager.
 */
public class ConsoleResourceManager extends SpringApplicationConsole {

	/**
	 * The base-configuration-files.
	 */
	private static final String[] BASE_CONFIG = { "classpath:adius-resourcemanager-core-context.xml",
			"classpath:adius-resourcemanager-message-context.xml" };

	/**
	 * The instance of the ResourceManager which is started by this
	 * SpringApplicationConsole.
	 */
	private ResourceManager resourceManager;

	/**
	 * Creates a new SpringApplicationConsole with the defined (default)
	 * base-configuration, running a ResourceManager.
	 */
	public ConsoleResourceManager() {
		super(BASE_CONFIG);
	}

	@Override
	public void parseCommand(String command) {
		String trimmedCommand = command;
		List<String> args = new Vector<String>();
		if (command.contains(" ")) {
			args = Arrays.asList(command.split(" "));
			trimmedCommand = args.get(0);
		}

		if ("search".equals(trimmedCommand)) {
			if (args.size() > 4) {
				ResourceRequirement rr = new ResourceRequirement();
				rr.setCapabilityRule(args.get(4));
				rr.setProtocols(args.get(3));
				rr.setTypes(args.get(2));
				StandardResourceQuery query = new StandardResourceQuery(rr, new SystemData());
				query.addQueryDomainData("category", "integration");
				resourceManager.getResource(args.get(1), query);
			} else
				System.out.println("search <uri> <type> <protocol> <capabilityrule>");
		}
	}

	@Override
	public void init(String[] runtimeConfig) {
		super.init(runtimeConfig);
		resourceManager = getContext().getBean(ResourceManager.class);
	}

	/**
	 * Creates a new ConsoleResourceManager and starts it with the given
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
		new ConsoleResourceManager().init(args);
	}
}
