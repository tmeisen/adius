package org.adiusframework.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a service is marked with this interface, it means that some native
 * libraries have to be executed, before the service is called.
 * 
 * @author tmeisen
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JniDependend {

	/**
	 * Name of the loader used to initialize the native libraries of this
	 * service.
	 */
	public String jniLoader() default "jniLoader";
}
