package org.adiusframework.util.console;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringApplicationConsoleTest {
	private SpringApplicationConsole console;

	private ClassPathXmlApplicationContext context;

	@Before
	public void init() {
		console = new SpringApplicationConsole();

		context = mock(ClassPathXmlApplicationContext.class);
		console.setContext(context);
	}

	@Test
	public void testRun() {
		try {
			PipedOutputStream output = new PipedOutputStream();
			System.setIn(new PipedInputStream(output));
			new PrintStream(output).print("exit\n");
		} catch (IOException e) {
			System.err.println("Can't run Test \"testRun\" because of following IOException");
			e.printStackTrace();
			fail();
		}

		console.run();

		verify(context, only()).close();
	}
}
