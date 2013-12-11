package org.adiusframework.util.reflection;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.typetools.TypeResolver;

/**
 * The PackageClassLoader is a loader that can be used to find a list of classes
 * that implements a specific interface or are a subclass of a specific class.
 */
public abstract class PackageClassLoader<T> extends PackageScanner {
	private static final Logger LOGGER = LoggerFactory.getLogger(PackageClassLoader.class);

	/**
	 * The type of the class which should be a super-type of the checked
	 * classes.
	 */
	private Class<?> typeClass;

	/**
	 * Constructor of the PackageClassLoader
	 * 
	 * @param packages
	 *            list of package names that have to be scanned.
	 */
	public PackageClassLoader(String... packages) {
		super(packages);

		// identify the template class
		typeClass = TypeResolver.resolveArguments(getClass(), PackageClassLoader.class)[0];
		LOGGER.debug("Type class identified as " + typeClass.getName());

		// find all classes in the defined package and handle it
		List<Class<?>> classes = scan();
		LOGGER.debug("Scan found " + classes.size() + " candidates");
		handleClasses(classes);
	}

	/**
	 * For every class in the package this method is called to validate if the
	 * class matches the filter criteria.
	 * 
	 * @param c
	 *            Class that have to be checked
	 * @return true if the class has to be handled, false otherwise
	 */
	protected abstract boolean validateClass(Class<?> c);

	/**
	 * Method called after all relevant classes, regarding the filter criteria,
	 * have been found.
	 * 
	 * @param classes
	 *            Classes that have been found during the package scan
	 */
	protected abstract void handleClasses(List<Class<?>> classes);

	@Override
	protected Class<?> validateMetadata(MetadataReader metadataReader) {
		try {
			Class<?> c = Class.forName(metadataReader.getClassMetadata().getClassName());
			LOGGER.debug("Validating class " + c);
			if (typeClass.isAssignableFrom(c) && validateClass(c))
				return c;
			return null;
		} catch (Throwable e) {
			return null;
		}
	}
}
