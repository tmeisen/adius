package org.adiusframework.util.testing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.adiusframework.util.IsConfigured;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.RemoteAccessException;

/**
 * This class is the base class for all integration-tests in the
 * adius-framework. It starts a XmlApplicationContext which is defined in the
 * file "integrationtest-core-context.xml", that should be located in the
 * classpath of the tested project, and general configuration files, that are
 * part of the util-core project. Then it initialize the access of a bean with
 * the name "remotedApplication" over a ApacheMQ broker. The class also tries to
 * start the broker manually if it can't reach anyone on the configured port
 * port.
 * 
 * The test can be configured with the property-file
 * "settings/integrationtest-messaging.properties". In this file should be
 * defined following properties:
 * <ul>
 * <li>messaging.queue - The queue which is used to communicate with the remoted
 * application.</li>
 * <li>messagebroker.port - The port of the message broker.</li>
 * <li>service.interface - The interface which defines the functions that are
 * remoted-</li>
 * </ul>
 * 
 * @param <Application>
 *            The class of the bean, that represents the remoted application.
 */
public abstract class AdiusIntegrationTest<Application extends IsConfigured> /*
																			 * extends
																			 * AbstractTransactionalJUnit4SpringContextTests
																			 */{
	public static final Logger LOGGER = LoggerFactory.getLogger(AdiusIntegrationTest.class);

	/**
	 * The name oft the file that stores the bean definition for the message
	 * context.
	 */
	private static final String MESSAGE_CONTEXT_FILE = "integrationtest-message-context.xml";

	/**
	 * The name of the file that should be used by the user to define the beans
	 * of the tested application.
	 */
	private static final String CORE_CONTEXT_FILE = "integrationtest-core-context.xml";

	/** The delay for the message broker to initialize. */
	private static final int WAIT_SECONDS = 5;

	/**
	 * The broker service should be initialized only once
	 */
	private static BrokerService jmsBroker;

	/**
	 * Path to the config for a test message broker
	 */
	private static final String TEST_ACTIVEMQ_PATH = "/settings/adius-test-activemq.xml";

	/** The context of the tested application. */
	private static ClassPathXmlApplicationContext context;

	/**
	 * The proxy object to access the tested application remoted through the
	 * message broker.
	 */
	private Application remotedApplication;

	/** Indicates if the class was initialized. */
	private boolean inited = false;

	/**
	 * Should be overwritten if the test case has to do some specific
	 * configuration right after the ApplicationContext is created. This method
	 * is called before the method {@link IsConfigured#checkConfiguration()} is
	 * called to check the configuration.
	 * 
	 * @param context
	 *            The context that should be configured.
	 */
	protected void doSpecificConfiguration(AbstractApplicationContext context) {
		// if not overwritten, than we have nothing to do
	}

	protected abstract void setApplication(Application application);

	public static void initMessageBroker() {
		try {
			jmsBroker = BrokerFactory.createBroker(new URI("xbean:" + TEST_ACTIVEMQ_PATH), true);
		} catch (Exception e) {
			LOGGER.error("Unexpected error: " + e.getMessage());
		}
	}

	@Before
	public void init() {

		// Ensure that this function is only called once during the test
		if (!inited) {
			inited = true;

			try {
				// Create the context and get the bean of the proxy object
				LOGGER.info("Starting test initialization!");
				context = new ClassPathXmlApplicationContext(new String[] { MESSAGE_CONTEXT_FILE, CORE_CONTEXT_FILE });
				doSpecificConfiguration(context);
				remotedApplication = typeSafeCast(context.getBean("applicationImporter"));

				// Check if the broker is running and the application is
				// configured proper
				try {
					assertTrue(remotedApplication.checkConfiguration());
					LOGGER.info("The remoted application is configured correctly!");
				} catch (RemoteAccessException exception) {
					LOGGER.debug("Can't connect to the Message-Broker on localhost. Try to start it automatically.");
					if (jmsBroker == null) {
						initMessageBroker();
					} else {
						jmsBroker.start();
					}

					// No the broker should be started up and we can check, that
					// the tested application was initiated properly
					LOGGER.debug("Test if the broker was started successfuly...");
					if (jmsBroker == null) {
						fail("Initialization of jms broker failed");
					} else {
						try {
							assertTrue(remotedApplication.checkConfiguration());
						} catch (RemoteAccessException e) {
							LOGGER.debug("RemoteAccessExcetpion at the first try. Wait another " + WAIT_SECONDS
									+ " and try it again!");
							try {
								Thread.sleep(WAIT_SECONDS * 1000);
							} catch (InterruptedException e2) {
								LOGGER.info("The thread was interrupted while waiting for the Message-Broker initialize.");
							}
							try {
								assertTrue(remotedApplication.checkConfiguration());
								LOGGER.debug("Message-Broker sucessfully started!");
							} catch (RemoteAccessException e3) {
								fail("Can't connect either to teh message broker or the remoted applicatiopn isn't initialized correctly");
							}
						}
					}
				}
			} catch (BeanDefinitionStoreException exception) {
				exception.printStackTrace();
				LOGGER.error("You must create a file with the name " + CORE_CONTEXT_FILE
						+ " that contains a bean called 'remotedApplication'!");
			} catch (Exception exception) {
				LOGGER.error("Unknown exception: " + exception.getMessage());
			}
			setApplication(remotedApplication);
		} else {
			LOGGER.debug("Initialization skipped, already setup");
		}
	}

	@After
	public void destroyContext() {
		context.close();
	}

	@AfterClass
	public static void destroyBroker() {
		if (jmsBroker != null) {
			try {
				jmsBroker.stop();
				jmsBroker = null;
			} catch (Exception e) {
				LOGGER.error("Closing jms broker failed: " + e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Application typeSafeCast(Object o) {
		return (Application) o;
	}

	/**
	 * Returns the proxy object that should be used to test the remoted
	 * application.
	 * 
	 * @return The proxy to be tested.
	 */
	protected Application getRemotedApplication() {
		return remotedApplication;
	}

}
