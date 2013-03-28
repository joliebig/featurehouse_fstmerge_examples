
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.util.ClassUtil;


public class TypeProperty extends AbstractPackagedProperty<Class> {

    
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);
    }

    
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, classFrom(defaultTypeStr), legalPackageNames, theUIOrder);
    }
    
    
    protected String defaultAsString() {
        return asString(defaultValue());
    }
    
    
    @Override
    protected String packageNameOf(Object item) {
        return ((Class<?>) item).getName();
    }

    
    public Class<Class> type() {
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

    
    static Class<?> classFrom(String className) {

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

    
    public Class<?> valueFrom(String valueString) {
        return classFrom(valueString);
    }
}
