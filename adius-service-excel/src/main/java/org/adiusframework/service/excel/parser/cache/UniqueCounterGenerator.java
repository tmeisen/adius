package org.adiusframework.service.excel.parser.cache;

public class UniqueCounterGenerator implements KeyGenerator<String> {

	private long counter;

	public UniqueCounterGenerator() {
		counter = 0;
	}

	@Override
	public String generate(Object object) {
		counter++;
		return String.valueOf(counter);
	}

}
