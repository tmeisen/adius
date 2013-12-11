package org.adiusframework.ontology.exception;

import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObject;

public class OWLInvalidContentException extends OWLException {

	private static final long serialVersionUID = -6172651287052940496L;

	public OWLInvalidContentException(String message) {
		super(message);
	}

	public OWLInvalidContentException(String message, OWLObject object) {
		super(message, object);
	}

	public OWLInvalidContentException(String message, OWLNamedObject object) {
		super(message, object);
	}

}
