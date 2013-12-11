package org.adiusframework.resource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If this annotation is present on a resource class the resource represented by
 * this class, is only valid on the source system and cannot be used on a
 * different system (e.g. a local file).
 * 
 * @author tm807416
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Transient {
	// Marker-interface
}
