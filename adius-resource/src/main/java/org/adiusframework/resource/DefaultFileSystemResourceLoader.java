package org.adiusframework.resource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.adiusframework.util.ArrayUtil;
import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.exception.UnexpectedException;
import org.adiusframework.util.file.FileUtil;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.ftp.FtpFileObject;
import org.apache.commons.vfs2.provider.local.LocalFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A file resource loader that is based upon the Apache commons VFS API.
 * 
 * @author tm807416
 * 
 */
public class DefaultFileSystemResourceLoader implements FileSystemResourceLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFileSystemResourceLoader.class);

	private static final String[] SCHEMES = { "http", "https", "ftp", "file" };

	private FileSystemManager fsManager;

	public DefaultFileSystemResourceLoader() {
		try {
			setFsManager(VFS.getManager());
		} catch (FileSystemException e) {
			LOGGER.error("Unexpected exception occured during initialization of file resource factory.");
			throw new UnexpectedException(e);
		}
	}

	public FileSystemManager getFsManager() {
		return fsManager;
	}

	public void setFsManager(FileSystemManager fsManager) {
		this.fsManager = fsManager;
	}

	@Override
	public FileSystemResource create(URI uri, ResourceCapability rc) {
		try {
			return create(uri.toURL().toExternalForm(), rc);
		} catch (MalformedURLException e) {
			LOGGER.error("URI is malformed: " + e.getMessage());
			return null;
		}
	}

	@Override
	public FileSystemResource create(String url, ResourceCapability rc) {
		if (url == null) {
			throw new NullPointerException("Url cannot be null");
		}

		try {

			// check for classpath url
			if (url.startsWith(ClassPathFileResource.URL_CLASSPATH_PREFIX)) {
				return createClassPathResource(url, rc);
			} else {

				// additional url can be handled by making use of the
				// FileSystemManager
				return createResourceByManager(url, rc);
			}

		} catch (FileSystemException e) {
			LOGGER.error("Unexpected exception: " + e.getMessage());
			return null;
		} catch (MalformedURLException e) {
			LOGGER.error("Malformed URL: " + e.getMessage());
			return null;
		}
	}

	protected FileSystemResource createClassPathResource(String url, ResourceCapability rc) {
		String path = url.substring(ClassPathFileResource.URL_CLASSPATH_PREFIX.length());
		ClassPathFileResource cpr = new ClassPathFileResource();
		cpr.setPath(path);
		cpr.setCapability(rc);
		return cpr;
	}

	protected FileSystemResource createResourceByManager(String url, ResourceCapability rc) throws FileSystemException,
			MalformedURLException {

		// first we use the manager to get the representative file object
		FileObject fo = getFsManager().resolveFile(url);

		// FTP resources
		if (FtpFileObject.class.isInstance(fo)) {
			LOGGER.debug("Ftp resource with type " + fo.getType() + " identified...");
			FtpFileObject ftpFo = FtpFileObject.class.cast(fo);

			// extracting the FTP server data
			URL tmpUrl = new URL(url);
			FtpConnectionContainer fcc = new FtpConnectionContainer();
			fcc.setData(tmpUrl);
			FtpServerResource ftpsr = new FtpServerResource();
			ftpsr.setFtpConnection(fcc);
			ftpsr.setCapability(new UnclassifiedCapability());

			// now we can handle the folder and file resource data
			// file
			if (ftpFo.getType().equals(FileType.FILE) || ftpFo.getType().equals(FileType.IMAGINARY)) {
				LOGGER.debug("Ftp file resource identified...");
				FtpFileResource ffr = new FtpFileResource();
				ffr.setPath(ftpFo.getName().getPath());
				ffr.setFtpServerResource(ftpsr);
				ffr.setCapability(rc);
				return ffr;
			}

			// folder
			else if (ftpFo.getType().equals(FileType.FOLDER)) {
				LOGGER.debug("Ftp directory resource identified...");
				FtpDirectoryResource fdr = new FtpDirectoryResource();
				fdr.setDirectory(ftpFo.getName().getPath());
				fdr.setFtpServerResource(ftpsr);
				fdr.setCapability(rc);
				return fdr;
			}

			// anything else
			else
				throw new UnsupportedOperationException("The type of FTP resource " + ftpFo.getType()
						+ " is not supported.");
		}

		// local resources (file and directory)
		else if (LocalFile.class.isInstance(fo)) {
			LocalFile lf = LocalFile.class.cast(fo);

			// file
			if (lf.getType().equals(FileType.FILE) || lf.getType().equals(FileType.IMAGINARY)) {
				LocalFileResource lfr = new LocalFileResource();
				lfr.setPath(new File(lf.getURL().getFile()).getPath());
				lfr.setCapability(rc);
				return lfr;
			}

			// folder
			else if (lf.getType().equals(FileType.FOLDER)) {
				LocalDirectoryResource ldr = new LocalDirectoryResource();
				ldr.setDirectory(new File(lf.getURL().getFile()).getPath());
				ldr.setCapability(rc);
				return ldr;
			}

			// anything else
			else
				throw new UnsupportedOperationException("The type of local resource " + lf.getType()
						+ " is not supported.");
		}

		// unsupported file object class
		else {
			LOGGER.error("Unsupported vfs file object specialisation.");
			throw new UnsupportedOperationException("File object of scheme " + fo.getName().getScheme()
					+ " is unsupported.");
		}

	}

	@Override
	public boolean supports(String scheme) {
		return ArrayUtil.contains(getFsManager().getSchemes(), scheme);
	}

	@Override
	public boolean validate(String url) {
		try {
			LOGGER.debug("Checking URL: " + url);
			boolean result = false;

			// first we check for a valid path and file name structure
			if (FileUtil.isPathAndFile(url)) {
				result = true;
			}

			// second we use the uri class to check if a valid url structure is
			// given
			else {
				URI uri = new URI(url);

				// if no schema is found, the URL is invalid
				if (uri.getScheme() == null) {
					LOGGER.debug("Scheme identification failed");
					return false;
				} else {

					// check if the scheme is valid
					result = ArrayUtil.contains(SCHEMES, uri.getScheme());
				}
			}
			LOGGER.debug("Result of check " + result);
			return result;
		} catch (URISyntaxException e) {
			LOGGER.debug("URL is malformed: " + e.getMessage());
			return false;
		}
	}

}
