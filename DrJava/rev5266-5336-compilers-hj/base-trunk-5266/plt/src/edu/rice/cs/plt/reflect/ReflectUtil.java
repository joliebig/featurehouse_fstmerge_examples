

package edu.rice.cs.plt.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.*;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;

import static edu.rice.cs.plt.reflect.ReflectException.*;
import static edu.rice.cs.plt.debug.DebugUtil.debug;

public final class ReflectUtil {
  
  
  private ReflectUtil() {}
  
  
  public static final ClassLoader BOOT_CLASS_LOADER = new ClassLoader(null) {};
  
  
  public static final Iterable<File> SYSTEM_CLASS_PATH = IOUtil.parsePath(System.getProperty("java.class.path", ""));
  
  
  public static String simpleName(Class<?> c) {
    if (c.isArray()) { return simpleName(c.getComponentType()) + "[]"; }
    else if (isAnonymousClass(c)) {
      if (c.getInterfaces().length > 0) { return "anonymous " + simpleName(c.getInterfaces()[0]); }
      else { return "anonymous " + simpleName(c.getSuperclass()); }
    }
    else {
      String fullName = c.getName();
      int dot = fullName.lastIndexOf('.');
      int dollar = fullName.lastIndexOf('$');
      int nameStart = (dot > dollar) ? dot+1 : dollar+1;
      int length = fullName.length();
      while (nameStart < length && Character.isDigit(fullName.charAt(nameStart))) { nameStart++; }
      return fullName.substring(nameStart);
    }
  }
  
  
  public static boolean isAnonymousClass(Class<?> c) {
    String name = c.getName();
    String nameEnd = name.substring(name.lastIndexOf('$') + 1); 
    for (int i = 0; i < nameEnd.length(); i++) {
      if (Character.isJavaIdentifierStart(nameEnd.charAt(i))) { return false; }
    }
    return true;
  }
  
  
  public static Class<?> arrayBaseClass(Class<?> c) {
    Class<?> result = c;
    while (result.isArray()) { result = result.getComponentType(); }
    return result;
  }
  
  
  public static int arrayDimensions(Class<?> c) {
    Class<?> rest = c;
    int result = 0;
    while (rest.isArray()) { rest = rest.getComponentType(); result++; }
    return result;
  }
  
  
  public static <T> T cast(Class<? extends T> c, Object o) throws ClassCastException {
    if (box(c).isInstance(o)) {
      @SuppressWarnings("unchecked") T result = (T) o;
      return result;
    }
    else { throw new ClassCastException("Casting to " + c.getName() + " from " + o.getClass().getName()); }
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Class<? extends T> getClass(T obj) {
    return (Class<? extends T>) obj.getClass();
  }
  
  
  @SuppressWarnings("unchecked") public static <T> Class<T> box(Class<T> c) {
    if (c.isPrimitive()) {
      if (c == Boolean.TYPE) { return (Class<T>) Boolean.class; }
      if (c == Character.TYPE) { return (Class<T>) Character.class; }
      if (c == Byte.TYPE) { return (Class<T>) Byte.class; }
      if (c == Short.TYPE) { return (Class<T>) Short.class; }
      if (c == Integer.TYPE) { return (Class<T>) Integer.class; }
      if (c == Long.TYPE) { return (Class<T>) Long.class; }
      if (c == Float.TYPE) { return (Class<T>) Float.class; }
      if (c == Double.TYPE) { return (Class<T>) Double.class; }
      if (c == Void.TYPE) { return (Class<T>) Void.class; }
    }
    return c;
  }
  

  private static final ClassLoader CURRENT_LOADER = ReflectUtil.class.getClassLoader();
  
  
  private static Class<?>[] getClasses(Object[] args) {
    if (args.length > 0) {
      Class<?>[] sig = new Class<?>[args.length];
      for (int i = 0; i < args.length; i++) { sig[i] = args[i].getClass(); }
      return sig;
    }
    else { return null; }
  }
  
  
  public static Object loadObject(String className, Object... constructorArgs) throws ReflectException {
    return loadObject(CURRENT_LOADER, className, getClasses(constructorArgs), constructorArgs);
  }
  
  
  public static Object loadObject(String className, Class<?>[] constructorSig, Object... constructorArgs) 
    throws ReflectException {
    return loadObject(CURRENT_LOADER, className, constructorSig, constructorArgs);
  }
  
  
  public static Object loadObject(ClassLoader loader, String className, Object... constructorArgs) 
    throws ReflectException {
    return loadObject(loader, className, getClasses(constructorArgs), constructorArgs);
  }
  
  
  public static Object loadObject(ClassLoader loader, String className, Class<?>[] constructorSig, 
                                  Object... constructorArgs) throws ReflectException {
    try {
      Class<?> c = Class.forName(className, true, loader);
      Constructor<?> k = c.getConstructor(constructorSig);
      return k.newInstance(constructorArgs);
    }
    catch (ClassNotFoundException e) { throw new ClassNotFoundReflectException(e); }
    catch (NoSuchMethodException e) { throw new NoSuchMethodReflectException(e); }
    catch (IllegalArgumentException e) { throw new IllegalArgumentReflectException(e); }
    catch (InvocationTargetException e) { throw new InvocationTargetReflectException(e); }
    catch (InstantiationException e) { throw new InstantiationReflectException(e); }
    catch (IllegalAccessException e) { throw new IllegalAccessReflectException(e); }
    catch (SecurityException e) { throw new SecurityReflectException(e); }
  }
  
  public static Object getStaticField(String className, String fieldName) throws ReflectException {
    return getStaticField(CURRENT_LOADER, className, fieldName);
  }
  
  public static Object getStaticField(ClassLoader loader, String className, String fieldName) throws ReflectException {
    try {
      Class<?> c = Class.forName(className, true, loader);
      Field f = c.getField(fieldName);
      try { return f.get(null); }
      
      catch (NullPointerException e) { throw new NullPointerReflectException(e); }
    }
    catch (ClassNotFoundException e) { throw new ClassNotFoundReflectException(e); }
    catch (NoSuchFieldException e) { throw new NoSuchFieldReflectException(e); }
    catch (IllegalArgumentException e) { throw new IllegalArgumentReflectException(e); }
    catch (IllegalAccessException e) { throw new IllegalAccessReflectException(e); }
    catch (SecurityException e) { throw new SecurityReflectException(e); }
  }
  
  public static Object invokeStaticMethod(String className, String methodName, Object... args) 
    throws ReflectException {
    return invokeStaticMethod(CURRENT_LOADER, className, methodName, getClasses(args), args);
  }
  
  public static Object invokeStaticMethod(ClassLoader loader, String className, String methodName, Object... args) 
    throws ReflectException {
    return invokeStaticMethod(loader, className, methodName, getClasses(args), args);
  }
  
  public static Object invokeStaticMethod(String className, String methodName, Class<?>[] signature, Object... args) 
    throws ReflectException {
    return invokeStaticMethod(CURRENT_LOADER, className, methodName, signature, args);
  }
  
  public static Object invokeStaticMethod(ClassLoader loader, String className, String methodName, Class<?>[] signature,
                                          Object... args) throws ReflectException {
    try {
      Class<?> c = Class.forName(className, true, loader);
      Method m = c.getMethod(methodName, signature);
      try { return m.invoke(null, args); }
      
      catch (NullPointerException e) { throw new NullPointerReflectException(e); }
    }
    catch (ClassNotFoundException e) { throw new ClassNotFoundReflectException(e); }
    catch (NoSuchMethodException e) { throw new NoSuchMethodReflectException(e); }
    catch (IllegalArgumentException e) { throw new IllegalArgumentReflectException(e); }
    catch (InvocationTargetException e) { throw new InvocationTargetReflectException(e); }
    catch (IllegalAccessException e) { throw new IllegalAccessReflectException(e); }
    catch (SecurityException e) { throw new SecurityReflectException(e); }
  }
  
  
  
  public static Object loadLibraryAdapter(Iterable<? extends File> libraryPath, String adapterName, 
                                          Object... constructorArgs) throws ReflectException {
    return loadLibraryAdapter(CURRENT_LOADER, libraryPath, adapterName, getClasses(constructorArgs), constructorArgs);
  }

  
  public static Object loadLibraryAdapter(Iterable<? extends File> libraryPath, String adapterName, 
                                          Class<?>[] constructorSig,Object... constructorArgs) throws ReflectException {
    return loadLibraryAdapter(CURRENT_LOADER, libraryPath, adapterName, constructorSig, constructorArgs);
  }

  
  public static Object loadLibraryAdapter(ClassLoader baseLoader, Iterable<? extends File> libraryPath, 
                                          String adapterName, Object... constructorArgs) throws ReflectException {
    return loadLibraryAdapter(baseLoader, libraryPath, adapterName, getClasses(constructorArgs), constructorArgs);
  }

  
  public static Object loadLibraryAdapter(ClassLoader baseLoader, Iterable<? extends File> libraryPath, 
                                          String adapterName, Class<?>[] constructorSig, Object... constructorArgs) 
    throws ReflectException {
    ClassLoader libraryLoader = new PathClassLoader(baseLoader, libraryPath);
    ClassLoader adapterLoader = new PreemptingClassLoader(libraryLoader, adapterName);
    return loadObject(adapterLoader, adapterName, constructorSig, constructorArgs);
  }
  
  
  public static ComposedClassLoader mergeLoaders(ClassLoader first, ClassLoader second) {
    return new ComposedClassLoader(first, second);
  }
  
  
  public static ComposedClassLoader mergeLoaders(ClassLoader first, ClassLoader second, String... firstIncludes) {
    return mergeLoaders(first, second, false, firstIncludes);
  }
  
  
  public static ComposedClassLoader mergeLoaders(ClassLoader first, ClassLoader second, boolean blackList,
                                                 String... firstPrefixes) {
    ClassLoader filteredFirst = new ShadowingClassLoader(first, blackList, IterUtil.asIterable(firstPrefixes), false);
    return new ComposedClassLoader(filteredFirst, second);
  }
  
  
  public static <T> Box<T> staticFieldAsBox(Class<?> c, String fieldName, Class<T> fieldType) {
    return new FieldBox<T>(c, fieldName, fieldType, null);
  }
  
  
  public static <T> Box<T> fieldAsBox(Object object, String fieldName, Class<T> fieldType) {
    return new FieldBox<T>(object.getClass(), fieldName, fieldType, object);
  }
  
  private static final class FieldBox<T> implements Box<T>, Serializable {
    private final Class<?> _objClass;
    private final String _name;
    private final Class<T> _type;
    private final Object _obj;
    private transient Field _field; 
    
    public FieldBox(Class<?> objClass, String name, Class<T> type, Object obj) {
      _objClass = objClass;
      _name = name;
      _type = type;
      _obj = obj;
      _field = null;
    }
    
    public T value() {
      try {
        if (_field == null) { _field = _objClass.getField(_name); }
        try { return cast(_type, _field.get(_obj)); }
        
        catch (NullPointerException e) { throw new WrappedException(new NullPointerReflectException(e)); }
      }
      catch (NoSuchFieldException e) { throw new WrappedException(new NoSuchFieldReflectException(e)); }
      catch (IllegalArgumentException e) { throw new WrappedException(new IllegalArgumentReflectException(e)); }
      catch (IllegalAccessException e) { throw new WrappedException(new IllegalAccessReflectException(e)); }
      catch (ClassCastException e) { throw new WrappedException(new ClassCastReflectException(e)); }
      catch (SecurityException e) { throw new WrappedException(new SecurityReflectException(e)); }
    }
    
    public void set(T value) {
      try {
        if (_field == null) { _field = _objClass.getField(_name); }
        _field.set(_obj, value);
      }
      catch (NoSuchFieldException e) { throw new WrappedException(new NoSuchFieldReflectException(e)); }
      catch (IllegalArgumentException e) { throw new WrappedException(new IllegalArgumentReflectException(e)); }
      catch (IllegalAccessException e) { throw new WrappedException(new IllegalAccessReflectException(e)); }
      catch (SecurityException e) { throw new WrappedException(new SecurityReflectException(e)); }
    }
  }
      

  
  public static <O, R> Thunk<R> staticMethodAsThunk(Class<? super O> c, String methodName, Class<? extends R> retT) {
    return LambdaUtil.bindFirst(new MethodLambda<O, R>(c, methodName, retT), null);
  }
  
  
  public static <R> Thunk<R> methodAsThunk(Object object, String methodName, Class<? extends R> retT) {
    return new ObjectMethodThunk<R>(object, methodName, retT);
  }
  
  
  public static <O, R> Lambda<O, R> methodAsLambda(Class<? super O> c, String methodName, Class<? extends R> retT) {
    return new MethodLambda<O, R>(c, methodName, retT);
  }
  
  
  public static <O, T, R> Lambda<T, R> staticMethodAsLambda(Class<? super O> c, String methodName, 
                                                            Class<? super T> argT, Class<? extends R> retT) {
    return LambdaUtil.bindFirst(new MethodLambda2<O, T, R>(c, methodName, argT, retT), null);
  }
  
  
  public static <T, R> Lambda<T, R> methodAsLambda(Object object, String methodName, Class<? super T> argT,
                                                   Class<? extends R> retT) {
    return new ObjectMethodLambda<T, R>(object, methodName, argT, retT);
  }
  
  
  public static <O, T, R> Lambda2<O, T, R> methodAsLambda2(Class<? super O> c, String methodName, 
                                                           Class<? super T> argT, Class<? extends R> retT) {
    return new MethodLambda2<O, T, R>(c, methodName, argT, retT);
  }
  
  
  public static <O, T1, T2, R> 
    Lambda2<T1, T2, R> staticMethodAsLambda2(Class<? super O> c, String methodName, Class<? super T1> arg1T, 
                                             Class<? super T2> arg2T, Class<? extends R> retT) {
    return LambdaUtil.bindFirst(new MethodLambda3<O, T1, T2, R>(c, methodName, arg1T, arg2T, retT), null);
  }
  
  
  public static <T1, T2, R>
    Lambda2<T1, T2, R> methodAsLambda2(Object object, String methodName, Class<? super T1> arg1T, 
                                       Class<? super T2> arg2T, Class<? extends R> retT) {
    return new ObjectMethodLambda2<T1, T2, R>(object, methodName, arg1T, arg2T, retT);
  }
  
  
  public static <O, T1, T2, R>
    Lambda3<O, T1, T2, R> methodAsLambda3(Class<? super O> c, String methodName, Class<? super T1> arg1T,
                                          Class<? super T2> arg2T, Class<? extends R> retT) {
    return new MethodLambda3<O, T1, T2, R>(c, methodName, arg1T, arg2T, retT);
  }
  
  
  public static <O, T1, T2, T3, R> 
    Lambda3<T1, T2, T3, R> staticMethodAsLambda3(Class<? super O> c, String methodName, Class<? super T1> arg1T, 
                                                 Class<? super T2> arg2T, Class<? super T3> arg3T,
                                                 Class<? extends R> retT) {
    return LambdaUtil.bindFirst(new MethodLambda4<O, T1, T2, T3, R>(c, methodName, arg1T, arg2T, arg3T, retT), null);
  }
  
  
  public static <T1, T2, T3, R>
    Lambda3<T1, T2, T3, R> methodAsLambda3(Object object, String methodName, Class<? super T1> arg1T, 
                                           Class<? super T2> arg2T, Class<? super T3> arg3T, Class<? extends R> retT) {
    return new ObjectMethodLambda3<T1, T2, T3, R>(object, methodName, arg1T, arg2T, arg3T, retT);
  }
  
  
  public static <O, T1, T2, T3, R>
    Lambda4<O, T1, T2, T3, R> methodAsLambda4(Class<? super O> c, String methodName, Class<? super T1> arg1T,
                                              Class<? super T2> arg2T, Class<? super T3> arg3T, 
                                              Class<? extends R> retT) {
    return new MethodLambda4<O, T1, T2, T3, R>(c, methodName, arg1T, arg2T, arg3T, retT);
  }
  
  
  
  private static abstract class MethodWrapper<R> implements Serializable {
    private final Class<?> _objClass;
    private final String _name;
    private final Class<? extends R> _returnType;
    private final Class<?>[] _signature;
    private transient Method _method; 
    
    protected MethodWrapper(Class<?> objClass, String name, Class<? extends R> returnType, Class<?>... signature) {
      _objClass = objClass;
      _name = name;
      _returnType = returnType;
      _signature = signature;
      _method = null;
    }
    
    
    protected R invoke(Object obj, Object... args) {
      try {
        if (_method == null) { _method = _objClass.getMethod(_name, _signature); }
        try { return cast(_returnType, _method.invoke(obj, args)); }
        
        catch (NullPointerException e) { throw new WrappedException(new NullPointerReflectException(e)); }
      }
      catch (NoSuchMethodException e) { throw new WrappedException(new NoSuchMethodReflectException(e)); }
      catch (IllegalArgumentException e) { throw new WrappedException(new IllegalArgumentReflectException(e)); }
      catch (InvocationTargetException e) { throw new WrappedException(new InvocationTargetReflectException(e)); }
      catch (IllegalAccessException e) { throw new WrappedException(new IllegalAccessReflectException(e)); }
      catch (ClassCastException e) { throw new WrappedException(new ClassCastReflectException(e)); }
      catch (SecurityException e) { throw new WrappedException(new SecurityReflectException(e)); }
    }
  }

  private static final class ObjectMethodThunk<R> extends MethodWrapper<R> implements Thunk<R> {
    private final Object _obj;
    public ObjectMethodThunk(Object obj, String name, Class<? extends R> returnT) {
      super(obj.getClass(), name, returnT); _obj = obj;
    }
    public R value() { return invoke(_obj); }
  }
  
  private static final class MethodLambda<O, R> extends MethodWrapper<R> implements Lambda<O, R> {
    public MethodLambda(Class<? super O> objT, String name, Class<? extends R> returnT) {
      super(objT, name, returnT);
    }
    public R value(O obj) { return invoke(obj); }
  }
  
  private static final class ObjectMethodLambda<T, R> extends MethodWrapper<R> implements Lambda<T, R> {
    private final Object _obj;
    public ObjectMethodLambda(Object obj, String name, Class<? super T> argT, Class<? extends R> returnT) {
      super(obj.getClass(), name, returnT, argT); _obj = obj;
    }
    public R value(T arg) { return invoke(_obj, arg); }
  }
  
  private static final class MethodLambda2<O, T, R> extends MethodWrapper<R> implements Lambda2<O, T, R> {
    public MethodLambda2(Class<? super O> objT, String name, Class<? super T> argT, Class<? extends R> returnT) {
      super(objT, name, returnT, argT);
    }
    public R value(O obj, T arg) { return invoke(obj, arg); }
  }
  
  private static final class ObjectMethodLambda2<T1, T2, R> extends MethodWrapper<R> implements Lambda2<T1, T2, R> {
    private final Object _obj;
    public ObjectMethodLambda2(Object obj, String name, Class<? super T1> arg1T, Class<? super T2> arg2T,
                               Class<? extends R> returnT) {
      super(obj.getClass(), name, returnT, arg1T, arg2T); _obj = obj;
    }
    public R value(T1 arg1, T2 arg2) { return invoke(_obj, arg1, arg2); }
  }
  
  private static final class MethodLambda3<O, T1, T2, R> extends MethodWrapper<R> implements Lambda3<O, T1, T2, R> {
    public MethodLambda3(Class<? super O> objT, String name, Class<? super T1> arg1T, Class<? super T2> arg2T,
                         Class<? extends R> returnT) {
      super(objT, name, returnT, arg1T, arg2T);
    }
    public R value(O obj, T1 arg1, T2 arg2) { return invoke(obj, arg1, arg2); }
  }
  
  private static final class ObjectMethodLambda3<T1, T2, T3, R> extends MethodWrapper<R> 
    implements Lambda3<T1, T2, T3, R> {
    private final Object _obj;
    public ObjectMethodLambda3(Object obj, String name, Class<? super T1> arg1T, Class<? super T2> arg2T,
                               Class<? super T3> arg3T, Class<? extends R> returnT) {
      super(obj.getClass(), name, returnT, arg1T, arg2T, arg3T); _obj = obj;
    }
    public R value(T1 arg1, T2 arg2, T3 arg3) { return invoke(_obj, arg1, arg2, arg3); }
  }
  
  private static final class MethodLambda4<O, T1, T2, T3, R> extends MethodWrapper<R> 
    implements Lambda4<O, T1, T2, T3, R> {
    public MethodLambda4(Class<? super O> objT, String name, Class<? super T1> arg1T, Class<? super T2> arg2T,
                         Class<? super T3> arg3T, Class<? extends R> returnT) {
      super(objT, name, returnT, arg1T, arg2T, arg3T);
    }
    public R value(O obj, T1 arg1, T2 arg2, T3 arg3) { return invoke(obj, arg1, arg2, arg3); }
  }
  
  
  
  public static <R> Thunk<R> constructorAsThunk(Class<? extends R> c) {
    return new ConstructorThunk<R>(c);
  }
  
  
  public static <T, R> Lambda<T, R> constructorAsLambda(Class<? extends R> c, Class<? super T> argT) {
    return new ConstructorLambda<T, R>(c, argT);
  }
  
  
  public static <T1, T2, R> Lambda2<T1, T2, R> constructorAsLambda2(Class<? extends R> c, Class<? super T1> arg1T, 
                                                                    Class<? super T2> arg2T) {
    return new ConstructorLambda2<T1, T2, R>(c, arg1T, arg2T);
  }
  
  
  public static <T1, T2, T3, R> Lambda3<T1, T2, T3, R>
    constructorAsLambda3(Class<? extends R> c, Class<? super T1> arg1T, Class<? super T2> arg2T, 
                         Class<? super T3> arg3T) {
    return new ConstructorLambda3<T1, T2, T3, R>(c, arg1T, arg2T, arg3T);
  }
  
  
  public static <T1, T2, T3, T4, R> Lambda4<T1, T2, T3, T4, R>
    constructorAsLambda4(Class<? extends R> c, Class<? super T1> arg1T, Class<? super T2> arg2T, 
                         Class<? super T3> arg3T, Class<? super T4> arg4T) {
    return new ConstructorLambda4<T1, T2, T3, T4, R>(c, arg1T, arg2T, arg3T, arg4T);
  }
  
  
  
  private static abstract class ConstructorWrapper<R> implements Serializable {
    private final Class<? extends R> _c;
    private final Class<?>[] _signature;
    private transient Constructor<? extends R> _k; 
    
    protected ConstructorWrapper(Class<? extends R> c, Class<?>... signature) {
      _c = c;
      _signature = signature;
      _k = null;
    }
    
    
    protected R invoke(Object... args) {
      try {
        if (_k == null) { _k = _c.getConstructor(_signature); }
        return _k.newInstance(args);
      }
      catch (NoSuchMethodException e) { throw new WrappedException(new NoSuchMethodReflectException(e)); }
      catch (IllegalArgumentException e) { throw new WrappedException(new IllegalArgumentReflectException(e)); }
      catch (InvocationTargetException e) { throw new WrappedException(new InvocationTargetReflectException(e)); }
      catch (IllegalAccessException e) { throw new WrappedException(new IllegalAccessReflectException(e)); }
      catch (InstantiationException e) { throw new WrappedException(new InstantiationReflectException(e)); }
      catch (SecurityException e) { throw new WrappedException(new SecurityReflectException(e)); }
    }
  }

  private static final class ConstructorThunk<R> extends ConstructorWrapper<R> implements Thunk<R> {
    public ConstructorThunk(Class<? extends R> c) { super(c); }
    public R value() { return invoke(); }
  }
  
  private static final class ConstructorLambda<T, R> extends ConstructorWrapper<R> implements Lambda<T, R> {
    public ConstructorLambda(Class<? extends R> c, Class<? super T> argT) { super(c, argT); }
    public R value(T arg) { return invoke(arg); }
  }
  
  private static final class ConstructorLambda2<T1, T2, R> extends ConstructorWrapper<R>
    implements Lambda2<T1, T2, R> {
    public ConstructorLambda2(Class<? extends R> c, Class<? super T1> arg1T, Class<? super T2> arg2T) {
      super(c, arg1T, arg2T);
    }
    public R value(T1 arg1, T2 arg2) { return invoke(arg1, arg2); }
  }
  
  private static final class ConstructorLambda3<T1, T2, T3, R> extends ConstructorWrapper<R>
    implements Lambda3<T1, T2, T3, R> {
    public ConstructorLambda3(Class<? extends R> c, Class<? super T1> arg1T, Class<? super T2> arg2T,
                              Class<? super T3> arg3T) {
      super(c, arg1T, arg2T, arg3T);
    }
    public R value(T1 arg1, T2 arg2, T3 arg3) { return invoke(arg1, arg2, arg3); }
  }
  
  private static final class ConstructorLambda4<T1, T2, T3, T4, R> extends ConstructorWrapper<R>
    implements Lambda4<T1, T2, T3, T4, R> {
    public ConstructorLambda4(Class<? extends R> c, Class<? super T1> arg1T, Class<? super T2> arg2T,
                              Class<? super T3> arg3T, Class<? super T4> arg4T) {
      super(c, arg1T, arg2T, arg3T, arg4T);
    }
    public R value(T1 arg1, T2 arg2, T3 arg3, T4 arg4) { return invoke(arg1, arg2, arg3, arg4); }
  }
}
