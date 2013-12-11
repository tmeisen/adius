package org.adiusframework.util.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Library containing static methods to help using the reflection API of java.
 */
public class ReflectionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtil.class);

	/**
	 * Constant defining the pattern to parse a class definition
	 */
	public static final Pattern CLASS_PARAM_PATTERN = Pattern
			.compile("^([^\\(\\)\\\"]+)(?:(?:\\((?:\\s*(?:\"((?:[^\"]|\\\\\")*)\")\\s*)(?:,(?:\\s*(?:\"((?:[^\"]|\\\\\")*)\")\\s*))*\\))|\\(\\))$");

	/**
	 * Number of group containing the class name
	 */
	private static final int GROUP_NUMBER_CLASS = 1;

	/**
	 * First group number containing params
	 */
	private static final int GROUP_NUMBER_PARAM = 2;

	/**
	 * Creates a new instance of the given class using the standard constructor.
	 * 
	 * @param c
	 *            Class whose instance has to be created.
	 * @return the generated instance.
	 * @throws ReflectionException
	 *             if the creation of the object failed.
	 */
	public static <T> T newInstance(Class<? extends T> c) throws ReflectionException {
		try {
			return c.getConstructor().newInstance();
		}
		// catch exceptions during reflection creation
		catch (Exception e) {
			LOGGER.error("Error in reflecting util: " + e.getMessage());
			throw new ReflectionException("Class " + c + " could not be created.", e);
		}
	}

	/**
	 * Creates a new instance for every class that is given in a List.
	 * 
	 * @param classes
	 *            The List of classes which should be instantiated.
	 * @return A List with the created objects.
	 */
	public static <T> List<T> newInstances(List<Class<? extends T>> classes) {
		if (classes == null)
			return new Vector<T>();

		List<T> instances = new Vector<T>();
		for (Class<? extends T> c : classes) {
			try {
				instances.add(ReflectionUtil.newInstance(c));
			} catch (ReflectionException e) {
				LOGGER.error("Error creating instance of class " + c.getName());
			}
		}
		return instances;
	}

	/**
	 * Creates a new instance using the given definition. The definition has to
	 * match the regular expression pattern. The automatic generation using
	 * parameters only works for string parameters.
	 * 
	 * @param definition
	 *            Definition used to create the new instance
	 * @return created object
	 * @throws ParseException
	 *             if the provided definition does not match the pattern
	 *             definition
	 * @throws ReflectionException
	 *             if class creation failed
	 */
	public static Object newInstance(String definition) throws ParseException, ReflectionException {

		// get class name and parameter list
		Matcher classMatcher = CLASS_PARAM_PATTERN.matcher(definition);
		if (classMatcher.matches()) {
			String clazz = classMatcher.group(GROUP_NUMBER_CLASS);
			try {
				if (classMatcher.groupCount() > GROUP_NUMBER_CLASS) {

					// process first param
					List<String> values = new Vector<String>();
					values.add(classMatcher.group(GROUP_NUMBER_PARAM));

					// process additional params
					while (classMatcher.group(3) != null) {
						values.add(classMatcher.group(GROUP_NUMBER_PARAM + 1));
						if (!classMatcher.find())
							break;
					}
					Class<?>[] classes = new Class<?>[values.size()];
					for (int i = 0; i < values.size(); i++)
						classes[i] = String.class;

					// create complex object using matching constructor
					LOGGER.debug("Creating complex class " + clazz + " with params " + values.toString());
					return Class.forName(clazz).getConstructor(classes).newInstance(values.toArray());
				}

				// create a simple object using default constructor
				return Class.forName(clazz).getConstructor().newInstance();
			}

			// catch exceptions during reflection creation
			catch (Exception e) {
				LOGGER.error("Error in reflecting util: " + e.getMessage());
				throw new ReflectionException("Class " + clazz + " could not be created.", e);
			}
		}
		throw new ParseException("Definition does not match regexp pattern.", 0);
	}

	/**
	 * Calls a special method of an object with given parameters and returns
	 * it's return-value. Moreover it wraps all Exceptions in a new
	 * ReflectionRuntimeException.
	 * 
	 * @param obj
	 *            The object.
	 * @param m
	 *            The method to be called.
	 * @param params
	 *            The parameters for the call.
	 * @return The return-value of the call.
	 */
	public static Object callMethod(Object obj, Method m, Object... params) {
		try {
			LOGGER.debug("Invoking method " + m + " on " + obj);
			Object r = m.invoke(obj, params);
			LOGGER.debug("Result of method call is " + r);
			return r;
		} catch (Exception e) {
			throw new ReflectionRuntimeException(e, "Invokation of method failed");
		}
	}

	/**
	 * Creates the name of the getter-method out of a given field.
	 * 
	 * @param f
	 *            The field to be converted.
	 * @return The name of the getter.
	 */
	public static String getGetterName(Field f) {
		return new StringBuilder().append("get").append(String.valueOf(f.getName().charAt(0)).toUpperCase())
				.append(f.getName().substring(1)).toString();
	}

	/**
	 * Creates the name of the setter-method out of a given field.
	 * 
	 * @param f
	 *            The field to be converted.
	 * @return The name of the setter.
	 */
	public static String getSetterName(Field f) {
		return new StringBuilder().append("set").append(String.valueOf(f.getName().charAt(0)).toUpperCase())
				.append(f.getName().substring(1)).toString();

	}

	/**
	 * Determines the method with the given name of a special class and given
	 * parameter-types and returns it.
	 * 
	 * @param clazz
	 *            The class of the method.
	 * @param methodName
	 *            The name of the method.
	 * @param types
	 *            The parameters-types.
	 * @return The method or null if an exception is thrown.
	 */
	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... types) {
		try {
			return clazz.getMethod(methodName, types);
		} catch (SecurityException e) {
			LOGGER.error("Security exception accessing " + methodName + " (" + e.getMessage() + ")");
			return null;
		} catch (NoSuchMethodException e) {
			LOGGER.error("No method of the name " + methodName + " with paramter types " + types + " found");
			return null;
		}
	}

	/**
	 * Determines the getter-method based on a given setter-method for a special
	 * class.
	 * 
	 * @param clazz
	 *            The class of the two methods.
	 * @param m
	 *            The setter-method.
	 * @return The getter or null if the given method is no setter or an
	 *         exception is thrown.
	 */
	public static Method getGetterBySetter(Class<?> clazz, Method m) {
		if (!isSetter(m))
			return null;
		String getterName = new StringBuilder("get").append(m.getName().substring(3)).toString();
		try {
			LOGGER.debug("Locking for getter method " + getterName);
			return clazz.getMethod(getterName, (Class<?>[]) null);
		} catch (SecurityException e) {
			LOGGER.error("Security exception accessing " + getterName + " (" + e.getMessage() + ")");
			return null;
		} catch (NoSuchMethodException e) {
			LOGGER.error("No method of the name " + getterName + " found");
			return null;
		}
	}

	/**
	 * Determines the setter-method based on a given getter-method for a special
	 * class.
	 * 
	 * @param clazz
	 *            The class of the two methods.
	 * @param m
	 *            The getter-method.
	 * @return The setter or null if the given method is no setter or an
	 *         exception is thrown.
	 */
	public static Method getSetterByGetter(Class<?> clazz, Method m) {
		if (!isGetter(m))
			return null;
		String setterName = new StringBuilder("set").append(m.getName().substring(3)).toString();
		try {
			LOGGER.debug("Locking for setter method " + setterName + " with parameter " + m.getReturnType());
			return clazz.getMethod(setterName, m.getReturnType());
		} catch (SecurityException e) {
			LOGGER.error("Security exception accessing " + setterName + " (" + e.getMessage() + ")");
			return null;
		} catch (NoSuchMethodException e) {
			LOGGER.error("No method of the name " + setterName + " with parameter typ " + m.getReturnType() + " found");
			return null;
		}
	}

	/**
	 * Determines if a given method is a getter-method by checking it's name and
	 * that it's return-value is not void.
	 * 
	 * @param m
	 *            The method to be checked.
	 * @return True if the method is a getter, false otherwise.
	 */
	public static boolean isGetter(Method m) {
		return m.getName().startsWith("get") && m.getReturnType() != null;
	}

	/**
	 * Determines if a given method is a getter-method by checking it's name and
	 * that it has exactly one parameter.
	 * 
	 * @param m
	 *            The method to be checked.
	 * @return True if the method is a setter, false otherwise.
	 */
	public static boolean isSetter(Method m) {
		return m.getName().startsWith("set") && m.getParameterTypes().length == 1;
	}
}
