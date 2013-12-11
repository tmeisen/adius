package org.adiusframework.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.adiusframework.util.exception.UnexpectedException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileResourceOperations {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileResourceOperations.class);

	/**
	 * Tries to delete the file system resource.
	 * 
	 * @param resource
	 * @return true if the resource has been deleted successfully or if the
	 *         resource does not exist, otherwise false is returned.
	 */
	public static boolean remove(FileSystemResource resource) {
		FileObject object = null;
		try {
			object = resolve(resource);
			if (object == null) {

				// the passed resource cannot be resolved as file object
				LOGGER.warn("Resource " + resource + " does not represent valid file object");
				return false;
			}

			// lets delete the file because resolve does a cache
			// refresh, we do not have to refresh the cache here
			if (object.exists())
				object.delete();
			else
				LOGGER.warn("Removing of resource skipped, because it does not exists.");
			return true;
		} catch (FileSystemException e) {
			LOGGER.error("Deletion of " + object + " failed. Reason: " + e.getMessage());
			return false;
		}
	}

	public static FileObject resolve(FileSystemResource resource) {
		try {
			URL url = resolveUrlForm(resource.getUrlForm());
			if (url == null) {

				// the passed resource has an malformed url form
				LOGGER.warn("Resource " + resource + " has malformed url form");
				return null;
			}

			// we have to refresh the file cache, before we resolve the URL
			VFS.getManager().getFilesCache().close();
			return VFS.getManager().resolveFile(url.toExternalForm());
		} catch (FileSystemException e) {
			LOGGER.error("Resolving of resource URL: " + resource.getUrlForm() + " failed: " + e.getMessage());
			throw new UnexpectedException(e);
		}
	}

	protected static URL resolveUrlForm(String urlForm) {
		try {

			// translate the url of the resource into url form
			URL url = new URL(urlForm);
			return url;
		} catch (MalformedURLException e) {

			// if anything goes wrong return null
			LOGGER.error("Malformed url exception: " + e.getMessage());
			return null;
		}
	}

}
