package org.adiusframework.resource;

import org.adiusframework.util.file.FileUtil;

public class FtpFileGenerator extends AbstractGenerator<FtpFileResource> {

	private String baseDirectory;

	private FtpServerResource ftpServerResource;

	public FtpServerResource getFtpServerResource() {
		return ftpServerResource;
	}

	public void setFtpServerResource(FtpServerResource ftpServerResource) {
		this.ftpServerResource = ftpServerResource;
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	@Override
	public FtpFileResource generate(Resource base) {

		// first we create the resource and set the obvious data
		FtpFileResource ftpFileResource = new FtpFileResource();
		ftpFileResource.setCapability(base.getCapability());
		ftpFileResource.setFtpServerResource(getFtpServerResource());

		// the base directory can either be default or set
		ftpFileResource.setDirectory(getBaseDirectory() == null ? "/" : getBaseDirectory());

		// the extension is either based upon the base resource, or is the
		// default temporary extension
		String extension = FileUtil.TMP_FILE_SUFFIX;
		if (FileResource.class.isInstance(base))
			extension = FileUtil.getFileExtension(FileResource.class.cast(base).getFile());
		ftpFileResource.setFile(FileUtil.generateUniqueFileName(FileUtil.TMP_FILE_PREFIX, extension));
		return ftpFileResource;
	}

}
