
package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Method;

import net.sourceforge.pmd.util.StringUtil;


public class MethodMultiProperty extends AbstractMultiPackagedProperty<Method[]> {
        
    
    public MethodMultiProperty(String theName, String theDescription, Method[] theDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);
    }

    
    public MethodMultiProperty(String theName, String theDescription, String methodDefaults, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, methodsFrom(methodDefaults), legalPackageNames, theUIOrder);
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
