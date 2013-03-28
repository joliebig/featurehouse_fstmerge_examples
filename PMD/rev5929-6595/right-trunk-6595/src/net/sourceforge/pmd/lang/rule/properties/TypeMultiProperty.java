
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.StringUtil;


public class TypeMultiProperty extends AbstractMultiPackagedProperty<Class[]> {

    
    public TypeMultiProperty(String theName, String theDescription, Class<?>[] theDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

    }

    
    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults, String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults), legalPackageNames, theUIOrder);

    }
    
    
    public static Class[] typesFrom(String classesStr) {
        String[] values = StringUtil.substringsOf(classesStr, DELIMITER);

        Class<?>[] classes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = TypeProperty.classFrom(values[i]);
        }
        return classes;
    }
    
    
    @Override
    protected String packageNameOf(Object item) {
        return ((Class<?>) item).getName();
    }
    
    
    public Class<Class[]> type() {
        return Class[].class;
    }

    
    @Override
    protected String itemTypeName() {
        return "type";
    }

    
    @Override
    protected String asString(Object value) {
        return value == null ? "" : ((Class<?>) value).getName();
    }

    
    public Class<?>[] valueFrom(String valueString) {
        return typesFrom(valueString);
    }
}
