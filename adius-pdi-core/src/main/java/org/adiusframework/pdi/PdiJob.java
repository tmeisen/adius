package org.adiusframework.pdi;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdiJob {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdiJob.class);

	private PdiEnvironment env;

	private String jobPath;

	private String jobFile;

	private String[] requiredVariables;

	private String[] optionalVariables;

	public void setEnv(PdiEnvironment env) {
		this.env = env;
	}

	public PdiEnvironment getEnv() {
		return this.env;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	public String getJobPath() {
		return this.jobPath;
	}

	public void setJobFile(String jobFile) {
		this.jobFile = jobFile;
	}

	public String getJobFile() {
		return this.jobFile;
	}

	public void setRequiredVariables(String[] requiredVariables) {
		this.requiredVariables = requiredVariables;
	}

	public String[] getRequiredVariables() {
		return this.requiredVariables;
	}

	public void setOptionalVariables(String[] optionalVariables) {
		this.optionalVariables = optionalVariables;
	}

	public String[] getOptionalVariables() {
		return this.optionalVariables;
	}

	public void execute(String[] jobArgs, Properties variables) {

		// first we create the job
		try {
			LOGGER.debug("Creating job with " + this.getJobPath() + this.getJobFile());
			PdiJobWrapper job = this.env.createJob(this.getJobPath() + this.getJobFile(), jobArgs);

			// second we initialize the variables and parameters
			if (variables != null) {
				LOGGER.debug("Initializing required variables: " + this.getRequiredVariables());
				if (this.getRequiredVariables() != null)
					if (!PdiUtil.initializeRequiredVariables(job, this.getRequiredVariables(), variables))
						throw new PdiException("Missing required variables detected, job cannot be started");
				LOGGER.debug("Initializing optional variables: " + this.getOptionalVariables());
				if (this.getOptionalVariables() != null)
					PdiUtil.initializeOptionalVariables(job, this.getOptionalVariables(), variables);
			}
			job.activateParameters();

			// third we execute the job and wait till it is finished
			job.start(true);
			PdiResult result = job.getResult();

			// if an error has been occurred we throw an error
			LOGGER.info(result.toString());
			if (result.getNrErrors() > 0)
				throw new PdiException("PDI Job failed, because of errors: " + result.toString());
		} catch (Exception e) {
			throw new PdiException(e);
		}
	}

}
