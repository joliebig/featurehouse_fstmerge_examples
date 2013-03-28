

package edu.rice.cs.drjava.model.repl.newjvm;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import java.rmi.*;





import edu.rice.cs.util.OutputStreamRedirector;
import edu.rice.cs.util.InputStreamRedirector;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.newjvm.*;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.OptionVisitor;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.text.TextUtil;

import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.model.junit.JUnitModelCallback;
import edu.rice.cs.drjava.model.junit.JUnitTestManager;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.drjava.model.repl.InteractionsPaneOptions;

import edu.rice.cs.dynamicjava.Options;
import edu.rice.cs.dynamicjava.interpreter.*;
import edu.rice.cs.dynamicjava.symbol.*;
import edu.rice.cs.dynamicjava.symbol.type.Type;


import javax.swing.JDialog;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class InterpreterJVM extends AbstractSlaveJVM implements InterpreterJVMRemoteI, JUnitModelCallback {
  
  
  public static final InterpreterJVM ONLY = new InterpreterJVM();
  
  
  
  
  private final InteractionsPaneOptions _interpreterOptions;
  private volatile Pair<String, Interpreter> _activeInterpreter;
  private final Interpreter _defaultInterpreter;
  private final Map<String, Interpreter> _interpreters;
  private final Set<Interpreter> _busyInterpreters;
  private final Map<String, Pair<TypeContext, RuntimeBindings>> _environments;
  
  private final ClassPathManager _classPathManager;
  private final ClassLoader _interpreterLoader;
  
  
  private final JUnitTestManager _junitTestManager;
  
  
  private volatile MainJVMRemoteI _mainJVM;
  
  
  private InterpreterJVM() {
    super("Reset Interactions Thread", "Poll DrJava Thread");
    
    _classPathManager = new ClassPathManager(ReflectUtil.SYSTEM_CLASS_PATH);
    _interpreterLoader = _classPathManager.makeClassLoader(null);
    _junitTestManager = new JUnitTestManager(this, _classPathManager);
    
    
    _interpreterOptions = new InteractionsPaneOptions();
    _defaultInterpreter = new Interpreter(_interpreterOptions, _interpreterLoader);
    _interpreters = new HashMap<String,Interpreter>();
    _busyInterpreters = new HashSet<Interpreter>();
    _environments = new HashMap<String, Pair<TypeContext, RuntimeBindings>>();
    _activeInterpreter = Pair.make("", _defaultInterpreter);
  }
  
  
  protected void handleStart(MasterRemote mainJVM) {
    
    _mainJVM = (MainJVMRemoteI) mainJVM;
    
    
    System.setIn(new InputStreamRedirector() {
      protected String _getInput() {
        try { return _mainJVM.getConsoleInput(); }
        catch(RemoteException re) {
          error.log(re);
          throw new UnexpectedException("Main JVM can't be reached for input.\n" + re);
        }
      }
    });
    
    
    System.setOut(new PrintStream(new OutputStreamRedirector() {
      public void print(String s) {
        try { _mainJVM.systemOutPrint(s); }
        catch (RemoteException re) {
          error.log(re);
          throw new UnexpectedException("Main JVM can't be reached for output.\n" + re);
        }
      }
    }));
    
    
    System.setErr(new PrintStream(new OutputStreamRedirector() {
      public void print(String s) {
        try { _mainJVM.systemErrPrint(s); }
        catch (RemoteException re) {
          error.log(re);
          throw new UnexpectedException("Main JVM can't be reached for output.\n" + re);
        }
      }
    }));
    
    
    if (PlatformFactory.ONLY.isWindowsPlatform()) {
      JDialog d = new JDialog();
      d.setSize(0,0);
      d.setVisible(true);
      d.setVisible(false);
    }
    
  }
  
  
  public InterpretResult interpret(String s) { return interpret(s, _activeInterpreter.second()); }
  
  
  public InterpretResult interpret(String s, String interpreterName) {
    Interpreter i = _interpreters.get(interpreterName);
    if (i == null) {
      throw new IllegalArgumentException("Interpreter '" + interpreterName + "' does not exist.");
    }
    return interpret(s, i);
  }
  
  private InterpretResult interpret(String input, Interpreter interpreter) {
    debug.logStart("Interpret " + input);
    
    boolean available = _busyInterpreters.add(interpreter);
    if (!available) { debug.logEnd(); return InterpretResult.busy(); }
    
    Option<Object> result = null;
    try { result = interpreter.interpret(input); }
    catch (InterpreterException e) { debug.logEnd(); return InterpretResult.exception(e); }
    catch (Throwable e) { debug.logEnd(); return InterpretResult.unexpectedException(e); }
    finally { _busyInterpreters.remove(interpreter); }
    
    return result.apply(new OptionVisitor<Object, InterpretResult>() {
      public InterpretResult forNone() { return InterpretResult.noValue(); }
      public InterpretResult forSome(Object obj) {
        if (obj instanceof String) { debug.logEnd(); return InterpretResult.stringValue((String) obj); }
        else if (obj instanceof Character) { debug.logEnd(); return InterpretResult.charValue((Character) obj); }
        else if (obj instanceof Number) { debug.logEnd(); return InterpretResult.numberValue((Number) obj); }
        else if (obj instanceof Boolean) { debug.logEnd(); return InterpretResult.booleanValue((Boolean) obj); }
        else {
          try {
            String resultString = TextUtil.toString(obj);
            String resultTypeStr = null;
            if (obj!=null) {
                Class<?> c = obj.getClass();
                resultTypeStr = getClassName(c);
            }
            debug.logEnd();
            return InterpretResult.objectValue(resultString,resultTypeStr);
          }
          catch (Throwable t) {
            
            debug.logEnd(); 
            return InterpretResult.exception(new EvaluatorException(t));
          }
        }
      }
    });
  }
  
  
  public Object[] getVariableValue(String var) {
    Pair<Object,String>[] arr = getVariable(var);
    if (arr.length==0) return new Object[0];
    else return new Object[] { arr[0].first() };
  }
  
  
  @SuppressWarnings("unchecked")
  public Pair<Object,String>[] getVariable(String var) {
    InterpretResult ir = interpret(var);
    return ir.apply(new InterpretResult.Visitor<Pair<Object,String>[]>() {
        public Pair<Object,String>[] fail() { return new Pair[0]; }
        public Pair<Object,String>[] value(Object val) {
          return new Pair[] { new Pair<Object,String>(val, getClassName(val.getClass())) };
        }
        public Pair<Object,String>[] forNoValue() { return fail(); }
        public Pair<Object,String>[] forStringValue(String val) { return value(val); }
        public Pair<Object,String>[] forCharValue(Character val) { return value(val); }
        public Pair<Object,String>[] forNumberValue(Number val) { return value(val); }
        public Pair<Object,String>[] forBooleanValue(Boolean val) { return value(val); }
        public Pair<Object,String>[] forObjectValue(String valString, String objTypeString) {
            return new Pair[] { new Pair<Object,String>(valString, objTypeString) }; }
        public Pair<Object,String>[] forException(String message) { return fail(); }
        public Pair<Object,String>[] forEvalException(String message, StackTraceElement[] stackTrace) { return fail(); }
        public Pair<Object,String>[] forUnexpectedException(Throwable t) { return fail(); }
        public Pair<Object,String>[] forBusy() { return fail(); }
    });
  }

  
  public Pair<String,String> getVariableToString(String var) {

    Pair<Object,String>[] val = getVariable(var);
    if (val.length == 0) { return new Pair<String,String>(null,null); }
    else {
      Object o = val[0].first();
      try { return new Pair<String,String>(TextUtil.toString(o),val[0].second()); }
      catch (Throwable t) { return new Pair<String,String>("<error in toString()>",""); }
    }
  }

  
  public static String getClassName(Class<?> c) {
    StringBuilder sb = new StringBuilder();
    boolean isArray = c.isArray();
    while(c.isArray()) {
      sb.append("[]");
      c = c.getComponentType();
    }
    
    if (!isArray) {
      
      if (c.equals(Byte.class))      { return "byte"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); } 
      if (c.equals(Short.class))     { return "short"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Integer.class))   { return "int"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Long.class))      { return "long"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Float.class))     { return "float"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Double.class))    { return "double"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Boolean.class))   { return "boolean"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      if (c.equals(Character.class)) { return "char"+sb.toString()+" or "+c.getSimpleName()+sb.toString(); }
      else return c.getName()+sb.toString();
    }
    else {
      
      return c.getName()+sb.toString();
    }
  }
  
  
  public void addInterpreter(String name) {
    if (_interpreters.containsKey(name)) {
      throw new IllegalArgumentException("'" + name + "' is not a unique interpreter name");
    }
    Interpreter i = new Interpreter(_interpreterOptions, _interpreterLoader);
    _interpreters.put(name, i);
  }
  
  
  public void addInterpreter(String name, Object thisVal, Class<?> thisClass, Object[] localVars,
                             String[] localVarNames, Class<?>[] localVarClasses) {
    debug.logValues(new String[]{ "name", "thisVal", "thisClass", "localVars", "localVarNames",
      "localVarClasses" }, name, thisVal, thisClass, localVars, localVarNames, localVarClasses);
    if (_interpreters.containsKey(name)) {
      throw new IllegalArgumentException("'" + name + "' is not a unique interpreter name");
    }
    if (localVars.length != localVarNames.length || localVars.length != localVarClasses.length) {
      throw new IllegalArgumentException("Local variable arrays are inconsistent");
    }
    
    
    
    Package pkg = thisClass.getPackage();
    DJClass c = SymbolUtil.wrapClass(thisClass);
    List<LocalVariable> vars = new LinkedList<LocalVariable>();
    for (int i = 0; i < localVars.length; i++) {
      if (localVarClasses[i] == null) {
        try { localVarClasses[i] = (Class<?>) localVars[i].getClass().getField("TYPE").get(null); }
        catch (IllegalAccessException e) { throw new IllegalArgumentException(e); }
        catch (NoSuchFieldException e) { throw new IllegalArgumentException(e); }
      }
      Type varT = SymbolUtil.typeOfGeneralClass(localVarClasses[i], _interpreterOptions.typeSystem());
      vars.add(new LocalVariable(localVarNames[i], varT, false));
    }
    
    TypeContext ctx = new ImportContext(_interpreterLoader, _interpreterOptions);
    if (pkg != null) { ctx = ctx.setPackage(pkg.getName()); }
    ctx = new ClassSignatureContext(ctx, c, _interpreterLoader);
    ctx = new ClassContext(ctx, c);
    ctx = new DebugMethodContext(ctx, thisVal == null);
    ctx = new LocalContext(ctx, vars);
    
    RuntimeBindings bindings = RuntimeBindings.EMPTY;
    if (thisVal != null) { bindings = new RuntimeBindings(bindings, c, thisVal); }
    bindings = new RuntimeBindings(bindings, vars, IterUtil.asIterable(localVars));
    
    Interpreter i = new Interpreter(_interpreterOptions, ctx, bindings);
    _environments.put(name, Pair.make(ctx, bindings));
    _interpreters.put(name, i);
  }
  
  
  private static class DebugMethodContext extends DelegatingContext {
    private final boolean _isStatic;
    public DebugMethodContext(TypeContext next, boolean isStatic) { super(next); _isStatic = isStatic; }
    protected TypeContext duplicate(TypeContext next) { return new DebugMethodContext(next, _isStatic); }
    @Override public DJClass getThis() { return _isStatic ? null : super.getThis(); }
    @Override public DJClass getThis(String className) { return _isStatic ? null : super.getThis(className); }
    @Override public Type getReturnType() { return null; }
    @Override public Iterable<Type> getDeclaredThrownTypes() { return IterUtil.empty(); }
  }
  
  
  
  public void removeInterpreter(String name) {
    _interpreters.remove(name);
    _environments.remove(name);
  }
  
  
  
  public synchronized Pair<Boolean, Boolean> setActiveInterpreter(String name) {
    Interpreter i = _interpreters.get(name);
    if (i == null) { throw new IllegalArgumentException("Interpreter '" + name + "' does not exist."); }
    boolean changed = (i != _activeInterpreter.second());
    _activeInterpreter = Pair.make(name, i);
    return Pair.make(changed, _busyInterpreters.contains(i));
  }
  
  
  public synchronized Pair<Boolean, Boolean> setToDefaultInterpreter() {
    boolean changed = (_defaultInterpreter != _activeInterpreter.second());
    _activeInterpreter = Pair.make("", _defaultInterpreter);
    return Pair.make(changed, _busyInterpreters.contains(_defaultInterpreter));
  }
  
  
  public synchronized void setEnforceAllAccess(boolean enforce) {
    _interpreterOptions.setEnforceAllAccess(enforce);
  }
  
  
  public synchronized void setEnforcePrivateAccess(boolean enforce) {
    _interpreterOptions.setEnforcePrivateAccess(enforce);
  }

  
  public synchronized void setRequireSemicolon(boolean require) {
    _interpreterOptions.setRequireSemicolon(require);
  }
  
  
  public synchronized void setRequireVariableType(boolean require) {
    _interpreterOptions.setRequireVariableType(require);
  }
  
  
  
  public List<String> findTestClasses(List<String> classNames, List<File> files) throws RemoteException {
    return _junitTestManager.findTestClasses(classNames, files);
  }
  
  
  public boolean runTestSuite() throws RemoteException { return _junitTestManager.runTestSuite(); }
  
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail) {
    try { _mainJVM.nonTestCase(isTestAll, didCompileFail); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public void classFileError(ClassFileError e) {
    try { _mainJVM.classFileError(e); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public void testSuiteStarted(int numTests) {
    try { _mainJVM.testSuiteStarted(numTests); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public void testStarted(String testName) {
    try { _mainJVM.testStarted(testName); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError) {
    try { _mainJVM.testEnded(testName, wasSuccessful, causedError); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public void testSuiteEnded(JUnitError[] errors) {
    try { _mainJVM.testSuiteEnded(errors); }
    catch (RemoteException re) { error.log(re); }
  }
  
  
  public File getFileForClassName(String className) {
    try { return _mainJVM.getFileForClassName(className); }
    catch (RemoteException re) { error.log(re); return null; }
  }
  
  public void junitJVMReady() { }
  
  
  public void addExtraClassPath(File f) { _classPathManager.addExtraCP(f); }
  public void addProjectClassPath(File f) { _classPathManager.addProjectCP(f); }
  public void addBuildDirectoryClassPath(File f) { _classPathManager.addBuildDirectoryCP(f); }
  public void addProjectFilesClassPath(File f) { _classPathManager.addProjectFilesCP(f); }
  public void addExternalFilesClassPath(File f) { _classPathManager.addExternalFilesCP(f); }
  public Iterable<File> getClassPath() {
    
    return IterUtil.snapshot(_classPathManager.getClassPath());
  }
  
}
