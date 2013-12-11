package org.adiusframework.pdi;

public interface PdiEnvironment {

	public abstract PdiJobWrapper createJob(String jobFile, String[] args);

	public abstract void close();

}