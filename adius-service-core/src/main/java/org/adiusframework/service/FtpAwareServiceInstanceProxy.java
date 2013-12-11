package org.adiusframework.service;

import java.io.File;

import org.adiusframework.resource.FileResource;
import org.adiusframework.resource.FileResourceOperations;
import org.adiusframework.resource.FtpFileResource;
import org.adiusframework.resource.LocalFileGenerator;
import org.adiusframework.resource.LocalFileResource;
import org.adiusframework.resource.VfsFtpToLocalFileConverter;
import org.adiusframework.resource.VfsLocalToFtpFileConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * If data is shared using a FTP server not all services directly support this
 * protocol. Hence, the data has to be downloaded to the local file (in case of
 * an integration) or uploaded (in case of an extraction). This class provides a
 * proxy solution for such service instances. It wraps the service call and the
 * data transfer between the temporary local storage and the FTP server.
 * 
 * @author tm807416
 * 
 */
public class FtpAwareServiceInstanceProxy implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpAwareServiceInstanceProxy.class);

	private ServiceInstance pointer;

	private FileResource sourceResource;

	private FileResource targetResource;

	public FileResource getSourceResource() {
		return sourceResource;
	}

	public void setSourceResource(FileResource sourceResource) {
		this.sourceResource = sourceResource;
	}

	public FileResource getTargetResource() {
		return targetResource;
	}

	public void setTargetResource(FileResource targetResource) {
		this.targetResource = targetResource;
	}

	public ServiceInstance getPointer() {
		return pointer;
	}

	public void setPointer(ServiceInstance pointer) {
		this.pointer = pointer;
	}

	@Override
	public ServiceResult execute() throws ServiceException {

		// handling integration
		if (LocalFileIntegrationSupport.class.isInstance(getPointer())) {

			// if a FTP resource is given we have to convert the data
			if (FtpFileResource.class.isInstance(getSourceResource())) {
				FtpFileResource ffr = FtpFileResource.class.cast(getSourceResource());

				// lets create a temporary file resource to store the data
				LocalFileResource lfr = new LocalFileGenerator().generate(ffr);
				VfsFtpToLocalFileConverter converter = new VfsFtpToLocalFileConverter();

				// now we can convert the data and update the pointer
				converter.convert(ffr, lfr);
				LocalFileIntegrationSupport.class.cast(getPointer()).setFile(new File(lfr.getPath()));
			}

			// if a local resource is given instead, we have to set the source
			else if (LocalFileResource.class.isInstance(getSourceResource())) {
				LocalFileIntegrationSupport.class.cast(getPointer()).setFile(
						LocalFileResource.class.cast(getSourceResource()).getFileInstance());
			}
		}

		// handling extraction
		else if (LocalFileExtractionSupport.class.isInstance(getPointer())) {
			LOGGER.debug("Local file extraction service is provided...");

			// if a FTP resource is given we have to set a temporary destination
			if (FtpFileResource.class.isInstance(getTargetResource())) {
				FtpFileResource ffr = FtpFileResource.class.cast(getTargetResource());
				LocalFileResource lfr = new LocalFileGenerator().generate(ffr);
				LocalFileExtractionSupport.class.cast(getPointer()).setFile(new File(lfr.getPath()));
			}

			// if instead a local resource is given we have to set the target
			if (LocalFileResource.class.isInstance(getTargetResource())) {
				LocalFileExtractionSupport.class.cast(getPointer()).setFile(
						LocalFileResource.class.cast(getTargetResource()).getFileInstance());
			}
		}

		// now we can execute the service
		ServiceResult result = getPointer().execute();

		// handling extraction
		if (LocalFileExtractionSupport.class.isInstance(getPointer())) {
			LOGGER.debug("Copying service result to FTP origin...");

			// now we can copy the data from the temporary location
			if (FtpFileResource.class.isInstance(getTargetResource())) {
				LocalFileResource lfr = new LocalFileResource();
				lfr.setPath(LocalFileExtractionSupport.class.cast(getPointer()).getFile().getAbsolutePath());
				VfsLocalToFtpFileConverter converter = new VfsLocalToFtpFileConverter();
				converter.convert(lfr, FtpFileResource.class.cast(getTargetResource()));

				// if the conversion has been successful we can delete the
				// temporary local file
				FileResourceOperations.remove(lfr);
			}
		}
		return result;
	}
}
