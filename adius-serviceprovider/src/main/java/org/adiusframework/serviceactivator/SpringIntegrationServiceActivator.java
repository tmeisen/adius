package org.adiusframework.serviceactivator;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Payload;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class SpringIntegrationServiceActivator extends AbstractServiceActivator {

	@Override
	@ServiceActivator
	public ServiceResult activate(@Payload ServiceInput input) {
		return super.activate(input);
	}

}
