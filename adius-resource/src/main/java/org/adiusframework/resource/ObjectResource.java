package org.adiusframework.resource;

import java.io.Serializable;

public class ObjectResource<T extends Serializable> extends AbstractResource {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -7293079007817308148L;

	private T object;

	public ObjectResource() {
		setType(Resource.TYPE_OBJECT);
	}

	public ObjectResource(T object) {
		this();
		setObject(object);
	}

	public void setObject(T object) {
		this.object = object;
	}

	public T getObject() {
		return object;
	}

}
