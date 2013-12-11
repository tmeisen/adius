package org.adiusframework.web.applicationlauncher.server;

import java.util.Scanner;

public class TestApplication implements Runnable {

	private boolean stopped;

	public TestApplication() {
		stopped = true;
	}

	@Override
	public void run() {
		System.out.println("Console started successfully, type 'exit' to close application");
		String command = "";
		Scanner in = new Scanner(System.in);
		boolean close = false;
		while (!close) {
			command = in.nextLine();
			close = command.equals("exit");
		}
		in.close();
		stopped = true;

		// close the application context
		System.out.println("Console closed");
	}

	public void start() {
		stopped = false;
		new Thread(this).start();
	}

	public boolean isStopped() {
		return stopped;
	}

	public static void main(String[] args) {
		TestApplication testApp = new TestApplication();
		testApp.start();
		System.out.println("Test application started");
		long counter = 0;
		while (!testApp.isStopped()) {
			System.out.println((++counter) + " Test app is running");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted");
			}
		}
		System.out.println("Test application stopped");
	}

}
