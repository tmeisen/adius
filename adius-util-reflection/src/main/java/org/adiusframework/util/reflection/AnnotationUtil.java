package org.adiusframework.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AnnotationUtil class contains several static methods to help accessing
 * Annotations.
 */
public class AnnotationUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationUtil.class);

	/**
	 * Determines the first Annotation of a given class that is applied to a
	 * given type or one of it's super-types.
	 * 
	 * @param clazz
	 *            The root of the searched super-type-structure.
	 * @param annotationClass
	 *            The class of the searched Annotation.
	 * @return The first Annotation that was found, or null is no Annotation was
	 *         found.
	 */
	public static Annotation getAnnotationInHierarchy(Class<?> clazz, Class<? extends Annotation> annotationClass) {

		// if class is null return null, because no hierarchy can be searched
		if (clazz == null)
			return null;

		// check if current class has annotation
		Annotation annotation = clazz.getAnnotation(annotationClass);
		if (annotation != null)
			return annotation;
		return AnnotationUtil.getAnnotationInHierarchy(clazz.getSuperclass(), annotationClass);
	}

	/**
	 * Determines all methods of a class that have a special Annotation.
	 * 
	 * @param clazz
	 *            The class that should be searched through.
	 * @param annotationClass
	 *            The class of the searched Annotation.
	 * @return A List with all found methods.
	 */
	public static List<Method> findMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {

		// check all public methods
		List<Method> methods = new Vector<Method>();
		for (Method method : clazz.getMethods())
			if (method.getAnnotation(annotationClass) != null)
				methods.add(method);
		return methods;
	}

	/**
	 * Determines all fields of a class that have a special Annotation. Moreover
	 * you can specify, that a special class of Annotations is ignored and
	 * whether super-types of the given type should be searched, too.
	 * 
	 * @param clazz
	 *            The class that should be searched through.
	 * @param annotationClass
	 *            The class of the searched Annotation.
	 * @param restrictionClass
	 *            The class of ignored Annotations.
	 * @param recursive
	 *            True, if super-types should be searched, too.
	 * @return A List with all found methods.
	 */
	public static List<Field> findFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass,
			Class<? extends Annotation> restrictionClass, boolean recursive) {
		List<Field> list = new Vector<Field>();
		AnnotationUtil.findFieldsWithAnnotation(clazz, annotationClass, restrictionClass, recursive, list);
		return list;
	}

	/**
	 * Determines all fields of a class that have a special Annotation. Moreover
	 * you can specify, whether super-types of the given type should be
	 * searched, too.
	 * 
	 * @param clazz
	 *            The class that should be searched through.
	 * @param annotationClass
	 *            The class of the searched Annotation.
	 * @param recursive
	 *            True, if super-types should be searched, too.
	 * @return A List with all found methods.
	 */
	public static List<Field> findFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass,
			boolean recursive) {
		return AnnotationUtil.findFieldsWithAnnotation(clazz, annotationClass, null, recursive);
	}

	/**
	 * Determines all fields of a class that have a special Annotation and adds
	 * them to a given List. Moreover you can specify, whether super-types of
	 * the given type should be searched, too.
	 * 
	 * @param clazz
	 *            The class that should be searched through.
	 * @param annotationClass
	 *            The class of the searched Annotation
	 * @param recursive
	 *            True, if super-types should be searched, too.
	 * @param list
	 *            The List where the found Fields are added.
	 */
	public static void findFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass,
			boolean recursive, List<Field> list) {
		AnnotationUtil.findFieldsWithAnnotation(clazz, annotationClass, null, recursive, list);
	}

	/**
	 * Determines all fields of a class that have a special Annotation and adds
	 * them to a given List. Moreover you can specify, that a special class of
	 * Annotations is ignored and whether super-types of the given type should
	 * be searched, too.
	 * 
	 * @param clazz
	 *            The class that should be searched through.
	 * @param annotationClass
	 *            The class of the searched Annotation
	 * @param restrictionClass
	 *            The class of ignored Annotations.
	 * @param recursive
	 *            True, if super-types should be searched, too.
	 * @param list
	 *            The List where the found Fields are added.
	 */
	public static void findFieldsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass,
			Class<? extends Annotation> restrictionClass, boolean recursive, List<Field> list) {

		// check all fields
		if (clazz == null)
			return;

		// find all declared fields of class and check if annotation is present
		// and restriction is not present
		for (Field field : clazz.getDeclaredFields())
			if (field.getAnnotation(annotationClass) != null
					&& (restrictionClass == null || field.getAnnotation(restrictionClass) == null))
				list.add(field);

		// if recursive is set, proceed with superclass
		if (recursive) {
			AnnotationUtil.findFieldsWithAnnotation(clazz.getSuperclass(), annotationClass, restrictionClass,
					recursive, list);
		}
	}

	/**
	 * Determines the getter of a given member if it's visibly is not public.
	 * 
	 * @param clazz
	 *            The class where a possible getter is defined.
	 * @param member
	 *            The member which's getter is searched.
	 * @return The member itself, if it's visibly is public, the getter
	 *         otherwise.
	 */
	public static Member findAccessibleOfMember(Class<?> clazz, Member member) {
		if (member.getModifiers() == Modifier.PUBLIC)
			return member;
		else if (Field.class.isInstance(member))
			return AnnotationUtil.findGetterMethodOfField(clazz, Field.class.cast(member));
		else
			throw new EntityNotFoundException("No member found for accessing " + member.getName());
	}

	/**
	 * Determines the getter-method of a given field in a given class.
	 * 
	 * @param clazz
	 *            The given class.
	 * @param field
	 *            The given field.
	 * @return The method-object of the getter.
	 */
	public static Method findGetterMethodOfField(Class<?> clazz, Field field) {

		// create the method name
		String fieldName = field.getName();
		String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1, fieldName.length());

		// search for the method
		try {
			Method method = clazz.getMethod(methodName);
			if (method.getModifiers() != Modifier.PUBLIC)
				throw new EntityNotFoundException("The getter " + methodName + " of the field " + field.getName()
						+ " cannot be accessed.");
			return method;
		} catch (SecurityException e) {
			throw new EntityNotFoundException("The getter " + methodName + " of the field " + field.getName()
					+ " cannot be accessed.");
		} catch (NoSuchMethodException e) {
			throw new EntityNotFoundException("The getter " + methodName + " of the field " + field.getName() + " in "
					+ clazz.toString() + " has not been found.");
		}
	}

	/**
	 * Reads the data that are associated with a given member of a given object.
	 * 
	 * @param member
	 *            The given member.
	 * @param dataObject
	 *            The given object.
	 * @return A object which represents the stored data.
	 */
	public static Object readMemberData(Member member, Object dataObject) {
		if (Field.class.isInstance(member))
			return AnnotationUtil.readFieldData(Field.class.cast(member), dataObject);
		else if (Method.class.isInstance(member))
			return AnnotationUtil.readMethodData(Method.class.cast(member), dataObject);
		else {
			LOGGER.error("Member data could not be read, because neither field nor method has been given.");
			return null;
		}
	}

	/**
	 * Reads the data that are associated with a given field of a given object.
	 * 
	 * @param field
	 *            The given field.
	 * @param dataObject
	 *            The given object.
	 * @return The data of the field.
	 */
	public static Object readFieldData(Field field, Object dataObject) {
		Object data = null;
		try {
			data = field.get(dataObject);
		} catch (IllegalArgumentException e) {
			LOGGER.error("IllegalArgumentException in readFieldData: " + e.getMessage());
			throw new ReflectionRuntimeException(e, "IllegalArgumentException in readFieldData");
		} catch (IllegalAccessException e) {
			LOGGER.error("IllegalAccessException in readFieldData: " + e.getMessage());
			throw new ReflectionRuntimeException(e, "IllegalAccessException in readFieldData");
		}
		return data;
	}

	/**
	 * Returns the return-value that is returned by the invocation of a given
	 * method on a given object with special arguments.
	 * 
	 * @param method
	 *            The given method.
	 * @param dataObject
	 *            The given object.
	 * @param args
	 *            The arguments for invoking.
	 * @return The return-value of the method.
	 */
	public static Object readMethodData(Method method, Object dataObject, Object... args) {
		Object data = null;
		try {
			data = method.invoke(dataObject, args);
		} catch (IllegalArgumentException e) {
			LOGGER.error("IllegalArgumentException in readFieldData: " + e.getMessage());
			throw new ReflectionRuntimeException(e, "IllegalArgumentException in readMethodData");
		} catch (IllegalAccessException e) {
			LOGGER.error("IllegalAccessException in readFieldData: " + e.getMessage());
			throw new ReflectionRuntimeException(e, "IllegalAccessException in readMethodData");
		} catch (InvocationTargetException e) {
			LOGGER.error("InvocationTargetException in readFieldData: " + e.getMessage());
			throw new ReflectionRuntimeException(e, "InvocationTargetException in readMethodData");
		}
		return data;
	}

	/**
	 * Determines if an object has a special Annotation.
	 * 
	 * @param object
	 *            The object.
	 * @param annotationClass
	 *            The class of the Annotation.
	 * @return True if it has, false otherwise.
	 */
	public static boolean hasAnnotation(AccessibleObject object, Class<? extends Annotation> annotationClass) {
		return (object.getAnnotation(annotationClass) != null);
	}
	
	/**
	 * Determines if a class has a special Annotation.
	 * 
	 * @param clazz
	 *            The class.
	 * @param annotationClass
	 *            The class of the Annotation.
	 * @return True if it has, false otherwise.
	 */
	public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		return (clazz.getAnnotation(annotationClass) != null);
	}
}
