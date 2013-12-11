package org.adiusframework.pdi;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdiUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdiUtil.class.getName());

	public static boolean initializeRequiredVariables(PdiJobWrapper job, String[] variableNames, Properties properties) {
		LOGGER.debug("Setting required variables...");
		Integer count = PdiUtil.initializeVariables(job, variableNames, properties, false);
		if (!count.equals(variableNames.length))
			return false;
		return true;
	}

	public static void initializeOptionalVariables(PdiJobWrapper job, String[] variableNames, Properties properties) {
		PdiUtil.initializeVariables(job, variableNames, properties, true);
	}

	private static Integer initializeVariables(PdiJobWrapper job, String[] variableNames, Properties properties,
			boolean initWithDefault) {
		Integer count = 0;
		for (String variableName : variableNames) {
			LOGGER.debug("Checking for variable: " + variableName + " Result: " + properties.containsKey(variableName));
			if (properties.containsKey(variableName)) {
				String value = properties.getProperty(variableName);
				if (value != null) {
					LOGGER.debug("Setting " + variableName + " to " + value);
					count++;
					job.setParameter(variableName, value, true);
				} else
					LOGGER.warn("Property exists but has a null value");
			} else if (initWithDefault) {
				LOGGER.debug("Setting " + variableName + " to default value");
				job.setParameter(variableName, "", true);
			}
		}
		return count;
	}
}
