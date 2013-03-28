
package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Method;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;


public class MethodMultiProperty extends AbstractMultiPackagedProperty<Method[]> {
       
	public static final PropertyDescriptorFactory factory = new BasicPropertyDescriptorFactory<MethodMultiProperty>(Method[].class, packagedFieldTypesByKey) {

		public MethodMultiProperty createWith(Map<String, String> valuesById) {
			return new MethodMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};
	
    
    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);
    }

    
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, methodsFrom(methodDefaults), legalPackageNames, theUIOrder);
    }
    
    
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults, Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, methodsFrom(methodDefaults), packageNamesIn(otherParams), theUIOrder);
    }    
        
    
    public static Method[] methodsFrom(String methodsStr) {      
    
        String[] values = StringUtil.substringsOf(methodsStr, DELIMITER);
    
        Method[] methods = new Method[values.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = MethodProperty.methodFrom(values[i], MethodProperty.CLASS_METHOD_DELIMITER, MethodProperty.METHOD_ARG_DELIMITER);
        }
        return methods;
    }
    
    
    @Override
    protected String asString(Object value) {
        return value == null ? "" : MethodProperty.asStringFor((Method) value);
    }

    
    @Override
    protected String packageNameOf(Object item) {

        final Method method = (Method) item;
        return method.getDeclaringClass().getName() + '.' + method.getName();
    }

    
    @Override
    protected String itemTypeName() {
        return "method";
    }
    
    
    public Class<Method[]> type() {
        return Method[].class;
    }

    
    public Method[] valueFrom(String valueString) throws IllegalArgumentException {
        return methodsFrom(valueString);
    }
}
