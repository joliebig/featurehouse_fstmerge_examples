

package edu.rice.cs.drjava.model.repl.newjvm;

import java.rmi.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;



import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.drjava.model.junit.JUnitModelCallback;
import edu.rice.cs.drjava.model.debug.DebugModelCallback;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.ArgumentTokenizer;
import edu.rice.cs.util.newjvm.*;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.Utilities;
import koala.dynamicjava.parser.wrapper.*;


public class MainJVM extends AbstractMasterJVM implements MainJVMRemoteI {
  
  private static final String SLAVE_CLASS_NAME = "edu.rice.cs.drjava.model.repl.newjvm.InterpreterJVM";
  
  public static final String DEFAULT_INTERPRETER_NAME = "DEFAULT";
  
  private Log _log = new Log("MainJVMLog", false);
  
  
  private volatile File _workDir;
  
  
  private volatile InteractionsModelCallback _interactionsModel;
  
  
  private volatile JUnitModelCallback _junitModel;
  
  
  private volatile DebugModelCallback _debugModel;
  
  
  private final Object _interpreterLock = new Object();
  
  
  private volatile boolean _slaveJVMUsed = false;
  
  
  private volatile boolean _restart = true;
  
  
  private volatile boolean _cleanlyRestarting = false;
  
  
  private final ResultHandler _handler = new ResultHandler();
  
  
  private volatile boolean _allowAssertions = false;
  
  
  private volatile String _startupClassPath;
  
  
  private volatile ClassPathVector _startupClassPathVector;
  
  
  private volatile List<String> _optionArgs;
  
  
  private volatile String _currentInterpreterName = DEFAULT_INTERPRETER_NAME;
  
  
  public MainJVM(File wd) {
    super(SLAVE_CLASS_NAME);
    _workDir = wd;
    _waitForQuitThreadName = "Wait for Interactions to Exit Thread";
    _exportMasterThreadName = "Export DrJava to RMI Thread";
    
    _interactionsModel = new DummyInteractionsModel();
    _junitModel = new DummyJUnitModel();
    _debugModel = new DummyDebugModel();
    _startupClassPath = System.getProperty("java.class.path");
    _parseStartupClassPath();
    _optionArgs = new ArrayList<String>();
    
  }
  
  private void _parseStartupClassPath() {
    String separator = System.getProperty("path.separator");
    int index = _startupClassPath.indexOf(separator);
    int lastIndex = 0;
    _startupClassPathVector = new ClassPathVector();
    while (index != -1) {
      try{
        _startupClassPathVector.add(new File(_startupClassPath.substring(lastIndex, index)).toURL());
      }
      catch(MalformedURLException murle) {
        
      }
      lastIndex = index + separator.length();
      index = _startupClassPath.indexOf(separator, lastIndex);
    }
    
    index = _startupClassPath.length();
    try{
      _startupClassPathVector.add(new File(_startupClassPath.substring(lastIndex, index)).toURL());
    }
    catch(MalformedURLException murle) {
      
    }
  }
  
  public boolean isInterpreterRunning() { return _interpreterJVM() != null; }
  
  public boolean slaveJVMUsed() { return _slaveJVMUsed; }
  
  
  public void setInteractionsModel(InteractionsModelCallback model) { _interactionsModel = model; }
  
  
  public void setJUnitModel(JUnitModelCallback model) { _junitModel = model; }
  
  
  public void setDebugModel(DebugModelCallback model) { _debugModel = model; }
  
  
  public void setAllowAssertions(boolean allow) { _allowAssertions = allow; }
  
  
  public void setOptionArgs(String argString) {
    _optionArgs = ArgumentTokenizer.tokenize(argString);
  }
  
  
  public void interpret(final String s) {
    
    if (! _restart) return;
    
    ensureInterpreterConnected();
    
    
    
    try {
      _log.logTime("main.interp: " + s);
      _slaveJVMUsed = true;
      _interactionsModel.slaveJVMUsed();
      _interpreterJVM().interpret(s);
    }
    catch (java.rmi.UnmarshalException ume) {
      
      
      _log.logTime("main.interp: UnmarshalException, so interpreter is dead:\n" + ume);
    }
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public String getVariableToString(String var) {
    
    if (! _restart) return null;
    
    ensureInterpreterConnected();
    
    try { return _interpreterJVM().getVariableToString(var); }
    catch (RemoteException re) {
      _threwException(re);
      return null;
    }
  }
  
  
  public String getVariableClassName(String var) {
    
    if (! _restart) return null;
    
    ensureInterpreterConnected();
    
    try { return _interpreterJVM().getVariableClassName(var); }
    catch (RemoteException re) {
      _threwException(re);
      return null;
    }
  }
  
  
  public void interpretResult(InterpretResult result) throws RemoteException {
    
    _log.logTime("main.interp result: " + result);
    result.apply(getResultHandler());
    
    
    
    
  }
  
  
  















  
  public void addProjectClassPath(URL path) {
    if (! _restart) return;
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addProjectClassPath(path.toString()); }
    catch(RemoteException re) { _threwException(re); }
  }
  
  public void addBuildDirectoryClassPath(URL path) {
    if (! _restart) return;
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addBuildDirectoryClassPath(path.toString()); }
    catch(RemoteException re) { _threwException(re); }
  }
  
  public void addProjectFilesClassPath(URL path) {
    if (! _restart) return;
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addProjectFilesClassPath(path.toString()); }
    catch(RemoteException re) { _threwException(re); }
  }
  
  public void addExternalFilesClassPath(URL path) {
    if (! _restart) return;
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addExternalFilesClassPath(path.toString()); }
    catch(RemoteException re) { _threwException(re); }
  }
  
  public void addExtraClassPath(URL path) {
    if (! _restart) return;
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addExtraClassPath(path.toString()); }
    catch(RemoteException re) { _threwException(re); }
  }
  
  
  public ClassPathVector getClassPath() {
    
    if (_restart) {
      
      ensureInterpreterConnected();
      
      try {
        Vector<String> strClassPath = new Vector<String>(_interpreterJVM().getAugmentedClassPath());
        ClassPathVector classPath = new ClassPathVector(strClassPath.size()+_startupClassPathVector.size());
        
        for(String s : strClassPath) { 
          classPath.add(s); 
        }
        
        classPath.addAll(_startupClassPathVector);
        
        
        
        
        
        
        
        return classPath;
      }
      catch (RemoteException re) { _threwException(re); }
    }
    return new ClassPathVector();
  }
  
  
  
  public void setPackageScope(String packageName) {
    
    if (! _restart) return;
    
    ensureInterpreterConnected();
    
    try { _interpreterJVM().setPackageScope(packageName); }
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public void setShowMessageOnResetFailure(boolean show) {
    
    if (! _restart) return;
    
    ensureInterpreterConnected();
    
    try { _interpreterJVM().setShowMessageOnResetFailure(show); }
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public void systemErrPrint(String s) throws RemoteException {
    _interactionsModel.replSystemErrPrint(s);
  }
  
  
  public void systemOutPrint(String s) throws RemoteException {
    _interactionsModel.replSystemOutPrint(s);
  }
  
  
  public List<String> findTestClasses(List<String> classNames, List<File> files) throws RemoteException {

    return _interpreterJVM().findTestClasses(classNames, files);
  }
  
  
  public boolean runTestSuite() throws RemoteException {
    return _interpreterJVM().runTestSuite();
  }
  
  
  public void nonTestCase(boolean isTestAll) throws RemoteException {
    _junitModel.nonTestCase(isTestAll);
  }
  
  
  public void classFileError(ClassFileError e) throws RemoteException {

    _junitModel.classFileError(e);
  }
  
  public void testSuiteStarted(int numTests) throws RemoteException {
    _slaveJVMUsed = true;

    _interactionsModel.slaveJVMUsed();
    _junitModel.testSuiteStarted(numTests);
  }
  
  
  public void testStarted(String testName) throws RemoteException {

    _slaveJVMUsed = true;

    _junitModel.testStarted(testName);
  }
  
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError) throws RemoteException {
    _junitModel.testEnded(testName, wasSuccessful, causedError);
  }
  
  
  public void testSuiteEnded(JUnitError[] errors) throws RemoteException {

    _junitModel.testSuiteEnded(errors);
  }
  
  
  public File getFileForClassName(String className) throws RemoteException {
    return _junitModel.getFileForClassName(className);
  }
  
  
  
  
  private InterpreterJVMRemoteI _interpreterJVM() { return (InterpreterJVMRemoteI) getSlave(); }
  









  
  
  
  public void addJavaInterpreter(String name) {
    
    if (! _restart) return;
    
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addJavaInterpreter(name);  }
    catch (RemoteException re) { _threwException(re);  }
  }
  
  
  public void addDebugInterpreter(String name, String className) {
    
    if (! _restart) return;
    
    ensureInterpreterConnected();
    
    try { _interpreterJVM().addDebugInterpreter(name, className); }
    
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public void removeInterpreter(String name) {
    
    if (!_restart)  return;
    
    ensureInterpreterConnected();
    
    try {
      _interpreterJVM().removeInterpreter(name);
      if (name.equals(_currentInterpreterName))  _currentInterpreterName = null;
    }
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public boolean setActiveInterpreter(String name) {
    
    if (!_restart) return false;
    
    ensureInterpreterConnected();
    
    try {
      boolean result = _interpreterJVM().setActiveInterpreter(name);
      _currentInterpreterName = name;
      return result;
    }
    catch (RemoteException re) {
      _threwException(re);
      return false;
    }
  }
  
  
  public boolean setToDefaultInterpreter() {
    
    if (! _restart) return false;
    
    ensureInterpreterConnected();
    
    try {
      boolean result = _interpreterJVM().setToDefaultInterpreter();
      _currentInterpreterName = DEFAULT_INTERPRETER_NAME;
      return result;
    }
    catch (ConnectIOException ce) {
      _log.logTime("Could not connect to the interpreterJVM after killing it", ce);
      return false;
    }
    catch (RemoteException re) {
      _threwException(re);
      return false;
    }
  }

  
  
  public String getCurrentInterpreterName() { return _currentInterpreterName; }
  
  

  public void killInterpreter(File wd) {
    synchronized(_masterJVMLock) {
      try {

        _workDir = wd;
        _restart = (wd != null);
        _cleanlyRestarting = true;
        if (_restart) _interactionsModel.interpreterResetting();
        quitSlave();  
      }
      catch (ConnectException ce) {
        _log.logTime("Could not connect to the interpreterJVM while trying to kill it", ce);
      }
      catch (RemoteException re) { _threwException(re); }
    }
  }
  
  
  public void setStartupClassPath(String classPath) {
    synchronized(_masterJVMLock) {
      _startupClassPath = classPath;
      _parseStartupClassPath();
    }
  }
  
  
  public void startInterpreterJVM() {

    synchronized(_masterJVMLock) {  
      if (isStartupInProgress() || isInterpreterRunning())  return;
    }
    
    ArrayList<String> jvmArgs = new ArrayList<String>();
    if (allowAssertions())  jvmArgs.add("-ea");
    
    
    
    
    
    int debugPort = getDebugPort();
    _log.logTime("starting with debug port: " + debugPort);
    if (debugPort > -1) {
      jvmArgs.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=" + debugPort);
      jvmArgs.add("-Xdebug");
      jvmArgs.add("-Xnoagent");
      jvmArgs.add("-Djava.compiler=NONE");
    }
    
    
    
    
    
    jvmArgs.addAll(_optionArgs);
    String[] jvmArgsArray = new String[jvmArgs.size()];
    for (int i = 0; i < jvmArgs.size(); i++) { jvmArgsArray[i] = jvmArgs.get(i); }
    
    
    try {
      



      invokeSlave(jvmArgsArray, _startupClassPath, _workDir);
      _slaveJVMUsed = false;
    }
    catch (RemoteException re) { _threwException(re); }
    catch (IOException ioe) { _threwException(ioe); }
  }
  
  
  protected void handleSlaveQuit(int status) {
    


    if (_restart) {
      
      if (!_cleanlyRestarting) _interactionsModel.interpreterResetting();

      startInterpreterJVM();
    }
    
    if (!_cleanlyRestarting) _interactionsModel.replCalledSystemExit(status);
    _cleanlyRestarting = false;
  }
  
  
  protected void slaveQuitDuringStartup(int status) {
    

    _restart = false;
    
    String msg = "Interpreter JVM exited before registering, status: " + status;
    IllegalStateException e = new IllegalStateException(msg);
    _interactionsModel.interpreterResetFailed(e);
    _cleanlyRestarting = false;
    throw e;
  }
  
  
  public void errorStartingSlave(Throwable cause) throws RemoteException {
    new edu.rice.cs.drjava.ui.DrJavaErrorHandler().handle(cause);
  }
  
  
  public void quitFailed(Throwable th) throws RemoteException {
    synchronized(_masterJVMLock) {
      _interactionsModel.interpreterResetFailed(th);
      _cleanlyRestarting = false;
    }
  }
  
  
  
  public boolean isStartupInProgress() { return super.isStartupInProgress(); }
  
  
  protected void handleSlaveConnected() {
    
    

    _restart = true;
    _cleanlyRestarting = false;
    
    Boolean allowAccess = DrJava.getConfig().getSetting(OptionConstants.ALLOW_PRIVATE_ACCESS);
    setPrivateAccessible(allowAccess.booleanValue());
    

    _interactionsModel.interpreterReady(_workDir);
    _junitModel.junitJVMReady();
    
    _log.logTime("thread in connected: " + Thread.currentThread());
    
    synchronized(_interpreterLock) {
      
      _interpreterLock.notify();
    }
  }
  
  
  protected InterpretResultVisitor<Object> getResultHandler() { return _handler; }
  
  
  protected int getDebugPort() {
    int port = -1;
    try {  port = _interactionsModel.getDebugPort(); }
    catch (IOException ioe) {
      
    }
    return port;
  }
  
  
  protected boolean allowAssertions() {
    String version = System.getProperty("java.version");
    return (_allowAssertions && (version != null) && ("1.4.0".compareTo(version) <= 0));
  }
  
  
  private void _threwException(Throwable t) {
    String shortMsg = null;
    if ((t instanceof ParseError) && ((ParseError) t).getParseException() != null) 
      shortMsg = ((ParseError) t).getMessage();  
    _interactionsModel.replThrewException(t.getClass().getName(), t.getMessage(), StringOps.getStackTrace(t), shortMsg);                                    ;
  } 
  
  
  public void setPrivateAccessible(boolean allow) {
    
    if (!_restart) return;
    
    ensureInterpreterConnected();
    try { _interpreterJVM().setPrivateAccessible(allow); }
    catch (RemoteException re) { _threwException(re); }
  }
  
  
  public void ensureInterpreterConnected() {
    try {
      synchronized(_interpreterLock) {
        
        
        
        
        while (_interpreterJVM() == null) {
          
          _interpreterLock.wait();
        }
        
      }
    }
    catch (InterruptedException ie) { throw new edu.rice.cs.util.UnexpectedException(ie); }
  }
  
  
  public String getConsoleInput() { return _interactionsModel.getConsoleInput();  }
  
  
  private class ResultHandler implements InterpretResultVisitor<Object> {
    
    public Object forVoidResult(VoidResult that) {
      _interactionsModel.replReturnedVoid();
      return null;
    }
    
    
    public Object forValueResult(ValueResult that) {
      String result = that.getValueStr();
      String style = that.getStyle();
      _interactionsModel.replReturnedResult(result, style);
      return null;
    }
    
    
    public Object forExceptionResult(ExceptionResult that) { 
      _interactionsModel.replThrewException(that.getExceptionClass(), that.getExceptionMessage(), that.getStackTrace(),
                                            that.getSpecialMessage());
      return null;
    }
    
    
    public Object forSyntaxErrorResult(SyntaxErrorResult that) {
      _interactionsModel.replReturnedSyntaxError(that.getErrorMessage(), that.getInteraction(), that.getStartRow(),
                                                 that.getStartCol(), that.getEndRow(), that.getEndCol() );
      return null;
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
    public void replThrewException(String exceptionClass, String message, String stackTrace, String specialMessage) { }
    public void replReturnedSyntaxError(String errorMessage, String interaction, int startRow, int startCol, int endRow,
                                        int endCol) { }
    public void replCalledSystemExit(int status) { }
    public void interpreterResetting() { }
    public void interpreterResetFailed(Throwable th) { }
    public void interpreterReady(File wd) { }
    public void slaveJVMUsed() { }
  }
  
  
  public static class DummyJUnitModel implements JUnitModelCallback {
    public void nonTestCase(boolean isTestAll) { }
    public void classFileError(ClassFileError e) { }
    public void testSuiteStarted(int numTests) { }
    public void testStarted(String testName) { }
    public void testEnded(String testName, boolean wasSuccessful, boolean causedError) { }
    public void testSuiteEnded(JUnitError[] errors) { }
    public File getFileForClassName(String className) { return null; }
    public ClassPathVector getClassPath() { return new ClassPathVector(); }
    public void junitJVMReady() { }
  }
  
  
  public static class DummyDebugModel implements DebugModelCallback {
    public void notifyDebugInterpreterAssignment(String name) {
    }
  }
}
