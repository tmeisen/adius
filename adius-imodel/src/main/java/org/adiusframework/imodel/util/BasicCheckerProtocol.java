package org.adiusframework.imodel.util;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BasicCheckerProtocol implements ConsistencyCheckerProtocol {

	private Set<CheckerProtocolEntry<?>> entries;

	public BasicCheckerProtocol() {
		entries = Collections.newSetFromMap(new ConcurrentHashMap<CheckerProtocolEntry<?>, Boolean>());
	}

	@Override
	public boolean passed() {
		return !(entries.size() > 0);
	}

	@Override
	public Set<CheckerProtocolEntry<?>> getEntries() {
		return Collections.unmodifiableSet(entries);
	}

	@Override
	public void addEntry(CheckerProtocolEntry<?> entry) {
		entries.add(entry);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (CheckerProtocolEntry<?> entry : entries) {
			buffer.append(entry.toString());
			buffer.append("\n");
		}
		return buffer.toString();
	}
}
