
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;


public class TypeProperty extends StringProperty {

    private static final char delimiter = '|';

    
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, float theUIOrder) {
	super(theName, theDescription, theDefault, theUIOrder, delimiter);

	maxValueCount(1);
    }

    
    public TypeProperty(String theName, String theDescription, Class<?>[] theDefaults, float theUIOrder) {
	super(theName, theDescription, theDefaults, theUIOrder, delimiter);

	maxValueCount(Integer.MAX_VALUE);
    }

    
    @Override
    public Class<?> type() {
	return Class.class;
    }

    
    @Override
    protected String asString(Object value) {
	return value == null ? "" : ((Class<?>) value).getName();
    }

    
    private Class<?> classFrom(String className) {

	Class<?> cls = ClassUtil.getTypeFor(className);
	if (cls != null) {
	    return cls;
	}

	try {
	    return Class.forName(className);
	} catch (Exception ex) {
	    throw new IllegalArgumentException(className);
	}
    }

    
    @Override
    public Object valueFrom(String valueString) {

	if (maxValueCount() == 1) {
	    return classFrom(valueString);
	}

	String[] values = (String[]) super.valueFrom(valueString);

	Class<?>[] classes = new Class[values.length];
	for (int i = 0; i < values.length; i++) {
	    classes[i] = classFrom(values[i]);
	}
	return classes;
    }

    
    @Override
    protected String valueErrorFor(Object value) {
	return null;
    }
}
