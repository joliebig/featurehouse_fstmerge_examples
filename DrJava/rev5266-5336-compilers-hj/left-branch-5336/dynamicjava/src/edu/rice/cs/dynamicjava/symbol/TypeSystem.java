package edu.rice.cs.dynamicjava.symbol;

import edu.rice.cs.dynamicjava.symbol.type.*;
import koala.dynamicjava.tree.Expression;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.ObjectUtil;

import java.io.Serializable;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public abstract class TypeSystem {
  
  public static final BooleanType BOOLEAN = new BooleanType();
  public static final CharType CHAR = new CharType();
  public static final ByteType BYTE = new ByteType();
  public static final ShortType SHORT = new ShortType();
  public static final IntType INT = new IntType();
  public static final LongType LONG = new LongType();
  public static final FloatType FLOAT = new FloatType();
  public static final DoubleType DOUBLE = new DoubleType();
  public static final NullType NULL = new NullType();
  public static final VoidType VOID = new VoidType();
  public static final TopType TOP = new TopType();
  public static final BottomType BOTTOM = new BottomType();
  
  public static final SimpleClassType OBJECT = new SimpleClassType(SymbolUtil.wrapClass(Object.class));
  public static final SimpleClassType STRING = new SimpleClassType(SymbolUtil.wrapClass(String.class));
  public static final SimpleClassType CLONEABLE = new SimpleClassType(SymbolUtil.wrapClass(Cloneable.class));
  public static final SimpleClassType SERIALIZABLE = new SimpleClassType(SymbolUtil.wrapClass(Serializable.class));
  public static final SimpleClassType THROWABLE = new SimpleClassType(SymbolUtil.wrapClass(Throwable.class));
  public static final SimpleClassType EXCEPTION = new SimpleClassType(SymbolUtil.wrapClass(Exception.class));
  public static final SimpleClassType RUNTIME_EXCEPTION =
    new SimpleClassType(SymbolUtil.wrapClass(RuntimeException.class));
  public static final SimpleClassType BOOLEAN_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Boolean.class));
  public static final SimpleClassType CHARACTER_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Character.class));
  public static final SimpleClassType BYTE_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Byte.class));
  public static final SimpleClassType SHORT_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Short.class));
  public static final SimpleClassType INTEGER_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Integer.class));
  public static final SimpleClassType LONG_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Long.class));
  public static final SimpleClassType FLOAT_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Float.class));
  public static final SimpleClassType DOUBLE_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Double.class));
  public static final SimpleClassType VOID_CLASS = new SimpleClassType(SymbolUtil.wrapClass(Void.class));
  
  protected static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
  protected static final Iterable<Type> EMPTY_TYPE_ITERABLE = IterUtil.empty();
  protected static final Iterable<Expression> EMPTY_EXPRESSION_ITERABLE = IterUtil.empty();
  protected static final Option<Type> NONE_TYPE_OPTION = Option.none();
  
  public TypeWrapper wrap(Type t) { return (t == null) ? null : new TypeWrapper(t); }
  
  public Iterable<TypeWrapper> wrap(Iterable<? extends Type> ts) {
    return (ts == null) ? null : IterUtil.map(ts, WRAP_TYPE);
  }
  
  private final Lambda<Type, TypeWrapper> WRAP_TYPE = new Lambda<Type, TypeWrapper>() {
    public TypeWrapper value(Type t) { return (t == null) ? null : new TypeWrapper(t); }
  };

  public Option<TypeWrapper> wrap(Option<Type> t) {
    if (t == null) return null;
    else return t.isSome() ? Option.some(new TypeWrapper(t.unwrap())) : Option.<TypeWrapper>none();
  }
  
  
  public class TypeWrapper {
    private Type _t;
    public TypeWrapper(Type t) { _t = t; }
    
    public String toString() { return typePrinter().print(_t); }
    
    public boolean equals(Object o) {
      if (this == o) { return true; }
      else if (!(o instanceof TypeWrapper)) { return false; }
      else { return isEqual(_t, ((TypeWrapper) o)._t); }
    }
    
    public int hashCode() { throw new UnsupportedOperationException(); }
  }
  
  
  public abstract TypePrinter typePrinter();
  
  
  
  
  public abstract boolean isPrimitive(Type t);
  
  
  public abstract boolean isReference(Type t);
  
  
  public abstract boolean isArray(Type t);


  
  public abstract boolean isWellFormed(Type t);
  
  
  public abstract boolean isIterable(Type t);

  
  public abstract boolean isEnum(Type t);
  
  
  public abstract boolean isReifiable(Type t);

  
  public abstract boolean isConcrete(Type t);
  
  
  public abstract boolean isExtendable(Type t);
  
  
  public abstract boolean isImplementable(Type t);
  
  
  
  
  
  public abstract boolean isEqual(Type t1, Type t2);
  
  
  public abstract boolean isSubtype(Type subT, Type superT);

  
  public abstract boolean isDisjoint(Type t1, Type t2);

  
  public abstract boolean isAssignable(Type target, Type expT);

  
  public abstract boolean isAssignable(Type target, Type expT, Object expValue);
  
  
  public abstract boolean isPrimitiveConvertible(Type t);
  
  
  public abstract boolean isReferenceConvertible(Type t);
  
  
  public abstract Type join(Iterable<? extends Type> ts);

  
  public Type join(Type t1, Type t2) { return join(IterUtil.make(t1, t2)); }

  
  public abstract Type meet(Iterable<? extends Type> ts);

  
  public Type meet(Type t1, Type t2) { return meet(IterUtil.make(t1, t2)); }


  
  
  
  public abstract Type capture(Type t);
  
  
  public abstract Type erase(Type t);
  
  
  public abstract Thunk<Class<?>> erasedClass(Type t);
  
  
  public abstract Type reflectionClassOf(Type t);
  
  
  
  public abstract Type arrayElementType(Type t);
  
  
  public abstract Option<Type> dynamicallyEnclosingType(Type t);

  
  
  
  public abstract ClassType makeClassType(DJClass c);
  
  
  public abstract ClassType makeClassType(DJClass c, Iterable<? extends Type> args) 
    throws InvalidTypeArgumentException;
   
  
  
    
  
  public abstract Expression makePrimitive(Expression e) throws UnsupportedConversionException;
  
  
  public abstract Expression makeReference(Expression e) throws UnsupportedConversionException;
  
  
  public abstract Expression unaryPromote(Expression e) throws UnsupportedConversionException;

  
  public abstract Pair<Expression, Expression> binaryPromote(Expression e1, Expression e2)
    throws UnsupportedConversionException;
  
  
  public abstract Pair<Expression, Expression> mergeConditional(Expression e1, Expression e2)
    throws UnsupportedConversionException;

  
  public abstract Expression cast(Type target, Expression e) throws UnsupportedConversionException;
  
  
  public abstract Expression assign(Type target, Expression e) throws UnsupportedConversionException;

  
  
    
  
  
  public abstract ConstructorInvocation lookupConstructor(Type t, Iterable<? extends Type> typeArgs, 
                                                          Iterable<? extends Expression> args,
                                                          Option<Type> expected, Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;
  
  
  public abstract boolean containsMethod(Type t, String name, Access.Module accessModule);
  
  public abstract boolean containsStaticMethod(Type t, String name, Access.Module accessModule);
  
  
  public abstract ObjectMethodInvocation lookupMethod(Expression object, String name,
                                                      Iterable<? extends Type> typeArgs, 
                                                      Iterable<? extends Expression> args,
                                                      Option<Type> expected, Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;
    
  
  
  public abstract StaticMethodInvocation lookupStaticMethod(Type t, String name,
                                                            Iterable<? extends Type> typeArgs, 
                                                            Iterable<? extends Expression> args,
                                                            Option<Type> expected, Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;
  
  
  public abstract boolean containsField(Type t, String name, Access.Module accessModule);
  
  public abstract boolean containsStaticField(Type t, String name, Access.Module accessModule);
  
  
  public abstract ObjectFieldReference lookupField(Expression object, String name, Access.Module accessModule)
    throws UnmatchedLookupException;
  
  
  public abstract StaticFieldReference lookupStaticField(Type t, String name, Access.Module accessModule)
    throws UnmatchedLookupException;
  
  
  public abstract boolean containsClass(Type t, String name, Access.Module accessModule);
  
  public abstract boolean containsStaticClass(Type t, String name, Access.Module accessModule);
  
    
  public abstract ClassType lookupClass(Expression object, String name, Iterable<? extends Type> typeArgs,
                                        Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;
  
    
  public abstract ClassType lookupClass(Type t, String name, Iterable<? extends Type> typeArgs,
                                        Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;

  
  public abstract ClassType lookupStaticClass(Type t, String name, Iterable<? extends Type> typeArgs,
                                              Access.Module accessModule)
    throws InvalidTypeArgumentException, UnmatchedLookupException;
  
  
  public static interface TypePrinter {
    
    public String print(Type t);
    
    public String print(Iterable<? extends Type> ts);
    
    public String print(Function f);
  }
  
  
  public static abstract class FunctionInvocation {
    private final Iterable<? extends Type> _typeArgs;
    private final Iterable<? extends Expression> _args;
    private final Iterable<? extends Type> _thrown;

    protected FunctionInvocation(Iterable<? extends Type> typeArgs, Iterable<? extends Expression> args, 
                                  Iterable<? extends Type> thrown) {
      _typeArgs = typeArgs;
      _args = args;
      _thrown = thrown;
    }
    
    
    public Iterable<? extends Type> typeArgs() { return _typeArgs; }
    
    
    public Iterable<? extends Expression> args() { return _args; }
    
    
    public Iterable<? extends Type> thrown() { return _thrown; }
  }
  
  
  
  public static class ConstructorInvocation extends FunctionInvocation {
    private final DJConstructor _constructor;
    
    public ConstructorInvocation(DJConstructor constructor, Iterable<? extends Type> typeArgs, 
                                 Iterable<? extends Expression> args, Iterable<? extends Type> thrown) {
      super(typeArgs, args, thrown);
      _constructor = constructor;
    }
    
    
    public DJConstructor constructor() { return _constructor; }
  }
  
  
  
  public static abstract class MethodInvocation extends FunctionInvocation {
    private final DJMethod _method;
    private final Type _returnType;
    
    protected MethodInvocation(DJMethod method, Type returnType, Iterable<? extends Type> typeArgs, 
                               Iterable<? extends Expression> args, Iterable<? extends Type> thrown) {
      super(typeArgs, args, thrown);
      _method = method;
      _returnType = returnType;
    }
    
    
    public DJMethod method() { return _method; }
    
    
    public Type returnType() { return _returnType; }
    
  }
  
  
  
  public static class ObjectMethodInvocation extends MethodInvocation {
    private final Expression _object;
    
    public ObjectMethodInvocation(DJMethod method, Type returnType, Expression object, 
                                  Iterable<? extends Type> typeArgs, Iterable<? extends Expression> args, 
                                  Iterable<? extends Type> thrown) {
      super(method, returnType, typeArgs, args, thrown);
      _object = object;
    }
    
    
    public Expression object() { return _object; }
  }
  
  
  
  public static class StaticMethodInvocation extends MethodInvocation {
    public StaticMethodInvocation(DJMethod method, Type returnType, Iterable<? extends Type> typeArgs, 
                                  Iterable<? extends Expression> args, Iterable<? extends Type> thrown) {
      super(method, returnType, typeArgs, args, thrown);
    }
  }
    
  
  
  public static abstract class FieldReference {
    private final DJField _field;
    private final Type _type;
    
    protected FieldReference(DJField field, Type type) {
      _field = field;
      _type = type;
    }
    
    
    public DJField field() { return _field; }
    
    
    public Type type() { return _type; }
    
    public boolean equals(Object that) {
      if (this == that) { return true; }
      else if (!(that instanceof FieldReference)) { return false; }
      else {
        FieldReference r = (FieldReference) that;
        return _field.equals(r._field) && _type.equals(r._type);
      }
    }
    
    public int hashCode() { return ObjectUtil.hash(FieldReference.class, _field, _type); }
    
  }
  
  
  
  public static class ObjectFieldReference extends FieldReference {
    private final Expression _object;
    
    public ObjectFieldReference(DJField field, Type type, Expression object) {
      super(field, type);
      _object = object;
    }
    
    
    public Expression object() { return _object; }
  }
  
  
  
  public static class StaticFieldReference extends FieldReference {
    public StaticFieldReference(DJField field, Type type) {
      super(field, type);
    }
  }
  
  public static class TypeSystemException extends Exception {
  }
  
  public static class InvalidTypeArgumentException extends TypeSystemException {
  }
  
  public static class UnsupportedConversionException extends TypeSystemException {
  }
  
  public static class UnmatchedLookupException extends TypeSystemException {
    private final int _matches;
    public UnmatchedLookupException(int matches) { _matches = matches; }
    public int matches() { return _matches; }
  }
  
  
  public static class UnmatchedFunctionLookupException extends UnmatchedLookupException {
    private final Iterable<? extends Function> _candidates;
    public UnmatchedFunctionLookupException(Iterable<? extends Function> candidates) {
      super(0);
      _candidates = candidates;
    }
    public Iterable<? extends Function> candidates() { return _candidates; }
  }
  
  
  public static class AmbiguousFunctionLookupException extends UnmatchedLookupException {
    private final Iterable<? extends Function> _candidates;
    public AmbiguousFunctionLookupException(Iterable<? extends Function> candidates) {
      super(IterUtil.sizeOf(candidates));
      _candidates = candidates;
    }
    public Iterable<? extends Function> candidates() { return _candidates; }
  }
  
}
