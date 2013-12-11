package org.adiusframework.resource;

import org.adiusframework.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileGenerator extends AbstractGenerator<LocalFileResource> {

	private static Logger LOGGER = LoggerFactory.getLogger(LocalFileGenerator.class);

	private String baseDirectory;

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	@Override
	public LocalFileResource generate(Resource base) {
		LOGGER.debug("Local file generator called with base " + base);

		// first we create the resource and set the obvious data
		LocalFileResource localFileResource = new LocalFileResource();
		localFileResource.setCapability(base.getCapability());

		// if a base directory is set we set one, otherwise we will use the
		// system default temporary folder
		if (getBaseDirectory() == null || getBaseDirectory().isEmpty()) {
			LOGGER.debug("Setting temporary directory for generated resource");
			localFileResource.setDirectory(FileUtil.getTemporaryDirectory());
		} else {
			LOGGER.debug("Base directory set for generated resource");
			localFileResource.setDirectory(getBaseDirectory());
		}
		LOGGER.debug("Directory set to " + localFileResource.getDirectory());

		// the extension is either based upon the base resource, or is the
		// default temporary extension
		String extension = FileUtil.TMP_FILE_SUFFIX;
		if (FileResource.class.isInstance(base)) {
			LOGGER.debug("Extraktion extension from base");
			extension = FileUtil.getFileExtension(FileResource.class.cast(base).getFile());
			LOGGER.debug("Extension extracted as " + extension);
		}
		localFileResource.setFile(FileUtil.generateUniqueFileName(FileUtil.TMP_FILE_PREFIX, extension));
		LOGGER.debug("Resource generated " + localFileResource);
		return localFileResource;
	}

}
