package org.adiusframework.console;

public class DataPackage {

	private String path;

	private String format;

	private boolean extractable;

	public DataPackage(String path, String format, boolean extractable) {
		setPath(path);
		setFormat(format);
		setExtractable(extractable);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isExtractable() {
		return extractable;
	}

	public void setExtractable(boolean extractable) {
		this.extractable = extractable;
	}

}
