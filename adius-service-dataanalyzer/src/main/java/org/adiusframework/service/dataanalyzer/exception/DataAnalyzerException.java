package org.adiusframework.service.dataanalyzer.exception;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

public class DataAnalyzerException extends Exception {
	/**
     * 
     */
	private static final long serialVersionUID = 1666968630265390281L;

	public DataAnalyzerException(String message) {
		super(message);
	}

	public DataAnalyzerException(String message, OWLObject object) {
		super(message + " - " + object.toString());
	}

	public DataAnalyzerException(String message, OWLNamedObject object) {
		super(message + " - " + object.getIRI().getFragment());
	}
}
