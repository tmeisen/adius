package org.adiusframework.util.reflection;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * The PackageScanner offers the possibility to specific several packages scan
 * them. That means that all classes, which match special conditions, are
 * determined.
 */
public abstract class PackageScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);

	/**
	 * Stores the package that should be scanned.
	 */
	private String[] packages;

	public PackageScanner(String... packages) {
		setPackages(packages);
	}

	/**
	 * Sets the packages that should be scanned.
	 * 
	 * @param packages
	 *            The packages to be scanned.
	 */
	public void setPackages(String[] packages) {
		this.packages = packages;
	}

	/**
	 * Returns the packages that will be scanned.
	 * 
	 * @return The package to be scanned.
	 */
	public String[] getPackages() {
		return packages;
	}

	/**
	 * Checks if a class, which is accessed by a MetaDataReader, fulfills
	 * special conditions.
	 * 
	 * @param metadataReader
	 *            The MetadataReader to access the class.
	 * @return The class itself it it fulfills, null otherwise.
	 */
	protected abstract Class<?> validateMetadata(MetadataReader metadataReader);

	/**
	 * Scans all packages that are set to be scanned and test if the classes in
	 * this packages fulfills the conditions, that are specified by overwriting
	 * the method {@link #validateMetadata(MetadataReader)}.
	 * 
	 * @return A List with all classes that match the conditions.
	 */
	protected List<Class<?>> scan() {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

		try {
			List<Class<?>> classes = new Vector<Class<?>>();

			for (String pckg : getPackages()) {
				LOGGER.debug("Start scanning of " + pckg);
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(pckg)
						+ "/" + "**/*.class";
				org.springframework.core.io.Resource[] resources = resourcePatternResolver
						.getResources(packageSearchPath);
				for (org.springframework.core.io.Resource resource : resources) {
					if (resource.isReadable()) {
						MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
						Class<?> c = validateMetadata(metadataReader);
						if (c != null) {
							LOGGER.debug("Scan found class " + c.getName());
							classes.add(c);
						}
					}
				}
			}
			return classes;
		} catch (IOException e) {
			LOGGER.error("IOException: " + e.getMessage() + " during package scanning");
		}
		return null;
	}

	/**
	 * Converts a name of a given package to the path which points to the
	 * addressed resources.
	 * 
	 * @param basePackage
	 *            The name of the package.
	 * @return The path to the resources.
	 */
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
	}

}
