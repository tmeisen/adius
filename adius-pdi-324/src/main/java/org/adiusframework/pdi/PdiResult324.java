package org.adiusframework.pdi;

import org.pentaho.di.core.Result;

public class PdiResult324 implements PdiResult {

	private Result result;

	public PdiResult324(Result result) {
		this.result = result;
	}

	@Override
	public long getNrErrors() {
		return this.result.getNrErrors();
	}

	@Override
	public String toString() {
		return this.result.toString();
	}
}
