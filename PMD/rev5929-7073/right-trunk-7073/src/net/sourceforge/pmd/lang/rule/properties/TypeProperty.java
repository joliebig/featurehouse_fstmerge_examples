
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.ClassUtil;


public class TypeProperty extends AbstractPackagedProperty<Class> {

	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<TypeProperty>(Class.class, packagedFieldTypesByKey) {

		public TypeProperty createWith(Map<String, String> valuesById) {
			return new TypeProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};
	
    
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);
    }

    
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, classFrom(defaultTypeStr), legalPackageNames, theUIOrder);
    }
    
    
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, classFrom(defaultTypeStr), packageNamesIn(otherParams), theUIOrder);
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
