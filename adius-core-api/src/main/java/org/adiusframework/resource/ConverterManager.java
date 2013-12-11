package org.adiusframework.resource;

import java.util.List;

public interface ConverterManager {

	/**
	 * The method determines a list of converters that can be used to convert a
	 * given resource class specified by the from parameter.
	 * 
	 * @param from
	 *            specifies the resource that is supported by the converter as
	 *            input
	 * @param noneTransientOnly
	 *            set to true if only none transient converters has to be
	 *            returned
	 * @return list of converters that accepts a resource of the class,
	 *         specified in the from parameter, as input
	 */
	public List<Converter<? extends Resource, ? extends Resource>> getConvertersFrom(Class<? extends Resource> from,
			boolean noneTransientOnly);

	/**
	 * The method determines a list of converters that can be used to convert a
	 * given resource of the specified class into a resource of the specified
	 * class.
	 * 
	 * @param from
	 *            the class of the existing resource
	 * @param to
	 *            the class of the required resource
	 * @param noneTransientOnly
	 *            set to true if only none transient converters has to be
	 *            returned
	 * @return list of converters that fulfills the given task
	 */
	public List<Converter<? extends Resource, ? extends Resource>> getConverters(Class<? extends Resource> from,
			Class<? extends Resource> to, boolean noneTransientOnly);

	/**
	 * The method determines a list of converters that can be used to convert
	 * into a given resource class. The resource class is specified in the to
	 * parameter.
	 * 
	 * @param to
	 *            resource class that is supported by the converter as output.
	 * @param noneTransientOnly
	 *            set to true if only none transient converters has to be
	 *            returned
	 * @return list of converters that accepts a resource of the class,
	 *         specified in the from parameter, as output.
	 */
	public List<Converter<? extends Resource, ? extends Resource>> getConvertersTo(Class<? extends Resource> to,
			boolean noneTransientOnly);

}
