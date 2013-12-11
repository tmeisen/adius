package org.adiusframework.service.excel.extract;

import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExcelExtractionService implements ServiceInstance {

	private ExcelWorkbookWriter writer;

	public ExcelWorkbookWriter getWriter() {
		return writer;
	}

	public void setWriter(ExcelWorkbookWriter writer) {
		this.writer = writer;
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		getWriter().write();
		return new StandardServiceResult();
	}

	public static void main(String[] args) throws ServiceException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:capacityplanning-extraction-service-context.xml");
		ExcelExtractionService service = context.getBean(ExcelExtractionService.class);
		service.execute();
		context.close();
	}

}
