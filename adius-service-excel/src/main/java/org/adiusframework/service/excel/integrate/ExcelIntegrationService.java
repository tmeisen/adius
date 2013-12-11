package org.adiusframework.service.excel.integrate;

import java.io.IOException;

import org.adiusframework.resource.FileSystemResource;
import org.adiusframework.resource.FileSystemResourceLoader;
import org.adiusframework.resource.DefaultFileSystemResourceLoader;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.excel.ExcelUtil;
import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.WorkbookParser;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.adiusframework.service.excel.parser.persistor.ParserResultCachePersistor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelIntegrationService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelIntegrationService.class);

	private FileSystemResourceLoader loader;

	private FileSystemResource resource;

	private WorkbookParser<?> parser;

	private ParserResultCachePersistor persistor;

	public ExcelIntegrationService() {
		super();
		loader = new DefaultFileSystemResourceLoader();
	}

	public WorkbookParser<?> getParser() {
		return parser;
	}

	public void setParser(WorkbookParser<?> parser) {
		this.parser = parser;
	}

	public ParserResultCachePersistor getPersistor() {
		return persistor;
	}

	public void setPersistor(ParserResultCachePersistor persistor) {
		this.persistor = persistor;
	}

	public void setUrl(String url) {

		// load the resource
		LOGGER.debug("Setting up with " + url);
		FileSystemResource resource = loader.create(url, null);
		if (resource == null || !resource.isFile()) {
			throw new IllegalArgumentException("Url is malformed or resource could not be identified");
		}

		// check if the file is of one of the supported excel file formats
		if (ExcelUtil.isExcelFileFormat(resource.getUrlForm())) {
			this.resource = resource;
		} else {
			throw new UnsupportedOperationException("Filetype is not supported.");
		}
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		if (resource == null) {
			throw new ServiceException("Service has not been initialized correctly, no source url specified.");
		}

		Workbook workbook = null;
		try {
			LOGGER.debug("Integration of " + resource.getUrlForm().toString());
			workbook = WorkbookFactory.create(resource.openInputStream());
		} catch (IOException e) {
			LOGGER.error("IOException: " + e.getMessage());
			throw new ServiceException(e);
		} catch (InvalidFormatException e) {
			LOGGER.error("InvalidFormatException:" + e.getMessage());
			throw new ServiceException(e);
		}
		try {
			ParserResultCache result = getParser().parse(workbook);
			getPersistor().persist(result);
		} catch (ExcelParseException e) {
			throw new ServiceException(e);
		}
		return new StandardServiceResult();
	}
}
