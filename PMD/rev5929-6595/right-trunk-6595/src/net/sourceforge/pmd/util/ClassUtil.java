package net.sourceforge.pmd.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public final class ClassUtil {

    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    private ClassUtil() {
    };

    @SuppressWarnings("PMD.AvoidUsingShortType")
    private static final TypeMap PRIMITIVE_TYPE_NAMES = new TypeMap(new Class[] { int.class, byte.class, long.class,
            short.class, float.class, double.class, char.class, boolean.class, });

    private static final TypeMap TYPES_BY_NAME = new TypeMap(new Class[] { Integer.class, Byte.class, Long.class,
            Short.class, Float.class, Double.class, Character.class, Boolean.class, BigDecimal.class, String.class,
            Object.class, });

    
    public static Class<?> getPrimitiveTypeFor(String name) {
        return PRIMITIVE_TYPE_NAMES.typeFor(name);
    }

    
    public static Map<Class, String> getClassShortNames() {
        Map<Class, String> map = new HashMap<Class, String>();
        map.putAll(PRIMITIVE_TYPE_NAMES.asInverseWithShortName());
        map.putAll(TYPES_BY_NAME.asInverseWithShortName());
        return map;
    }

    
    public static Class<?> getTypeFor(String shortName) {
        Class<?> type = TYPES_BY_NAME.typeFor(shortName);
        if (type != null) {
            return type;
        }

        type = PRIMITIVE_TYPE_NAMES.typeFor(shortName);
        if (type != null) {
            return type;
        }

        return CollectionUtil.getCollectionTypeFor(shortName);
    }

    

    public static String withoutPackageName(String fullTypeName) {
        int dotPos = fullTypeName.lastIndexOf('.');
        return dotPos > 0 ? fullTypeName.substring(dotPos + 1) : fullTypeName;
    }

    
    public static Method methodFor(Class clasz, String methodName, Class[] paramTypes) {
        Method method = null;
        Class current = clasz;
        while (current != Object.class) {
            try {
                method = current.getDeclaredMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                current = current.getSuperclass();
            }
            if (method != null) {
                return method;
            }
        }
        return null;
    }
}
