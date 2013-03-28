

package edu.rice.cs.drjava.model.repl.newjvm;

import java.rmi.*;
import java.io.*;
import java.net.SocketException;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.drjava.model.junit.JUnitModelCallback;
import edu.rice.cs.drjava.model.debug.DebugModelCallback;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;

import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.concurrent.JVMBuilder;
import edu.rice.cs.plt.concurrent.StateMonitor;

import edu.rice.cs.util.newjvm.*;
import edu.rice.cs.util.classloader.ClassFileError;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class MainJVM extends AbstractMasterJVM implements MainJVMRemoteI {
  
  
  private static final int MAX_STARTUP_FAILURES = 3;
  
  
  private static final int STARTUP_TIMEOUT = 10000;  
  
  
  private final StateMonitor<State> _state;
  
  
  private final ResultHandler _handler = new ResultHandler();
  
  
  private volatile InteractionsModelCallback _interactionsModel;
  
  
  private volatile JUnitModelCallback _junitModel;
  
  
  private volatile DebugModelCallback _debugModel;
  
  
  
  
  
  private volatile boolean _allowAssertions = false;
  
  
  private volatile Iterable<File> _startupClassPath;
  
  
  private volatile File _workingDir;
  
  
  public MainJVM(File wd) {
    super(InterpreterJVM.class.getName());
    _workingDir = wd;
    _interactionsModel = new DummyInteractionsModel();
    _junitModel = new DummyJUnitModel();
    _debugModel = new DummyDebugModel();
    _state = new StateMonitor<State>(new FreshState());
    _startupClassPath = ReflectUtil.SYSTEM_CLASS_PATH;
  }
  
  
  
  
  
  public void startInterpreterJVM() { _state.value().start(); }
  
  
  public void stopInterpreterJVM() { _state.value().stop(); }
  
  
  public void restartInterpreterJVM(boolean force) { _state.value().restart(force); }
    
  
  public void dispose() { _state.value().dispose(); }
  
  
  

  
  protected void handleSlaveConnected(SlaveRemote newSlave) {
    InterpreterJVMRemoteI slaveCast = (InterpreterJVMRemoteI) newSlave;
    _state.value().started(slaveCast);
  }

  
  protected void handleSlaveQuit(int status) {
    debug.logValue("Slave quit", "status", status);
    _state.value().stopped(status);
  }
    
  
  protected void handleSlaveWontStart(Exception e) {
    debug.log("Slave won't start", e);
    _state.value().startFailed(e);
  }
    
  

  
  
  
  
  
  public void systemErrPrint(String s) {
    debug.logStart();
    _interactionsModel.replSystemErrPrint(s);

    debug.logEnd();
  }
  
  
  public void systemOutPrint(String s) {
    debug.logStart();
    _interactionsModel.replSystemOutPrint(s); 

    debug.logEnd();
  }
  
  
  public String getConsoleInput() { 
    String s = _interactionsModel.getConsoleInput(); 
    
    return s; 
  }
 
  
  public void nonTestCase(boolean isTestAll, boolean didCompileFail) {
    _junitModel.nonTestCase(isTestAll, didCompileFail);
  }
 
  
  public void classFileError(ClassFileError e) {
    _junitModel.classFileError(e);
  }
  
  
  public void testSuiteStarted(int numTests) {
    _junitModel.testSuiteStarted(numTests);
  }

  
  public void testStarted(String testName) {
    _junitModel.testStarted(testName);
  }
 
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError) {
    _junitModel.testEnded(testName, wasSuccessful, causedError);
  }
 
  
  public void testSuiteEnded(JUnitError[] errors) {
    _junitModel.testSuiteEnded(errors);
  }
 
  
  public File getFileForClassName(String className) {
    return _junitModel.getFileForClassName(className);
  }
 










 
    
  
  
  
  public void setInteractionsModel(InteractionsModelCallback model) { _interactionsModel = model; }
  
  
  public void setJUnitModel(JUnitModelCallback model) { _junitModel = model; }
  
  
  public void setDebugModel(DebugModelCallback model) { _debugModel = model; }
  
  
  public void setAllowAssertions(boolean allow) { _allowAssertions = allow; }
  
  
  public void setStartupClassPath(String classPath) {
    _startupClassPath = IOUtil.parsePath(classPath);
  }
  
  
  public void setWorkingDirectory(File dir) {
    _workingDir = dir;
  }
  
  
  protected InterpretResult.Visitor<Void> resultHandler() { return _handler; }
  
  
  

  
  public boolean interpret(final String s) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(true);
    if (remote == null) { return false; }
    try {
      debug.logStart("Interpreting " + s);
      InterpretResult result = remote.interpret(s);
      result.apply(resultHandler());
      debug.logEnd("result", result);
      return true;
    }
    catch (RemoteException e) { debug.logEnd(); _handleRemoteException(e); return false; }
  }
  
  
  public Option<String> getVariableToString(String var, int... indices) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.getVariableToString(var,indices)); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }
  
  
  public Option<String> getVariableType(String var, int... indices) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.getVariableType(var,indices)); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }
  
  
  public boolean addProjectClassPath(File f) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addProjectClassPath(f); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean addBuildDirectoryClassPath(File f) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addBuildDirectoryClassPath(f); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean addProjectFilesClassPath(File f) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addProjectFilesClassPath(f); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean addExternalFilesClassPath(File f) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addExternalFilesClassPath(f); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean addExtraClassPath(File f) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addExtraClassPath(f); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public Option<Iterable<File>> getClassPath() {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.getClassPath()); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }
  
  
  public boolean setPackageScope(String packageName) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.interpret("package " + packageName + ";"); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public Option<List<String>> findTestClasses(List<String> classNames, List<File> files) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.findTestClasses(classNames, files)); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }
  
  
  public boolean runTestSuite() { 
    InterpreterJVMRemoteI remote = _state.value().interpreter(true);
    if (remote == null) { return false; }
    try { return remote.runTestSuite(); }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  









  
  
  
  public boolean addInterpreter(String name) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.addInterpreter(name); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean removeInterpreter(String name) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.removeInterpreter(name); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public Option<Pair<Boolean, Boolean>> setActiveInterpreter(String name) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.setActiveInterpreter(name)); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }
  
  
  public Option<Pair<Boolean, Boolean>> setToDefaultInterpreter() {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return Option.none(); }
    try { return Option.some(remote.setToDefaultInterpreter()); }
    catch (RemoteException e) { _handleRemoteException(e); return Option.none(); }
  }

  
  public boolean setEnforceAllAccess(boolean enforce) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.setEnforceAllAccess(enforce); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean setEnforcePrivateAccess(boolean enforce) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.setEnforcePrivateAccess(enforce); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean setRequireSemicolon(boolean require) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.setRequireSemicolon(require); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  public boolean setRequireVariableType(boolean require) {
    InterpreterJVMRemoteI remote = _state.value().interpreter(false);
    if (remote == null) { return false; }
    try { remote.setRequireVariableType(require); return true; }
    catch (RemoteException e) { _handleRemoteException(e); return false; }
  }
  
  
  
  
  private void _doStartup() {
    File dir = _workingDir;
    
    
    if (dir == FileOps.NULL_FILE) { dir = IOUtil.WORKING_DIRECTORY; }

    List<String> jvmArgs = new ArrayList<String>();
    
    File junitLocation = DrJava.getConfig().getSetting(OptionConstants.JUNIT_LOCATION);
    boolean concJUnitLocationConfigured =
      edu.rice.cs.drjava.model.junit.DefaultJUnitModel.isValidConcJUnitFile(junitLocation);
    File rtLocation = DrJava.getConfig().getSetting(OptionConstants.RT_CONCJUNIT_LOCATION);
    boolean rtLocationConfigured =
      edu.rice.cs.drjava.model.junit.DefaultJUnitModel.isValidRTConcJUnitFile(rtLocation);
    if (!rtLocationConfigured && 
        (rtLocation != null) && 
        (!FileOps.NULL_FILE.equals(rtLocation)) && 
        (rtLocation.exists())) { 
      
      DrJava.getConfig().setSetting(OptionConstants.RT_CONCJUNIT_LOCATION, FileOps.NULL_FILE);
      rtLocationConfigured = false;
    }
    if (concJUnitLocationConfigured && rtLocationConfigured) {
      try {
        
        
        
        File shortF = FileOps.getShortFile(rtLocation);
        jvmArgs.add("-Xbootclasspath/p:"+shortF.getAbsolutePath().replace(File.separatorChar, '/'));
      }
      catch(IOException ioe) {
        
        DrJava.getConfig().setSetting(OptionConstants.RT_CONCJUNIT_LOCATION, FileOps.NULL_FILE);
        rtLocationConfigured = false;
      }
    }
    
    if (_allowAssertions) { jvmArgs.add("-ea"); }
    int debugPort = _getDebugPort();
    if (debugPort > -1) {
      jvmArgs.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
      jvmArgs.add("-Xdebug");
      jvmArgs.add("-Xnoagent");
      jvmArgs.add("-Djava.compiler=NONE");
    }
    String slaveMemory = DrJava.getConfig().getSetting(OptionConstants.SLAVE_JVM_XMX);
    if (!"".equals(slaveMemory) && !OptionConstants.heapSizeChoices.get(0).equals(slaveMemory)) {
      jvmArgs.add("-Xmx" + slaveMemory + "M");
    }
    String slaveArgs = DrJava.getConfig().getSetting(OptionConstants.SLAVE_JVM_ARGS);
    if (PlatformFactory.ONLY.isMacPlatform()) {
      jvmArgs.add("-Xdock:name=Interactions");
    }
    
    jvmArgs.addAll(ArgumentTokenizer.tokenize(slaveArgs));
    
    invokeSlave(new JVMBuilder(_startupClassPath).directory(dir).jvmArguments(jvmArgs));
  }
  
  
  private int _getDebugPort() {
    int port = -1;
    try {  port = _interactionsModel.getDebugPort(); }
    catch (IOException ioe) {
      
    }
    return port;
  }
  
  
  private void _handleRemoteException(RemoteException e) {
    if (e instanceof UnmarshalException) {
      
      if (e.getCause() instanceof EOFException) return;
      
      if ((e.getCause() instanceof SocketException) &&
          (e.getCause().getMessage().equals("Connection reset"))) return;
    }
    DrJavaErrorHandler.record(e);
  }
  
  

  
  private abstract class State {
    
    public abstract InterpreterJVMRemoteI interpreter(boolean used); 
    
    public abstract void start();
    
    public abstract void stop();
    
    public abstract void restart(boolean force);
    public abstract void dispose();
    
    public void started(InterpreterJVMRemoteI i) { throw new IllegalStateException("Unexpected started() call"); }
    
    public void startFailed(Exception e) { throw new IllegalStateException("Unexpected startFailed() call"); }
    
    public void stopped(int status) { throw new IllegalStateException("Unexpected stopped() call"); }
  }
  
  
  private class FreshState extends State {
    public InterpreterJVMRemoteI interpreter(boolean used) { return null; } 
    public void start() {
      if (_state.compareAndSet(this, new StartingState())) { _doStartup(); }
      else { _state.value().start(); }
    }
    public void stop() {}
    public void restart(boolean force) { start(); }
    public void dispose() {
      if (_state.compareAndSet(this, new DisposedState())) { MainJVM.super.dispose(); }
      else { _state.value().dispose(); }
    }
  }
  
  
  private class StartingState extends State {
    private final int _failures;
    public StartingState() { _failures = 0; }
    private StartingState(int failures) { _failures = failures; }
    
    public InterpreterJVMRemoteI interpreter(boolean used) {
      try { return _state.ensureNotState(this, STARTUP_TIMEOUT).interpreter(used); }
      catch (TimeoutException e) { return null; }
      catch (InterruptedException e) { throw new UnexpectedException(e); }
    }
    
    public void start() {}
    
    public void restart(boolean force) {
      try { _state.ensureNotState(this, STARTUP_TIMEOUT).restart(force); }
      catch (Exception e) { throw new UnexpectedException(e); }
    }
    
    public void stop() {
      try { _state.ensureNotState(this, STARTUP_TIMEOUT).stop(); }
      catch (Exception e) { throw new UnexpectedException(e); }
    }
    
    public void dispose() { stop(); _state.value().dispose(); }

    @Override public void started(InterpreterJVMRemoteI i) {
      if (_state.compareAndSet(this, new FreshRunningState(i))) {
        boolean enforceAllAccess = DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL)
          .equals(OptionConstants.DynamicJavaAccessControlChoices.PRIVATE_AND_PACKAGE); 
        try { i.setEnforceAllAccess(enforceAllAccess); }
        catch (RemoteException re) { _handleRemoteException(re); }
        
        boolean enforcePrivateAccess = !DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL)
          .equals(OptionConstants.DynamicJavaAccessControlChoices.DISABLED); 
        try { i.setEnforcePrivateAccess(enforcePrivateAccess); }
        catch (RemoteException re) { _handleRemoteException(re); }
        
        Boolean requireSemicolon = DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_REQUIRE_SEMICOLON);
        try { i.setRequireSemicolon(requireSemicolon); }
        catch (RemoteException re) { _handleRemoteException(re); }
        
        Boolean requireVariableType = DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_REQUIRE_VARIABLE_TYPE);
        try { i.setRequireVariableType(requireVariableType); }
        catch (RemoteException re) { _handleRemoteException(re); }
        
        
        
        _interactionsModel.interpreterReady(_workingDir);
        _junitModel.junitJVMReady();        
      }
      else { _state.value().started(i); }
    }
    
    @Override public void startFailed(Exception e) {
      int count = _failures + 1;
      if (count < MAX_STARTUP_FAILURES) {
        if (_state.compareAndSet(this, new StartingState(count))) { _doStartup(); }
        else { _state.value().startFailed(e); }
      }
      else {
        if (_state.compareAndSet(this, new FreshState())) { _interactionsModel.interpreterWontStart(e); }
        else { _state.value().startFailed(e); }
      }
    }
  }
  
  
  private class RunningState extends State {
    protected final InterpreterJVMRemoteI _interpreter;
    public RunningState(InterpreterJVMRemoteI interpreter) { _interpreter = interpreter; }
    public InterpreterJVMRemoteI interpreter(boolean used) { return _interpreter; }
    public void start() {}
    
    public void stop() {
      if (_state.compareAndSet(this, new StoppingState())) { quitSlave(); }
      else { _state.value().stop(); }
    }
    
    public void restart(boolean force) {
      if (_state.compareAndSet(this, new RestartingState())) {
        _interactionsModel.interpreterResetting();
        quitSlave();
      }
      else { _state.value().restart(force); }
    }
    
    public void dispose() { stop(); _state.value().dispose(); }

    @Override public void stopped(int status) {
      if (_state.compareAndSet(this, new RestartingState())) {
        _interactionsModel.replCalledSystemExit(status);
        _interactionsModel.interpreterResetting();
      }
      _state.value().stopped(status); 
    }
  }
  
  
  private class FreshRunningState extends RunningState {
    public FreshRunningState(InterpreterJVMRemoteI interpreter) { super(interpreter); }
    @Override public InterpreterJVMRemoteI interpreter(boolean used) {
      if (used) {
        _state.compareAndSet(this, new RunningState(_interpreter));
        return _state.value().interpreter(used); 
      }
      else { return super.interpreter(used); }
    }
    @Override public void restart(boolean force) {
      if (force) { super.restart(force); }
      else {
        
        _interactionsModel.interpreterReady(_workingDir);
      }
    }
  }
  
  
  private class RestartingState extends State {

    public InterpreterJVMRemoteI interpreter(boolean used) {
      try { return _state.ensureNotState(this, STARTUP_TIMEOUT).interpreter(used); }
      catch (TimeoutException e) { return null; }
      catch (InterruptedException e) { throw new UnexpectedException(e); }
    }

    public void start() {}

    public void stop() {
      if (!_state.compareAndSet(this, new StoppingState())) { _state.value().stop(); }
    }

    public void restart(boolean force) {}

    public void dispose() {
      if (_state.compareAndSet(this, new DisposedState())) { MainJVM.super.dispose(); }
      else { _state.value().dispose(); }
    }
    
    @Override public void stopped(int status) {
      if (_state.compareAndSet(this, new StartingState())) { _doStartup(); }
      else { _state.value().stopped(status); }
    }
  }
  
  
  private class StoppingState extends State {
    public InterpreterJVMRemoteI interpreter(boolean used) { return null; }

    public void start() {
      try { _state.ensureNotState(this, STARTUP_TIMEOUT).start(); }
      catch (Exception e) { throw new UnexpectedException(e); }
    }

    public void stop() {}

    public void restart(boolean force) {
      if (!_state.compareAndSet(this, new RestartingState())) { _state.value().restart(force); }
    }

    public void dispose() {
      if (_state.compareAndSet(this, new DisposedState())) { MainJVM.super.dispose(); }
      else { _state.value().dispose(); }
    }
    
    @Override public void stopped(int status) {
      if (!_state.compareAndSet(this, new FreshState())) { _state.value().stopped(status); } 
    }
  }
  
  private class DisposedState extends State {
    public InterpreterJVMRemoteI interpreter(boolean used) { throw new IllegalStateException("MainJVM is disposed"); }
    public void start() { throw new IllegalStateException("MainJVM is disposed"); }
    public void stop() { throw new IllegalStateException("MainJVM is disposed"); }
    public void restart(boolean force) { throw new IllegalStateException("MainJVM is disposed"); }
    public void dispose() {}
    public void stopped() {  }
  }

  
  
  private class ResultHandler implements InterpretResult.Visitor<Void> {
    
    public Void forNoValue() {
      _interactionsModel.replReturnedVoid();
      return null;
    }
    
    
    public Void forObjectValue(String objString) {
      _interactionsModel.replReturnedResult(objString, InteractionsDocument.OBJECT_RETURN_STYLE);
      return null;
    }
    
    
    public Void forStringValue(String s) {
      _interactionsModel.replReturnedResult('"' + s + '"', InteractionsDocument.STRING_RETURN_STYLE);
      return null;
    }
    
    
    public Void forCharValue(Character c) {
      _interactionsModel.replReturnedResult("'" + c + "'", InteractionsDocument.CHARACTER_RETURN_STYLE);
      return null;
    }
    
    
    public Void forNumberValue(Number n) {
      _interactionsModel.replReturnedResult(n.toString(), InteractionsDocument.NUMBER_RETURN_STYLE);
      return null;
    }
    
    
    public Void forBooleanValue(Boolean b) {
      _interactionsModel.replReturnedResult(b.toString(), InteractionsDocument.OBJECT_RETURN_STYLE);
      return null;
    }
    
    
    public Void forEvalException(String message, StackTraceElement[] stackTrace) {
      
      _interactionsModel.replThrewException(message, stackTrace);
      return null;
    }
    
    
    public Void forException(String message) {
      
      _interactionsModel.replThrewException(message);
      return null;
    }
    
    public Void forUnexpectedException(Throwable t) {
      _interactionsModel.replReturnedVoid();
      throw new UnexpectedException(t);
    }
    
    public Void forBusy() {
      _interactionsModel.replReturnedVoid();
      throw new UnexpectedException("MainJVM.interpret() called when InterpreterJVM was busy!");
    }
  }
  
  
  public static class DummyInteractionsModel implements InteractionsModelCallback {
    public int getDebugPort() throws IOException { return -1; }
    public void replSystemOutPrint(String s) { }
    public void replSystemErrPrint(String s) { }
    public String getConsoleInput() {
      throw new IllegalStateException("Cannot request input from dummy interactions model!");
    }
    public void setInputListener(InputListener il) {
      throw new IllegalStateException("Cannot set the input listener of dummy interactions model!");
    }
    public void changeInputListener(InputListener from, InputListener to) {
      throw new IllegalStateException("Cannot change the input listener of dummy interactions model!");
    }
    public void replReturnedVoid() { }
    public void replReturnedResult(String result, String style) { }
    public void replThrewException(String message, StackTraceElement[] stackTrace) { }
    public void replThrewException(String message) { }
    public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol, int endRow,
                                        int endCol) { }
    public void replCalledSystemExit(int status) { }
    public void interpreterResetting() { }
    public void interpreterResetFailed(Throwable th) { }
    public void interpreterWontStart(Exception e) { }
    public void interpreterReady(File wd) { }
  }
  
  
  public static class DummyJUnitModel implements JUnitModelCallback {
    public void nonTestCase(boolean isTestAll, boolean didCompileFail) { }
    public void classFileError(ClassFileError e) { }
    public void testSuiteStarted(int numTests) { }
    public void testStarted(String testName) { }
    public void testEnded(String testName, boolean wasSuccessful, boolean causedError) { }
    public void testSuiteEnded(JUnitError[] errors) { }
    public File getFileForClassName(String className) { return null; }
    public Iterable<File> getClassPath() { return IterUtil.empty(); }
    public void junitJVMReady() { }
  }
  
  
  public static class DummyDebugModel implements DebugModelCallback {
    public void notifyDebugInterpreterAssignment(String name) {
    }
  }
}
