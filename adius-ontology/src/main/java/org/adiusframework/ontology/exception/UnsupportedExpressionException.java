package org.adiusframework.ontology.exception;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

public class UnsupportedExpressionException extends OWLException {

	private static final long serialVersionUID = -5621221430669681567L;

	public UnsupportedExpressionException(String message) {
		super(message);
	}

	public UnsupportedExpressionException(String message, OWLObject object) {
		super(message, object);
	}

	public UnsupportedExpressionException(String message, OWLNamedObject object) {
		super(message, object);
	}

}
