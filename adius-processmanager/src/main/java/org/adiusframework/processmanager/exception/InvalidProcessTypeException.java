package org.adiusframework.processmanager.exception;

import org.adiusframework.processmanager.domain.ServiceProcess;

public class InvalidProcessTypeException extends ServiceProcessRelatedException {
	private static final long serialVersionUID = 3014473343161724149L;

	public InvalidProcessTypeException(String message, ServiceProcess process) {
		super(message, process);
	}
}
