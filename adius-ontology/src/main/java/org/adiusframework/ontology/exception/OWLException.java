package org.adiusframework.ontology.exception;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

public class OWLException extends Exception {

	private static final long serialVersionUID = 1L;

	public OWLException(String message) {
		super(message);
	}

	public OWLException(String message, OWLObject object) {
		super(message + " - " + object.toString());
	}

	public OWLException(String message, OWLNamedObject object) {
		super(message + " - " + object.getIRI().getFragment());
	}

}
