package org.adiusframework.util.console;

import java.util.Scanner;

import org.adiusframework.util.SystemUtil;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplicationConsole implements Runnable {

	private ClassPathXmlApplicationContext context;

	private String[] baseConfig;

	public SpringApplicationConsole() {
		this.baseConfig = null;
	}

	public SpringApplicationConsole(String[] baseConfig) {
		this.baseConfig = baseConfig;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(ClassPathXmlApplicationContext context) {
		this.context = context;
	}

	/**
	 * @return the context
	 */
	public ClassPathXmlApplicationContext getContext() {
		return this.context;
	}

	protected void start() {
		// method stub can be overwritten if needed
	}

	@Override
	public void run() {
		start();

		System.out.println(SystemUtil.getSystemDescription());
		System.out.println("Console started successfully, type 'exit' to close application");
		String command = "";
		Scanner in = new Scanner(System.in);
		boolean close = false;
		while (!close) {
			command = in.nextLine();
			close = command.equals("exit");
			if (!close) {
				System.out.println("Parsing command " + command);
				parseCommand(command);
			}
		}
		in.close();

		// close the application context
		stop();
		System.out.println("Console closed");
	}

	protected void stop() {
		this.context.close();
	}

	/**
	 * Method that can be overwritten to handle application specific commands
	 * 
	 * @param command
	 *            that has been entered
	 */
	protected void parseCommand(String command) {
		// nothing to do has to be overwritten if something has to be done
	}

	public void init(String[] runtimeConfig) {

		// start the application context set by the arguments of the method
		String[] config = null;
		int baseConfigLength;
		if (baseConfig == null) {
			config = new String[runtimeConfig.length];
			baseConfigLength = 0;
		} else {
			config = new String[baseConfig.length + runtimeConfig.length];
			baseConfigLength = baseConfig.length;
			System.arraycopy(baseConfig, 0, config, 0, baseConfigLength);
		}
		System.arraycopy(runtimeConfig, 0, config, baseConfigLength, runtimeConfig.length);
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(config);
		setContext(context);
		new Thread(this).start();
		context.registerShutdownHook();
	}

}
