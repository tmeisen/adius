package org.adiusframework.pdi;

import org.adiusframework.util.exception.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

public class PdiEnvironment420 implements PdiEnvironment {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PdiEnvironment420.class);

	protected PdiEnvironment420() {

		// initialize pdi environment
		if (!KettleEnvironment.isInitialized()) {
			try {
				KettleEnvironment.init();
			} catch (KettleException e) {
				throw new PdiException420(e);
			}
		}
	}

	@Override
	public PdiJobWrapper createJob(String jobFile, String[] args) {

		// create job and corresponding meta-data
		try {
			JobMeta jobMeta = new JobMeta(jobFile, null, null);
			Job job = new Job(null, jobMeta);
			job.setLogLevel(LogLevel.BASIC);
			job.getJobMeta().setArguments(args);
			job.copyParametersFrom(jobMeta);
			job.getJobMeta().setInternalKettleVariables(job);

			try {
				for (String key : job.getJobMeta().listParameters()) {
					String value = job.getJobMeta().getParameterValue(key);
					if (Const.isEmpty(value))
						value = job.getJobMeta().getParameterDefault(key);
					LOGGER.debug("Initialising parameter " + key + " with " + value);
					LOGGER.debug("Previous value " + job.getJobMeta().getParameterValue(key));
					job.getJobMeta().setParameterValue(key, value);
				}
			} catch (UnknownParamException e) {

				// in PDI 4.2.0 this exception is not thrown by the set
				// parameter value it is only part of the method declaration,
				// hence we declare it as an unexpected exception
				LOGGER.error("Unexpected error " + e.getMessage() + " thrown.");
				throw new UnexpectedException(e);
			}
			return new PdiJob420(job);
		} catch (KettleXMLException e) {
			throw new PdiException420(e);
		}
	}

	@Override
	public void close() {
		LOGGER.info("Closing pdi environment");
		// nothing to do in pdi 4.2
	}

}
