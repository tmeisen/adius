package org.adiusframework.pdi;

public interface PdiJobWrapper {

	public abstract void start(boolean waitUntilFinished);

	public abstract PdiResult getResult();

	public abstract void setVariable(String name, String value);

	public abstract void setParameter(String name, String value, boolean tryAsVariable);

	public abstract void activateParameters();
}