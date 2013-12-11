package org.adiusframework.service.excel.exception;

import org.springframework.expression.spel.SpelEvaluationException;

public class ParserConfigException extends RuntimeException {
	private static final long serialVersionUID = -6060243407166769586L;

	public ParserConfigException(String msg) {
		super(msg);
	}

	public ParserConfigException(SpelEvaluationException e) {
		super(e);
	}

}
