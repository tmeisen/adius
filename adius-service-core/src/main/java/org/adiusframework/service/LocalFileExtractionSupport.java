package org.adiusframework.service;

import java.io.File;

public interface LocalFileExtractionSupport extends LocalFileSupport {

	public File getFile();

	public void setFile(File file);

}
