package org.adiusframework.resource;

import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VfsConverter<F extends FileSystemResource, T extends FileSystemResource> extends AbstractConverter<F, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(VfsFtpToLocalFileConverter.class);

	static {
		LOGGER.info("Setting FTP file system option passive mode to true");
		FtpFileSystemConfigBuilder.getInstance().setPassiveMode(new FileSystemOptions(), true);
	}

	@Override
	protected T typeSafeConvert(F from, T to) {
		LOGGER.debug("Converting '" + from + "' to " + to);
		FileObject fromObject = FileResourceOperations.resolve(from);
		FileObject toObject = FileResourceOperations.resolve(to);
		LOGGER.debug("Resolved resource to '" + fromObject + "' and '" + toObject + "'");
		try {

			// we have to refresh the file cache
			VFS.getManager().getFilesCache().close();

			// now lets check the type and if the file exists
			if (fromObject.exists() && fromObject.getType().equals(FileType.FILE)) {
				LOGGER.debug("Copy process started...");
				toObject.copyFrom(fromObject, new AllFileSelector());
			} else
				LOGGER.warn("No copying because source is of file type " + fromObject.getType()
						+ " and has existance status " + fromObject.exists());
		} catch (FileSystemException e) {
			LOGGER.error("Error copying resources: " + e.getMessage());
			return null;
		}
		to.setCapability(from.getCapability());
		return to;
	}

}
