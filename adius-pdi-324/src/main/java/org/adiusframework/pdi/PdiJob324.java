package org.adiusframework.pdi;

import org.adiusframework.util.ArrayUtil;
import org.adiusframework.util.exception.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;

public class PdiJob324 implements PdiJobWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdiJob324.class);

	private Job job;

	public PdiJob324(Job job) {
		this.job = job;
	}

	@Override
	public void start(boolean waitUntilFinished) {
		this.job.start();
		if (waitUntilFinished) {
			LOGGER.debug("Waiting for job to finish");
			this.job.waitUntilFinished();
		}
	}

	@Override
	public PdiResult getResult() {
		return new PdiResult324(this.job.getResult());
	}

	@Override
	public void setVariable(String name, String value) {
		LOGGER.debug("Setting variable " + name + " to " + value);
		this.job.setVariable(name, value);
	}

	@Override
	public void setParameter(String name, String value, boolean handleAsVariable) {
		LOGGER.debug("Setting parameter " + name + " to " + value);
		try {
			if (ArrayUtil.contains(this.job.getJobMeta().listParameters(), name))
				this.job.getJobMeta().setParameterValue(name, value);
			else if (handleAsVariable) {
				LOGGER.warn("Parameter " + name + " not found, handling as variable instead");
				this.setVariable(name, value);
			}
		} catch (UnknownParamException e) {

			// in PDI 3.2.4 this exception is not thrown by the set parameter
			// value it is only part of the method declaration, hence we declare
			// it as an unexpected exception
			LOGGER.error("Unexpected error " + e.getMessage() + " thrown.");
			throw new UnexpectedException(e);
		}
	}

	@Override
	public void activateParameters() {
		this.job.activateParameters();
	}

}
