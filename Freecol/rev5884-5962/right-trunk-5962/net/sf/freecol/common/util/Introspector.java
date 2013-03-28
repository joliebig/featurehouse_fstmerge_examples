

package net.sf.freecol.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public class Introspector {

    
    private Class<?> theClass;

    
    private String field;

    
    public Introspector(Class<?> theClass, String field)
        throws IllegalArgumentException {
        if (field == null || field.length() == 0) {
            throw new IllegalArgumentException("Field may not be empty");
        }
        this.theClass = theClass;
        this.field = field;
    }

    
    private Method getGetMethod()
        throws IllegalArgumentException {
        String methodName = "get" + field.substring(0, 1).toUpperCase()
            + field.substring(1);

        try {
            return theClass.getMethod(methodName);
        } catch (Exception e) {
            throw new IllegalArgumentException(theClass.getName()
                                               + "." + methodName
                                               + ": " + e.toString());
        }
    }

    
    private Method getSetMethod(Class argType)
        throws IllegalArgumentException {
        String methodName = "set" + field.substring(0, 1).toUpperCase()
            + field.substring(1);

        try {
            return theClass.getMethod(methodName, argType);
        } catch (Exception e) {
            throw new IllegalArgumentException(theClass.getName()
                                               + "." + methodName
                                               + ": " + e.toString());
        }
    }

    
    private Class<?> getMethodReturnType(Method method)
        throws IllegalArgumentException {
        Class<?> ret;

        try {
            ret = method.getReturnType();
        } catch (Exception e) {
            throw new IllegalArgumentException(theClass.getName()
                                               + "." + method.getName()
                                               + " return type: "
                                               + e.toString());
        }
        return ret;
    }

    
    private Method getToStringConverter(Class<?> argType)
        throws IllegalArgumentException {
        Method method;

        if (argType.isEnum()) {
            try {
                method = argType.getMethod("name", (Class<?>[]) null);
            } catch (Exception e) {
                throw new IllegalArgumentException(argType.getName()
                                                   + ".getMethod(name()): "
                                                   + e.toString());
            }
        } else {
            try {
                method = String.class.getMethod("valueOf", argType);
            } catch (Exception e) {
                throw new IllegalArgumentException("String.getMethod(valueOf("
                                                   + argType.getName()
                                                   + ")): " + e.toString());
            }
        }
        return method;
    }

    
    private Method getFromStringConverter(Class<?> argType)
        throws IllegalArgumentException {
        Method method;

        if (argType.isEnum()) {
            try {
                method = Enum.class.getMethod("valueOf", Class.class, String.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Enum.getMethod(valueOf(Class, String)): "
                                                   + e.toString());
            }
        } else {
            if (argType.isPrimitive()) {
                if (argType == Integer.TYPE) {
                    argType = Integer.class;
                } else if (argType == Boolean.TYPE) {
                    argType = Boolean.class;
                } else if (argType == Float.TYPE) {
                    argType = Float.class;
                } else if (argType == Double.TYPE) {
                    argType = Double.class;
                } else if (argType == Character.TYPE) {
                    argType = Character.class;
                } else {
                    throw new IllegalArgumentException("Need compatible class for primitive " + argType.getName());
                }
            }
            try {
                method = argType.getMethod("valueOf", String.class);
            } catch (Exception e) {
                throw new IllegalArgumentException(argType.getName()
                                                   + ".getMethod(valueOf(String)): "
                                                   + e.toString());
            }
        }
        return method;
    }

    
    public String getter(Object obj)
        throws IllegalArgumentException {
        Method getMethod = getGetMethod();
        Class<?> fieldType = getMethodReturnType(getMethod);

        if (fieldType == String.class) {
            try {
                return (String) getMethod.invoke(obj);
            } catch (Exception e) {
                throw new IllegalArgumentException(getMethod.getName()
                                                   + "(obj)): "
                                                   + e.toString());
            }
        } else {
            Object result = null;
            try {
                result = getMethod.invoke(obj);
            } catch (Exception e) {
                throw new IllegalArgumentException(getMethod.getName()
                                                   + "(obj): "
                                                   + e.toString());
            }
            Method convertMethod = getToStringConverter(fieldType);
            try {
                return (String) convertMethod.invoke(null, result);
            } catch (Exception e) {
                throw new IllegalArgumentException(convertMethod.getName()
                                                   + "(null, result): "
                                                   + e.toString());
            }
        }
    }

    
    public void setter(Object obj, String value)
        throws IllegalArgumentException {
        Method getMethod = getGetMethod();
        Class<?> fieldType = getMethodReturnType(getMethod);
        Method setMethod = getSetMethod(fieldType);

        if (fieldType == String.class) {
            try {
                setMethod.invoke(obj, value);
            } catch (Exception e) {
                throw new IllegalArgumentException(setMethod.getName()
                                                   + "(obj, " + value + ")): "
                                                   + e.toString());
            }
        } else {
            Method convertMethod = getFromStringConverter(fieldType);
            Object result = null;

            if (fieldType.isEnum()) {
                try {
                    result = convertMethod.invoke(null, fieldType, value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(convertMethod.getName()
                                                       + "(null, " + fieldType.getName()
                                                       + ", " + value + "):"
                                                       + e.toString());
                }
            } else {
                try {
                    result = convertMethod.invoke(null, value);
                } catch (Exception e) {
                    throw new IllegalArgumentException(convertMethod.getName()
                                                       + "(null, " + value + "):"
                                                       + e.toString());
                }
            }
            try {
                setMethod.invoke(obj, result);
            } catch (Exception e) {
                throw new IllegalArgumentException(setMethod.getName()
                                                   + "(result): "
                                                   + e.toString());
            }
        }
    }
}
