package org.adiusframework.web.applicationlauncher.shared;

/**
 * Class that has utility custom method for String handling,
 * that will be available for all WAL.
 * 
 * @author Tobias Meisen
 */
public class StringUtils {

	/** 
	 * Validates if all the important attributes for defining an application are assigned. 
	 * 
	 * @param str
	 * 		  String wanted to know if it is empty or not.
	 * @return False if the string was null, or, not empty, true otherwise.
	 */	
	public static boolean isNotEmpty(String str) {
		return (str != null) && !str.isEmpty();
	} // end : isNotEmpty method 	

}