package org.adiusframework.service.excel.parser.cache;

import org.adiusframework.service.excel.exception.ParserConfigException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class SpelKeyGenerator implements KeyGenerator<String> {

	private static final String DEFAULT_OBJECT_REFERENCE = "object";

	private ExpressionParser parser;

	private StandardEvaluationContext context;

	private String expression;

	private String objectReference;

	public SpelKeyGenerator(String expression) {
		this(expression, DEFAULT_OBJECT_REFERENCE);
	}

	public SpelKeyGenerator(String expression, String objectReference) {
		parser = new SpelExpressionParser();
		context = new StandardEvaluationContext();
		this.expression = expression;
		if (objectReference == null || objectReference.isEmpty()) {
			this.objectReference = DEFAULT_OBJECT_REFERENCE;
		} else {
			this.objectReference = objectReference;
		}
	}

	@Override
	public String generate(Object object) {
		context.setVariable(objectReference, object);
		Object value = null;
		try {
			value = parser.parseExpression(expression).getValue(context);
		} catch (SpelEvaluationException e) {
			throw new ParserConfigException(e);
		}
		if (value == null) {
			throw new ParserConfigException("Key generation for " + object + " failed for expression " + expression);
		}
		return value.toString();
	}

}
