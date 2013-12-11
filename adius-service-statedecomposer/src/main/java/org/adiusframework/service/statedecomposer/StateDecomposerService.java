package org.adiusframework.service.statedecomposer;

import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateCollection;
import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.statedecomposer.exception.StateDecomposerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateDecomposerService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(StateDecomposerService.class);

	private State baseState;

	private StateDecomposer decomposer;

	public State getBaseState() {
		return baseState;
	}

	public void setBaseState(State baseState) {
		this.baseState = baseState;
	}

	public StateDecomposer getDecomposer() {
		return decomposer;
	}

	public void setDecomposer(StateDecomposer decomposer) {
		this.decomposer = decomposer;
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		StandardServiceResult result = new StandardServiceResult();

		try {

			// we have to decompose the base state, therefore we use the
			// registered state decomposer
			LOGGER.info("Executing decomposing service for base state");
			StateCollection decomposedState = getDecomposer().decompose(getBaseState());
			ObjectResource<StateCollection> r = new ObjectResource<StateCollection>(decomposedState);
			r.setCapability(new StringResourceCapability("decomposed_actual_state"));
			result.addResource(r);
			return result;
		} catch (StateDecomposerException e) {

			// in case of an error, we just forward it to the service executor
			return new ErrorServiceResult(e.getMessage());
		}
	}

}
