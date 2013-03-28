
package net.sourceforge.pmd.dcd;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ClassLoaderUtil {

	public static final String CLINIT = "<clinit>";

	public static final String INIT = "<init>";

	public static String fromInternalForm(String internalForm) {
		return internalForm.replace('/', '.');
	}

	public static String toInternalForm(String internalForm) {
		return internalForm.replace('.', '/');
	}

	public static Class getClass(String name) {
		try {
			return ClassLoaderUtil.class.getClassLoader().loadClass(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Field getField(Class type, String name) {
		try {
			return myGetField(type, name);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	private static Field myGetField(Class type, String name) throws NoSuchFieldException {
		
		
		try {
			return type.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			
			for (Class superInterface : type.getInterfaces()) {
				try {
					return myGetField(superInterface, name);
				} catch (NoSuchFieldException e2) {
					
				}
			}
			
			if (type.getSuperclass() != null) {
				return myGetField(type.getSuperclass(), name);
			} else {
				throw new NoSuchFieldException(type.getName() + "." + name);
			}
		}
	}

	public static Method getMethod(Class type, String name, Class... parameterTypes) {
		try {
			return myGetMethod(type, name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	private static Method myGetMethod(Class type, String name, Class... parameterTypes) throws NoSuchMethodException {
		
		
		
		
		
		
		try {
			
			
			
			
			return type.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			try {
				
				if (type.getSuperclass() != null) {
					
					
					return myGetMethod(type.getSuperclass(), name, parameterTypes);
				}
			} catch (NoSuchMethodException e2) {
				
			}
			
			for (Class superInterface : type.getInterfaces()) {
				try {
					
					
					return myGetMethod(superInterface, name, parameterTypes);
				} catch (NoSuchMethodException e3) {
					
				}
			}
			throw new NoSuchMethodException(type.getName() + "." + getMethodSignature(name, parameterTypes));
		}
	}

	public static Constructor getConstructor(Class type, String name, Class... parameterTypes) {
		try {
			return type.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMethodSignature(String name, Class... parameterTypes) {
		StringBuilder builder = new StringBuilder();
		builder.append(name);
		if (!(name.equals(CLINIT) || name.equals(INIT))) {
			builder.append("(");
			if (parameterTypes != null) {
				for (int i = 0; i < parameterTypes.length; i++) {
					if (i > 0) {
						builder.append(", ");
					}
					builder.append(parameterTypes[i].getName());
				}
			}
			builder.append(")");
		}
		return builder.toString();
	}

	public static Class[] getParameterTypes(String... parameterTypeNames) {
		Class[] parameterTypes = new Class[parameterTypeNames.length];
		for (int i = 0; i < parameterTypeNames.length; i++) {
			parameterTypes[i] = getClass(parameterTypeNames[i]);
		}
		return parameterTypes;
	}

	public static boolean isOverridenMethod(Class clazz, Method method, boolean checkThisClass) {
		try {
			if (checkThisClass) {
				clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
				return true;
			}
		} catch (NoSuchMethodException e) {
		}
		
		if (clazz.getSuperclass() != null) {
			if (isOverridenMethod(clazz.getSuperclass(), method, true)) {
				return true;
			}
		}
		
		for (Class anInterface : clazz.getInterfaces()) {
			if (isOverridenMethod(anInterface, method, true)) {
				return true;
			}
		}
		return false;
	}
}
