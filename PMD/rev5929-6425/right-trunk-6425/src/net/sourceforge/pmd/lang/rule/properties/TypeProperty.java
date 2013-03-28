
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;


public class TypeProperty extends AbstractPackagedProperty {

    private static final char DELIMITER = '|';

    
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);

        isMultiValue(false);
    }

    
    public TypeProperty(String theName, String theDescription, Class<?>[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

        isMultiValue(true);
    }

    
    @Override
    protected String packageNameOf(Object item) {
        return ((Class) item).getName();
    }

    
    public Class<?> type() {
        return Class.class;
    }

    
    @Override
    protected String itemTypeName() {
        return "type";
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

    
    public Object valueFrom(String valueString) {

        if (!isMultiValue()) {
            return classFrom(valueString);
        }

        String[] values = StringUtil.substringsOf(valueString, DELIMITER);

        Class<?>[] classes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = classFrom(values[i]);
        }
        return classes;
    }
}
