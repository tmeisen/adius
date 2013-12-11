package org.adiusframework.service.pdi;

import java.util.Properties;

import org.adiusframework.pdi.PdiJob;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlPdiParamsTransferService implements ServiceInstance {
	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlPdiParamsTransferService.class);

	private PdiJob pdiJob;

	private Properties parameters;

	private Properties properties;

	public Properties getParameters() {
		return parameters;
	}

	public void setParameters(Properties parameters) {
		this.parameters = parameters;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void setPdiJob(PdiJob pdiJob) {
		this.pdiJob = pdiJob;
	}

	public PdiJob getPdiJob() {
		return this.pdiJob;
	}

	@Override
	public ServiceResult execute() throws ServiceException {

		// first we have to set the arguments
		LOGGER.debug("Preparing arguments...");
		String[] args = new String[0];

		// second we have to define the properties
		LOGGER.debug("Preparing properties...");
		Properties properties = new Properties();
		properties.putAll(getParameters());
		properties.putAll(getProperties());

		// now we can execute the job using the previously defined arguments and
		// properties
		LOGGER.debug("Executing the job...");
		getPdiJob().execute(args, properties);
		return new StandardServiceResult();
	}

}
