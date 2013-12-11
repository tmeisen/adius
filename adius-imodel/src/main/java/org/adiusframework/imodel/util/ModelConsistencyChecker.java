package org.adiusframework.imodel.util;

public interface ModelConsistencyChecker {

	public ConsistencyCheckerProtocol check();

	public <T> void addModelFact(T fact);
	
	public void addModelFacts(Object... facts);
	
}
