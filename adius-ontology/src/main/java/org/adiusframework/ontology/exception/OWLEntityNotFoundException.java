package org.adiusframework.ontology.exception;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

public class OWLEntityNotFoundException extends OWLException {

	private static final long serialVersionUID = 3889810434760024687L;

	public OWLEntityNotFoundException(String message) {
		super(message);
	}

	public OWLEntityNotFoundException(String message, OWLObject object) {
		super(message, object);
	}

	public OWLEntityNotFoundException(String message, OWLNamedObject object) {
		super(message, object);
	}

}
