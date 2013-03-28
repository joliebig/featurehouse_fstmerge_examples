package net.sourceforge.pmd.util;

import java.math.BigDecimal;


public class ClassUtil {

    private ClassUtil() {
    };

    @SuppressWarnings("PMD.AvoidUsingShortType")
    private static final TypeMap primitiveTypesByName = new TypeMap(new Class[] { int.class, byte.class, long.class,
	    short.class, float.class, double.class, char.class, boolean.class, });

    private static final TypeMap typesByNames = new TypeMap(new Class[] { Integer.class, Byte.class, Long.class,
	    Short.class, Float.class, Double.class, Character.class, Boolean.class, BigDecimal.class, String.class,
	    Object.class, });

    
    public static Class<?> getPrimitiveTypeFor(String name) {
	return primitiveTypesByName.typeFor(name);
    }

    
    public static Class<?> getTypeFor(String shortName) {

	Class<?> type = typesByNames.typeFor(shortName);
	if (type != null) {
	    return type;
	}

	type = primitiveTypesByName.typeFor(shortName);
	if (type != null) {
	    return type;
	}

	return CollectionUtil.getCollectionTypeFor(shortName);
    }

    

    public static String withoutPackageName(String fullTypeName) {

	int dotPos = fullTypeName.lastIndexOf('.');

	return dotPos > 0 ? fullTypeName.substring(dotPos + 1) : fullTypeName;
    }
}
