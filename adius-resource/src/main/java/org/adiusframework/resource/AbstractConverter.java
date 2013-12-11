package org.adiusframework.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typetools.TypeResolver;

public abstract class AbstractConverter<F extends Resource, T extends Resource> implements Converter<F, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);

	@SuppressWarnings("unchecked")
	@Override
	public Class<F> getClassFrom() {
		return (Class<F>) TypeResolver.resolveArguments(this.getClass(), Converter.class)[0];
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getClassTo() {
		return (Class<T>) TypeResolver.resolveArguments(this.getClass(), Converter.class)[1];
	}

	@Override
	public Resource convert(Resource from, Resource to) {
		if (!getClassFrom().isInstance(from))
			throw new UnsupportedOperationException("The resource class " + from.getClass()
					+ " is not supported by this converter.");
		if (!getClassTo().isInstance(to))
			throw new UnsupportedOperationException("The resource class " + to.getClass()
					+ " is not supported by this converter.");
		LOGGER.debug("Converting " + from + " to " + to);
		return typeSafeConvert(getClassFrom().cast(from), getClassTo().cast(to));
	}

	protected abstract T typeSafeConvert(F from, T to);

}
