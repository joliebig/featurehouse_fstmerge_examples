package net.sourceforge.pmd.properties;

import java.lang.reflect.Method;


public class MethodProperty extends AbstractPMDProperty {
	
	
	public MethodProperty(String theName, String theDescription, Object theDefault, float theUIOrder) {
		super(theName, theDescription, theDefault, theUIOrder);
	}

	
	public Class<Method> type() {
		return Method.class;
	}

	
	public Object valueFrom(String propertyString) throws IllegalArgumentException {
		
		Class<?> cls = classIn(propertyString);
		String methodName = methodNameIn(propertyString);
		Class[] parameterTypes = parameterTypesIn(propertyString);
		
		try {
			return cls.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			throw new IllegalArgumentException("invalid method: " + propertyString);
		}
	}

	private Class<?> classIn(String propertyString) throws IllegalArgumentException {
		
		int dotPos = propertyString.lastIndexOf('.');
		String className = propertyString.substring(0, dotPos);
		
		try {
			return Class.forName(className);
			} catch (Exception ex) {
				throw new IllegalArgumentException("class not found: " + className);
			}
	}
	
	private String methodNameIn(String propertyString) throws IllegalArgumentException {
		
		int dotPos = propertyString.lastIndexOf('.');
		return propertyString.substring(dotPos);
	}
	
	private Class[] parameterTypesIn(String propertyString) {
		return null;
	}
}
