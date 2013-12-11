package org.adiusframework.service.statedecomposer.exception;

import org.adiusframework.resource.state.Assertion;

public class UnsupportedAssertionException extends StateDecomposerException {
	private static final long serialVersionUID = 2137625015784060498L;

	public UnsupportedAssertionException(Assertion a) {
		super(a.toString());
	}

}
