package org.adiusframework.processmanager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

/**
 * A very simple test of AbstractQueryStatusEventSource. It is tested if a given
 * QueryStatusEvent is published to all registered QueryStatusListeners.
 */
public class AbstractQueryStatusEventSourceTest {
	private static final int TEST_LISTENER_COUNT = 16;

	private QueryStatusListener[] listeners;
	private QueryStatusEvent event;

	// This object will be tested
	private AbstractQueryStatusEventSource source;

	@Before
	public void init() {
		source = new AbstractQueryStatusEventSource();

		// Create the test-listeners and register them
		listeners = new QueryStatusListener[TEST_LISTENER_COUNT];
		for (int i = 0; i < TEST_LISTENER_COUNT; i++) {
			listeners[i] = mock(QueryStatusListener.class);
			source.registerListener(listeners[i]);
		}

		// Create the test-event
		event = mock(QueryStatusEvent.class);
	}

	@Test
	public void testFireEvent() {
		source.fireEvent(event);

		// Verify if all listeners have received the test-event
		for (int i = 0; i < TEST_LISTENER_COUNT; i++) {
			verify(listeners[i], times(1)).queryStatusChanged(event);
		}
	}
}
