package org.adiusframework.processmanager;

public interface DomainSpecificServiceProcessHandler {

	public void handleError(int entityId);

	public void handleServiceProcessResult(int entityId);

}
