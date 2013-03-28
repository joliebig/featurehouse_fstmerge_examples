
package net.sourceforge.pmd.lang.rule.properties;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;


public class MethodProperty extends AbstractPackagedProperty {

    public static final char CLASS_METHOD_DELIMITER = '#';
    public static final char METHOD_ARG_DELIMITER = ',';
    public static final char[] METHOD_GROUP_DELIMITERS = new char[] { '(', ')' };

    private static final String ARRAY_FLAG = "[]";
    private static final Map<Class, String> TYPE_SHORTCUTS = ClassUtil.getClassShortNames();

    
    private static String shortestNameFor(Class<?> cls) {
        String compactName = TYPE_SHORTCUTS.get(cls);
        return compactName == null ? cls.getName() : compactName;
    }

    
    public static String asStringFor(Method method) {
        StringBuilder sb = new StringBuilder();
        asStringOn(method, sb);
        return sb.toString();
    }

    
    @Override
    protected String asString(Object value) {
        return value == null ? "" : asStringFor((Method) value);
    }

    
    private static void serializedTypeIdOn(Class<?> type, StringBuilder sb) {

        Class<?> arrayType = type.getComponentType();
        if (arrayType == null) {
            sb.append(shortestNameFor(type));
            return;
        }
        sb.append(shortestNameFor(arrayType)).append(ARRAY_FLAG);
    }

    
    public static void asStringOn(Method method, StringBuilder sb) {

        Class<?> clazz = method.getDeclaringClass();

        sb.append(shortestNameFor(clazz));
        sb.append(CLASS_METHOD_DELIMITER);
        sb.append(method.getName());

        sb.append(METHOD_GROUP_DELIMITERS[0]);

        Class<?>[] argTypes = method.getParameterTypes();
        if (argTypes.length == 0) {
            sb.append(METHOD_GROUP_DELIMITERS[1]);
            return;
        }

        serializedTypeIdOn(argTypes[0], sb);
        for (int i = 1; i < argTypes.length; i++) {
            sb.append(METHOD_ARG_DELIMITER);
            serializedTypeIdOn(argTypes[i], sb);
        }
        sb.append(METHOD_GROUP_DELIMITERS[1]);
    }

    
    private static Class<?> typeFor(String typeName) {

        Class<?> type = null;

        if (typeName.endsWith(ARRAY_FLAG)) {
            String arrayTypeName = typeName.substring(0, typeName.length() - ARRAY_FLAG.length());
            type = typeFor(arrayTypeName); 
            return Array.newInstance(type, 0).getClass(); 
        }

        type = ClassUtil.getTypeFor(typeName); 
        if (type != null) {
            return type;
        }

        try {
            return Class.forName(typeName);
        } catch (Exception ex) {
            return null;
        }
    }

    
    public static Method methodFrom(String methodNameAndArgTypes, char classMethodDelimiter, char methodArgDelimiter) {

        
        

        int delimPos0 = methodNameAndArgTypes.indexOf(classMethodDelimiter);
        if (delimPos0 < 0) {
            return null;
        }

        String className = methodNameAndArgTypes.substring(0, delimPos0);
        Class<?> type = ClassUtil.getTypeFor(className);
        if (type == null) {
            return null;
        }

        int delimPos1 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[0]);
        if (delimPos1 < 0) {
            String methodName = methodNameAndArgTypes.substring(delimPos0 + 1);
            return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
        }

        String methodName = methodNameAndArgTypes.substring(delimPos0 + 1, delimPos1);
        if (StringUtil.isEmpty(methodName)) {
            return null;
        } 

        int delimPos2 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[1]);
        if (delimPos2 < 0) {
            return null;
        } 

        String argTypesStr = methodNameAndArgTypes.substring(delimPos1 + 1, delimPos2);
        if (StringUtil.isEmpty(argTypesStr)) {
            return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
        } 

        String[] argTypeNames = StringUtil.substringsOf(argTypesStr, methodArgDelimiter);
        Class<?>[] argTypes = new Class[argTypeNames.length];
        for (int i = 0; i < argTypes.length; i++) {
            argTypes[i] = typeFor(argTypeNames[i]);
        }

        return ClassUtil.methodFor(type, methodName, argTypes);
    }

    
    public MethodProperty(String theName, String theDescription, Method theDefault, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);

        isMultiValue(false);
        multiValueDelimiter(' ');
    }

    
    public MethodProperty(String theName, String theDescription, Method[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

        isMultiValue(true);
        multiValueDelimiter(' ');
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

    
    public Class<Method> type() {
        return Method.class;
    }

    
    public Object valueFrom(String valueString) throws IllegalArgumentException {

        if (!isMultiValue()) {
            return methodFrom(valueString, CLASS_METHOD_DELIMITER, METHOD_ARG_DELIMITER);
        }

        String[] values = StringUtil.substringsOf(valueString, multiValueDelimiter());

        Method[] methods = new Method[values.length];
        for (int i = 0; i < methods.length; i++) {
            methods[i] = methodFrom(values[i], CLASS_METHOD_DELIMITER, METHOD_ARG_DELIMITER);
        }
        return methods;
    }
}
