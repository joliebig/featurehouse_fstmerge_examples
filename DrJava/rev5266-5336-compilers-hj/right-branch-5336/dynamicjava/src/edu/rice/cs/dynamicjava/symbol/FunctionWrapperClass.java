package edu.rice.cs.dynamicjava.symbol;

import java.util.Iterator;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SequenceIterator;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.RuntimeBindings;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;
import edu.rice.cs.dynamicjava.symbol.type.Type;
import edu.rice.cs.dynamicjava.symbol.type.VariableType;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class FunctionWrapperClass implements DJClass {

  private static final Iterator<Integer> ID_COUNTER =
    new SequenceIterator<Integer>(1, LambdaUtil.INCREMENT_INT);
  
  private final Access.Module _accessModule;
  private final Iterable<DJMethod> _methods;
  private final String _name;
  
  public FunctionWrapperClass(Access.Module accessModule, Iterable<? extends LocalFunction> functions) {
    _accessModule = accessModule;
    _methods = IterUtil.mapSnapshot(functions, FUNCTION_AS_METHOD);
    _name = "Overload" + ID_COUNTER.next();
  }
  
  
  private final Lambda<LocalFunction, DJMethod> FUNCTION_AS_METHOD =
    new Lambda<LocalFunction, DJMethod>() {
    public DJMethod value(LocalFunction f) { return new FunctionWrapperMethod(f); }
  };
  
  public String packageName() { return _accessModule.packageName(); }
  
  
  public String fullName() {
    
    String pkg = packageName();
    if (pkg.length() > 0) pkg += ".";
    return pkg + "$" + _name;
  }
  
  public boolean isAnonymous() { return false; }
  public String declaredName() { return _name; }
  public boolean isInterface() { return false; }
  public boolean isStatic() { return false; }
  public boolean isAbstract() { return false; }
  public boolean isFinal() { return true; }
  public Access accessibility() { return Access.PUBLIC; }
  public Access.Module accessModule() { return _accessModule; }
  public boolean hasRuntimeBindingsParams() { return false; }
  
  public DJClass declaringClass() { return null; }
  
  public Iterable<VariableType> declaredTypeParameters() { return IterUtil.empty(); }
  
  public Iterable<Type> declaredSupertypes() { return IterUtil.empty(); }
  public Iterable<DJField> declaredFields() { return IterUtil.empty(); }
  public Iterable<DJConstructor> declaredConstructors() { return IterUtil.empty(); }
  public Iterable<DJMethod> declaredMethods() { return _methods; }
  public Iterable<DJClass> declaredClasses() { return IterUtil.empty(); }

  
  public Type immediateSuperclass() { return null; }
  
  
  public Class<?> load() { throw new UnsupportedOperationException(); }
  
  
  public boolean equals(Object o) { return this == o; }
  
  public int hashCode() { return System.identityHashCode(this); }
  
  
  private class FunctionWrapperMethod implements DJMethod {
    private final LocalFunction _f;
    public FunctionWrapperMethod(LocalFunction f) { _f = f; }
    public Iterable<VariableType> typeParameters() { return _f.typeParameters(); }
    public Iterable<LocalVariable> parameters() { return _f.parameters(); }
    public Iterable<Type> thrownTypes() { return _f.thrownTypes(); }
    public String declaredName() { return _f.declaredName(); }
    public DJClass declaringClass() { return FunctionWrapperClass.this; }
    public Type returnType() { return _f.returnType(); }
    public boolean isStatic() { return true; }
    public boolean isAbstract() { return false; }
    public boolean isFinal() { return false; }
    public Access accessibility() { return Access.PUBLIC; }
    public Access.Module accessModule() { return _accessModule; }
    public DJMethod declaredSignature() { return this; }
    public Object evaluate(Object receiver, Iterable<Object> args, RuntimeBindings bindings, Options options) 
      throws EvaluatorException {
      return _f.evaluate(args, bindings, options);
    }
  }

} 
