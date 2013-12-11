package org.adiusframework.service.statedecomposer;

import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateCollection;
import org.adiusframework.service.statedecomposer.exception.StateDecomposerException;

public interface StateDecomposer {

	public StateCollection decompose(State baseState) throws StateDecomposerException;

}
