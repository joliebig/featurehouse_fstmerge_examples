package edu.rice.cs.dynamicjava.symbol;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.*;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.symbol.type.*;
import edu.rice.cs.dynamicjava.symbol.type.Type; 
import edu.rice.cs.dynamicjava.interpreter.RuntimeBindings;
import edu.rice.cs.dynamicjava.interpreter.EvaluatorException;

import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Box;
import edu.rice.cs.plt.lambda.LazyThunk;
import edu.rice.cs.plt.lambda.WrappedException;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class JavaClass implements DJClass {
  
  protected Class<?> _c;
  
  public JavaClass(Class<?> c) { _c = c; }
  
  public String packageName() {
    String name = _c.getName();
    int dot = name.lastIndexOf('.');
    if (dot == -1) { return ""; }
    else { return name.substring(0, dot); }
  }
  
  
  public String fullName() { return _c.getName(); }
  
  public boolean isAnonymous() { return ReflectUtil.isAnonymousClass(_c); }
  
  public String declaredName() {
    if (ReflectUtil.isAnonymousClass(_c)) {
      throw new IllegalArgumentException("Anonymous class has no declared name");
    }
    else { return ReflectUtil.simpleName(_c); }
  }
  
  public boolean isInterface() { return Modifier.isInterface(_c.getModifiers()); }
  public boolean isStatic() { return Modifier.isStatic(_c.getModifiers()); }
  public boolean isAbstract() { return Modifier.isAbstract(_c.getModifiers()); }
  public boolean isFinal() { return Modifier.isFinal(_c.getModifiers()); }
  public Access accessibility() { return extractAccessibility(_c.getModifiers()); }  
  public boolean hasRuntimeBindingsParams() { return false; }
  
  public Access.Module accessModule() {
    
    Class<?> result = _c;
    Class<?> outer = result.getDeclaringClass();
    while (outer != null) { result = outer; outer = result.getDeclaringClass(); }
    return new JavaClass(result);
  }

  public DJClass declaringClass() {
    Class<?> outer = _c.getDeclaringClass();
    return (outer == null) ? null : new JavaClass(outer);
  }
  
  
  public Iterable<VariableType> declaredTypeParameters() { return IterUtil.empty(); }
  
  
  public Iterable<Type> declaredSupertypes() {
    Type superC = immediateSuperclass();
    Iterable<Type> superIs;
    if (_c.getInterfaces() == null) { superIs = IterUtil.empty(); }
    else { superIs = IterUtil.mapSnapshot(IterUtil.asIterable(_c.getInterfaces()), CLASS_AS_TYPE); }
    return superC == null ? superIs : IterUtil.compose(superC, superIs);
  }
  
  public Iterable<DJField> declaredFields() {
    return IterUtil.mapSnapshot(IterUtil.asIterable(_c.getDeclaredFields()), CONVERT_FIELD);
  }
  
  public Iterable<DJConstructor> declaredConstructors() {
    return IterUtil.mapSnapshot(IterUtil.asIterable(_c.getDeclaredConstructors()), CONVERT_CONSTRUCTOR);
  }
  
  public Iterable<DJMethod> declaredMethods() {
    return IterUtil.mapSnapshot(IterUtil.asIterable(_c.getDeclaredMethods()), CONVERT_METHOD);
  }
  
  public Iterable<DJClass> declaredClasses() {
    return IterUtil.mapSnapshot(IterUtil.asIterable(_c.getDeclaredClasses()), CONVERT_CLASS);
  }
  

  
  public Type immediateSuperclass() {
    Class<?> superT = _c.getSuperclass();
    return (superT == null) ? null : classAsType(superT);
  }
  
  public Class<?> load() { return _c; }
  
  public String toString() { return "JavaClass(" + _c.getName() + ")"; }
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (!o.getClass().equals(getClass())) { return false; }
    else { return _c.equals(((JavaClass) o)._c); }
  }
  
  public int hashCode() { return (getClass().hashCode() << 1) ^ _c.hashCode(); }
  
  
  private static Type classAsType(Class<?> c) {
    if (c.isPrimitive()) { return SymbolUtil.typeOfPrimitiveClass(c); }
    else if (c.isArray()) { return new SimpleArrayType(classAsType(c.getComponentType())); }
    else { return new SimpleClassType(new JavaClass(c)); }
  }
  
  @SuppressWarnings("unchecked") 
  private static final Lambda<Class, Type> CLASS_AS_TYPE = new Lambda<Class, Type>() {
    public Type value(Class c) {
      return classAsType(c);
    }
  };
  
  @SuppressWarnings("unchecked") 
  private static final Lambda<Class, DJClass> CONVERT_CLASS = new Lambda<Class, DJClass>() {
    public DJClass value(Class c) { return new JavaClass(c); }
  };
  
  
  private final Lambda<Field, DJField> CONVERT_FIELD = new Lambda<Field, DJField>() {
    public DJField value(Field f) { return new JavaField(f); }
  };
  
  
  @SuppressWarnings("unchecked") 
  private final Lambda<Constructor, DJConstructor> CONVERT_CONSTRUCTOR =
    new Lambda<Constructor, DJConstructor>() {
    public DJConstructor value(Constructor k) { return new JavaConstructor(k); }
  };
  
  
  private final Lambda<Method, DJMethod> CONVERT_METHOD = new Lambda<Method, DJMethod>() {
    public DJMethod value(Method m) { return new JavaMethod(m); }
  };

  protected class JavaField implements DJField {
    protected final Field _f;
    public JavaField(Field f) { _f = f; }
    public String declaredName() { return _f.getName(); }
    public DJClass declaringClass() { return JavaClass.this; }
    public Type type() { return classAsType(_f.getType()); }
    public boolean isFinal() { return Modifier.isFinal(_f.getModifiers()); }
    public boolean isStatic() { return Modifier.isStatic(_f.getModifiers()); }
    public Access accessibility() { return extractAccessibility(_f.getModifiers()); }
    public Access.Module accessModule() { return JavaClass.this.accessModule(); }
    
    public Option<Object> constantValue() {
      
      
      
      
      if (isStatic() && isFinal() && (_f.getType().isPrimitive() || _f.getType().equals(String.class))) {
        try { return Option.some(boxForReceiver(null).value()); }
        catch (WrappedException e) { return Option.none(); }
      }
      else { return Option.none(); }
    }
    
    public Box<Object> boxForReceiver(final Object receiver) {
      return new Box<Object>() {

        public Object value() {
          if (!isStatic() && receiver == null) {
            throw new WrappedException(new EvaluatorException(new NullPointerException()));
          }
          try { _f.setAccessible(true); }
          catch (SecurityException e) { debug.log(e);  }
          try { return _f.get(receiver); }
          catch (IllegalAccessException e) {
            
            throw new RuntimeException(e);
          }
          catch (LinkageError e) {
            
            throw new WrappedException(new EvaluatorException(e, FIELD_GET_EXTRA_STACK));
          }
          catch (Throwable t) {
            
            throw new WrappedException(new EvaluatorException(t, FIELD_GET_EXTRA_STACK));
          }
        }
        
        public void set(Object o) {
          if (!isStatic() && receiver == null) {
            throw new WrappedException(new EvaluatorException(new NullPointerException()));
          }
          try { _f.setAccessible(true); }
          catch (SecurityException e) { debug.log(e);  }
          try { _f.set(receiver, o); }
          catch (IllegalAccessException e) {
            
            throw new RuntimeException(e);
          }
          catch (LinkageError e) {
            
            throw new WrappedException(new EvaluatorException(e, FIELD_SET_EXTRA_STACK));
          }
          catch (Throwable t) {
            
            throw new WrappedException(new EvaluatorException(t, FIELD_SET_EXTRA_STACK));
          }
        }
        
      };
    }
      
    public String toString() { return "JavaField(" + declaredName() + ")"; }
}

  private static final String[] FIELD_GET_EXTRA_STACK =
    new String[]{ "java.lang.reflect.Field.get",
                  "java.lang.reflect.Field.getFieldAccessor",
                  "java.lang.reflect.Field.acquireFieldAccessor",
                  "sun.reflect.ReflectionFactory.newFieldAccessor",
                  "sun.reflect.UnsafeFieldAccessorFactory.newFieldAccessor",
                  "sun.misc.Unsafe.ensureClassInitialized" };

  private static final String[] FIELD_SET_EXTRA_STACK =
    new String[]{ "java.lang.reflect.Field.set",
                  "java.lang.reflect.Field.getFieldAccessor",
                  "java.lang.reflect.Field.acquireFieldAccessor",
                  "sun.reflect.ReflectionFactory.newFieldAccessor",
                  "sun.reflect.UnsafeFieldAccessorFactory.newFieldAccessor",
                  "sun.misc.Unsafe.ensureClassInitialized" };  
  
  
  
  protected class JavaConstructor implements DJConstructor {
    protected final Constructor<?> _k;
    protected final Type _outerType;
    private final Thunk<Iterable<LocalVariable>> _params;
    
    public JavaConstructor(Constructor<?> k) {
      _k = k;
      DJClass outer = SymbolUtil.dynamicOuterClass(JavaClass.this);
      _outerType = (outer == null) ? null : SymbolUtil.thisType(outer);
      _params = makeParamThunk(); 
    }
    
    public String declaredName() { return isAnonymous() ? "<anonymous>" : JavaClass.this.declaredName(); }
    public DJClass declaringClass() { return JavaClass.this; }
    public Access accessibility() { return extractAccessibility(_k.getModifiers()); }
    public Access.Module accessModule() { return JavaClass.this.accessModule(); }
    protected Thunk<Iterable<LocalVariable>> makeParamThunk() { return paramFactory(_k.getParameterTypes()); }
    public Iterable<VariableType> typeParameters() { return IterUtil.empty(); }
    public DJConstructor declaredSignature() { return this; }
    
    public Iterable<LocalVariable> parameters() {
      Iterable<LocalVariable> result = _params.value();
      if (_outerType != null) { result = IterUtil.skipFirst(result); }
      return result;
    }
    
    public Type returnType() { return SymbolUtil.thisType(JavaClass.this); }
    
    public Iterable<Type> thrownTypes() {
      return IterUtil.mapSnapshot(IterUtil.asIterable(_k.getExceptionTypes()), CLASS_AS_TYPE);
    }
    
    public Object evaluate(Object outer, Iterable<Object> args, RuntimeBindings bindings, Options options) 
      throws EvaluatorException {
      if (_outerType != null) {
        if (outer == null) {
          throw new WrappedException(new EvaluatorException(new NullPointerException()));
        }
        args = IterUtil.compose(outer, args);
      }
      
      try { _k.setAccessible(true); }
      catch (SecurityException e) { debug.log(e);  }
      
      Object[] argsArray = IterUtil.toArray(args, Object.class);
      try {
        return _k.newInstance(argsArray);
      }
      catch (InvocationTargetException e) {
        throw new EvaluatorException(e.getCause(), CONSTRUCTOR_EXTRA_STACK);
      }
      catch (LinkageError e) {
        
        throw new EvaluatorException(e, CONSTRUCTOR_EXTRA_STACK);
      }
      catch (IllegalAccessException e) {
        
        throw new RuntimeException(e);
      }
      catch (InstantiationException e) {
        
        throw new RuntimeException(e);
      }
      catch (Throwable t) {
        
        throw new EvaluatorException(t, METHOD_EXTRA_STACK);
      }
    }
    
    public String toString() { return "JavaConstructor(" + declaredName() + ")"; }
  }
  
  private static final String[] CONSTRUCTOR_EXTRA_STACK =
    new String[]{ "java.lang.reflect.Constructor.newInstance",
                  "sun.reflect.DelegatingConstructorAccessorImpl.newInstance",
                  "sun.reflect.NativeConstructorAccessorImpl.newInstance",
                  "sun.reflect.NativeConstructorAccessorImpl.newInstance0" };
  
  
  protected class JavaMethod implements DJMethod {
    protected final Method _m;
    private final Thunk<Iterable<LocalVariable>> _params;
    public JavaMethod(Method m) { _m = m; _params = makeParamThunk();  }
    protected Thunk<Iterable<LocalVariable>> makeParamThunk() { return paramFactory(_m.getParameterTypes()); }
    public String declaredName() { return _m.getName(); }
    public DJClass declaringClass() { return JavaClass.this; }
    public boolean isStatic() { return Modifier.isStatic(_m.getModifiers()); }
    public boolean isAbstract() { return Modifier.isAbstract(_m.getModifiers()); }
    public boolean isFinal() { return Modifier.isFinal(_m.getModifiers()); }
    public Access accessibility() { return extractAccessibility(_m.getModifiers()); }
    public Access.Module accessModule() { return JavaClass.this.accessModule(); }
    public Type returnType() { return classAsType(_m.getReturnType()); }
    public Iterable<VariableType> typeParameters() { return IterUtil.empty(); }
    public Iterable<LocalVariable> parameters() { return _params.value(); }
    public Iterable<Type> thrownTypes() {
      return IterUtil.mapSnapshot(IterUtil.asIterable(_m.getExceptionTypes()), CLASS_AS_TYPE);
    }
    
    public DJMethod declaredSignature() { return this; }
    public Object evaluate(Object receiver, Iterable<Object> args, RuntimeBindings bindings, 
                           Options options) throws EvaluatorException {
      if (!isStatic() && receiver == null) {
        throw new WrappedException(new EvaluatorException(new NullPointerException()));
      }
      
      try { _m.setAccessible(true); }
      catch (SecurityException e) { debug.log(e);  }
      
      Object[] argsArray = IterUtil.toArray(args, Object.class);
      try {
        return _m.invoke(receiver, argsArray);
      }
      catch (InvocationTargetException e) {
        throw new EvaluatorException(e.getCause(), METHOD_EXTRA_STACK);
      }
      catch (LinkageError e) {
        
        throw new EvaluatorException(e, METHOD_EXTRA_STACK);
      }
      catch (IllegalAccessException e) {
        
        throw new RuntimeException(e);
      }
      catch (Throwable t) {
        
        throw new EvaluatorException(t, METHOD_EXTRA_STACK);
      }
    }
    
    public String toString() { return "JavaMethod(" + declaredName() + ")"; }
  }
  
  private static final String[] METHOD_EXTRA_STACK =
    new String[] { "java.lang.reflect.Method.invoke",
                   "sun.reflect.DelegatingMethodAccessorImpl.invoke",
                   "sun.reflect.NativeMethodAccessorImpl.invoke",
                   "sun.reflect.NativeMethodAccessorImpl.invoke0" };
  
  private static Thunk<Iterable<LocalVariable>> paramFactory(final Class<?>[] cs) {
    
    return LazyThunk.make(new Thunk<Iterable<LocalVariable>>() {
      public Iterable<LocalVariable> value() {
        List<LocalVariable> result = new ArrayList<LocalVariable>(cs.length);
        
        int argNum = 1;
        for (Class<?> c : cs) { result.add(new LocalVariable("a" + argNum++, classAsType(c), false)); }
        
        
        
        return IterUtil.asSizedIterable(result);
      }
    });
  }
  
  
  private static Access extractAccessibility(int mods) {
    if (Modifier.isPublic(mods)) { return Access.PUBLIC; }
    else if (Modifier.isProtected(mods)) { return Access.PROTECTED; }
    else if (Modifier.isPrivate(mods)) { return Access.PRIVATE; }
    else { return Access.PACKAGE; }
  }

}
