

package koala.dynamicjava.interpreter;

import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.Node;
import koala.dynamicjava.tree.Expression;
import edu.rice.cs.dynamicjava.interpreter.TypeContext;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Lambda2;


public class NodeProperties {

    
    public final static String TYPE = "type";

    public static Type getType(Node n) {
        return (Type)n.getProperty(TYPE);
    }
    
    public static Type setType(Node n, Type t) {
      n.setProperty(TYPE, t);
      return t;
    }
    
    public static boolean hasType(Node n) {
      return n.hasProperty(TYPE);
    }
    
    public static final Lambda<Node, Type> NODE_TYPE = new Lambda<Node, Type>() {
      public Type value(Node n) { return getType(n); }
    };
    

    
    public final static String VARIABLE_TYPE = "variableType";

    public static Type getVariableType(Node n) {
        return (Type)n.getProperty(VARIABLE_TYPE);
    }
    
    public static Type setVariableType(Node n, Type t) {
      n.setProperty(VARIABLE_TYPE, t);
      return t;
    }
    
    public static boolean hasVariableType(Node n) {
      return n.hasProperty(VARIABLE_TYPE);
    }
    

    
    public final static String SUPER_TYPE = "superType";

    public static Type getSuperType(Node n) {
        return (Type)n.getProperty(SUPER_TYPE);
    }
    
    public static Type setSuperType(Node n, Type t) {
      n.setProperty(SUPER_TYPE, t);
      return t;
    }
    
    public static boolean hasSuperType(Node n) {
      return n.hasProperty(SUPER_TYPE);
    }
    

    
    public final static String CONVERTED_TYPE = "convertedType";

    @SuppressWarnings("unchecked")
    public static Thunk<Class<?>> getConvertedType(Node n) {
        return (Thunk<Class<?>>) n.getProperty(CONVERTED_TYPE);
    }
    
    public static Thunk<Class<?>> setConvertedType(Node n, Thunk<Class<?>> c) {
      n.setProperty(CONVERTED_TYPE, c);
      return c;
    }
    
    public static boolean hasConvertedType(Node n) {
      return n.hasProperty(CONVERTED_TYPE);
    }
    

    
    public final static String ASSERTED_TYPE = "assertedType";

    @SuppressWarnings("unchecked")
    public static Thunk<Class<?>> getAssertedType(Node n) {
        return (Thunk<Class<?>>) n.getProperty(ASSERTED_TYPE);
    }
    
    public static Thunk<Class<?>> setAssertedType(Node n, Thunk<Class<?>> c) {
      n.setProperty(ASSERTED_TYPE, c);
      return c;
    }
    
    public static boolean hasAssertedType(Node n) {
      return n.hasProperty(ASSERTED_TYPE);
    }
    

    
    public final static String CHECKED_TYPE = "checkedType";

    @SuppressWarnings("unchecked")
    public static Thunk<Class<?>> getCheckedType(Node n) {
        return (Thunk<Class<?>>) n.getProperty(CHECKED_TYPE);
    }
    
    public static Thunk<Class<?>> setCheckedType(Node n, Thunk<Class<?>> c) {
      n.setProperty(CHECKED_TYPE, c);
      return c;
    }
    
    public static boolean hasCheckedType(Node n) {
      return n.hasProperty(CHECKED_TYPE);
    }
    

    
    public final static String ERASED_TYPE = "erasedType";

    @SuppressWarnings("unchecked")
    public static Thunk<Class<?>> getErasedType(Node n) {
        return (Thunk<Class<?>>) n.getProperty(ERASED_TYPE);
    }
    
    public static Thunk<Class<?>> setErasedType(Node n, Thunk<Class<?>> c) {
      n.setProperty(ERASED_TYPE, c);
      return c;
    }
    
    public static boolean hasErasedType(Node n) {
      return n.hasProperty(ERASED_TYPE);
    }
    

    
    public final static String LEFT_EXPRESSION = "leftExpression";

    public static Expression getLeftExpression(Node n) {
        return (Expression) n.getProperty(LEFT_EXPRESSION);
    }
    
    public static Expression setLeftExpression(Node n, Expression exp) {
      n.setProperty(LEFT_EXPRESSION, exp);
      return exp;
    }
    
    public static boolean hasLeftExpression(Node n) {
      return n.hasProperty(LEFT_EXPRESSION);
    }
    

    
    public final static String TRANSLATION = "translation";

    public static Expression getTranslation(Node n) {
        return (Expression) n.getProperty(TRANSLATION);
    }
    
    public static Expression setTranslation(Node n, Expression exp) {
      n.setProperty(TRANSLATION, exp);
      return exp;
    }
    
    public static boolean hasTranslation(Node n) {
      return n.hasProperty(TRANSLATION);
    }
    
    
    public final static String STATEMENT_TRANSLATION = "statementTranslation";

    public static Node getStatementTranslation(Node n) {
        return (Node) n.getProperty(STATEMENT_TRANSLATION);
    }
    
    public static Node setStatementTranslation(Node n, Node s) {
      n.setProperty(STATEMENT_TRANSLATION, s);
      return s;
    }
    
    public static boolean hasStatementTranslation(Node n) {
      return n.hasProperty(STATEMENT_TRANSLATION);
    }
    

    
    public final static String VALUE = "value";

    public static Object getValue(Node n) {
        return n.getProperty(VALUE);
    }
    
    public static Object setValue(Node n, Object o) {
      n.setProperty(VALUE, o);
      return o;
    }
    
    public static boolean hasValue(Node n) {
      return n.hasProperty(VALUE);
    }
    

    
    public final static String ERROR_STRINGS = "errorStrings";

    public static String[] getErrorStrings(Node n) {
      return (String[]) n.getProperty(ERROR_STRINGS);
    }
    
    public static String[] setErrorStrings(Node n, String... strings) {
      n.setProperty(ERROR_STRINGS, strings);
      return strings;
    }
    
    public static boolean hasErrorStrings(Node n) {
      return n.hasProperty(ERROR_STRINGS);
    }


    
    public final static String VARIABLE = "variable";
    
    public static LocalVariable getVariable(Node n) {
      return (LocalVariable) n.getProperty(VARIABLE);
    }
    
    public static LocalVariable setVariable(Node n, LocalVariable v) {
      n.setProperty(VARIABLE, v);
      return v;
    }
    
    public static boolean hasVariable(Node n) {
      return n.hasProperty(VARIABLE);
    }
    
    public static final Lambda<Node, LocalVariable> NODE_VARIABLE = new Lambda<Node, LocalVariable>() {
      public LocalVariable value(Node n) { return getVariable(n); }
    };
    
    
    
    public final static String CONSTRUCTOR = "constructor";

    public static DJConstructor getConstructor(Node n) {
      return (DJConstructor) n.getProperty(CONSTRUCTOR);
    }
    
    public static DJConstructor setConstructor(Node n, DJConstructor c) {
      n.setProperty(CONSTRUCTOR, c);
      return c;
    }
    
    public static boolean hasConstructor(Node n) {
      return n.hasProperty(CONSTRUCTOR);
    }


    
    public final static String FIELD = "field";

    public static DJField getField(Node n) {
      return (DJField) n.getProperty(FIELD);
    }
    
    public static DJField setField(Node n, DJField f) {
      n.setProperty(FIELD, f);
      return f;
    }
    
    public static boolean hasField(Node n) {
      return n.hasProperty(FIELD);
    }

    
    public final static String METHOD = "method";
    
    public static DJMethod getMethod(Node n) {
      return (DJMethod) n.getProperty(METHOD);
    }
    
    public static DJMethod setMethod(Node n, DJMethod m) {
      n.setProperty(METHOD, m);
      return m;
    }
    
    public static boolean hasMethod(Node n) {
      return n.hasProperty(METHOD);
    }


    
    public final static String DJCLASS = "djclass";
    
    public static DJClass getDJClass(Node n) {
      return (DJClass) n.getProperty(DJCLASS);
    }
    
    public static DJClass setDJClass(Node n, DJClass c) {
      n.setProperty(DJCLASS, c);
      return c;
    }
    
    public static boolean hasDJClass(Node n) {
      return n.hasProperty(DJCLASS);
    }


    
    public final static String ENCLOSING_THIS = "enclosingThis";
    
    public static DJClass getEnclosingThis(Node n) {
      return (DJClass) n.getProperty(ENCLOSING_THIS);
    }
    
    public static DJClass setEnclosingThis(Node n, DJClass c) {
      n.setProperty(ENCLOSING_THIS, c);
      return c;
    }
    
    public static boolean hasEnclosingThis(Node n) {
      return n.hasProperty(ENCLOSING_THIS);
    }


    
    public final static String TYPE_VARIABLE = "typeVariable";

    public static VariableType getTypeVariable(Node n) {
      return (VariableType) n.getProperty(TYPE_VARIABLE);
    }
    
    public static VariableType setTypeVariable(Node n, VariableType v) {
      n.setProperty(TYPE_VARIABLE, v);
      return v;
    }
    
    public static boolean hasTypeVariable(Node n) {
      return n.hasProperty(TYPE_VARIABLE);
    }

    public static final Lambda<Node, VariableType> NODE_TYPE_VARIABLE = new Lambda<Node, VariableType>() {
      public VariableType value(Node n) { return getTypeVariable(n); }
    };
    
    
    
    public final static String ERROR = "error";

    public static ExecutionError getError(Node n) {
      return (ExecutionError) n.getProperty(ERROR);
    }
    
    public static ExecutionError setError(Node n, ExecutionError e) {
      n.setProperty(ERROR, e);
      return e;
    }
    
    public static boolean hasError(Node n) {
      return n.hasProperty(ERROR);
    }

    
    public final static String ERROR_CONTEXT = "errorContext";

    public static TypeContext getErrorContext(Node n) {
      return (TypeContext) n.getProperty(ERROR_CONTEXT);
    }
    
    public static TypeContext setErrorContext(Node n, TypeContext c) {
      n.setProperty(ERROR_CONTEXT, c);
      return c;
    }
    
    public static boolean hasErrorContext(Node n) {
      return n.hasProperty(ERROR_CONTEXT);
    }

    
    public final static String OPERATION = "operation";

    @SuppressWarnings("unchecked")
    public static Lambda2<Object, Object, Object> getOperation(Node n) {
      return (Lambda2<Object, Object, Object>) n.getProperty(OPERATION);
    }
    
    public static Lambda2<Object, Object, Object> setOperation(Node n, 
                                                               Lambda2<Object, Object, Object> f) {
      n.setProperty(OPERATION, f);
      return f;
    }
    
    public static boolean hasOperation(Node n) {
      return n.hasProperty(OPERATION);
    }

    
    protected NodeProperties() {
    }
}
