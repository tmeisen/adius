package org.adiusframework.util.jni;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a service loading native libraries before the service is started.
 */
public class JniLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(JniLoader.class);

	/**
	 * the path where the files are located.
	 */
	private String libraryPath;

	/**
	 * A Set which stores the file-names of the library-files, that should be
	 * load.
	 */
	private Set<String> libraryFiles;

	/**
	 * Create a new JniLoader without any specific configuration.
	 */
	public JniLoader() {
		libraryFiles = new HashSet<String>();
	}

	/**
	 * Sets a new library-path.
	 * 
	 * @param libraryPath
	 *            The new library-path.
	 */
	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	/**
	 * Sets new library-files.
	 * 
	 * @param libraryFiles
	 *            The new library-files.
	 */
	public void setLibraryFilesSet(Set<String> libraryFiles) {
		this.libraryFiles = libraryFiles;
	}

	/**
	 * Sets anew library-files based on a String which contains the file-names
	 * separated by ";".
	 * 
	 * @param libraryFiles
	 *            The String with the file-names.
	 */
	public void setLibraryFiles(String libraryFiles) {
		this.libraryFiles = new HashSet<String>();
		for (String libraryFile : libraryFiles.split(";"))
			this.libraryFiles.add(libraryFile);
	}

	/**
	 * Loads all valid files, defined in the library-files set, from the
	 * library-path.
	 * 
	 * @return True if all files are valid, false otherwise.
	 */
	public boolean load() {
		LOGGER.debug("Initializing native libraries from " + this.libraryPath);
		String libraryPath = this.libraryPath;
		if (!libraryPath.endsWith("\\") && !libraryPath.endsWith("/"))
			libraryPath = libraryPath.concat("\\");
		for (String libraryFile : this.libraryFiles) {
			LOGGER.debug("Validating library file");
			String concateLibraryFile = libraryPath + libraryFile;
			if (!new File(concateLibraryFile).isFile()) {
				LOGGER.debug("Invalid library file " + concateLibraryFile);
				return false;
			}
			LOGGER.debug("Loading native library file " + concateLibraryFile);
			System.load(concateLibraryFile);
		}
		LOGGER.debug("Loading of native libraries finished");
		return true;
	}
}
