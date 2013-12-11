package org.adiusframework.imodel.util;

import java.util.Set;

public interface ConsistencyCheckerProtocol {

	public boolean passed();

	public Set<CheckerProtocolEntry<?>> getEntries();

	public void addEntry(CheckerProtocolEntry<?> entry);

}
