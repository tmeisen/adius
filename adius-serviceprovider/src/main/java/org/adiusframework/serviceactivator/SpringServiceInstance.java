package org.adiusframework.serviceactivator;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.adiusframework.service.JniDependend;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.util.jni.JniLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

public class SpringServiceInstance implements GenericServiceInstance {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringServiceInstance.class);

	private static final String DEFAULT_SERVICE_INSTANCE_NAME = "executiveServiceInstance";

	private String[] configuration;

	private String serviceInstanceName;

	public SpringServiceInstance() {
		setServiceInstanceName(DEFAULT_SERVICE_INSTANCE_NAME);
	}

	public SpringServiceInstance(String[] configuration) {
		this();
		this.setConfiguration(configuration);
	}

	public SpringServiceInstance(String serviceInstanceName, String[] configuration) {
		this.setServiceInstanceName(serviceInstanceName);
		this.setConfiguration(configuration);
	}

	public void setConfiguration(String... configuration) {
		this.configuration = configuration;
	}

	public String getServiceInstanceName() {
		return serviceInstanceName;
	}

	public void setServiceInstanceName(String serviceInstanceName) {
		this.serviceInstanceName = serviceInstanceName;
	}

	@Override
	public ServiceResult execute(ServiceInput input) throws ServiceException {

		// first we create a new bean factory to add the resources into this
		// context
		LOGGER.debug("Loading objects into context...");
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		for (String key : input.getKeys()) {
			Serializable value = input.get(key);
			LOGGER.debug("Injecting bean with name " + key + " and value " + value);
			beanFactory.registerSingleton(key, value);
		}

		// next we have to load the beans defined in the spring configuration
		GenericApplicationContext context = null;
		try {
			LOGGER.debug("Loading xml configuration " + this.configuration);
			context = new GenericApplicationContext(beanFactory);
			XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(context);
			xmlReader.loadBeanDefinitions(configuration);

			// after this we can finally refresh the context
			context.refresh();
			ServiceInstance service = null;
			Map<String, ServiceInstance> serviceBeans = context.getBeansOfType(ServiceInstance.class);
			if (serviceBeans.size() > 1) {
				LOGGER.debug("Multiple service instances found searching for bean with name "
						+ getServiceInstanceName());
				service = (ServiceInstance) context.getBean(DEFAULT_SERVICE_INSTANCE_NAME);
			} else {
				LOGGER.debug("One service instance identified in context, using this one...");
				service = serviceBeans.values().iterator().next();
			}
			if (service == null)
				throw new ServiceException("No unique service instance found within context.");
			LOGGER.debug("Service build successfully...");

			// last but no least we have to search for the created service in
			// the context and check if some initialization has to be done
			LOGGER.debug("Searching for jni dependend beans...");
			Map<String, Object> jniDependentBeans = context.getBeansWithAnnotation(JniDependend.class);
			for (Entry<String, Object> jniDependentBean : jniDependentBeans.entrySet()) {
				LOGGER.debug("Found jni depended bean " + jniDependentBean.getKey() + " loading libraries");
				if (!((JniLoader) context.getBean(jniDependentBean.getValue().getClass()
						.getAnnotation(JniDependend.class).jniLoader())).load()) {
					throw new ServiceException("Loading of jni libraries failed.");
				}
			}

			// last we can run the service
			return service.execute();
		} finally {

			// we have to make sure, that the context is closed, doesn't matter
			// what happens
			if (context != null)
				context.close();
		}
	}
}
