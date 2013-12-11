package org.adiusframework.service.pdi;

import java.io.File;

import org.adiusframework.pdi.PdiJob;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.util.datastructures.MySqlConnectionContainer;
import org.adiusframework.util.datastructures.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySqlPdiArgsTransferService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlPdiArgsTransferService.class);

	private PdiJob pdiJob;

	private String fileProperty;

	private Properties properties;

	private MySqlConnectionContainer dbConnector;

	private Integer entityId;

	private File file;

	public MySqlConnectionContainer getDbConnector() {
		return dbConnector;
	}

	public void setDbConnector(MySqlConnectionContainer dbConnector) {
		this.dbConnector = dbConnector;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileProperty(String fileProperty) {
		this.fileProperty = fileProperty;
	}

	public String getFileProperty() {
		return this.fileProperty;
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

		// first we have to set the arguments (connection container + id)
		LOGGER.debug("Preparing arguments...");
		MySqlConnectionContainer container = this.getDbConnector();
		String[] args = new String[] { getEntityId().toString(), container.getIp(), container.getPort().toString(),
				container.getSchema(), container.getUser(), container.getPassword() };

		// second we have to define the properties
		LOGGER.debug("Preparing properties...");
		Properties properties = new Properties();

		// user defined properties
		properties.setProperties(getProperties());

		// standard properties of file and database
		properties.setProperty(getFileProperty(), getFile().getAbsolutePath());

		// now we can execute the job using the previously defined arguments and
		// properties
		LOGGER.debug("Executing the job...");
		getPdiJob().execute(args, properties);
		return new StandardServiceResult();
	}

}
