package org.adiusframework.imodel.util;

public class StringCheckerProtocolEntry implements CheckerProtocolEntry<String> {

	private String value;

	public StringCheckerProtocolEntry(String value) {
		setValue(value);
	}

	@Override
	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
