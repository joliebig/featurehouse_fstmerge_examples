

package edu.rice.cs.drjava.model.repl.newjvm;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.io.*;

import java.rmi.*;
import java.net.URL;
import java.net.MalformedURLException;





import edu.rice.cs.util.newjvm.*;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.OutputStreamRedirector;
import edu.rice.cs.util.InputStreamRedirector;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.drjava.platform.PlatformFactory;
import edu.rice.cs.drjava.model.junit.JUnitModelCallback;
import edu.rice.cs.drjava.model.junit.JUnitTestManager;
import edu.rice.cs.drjava.model.junit.JUnitError;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.ClassPathEntry;


import javax.swing.JDialog;

import koala.dynamicjava.parser.wrapper.*;
import koala.dynamicjava.parser.*;


public class InterpreterJVM extends AbstractSlaveJVM implements InterpreterJVMRemoteI, JUnitModelCallback {
  
  private static final boolean printMessages = false;
  
  public static final InterpreterJVM ONLY = new InterpreterJVM();
  
  
  public static final String EMPTY_TRACE_TEXT = "";
  
  
  
  private MainJVMRemoteI _mainJVM;
  
  
  private InterpreterData _defaultInterpreter;
  
  
  private Hashtable<String,InterpreterData> _interpreters;
  
  
  private InterpreterData _activeInterpreter;
  
  
  private ClassPathVector _classPath;
  
  
  private JUnitTestManager _junitTestManager;
  
  
  ClassPathManager classPathManager;
  
  
  
  
  
  private boolean _messageOnResetFailure;
  
  
  private InterpreterJVM() {
    _quitSlaveThreadName = "Reset Interactions Thread";
    _pollMasterThreadName = "Poll DrJava Thread";
    reset();
    
    _messageOnResetFailure = true;
  }
  
  
  private void reset() {
    classPathManager = new ClassPathManager();
    _defaultInterpreter = new InterpreterData(new DynamicJavaAdapter(classPathManager));
    _activeInterpreter = _defaultInterpreter;
    _interpreters = new Hashtable<String,InterpreterData>();
    _classPath = new ClassPathVector();
    _junitTestManager = new JUnitTestManager(this);
    
    
    try { _activeInterpreter.getInterpreter().interpret("0"); }
    catch (ExceptionReturnedException e) { throw new edu.rice.cs.util.UnexpectedException(e); }
  }
  









  
  private static final Log _log = new Log("IntJVMLog", false);
  private static void _dialog(String s) {
    
    _log.logTime(s);
  }
  
  
  protected void handleStart(MasterRemote mainJVM) {
    
    _mainJVM = (MainJVMRemoteI) mainJVM;
    






    
    
    System.setIn(new InputStreamRedirector() {
      protected String _getInput() {
        try { return _mainJVM.getConsoleInput(); }
        catch(RemoteException re) {
          
          _log.logTime("System.in: " + re.toString());
          throw new IllegalStateException("Main JVM can't be reached for input.\n" + re);
        }
      }
    });
    
    
    System.setOut(new PrintStream(new OutputStreamRedirector() {
      public void print(String s) {
        try {
          
          _mainJVM.systemOutPrint(s);
        }
        catch (RemoteException re) {
          
          _log.logTime("System.out: " + re.toString());
        }
      }
    }));
    
    
    System.setErr(new PrintStream(new OutputStreamRedirector() {
      public void print(String s) {
        try {
          
          _mainJVM.systemErrPrint(s);
        }
        catch (RemoteException re) {
          
          _log.logTime("System.err: " + re.toString());
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
  
  
  
  public void interpret(String s) { interpret(s, _activeInterpreter); }
  
  
  public void interpret(String s, String interpreterName) {
    interpret(s, getInterpreter(interpreterName));
  }
  
  
  public void interpret(final String input, final InterpreterData interpreter) {

    Thread thread = new Thread("interpret thread: " + input) {
      public void run() {
        String s = input;
        try {
          interpreter.setInProgress(true);
          try {
            _dialog("to interp: " + s);
            
            String s1 = s;  

            Object result = interpreter.getInterpreter().interpret(s1);
            String resultString = String.valueOf(result);

            
            if (result == Interpreter.NO_RESULT) {
              
              
              _mainJVM.interpretResult(new VoidResult());
            }
            else {
              
              
              
              String style = InteractionsDocument.OBJECT_RETURN_STYLE;
              if (result instanceof String) {
                style = InteractionsDocument.STRING_RETURN_STYLE;
                
                String possibleChar = (String)result;
                
                if (possibleChar.startsWith("\'") && possibleChar.endsWith("\'") && possibleChar.length()==3)
                  style = InteractionsDocument.CHARACTER_RETURN_STYLE;                
              }
              if (result instanceof Number) style = InteractionsDocument.NUMBER_RETURN_STYLE;
              _mainJVM.interpretResult(new ValueResult(resultString, style));
              
            }
          }
          catch (ExceptionReturnedException e) {
            Throwable t = e.getContainedException();

            _dialog("interp exception: " + t);
            
            if (t instanceof ParseException)
              _mainJVM.interpretResult(new SyntaxErrorResult((ParseException) t, input));
            else if (t instanceof TokenMgrError)
              _mainJVM.interpretResult(new SyntaxErrorResult((TokenMgrError) t, input));
            else if (t instanceof ParseError)
              _mainJVM.interpretResult(new SyntaxErrorResult((ParseError) t, input));
            else {
              
              
              
              _mainJVM.interpretResult(new ExceptionResult(t.getClass().getName(),
                                                           t.getMessage(),
                                                           InterpreterJVM.getStackTrace(t),
                                                           null));
            }                                                                                                                                        
          }
          catch (Throwable t) {
            
            _dialog("irregular interp exception: " + t);

            String shortMsg = null;
            if ((t instanceof ParseError) &&  ((ParseError) t).getParseException() != null) 
              shortMsg = ((ParseError) t).getMessage(); 
            _mainJVM.interpretResult(new ExceptionResult(t.getClass().getName(), t.getMessage(),
                                                         InterpreterJVM.getStackTrace(t), shortMsg));
          }          
        }
        catch (RemoteException re) {
          
          _log.logTime("interpret: " + re.toString());
        }
        finally {
          interpreter.setInProgress(false);
        }
      }
    };
    
    thread.setDaemon(true);
    thread.start();
  }
  
  private String _processReturnValue(Object o) {
    if (o instanceof String) return "\"" + o + "\"";
    if (o instanceof Character) return "'" + o + "'";
    return o.toString();
  }
  
  
  public String getVariableToString(String var) throws RemoteException {
    
    Interpreter i = _activeInterpreter.getInterpreter();
    if (i instanceof JavaInterpreter) {
      try {
        Object value = ((JavaInterpreter)i).getVariable(var);
        if (value == null)  return "null";
        if (value instanceof koala.dynamicjava.interpreter.UninitializedObject) return null;
        return _processReturnValue(value);
      }
      catch (IllegalStateException e) {
        
        return null;
      }
    }
    return null;
  }
  
  
  public String getVariableClassName(String var) throws RemoteException {
    
    Interpreter i = _activeInterpreter.getInterpreter();
    if (i instanceof JavaInterpreter) {
      try {
        Class c = ((JavaInterpreter)i).getVariableClass(var);
        if (c == null) return "null";
        else return c.getName();
      }
      catch (IllegalStateException e) {
        
        return null;
      }
    }
    else return null;
  }
  
  
  public void addJavaInterpreter(String name) {
    JavaInterpreter interpreter = new DynamicJavaAdapter(classPathManager);
    
    _updateInterpreterClassPath(interpreter);
    addInterpreter(name, interpreter);
  }
  
  
  public void addDebugInterpreter(String name, String className) {
    JavaDebugInterpreter interpreter = new JavaDebugInterpreter(name, className);
    interpreter.setPrivateAccessible(true);
    
    _updateInterpreterClassPath(interpreter);
    addInterpreter(name, interpreter);
  }
  
  
  public void addInterpreter(String name, Interpreter interpreter) {
    if (_interpreters.containsKey(name)) {
      throw new IllegalArgumentException("'" + name + "' is not a unique interpreter name");
    }
    _interpreters.put(name, new InterpreterData(interpreter));
  }
  
  
  public void removeInterpreter(String name) {
    _interpreters.remove(name);
  }
  
  
  InterpreterData getInterpreter(String name) {
    InterpreterData interpreter = _interpreters.get(name);
    
    if (interpreter != null) return interpreter;
    else throw new IllegalArgumentException("Interpreter '" + name + "' does not exist.");
  }
  
  
  public JavaInterpreter getJavaInterpreter(String name) {
    if (printMessages) System.out.println("Getting interpreter data");
    InterpreterData interpreterData = getInterpreter(name);
    if (printMessages) System.out.println("Getting interpreter instance");
    Interpreter interpreter = interpreterData.getInterpreter();
    if (printMessages) System.out.println("returning");
    
    if (interpreter instanceof JavaInterpreter) return (JavaInterpreter) interpreter;
    else {
      throw new IllegalArgumentException("Interpreter '" + name + "' is not a JavaInterpreter.");
    }
  }
  
  
  public boolean setActiveInterpreter(String name) {
    _activeInterpreter = getInterpreter(name);
    return _activeInterpreter.isInProgress();
  }
  
  
  public boolean setToDefaultInterpreter() {
    _activeInterpreter = _defaultInterpreter;
    return _activeInterpreter.isInProgress();
  }
  
  
  Hashtable<String,InterpreterData> getInterpreters() { return _interpreters; }
  
  
  Interpreter getActiveInterpreter() { return _activeInterpreter.getInterpreter(); }
  
  
  public static String getStackTrace(Throwable t) {
    
    BufferedReader reader = new BufferedReader(new StringReader(StringOps.getStackTrace(t)));
    
    
    LinkedList<String> traceItems = new LinkedList<String>();
    try {
      
      
      
      reader.readLine();
      
      
      String s;
      while ((s = reader.readLine()) != null) {
        
        traceItems.add(s);
      }
    }
    catch (IOException ioe) {
      return "Unable to get stack trace";
    }
    
    
    
    
    int index = -1;
    for (int i=0; i < traceItems.size(); i++) {
      String item = traceItems.get(i);
      item = item.trim();
      if (item.startsWith("at edu.rice.cs.drjava.") || item.startsWith("at koala.dynamicjava.")) {
        index = i;
        break;
      }
    }
    
    
    if (index > -1) {
      while (traceItems.size() > index) traceItems.removeLast();
    }
    
    
    if (traceItems.isEmpty()) traceItems.add(EMPTY_TRACE_TEXT);
    
    
    StringBuffer buf = new StringBuffer();
    ListIterator itor = traceItems.listIterator();
    String newLine = System.getProperty("line.separator");
    boolean first = true;
    while (itor.hasNext()) {
      if (first) first = false; else buf.append(newLine);

      buf.append("  " + ((String) itor.next()).trim());
    }
    
    return buf.toString();
  }
  
  
  
  
  public void setPackageScope(String s) {
    Interpreter active = _activeInterpreter.getInterpreter();
    if (active instanceof JavaInterpreter) {
      ((JavaInterpreter)active).setPackageScope(s);
    }
  }
  
  
  public void setShowMessageOnResetFailure(boolean show) {
    _messageOnResetFailure = show;
  }
  
  
  protected void quitFailed(Throwable th) {
    if (_messageOnResetFailure) {
      String msg = "The interactions pane could not be reset:\n" + th;
      javax.swing.JOptionPane.showMessageDialog(null, msg);
    }
    
    try { _mainJVM.quitFailed(th); }
    catch (RemoteException re) {
      
      _log.logTime("quitFailed: " + re.toString());
    }
  }
  
  
  public void setPrivateAccessible(boolean allow) {
    Interpreter active = _activeInterpreter.getInterpreter();
    if (active instanceof JavaInterpreter) {
      ((JavaInterpreter)active).setPrivateAccessible(allow);
    }
  } 
  
  
  
  public List<String> findTestClasses(List<String> classNames, List<File> files)
    throws RemoteException {
    
    return _junitTestManager.findTestClasses(classNames, files);
  }
  
  
  public boolean runTestSuite() throws RemoteException {
    
    return _junitTestManager.runTestSuite();
  }
  
  
  public void nonTestCase(boolean isTestAll) {
    try { _mainJVM.nonTestCase(isTestAll); }
    catch (RemoteException re) {
      
      _log.logTime("nonTestCase: " + re.toString());
    }
  }
  
  
  public void classFileError(ClassFileError e) {
    try { _mainJVM.classFileError(e); }
    catch (RemoteException re) {
      
      _log.logTime("classFileError: " + re.toString());
    }
  }
  
  
  public void testSuiteStarted(int numTests) {
    try {
      _mainJVM.testSuiteStarted(numTests);
    }
    catch (RemoteException re) {
      
      _log.logTime("testSuiteStarted: " + re.toString());
    }
  }
  
  
  public void testStarted(String testName) {
    try {
      _mainJVM.testStarted(testName);
    }
    catch (RemoteException re) {
      
      _log.logTime("testStarted" + re.toString());
    }
  }
  
  
  public void testEnded(String testName, boolean wasSuccessful, boolean causedError) {
    try {
      _mainJVM.testEnded(testName, wasSuccessful, causedError);
    }
    catch (RemoteException re) {
      
      _log.logTime("testEnded: " + re.toString());
    }
  }
  
  
  public void testSuiteEnded(JUnitError[] errors) {
    try {
      _mainJVM.testSuiteEnded(errors);
    }
    catch (RemoteException re) {
      
      _log.logTime("testSuiteFinished: " + re.toString());
    }
  }
  
  
  public File getFileForClassName(String className) {
    try {
      return _mainJVM.getFileForClassName(className);
    }
    catch (RemoteException re) {
      
      _log.logTime("getFileForClassName: " + re.toString());
      return null;
    }
  }
  
  public void junitJVMReady() {
  }
  
  
  
  
  
  
  
  protected void _updateInterpreterClassPath(JavaInterpreter interpreter) {
    List<ClassPathEntry> locpe = classPathManager.getProjectCP();
    for (ClassPathEntry e: locpe) {
      interpreter.addProjectClassPath(e.getEntry());
    }

    locpe = classPathManager.getBuildDirectoryCP();
    for (ClassPathEntry e: locpe) {
      interpreter.addBuildDirectoryClassPath(e.getEntry());
    }

    locpe = classPathManager.getProjectFilesCP();
    for (ClassPathEntry e: locpe) {
      interpreter.addProjectFilesClassPath(e.getEntry());
    }

    locpe = classPathManager.getExternalFilesCP();
    for (ClassPathEntry e: locpe) {
      interpreter.addExternalFilesClassPath(e.getEntry());
    }

    locpe = classPathManager.getExtraCP();
    for (ClassPathEntry e: locpe) {
      interpreter.addExtraClassPath(e.getEntry());
    }
}
  
  public void addExtraClassPath(URL s) {
    
    if (_classPath.contains(s)) {
      
      return;
    }
    
    
    if (_defaultInterpreter.getInterpreter() instanceof JavaInterpreter) {
      ((JavaInterpreter)_defaultInterpreter.getInterpreter()).addExtraClassPath(s);
    }
    
    
    Enumeration<InterpreterData> interpreters = _interpreters.elements();
    while (interpreters.hasMoreElements()) {
      Interpreter interpreter = interpreters.nextElement().getInterpreter();
      if (interpreter instanceof JavaInterpreter) {
        ((JavaInterpreter)interpreter).addExtraClassPath(s);
      }
    }
    
    
    _classPath.add(s);
  }
 
  
  public void addProjectClassPath(URL s) {
    
    if (_classPath.contains(s)) {
      
      return;
    }
    
    
    if (_defaultInterpreter.getInterpreter() instanceof JavaInterpreter) {
      ((JavaInterpreter)_defaultInterpreter.getInterpreter()).addProjectClassPath(s);
    }
    
    
    Enumeration<InterpreterData> interpreters = _interpreters.elements();
    while (interpreters.hasMoreElements()) {
      Interpreter interpreter = interpreters.nextElement().getInterpreter();
      if (interpreter instanceof JavaInterpreter) {
        ((JavaInterpreter)interpreter).addProjectClassPath(s);
      }
    }
    
    
    _classPath.add(s);
  }
 
  
  public void addBuildDirectoryClassPath(URL s) {
    
    if (_classPath.contains(s)) {
      
      return;
    }
    
    
    if (_defaultInterpreter.getInterpreter() instanceof JavaInterpreter) {
      ((JavaInterpreter)_defaultInterpreter.getInterpreter()).addBuildDirectoryClassPath(s);
    }
    
    
    Enumeration<InterpreterData> interpreters = _interpreters.elements();
    while (interpreters.hasMoreElements()) {
      Interpreter interpreter = interpreters.nextElement().getInterpreter();
      if (interpreter instanceof JavaInterpreter) {
        ((JavaInterpreter)interpreter).addBuildDirectoryClassPath(s);
      }
    }
    
    
    _classPath.add(s);
  }
  
 
  
  public void addProjectFilesClassPath(URL s) {
    
    if (_classPath.contains(s)) {
      
      return;
    }
    
    
    if (_defaultInterpreter.getInterpreter() instanceof JavaInterpreter) {
      ((JavaInterpreter)_defaultInterpreter.getInterpreter()).addProjectFilesClassPath(s);
    }
    
    
    Enumeration<InterpreterData> interpreters = _interpreters.elements();
    while (interpreters.hasMoreElements()) {
      Interpreter interpreter = interpreters.nextElement().getInterpreter();
      if (interpreter instanceof JavaInterpreter) {
        ((JavaInterpreter)interpreter).addProjectFilesClassPath(s);
      }
    }
    
    
    _classPath.add(s);
  }
 
  
  public void addExternalFilesClassPath(URL s) {
    
    if (_classPath.contains(s)) {
      
      return;
    }
    
    
    if (_defaultInterpreter.getInterpreter() instanceof JavaInterpreter) {
      ((JavaInterpreter)_defaultInterpreter.getInterpreter()).addExternalFilesClassPath(s);
    }
    
    
    Enumeration<InterpreterData> interpreters = _interpreters.elements();
    while (interpreters.hasMoreElements()) {
      Interpreter interpreter = interpreters.nextElement().getInterpreter();
      if (interpreter instanceof JavaInterpreter) {
        ((JavaInterpreter)interpreter).addExternalFilesClassPath(s);
      }
    }
    
    
    _classPath.add(s);
  }
  
  
  public Vector<String> getAugmentedClassPath() {
    Vector<String> ret = new Vector<String>();
    List<ClassPathEntry> locpe = classPathManager.getProjectCP();
    for (ClassPathEntry e: locpe) {
      ret.add(e.getEntry().toString());
    }

    locpe = classPathManager.getBuildDirectoryCP();
    for (ClassPathEntry e: locpe) {
      ret.add(e.getEntry().toString());
    }

    locpe = classPathManager.getProjectFilesCP();
    for (ClassPathEntry e: locpe) {
      ret.add(e.getEntry().toString());
    }

    locpe = classPathManager.getExternalFilesCP();
    for (ClassPathEntry e: locpe) {
      ret.add(e.getEntry().toString());
    }

    locpe = classPathManager.getExtraCP();
    for (ClassPathEntry e: locpe) {
      ret.add(e.getEntry().toString());
    }
    return ret;
  }
  
  
  
  
  
  public void addExtraClassPath(String s) {
    try {
      addExtraClassPath(new URL(s));
    } catch(MalformedURLException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
  }
  public void addProjectClassPath(String s) {
    try {
      addProjectClassPath(new URL(s));
    } catch(MalformedURLException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
  } 
  public void addBuildDirectoryClassPath(String s) {
    try {
      addBuildDirectoryClassPath(new URL(s));
    } catch(MalformedURLException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
  }
  public void addProjectFilesClassPath(String s) {
    try {
      addProjectFilesClassPath(new URL(s));
    } catch(MalformedURLException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
  }
  public void addExternalFilesClassPath(String s) { 
    try {
      addExternalFilesClassPath(new URL(s));
    } catch(MalformedURLException e) {
      throw new edu.rice.cs.util.UnexpectedException(e);
    }
  }
  
  
  
  public ClassPathVector getClassPath() {
    ClassPathVector ret = new ClassPathVector();
    List<ClassPathEntry> locpe;
    
    locpe = classPathManager.getProjectCP();
    for (ClassPathEntry e: locpe) ret.add(e.getEntry());

    locpe = classPathManager.getBuildDirectoryCP();
    for (ClassPathEntry e: locpe) ret.add(e.getEntry());
    
    locpe = classPathManager.getProjectFilesCP();
    for (ClassPathEntry e: locpe) ret.add(e.getEntry());
    
    locpe = classPathManager.getExternalFilesCP();
    for (ClassPathEntry e: locpe) ret.add(e.getEntry());
    
    locpe = classPathManager.getExtraCP();
    for (ClassPathEntry e: locpe) ret.add(e.getEntry());
    
    return ret;
  } 
}


class InterpreterData {
  protected final Interpreter _interpreter;
  protected boolean _inProgress;
  
  InterpreterData(Interpreter interpreter) {
    _interpreter = interpreter;
    _inProgress = false;
  }
  
  
  public Interpreter getInterpreter() { return _interpreter; }
  
  
  public boolean isInProgress() { return _inProgress; }
  
  
  public void setInProgress(boolean inProgress) { _inProgress = inProgress; }
}
