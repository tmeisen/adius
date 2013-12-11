package org.adiusframework.serviceprovider;

import org.adiusframework.util.console.SpringApplicationConsole;

public class ConsoleServiceProvider extends SpringApplicationConsole {

	private static final String[] BASE_CONFIG = { "classpath:adius-serviceprovider-core-context.xml",
			"classpath:adius-serviceprovider-message-context.xml" };

	public ConsoleServiceProvider() {
		super(BASE_CONFIG);
	}

	public static void main(String[] args) {

		// check args
		new ConsoleServiceProvider().init(args);
	}
}
