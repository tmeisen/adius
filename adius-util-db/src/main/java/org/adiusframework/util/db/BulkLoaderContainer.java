package org.adiusframework.util.db;

import java.util.concurrent.ConcurrentHashMap;

public class BulkLoaderContainer extends ConcurrentHashMap<String, BulkLoader> {

	/**
	 * Version for serialization
	 */
	private static final long serialVersionUID = 6052661559971997206L;

	public BulkLoader put(BulkLoader loader) {
		return this.put(loader.getTable(), loader);
	}

	public void open() {

		// close all open bulk loaders
		for (BulkLoader loader : this.values())
			loader.open();
	}

	public void close() {

		// close all open bulk loaders
		for (BulkLoader loader : this.values())
			loader.close();
	}

	public void delete() {

		// delete all bulk loaders
		for (BulkLoader loader : this.values())
			loader.delete();
	}
}
