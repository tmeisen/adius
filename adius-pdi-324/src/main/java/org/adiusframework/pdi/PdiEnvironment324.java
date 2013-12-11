package org.adiusframework.pdi;

import org.adiusframework.util.datastructures.Properties;
import org.pentaho.di.core.JndiUtil;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogWriter;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryLoader;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.StepLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdiEnvironment324 implements PdiEnvironment {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PdiEnvironment324.class);

	private static final String PLUGIN_PACKAGES_TAG = "pdi.plugin_packages";

	private static final String PLUGIN_PACKAGES_KEY = "KETTLE_PLUGIN_PACKAGES";

	private LogWriter lw;

	protected PdiEnvironment324(Properties properties) {

		// load configuration
		if (properties != null && properties.containsKey(PLUGIN_PACKAGES_TAG)) {
			LOGGER.info("Properties loaded " + properties.toString());
			String packages = properties.getProperty(PLUGIN_PACKAGES_TAG);
			System.setProperty(PLUGIN_PACKAGES_KEY, packages);
			LOGGER.info("Setting: " + PLUGIN_PACKAGES_KEY + " to " + packages);
		}

		// initialize pdi environment
		EnvUtil.environmentInit();
		JndiUtil.initJNDI();

		// create step loader and job entry loader
		try {
			StepLoader.init();
			JobEntryLoader.init();
		} catch (KettleException e) {
			throw new PdiException(e);
		}

		// setup log writer
		LogWriter.setConsoleAppenderDebug();
		this.lw = LogWriter.getInstance(LogWriter.LOG_LEVEL_BASIC);
	}

	private LogWriter getLogWriter() {
		return this.lw;
	}

	private StepLoader getStepLoader() {
		return StepLoader.getInstance();
	}

	@Override
	public PdiJobWrapper createJob(String jobFile, String[] args) {

		// create job and meta-data of job
		try {
			LOGGER.info("Creating job based upon " + jobFile);
			JobMeta jobMeta = new JobMeta(this.getLogWriter(), jobFile, null, null);
			Job job = new Job(this.getLogWriter(), this.getStepLoader(), null, jobMeta);
			job.getJobMeta().setArguments(args);

			// setup internal and external variables of job
			job.initializeVariablesFrom(null);
			job.getJobMeta().setInternalKettleVariables(job);
			job.copyParametersFrom(job.getJobMeta());
			job.activateParameters();
			return new PdiJob324(job);
		} catch (KettleException e) {
			throw new PdiException(e);
		}
	}

	@Override
	public void close() {
		LOGGER.info("Closing pdi environment");
		this.lw.close();
	}
}
