package org.adiusframework.console;

import org.adiusframework.util.StoppableThreadList;
import org.adiusframework.util.console.SpringApplicationConsole;

public class CommandConsole extends SpringApplicationConsole {

	private CommandParser commandParser;

	private StoppableThreadList threadList;

	public CommandConsole() {
		super(new String[] {});
	}

	public CommandParser getCommandParser() {
		return commandParser;
	}

	public void setCommandParser(CommandParser commandParser) {
		this.commandParser = commandParser;
	}

	public StoppableThreadList getThreadList() {
		return threadList;
	}

	public void setThreadList(StoppableThreadList threadList) {
		this.threadList = threadList;
	}

	@Override
	public void init(String[] runtimeConfig) {
		super.init(runtimeConfig);
		setCommandParser(getContext().getBean(CommandParser.class));
		setThreadList(getContext().getBean(StoppableThreadList.class));

		// lets start all threads running in parallel
		getThreadList().startList();
	}

	@Override
	public void stop() {
		System.out.println("Sending stop signal to daemon threads");
		getThreadList().stopList();
		super.stop();
	}

	@Override
	protected void parseCommand(String command) {
		if (!commandParser.parse(command)) {
			System.err.println("Execution of command " + command + " failed.");
		}
	}

	public static void main(String[] args) {
		new CommandConsole().init(args);
	}

}
