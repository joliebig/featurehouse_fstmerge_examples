

package edu.rice.cs.drjava.model.junit;

import junit.framework.*;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

import edu.rice.cs.util.Log;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.reflect.ShadowingClassLoader;

import java.lang.reflect.Modifier;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;

import edu.rice.cs.drjava.model.compiler.LanguageLevelStackTraceMapper;


public class JUnitTestManager {
  
  protected static final Log _log = new Log("/Users/cork/drjava/drjava/GlobalModel.txt", false);
  
  
  private final JUnitModelCallback _jmc;
  
  
  private final Lambda<ClassLoader, ClassLoader> _loaderFactory;
  
  
  private JUnitTestRunner _testRunner;
  
  
  private TestSuite _suite = null;
  
  
  private List<String> _testClassNames = null;
  
  
  private List<File> _testFiles = null;
  
  
  public JUnitTestManager(JUnitModelCallback jmc, Lambda<ClassLoader, ClassLoader> loaderFactory) {
    _jmc = jmc;
    _loaderFactory = loaderFactory;
  }
  
  
  public List<String> findTestClasses(final List<String> classNames, final List<File> files) {

    _log.log("findTestClasses(" + classNames + ", " + files + ")");
    
    if (_testClassNames != null && ! _testClassNames.isEmpty()) 
      throw new IllegalStateException("Test suite is still pending!");
    
    _testRunner = makeRunner();
    
    _testClassNames = new ArrayList<String>();
    _testFiles = new ArrayList<File>();
    _suite = new TestSuite();
    
    for (Pair<String, File> pair : IterUtil.zip(classNames, files)) {
      String cName = pair.first();
      try {
        if (_isJUnitTest(_testRunner.loadPossibleTest(cName))) {
          _testClassNames.add(cName);
          _testFiles.add(pair.second());
          _suite.addTest(_testRunner.getTest(cName));
        }
      }
      catch (ClassNotFoundException e) { error.log(e); }
      catch(LinkageError e) {
        
        String path = IOUtil.attemptAbsoluteFile(pair.second()).getPath();
        _jmc.classFileError(new ClassFileError(cName, path, e));
      }
    }
    

    _log.log("returning: " + _testClassNames);
    return _testClassNames;
  }
  
  
  @SuppressWarnings("unchecked")
  public  boolean runTestSuite() {
    
    _log.log("runTestSuite() called");
    
    if (_testClassNames == null || _testClassNames.isEmpty()) return false;
    

    
    try {

      TestResult result = _testRunner.runSuite(_suite);
      
      JUnitError[] errors = new JUnitError[result.errorCount() + result.failureCount()];
      
      Enumeration<TestFailure> failures = result.failures();
      Enumeration<TestFailure> errEnum = result.errors();
      
      int i = 0;
      
      while (errEnum.hasMoreElements()) {
        TestFailure tErr = errEnum.nextElement();
        errors[i] = _makeJUnitError(tErr, _testClassNames, true, _testFiles);
        i++;
      }
      
      while (failures.hasMoreElements()) {
        TestFailure tFail = failures.nextElement();
        errors[i] = _makeJUnitError(tFail, _testClassNames, false, _testFiles);
        i++;
      }

      _reset();
      _jmc.testSuiteEnded(errors);
    }
    catch(Exception e) { 
      JUnitError[] errors = new JUnitError[1];      
      errors[0] = new JUnitError(null, -1, -1, e.getMessage(), false, "", "", e.toString(), e.getStackTrace());
      _reset();
      _jmc.testSuiteEnded(errors);

    }
    _log.log("Exiting runTestSuite()");
    return true;
  }
  
  private void _reset() {
    _suite = null;
    _testClassNames = null;
    _testFiles = null;
    _log.log("test manager state reset");
  }
  
  
  private boolean _isJUnitTest(Class<?> c) {
    boolean result = Test.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()) && 
      !Modifier.isInterface(c.getModifiers());
    
    return result;
  }
  
  
  private JUnitError _makeJUnitError(TestFailure failure, List<String> classNames, boolean isError, List<File> files) {
    
    Test failedTest = failure.failedTest();
    String testName;
    if (failedTest instanceof TestCase) testName = ((TestCase)failedTest).getName();
    else testName = failedTest.getClass().getName();
    
    String testString = failure.toString();
    int firstIndex = testString.indexOf('(') + 1;
    int secondIndex = testString.indexOf(')');
    
    
    
    String className;
    if (firstIndex != secondIndex)
      className = testString.substring(firstIndex, secondIndex);
    else
      className = testString.substring(0, firstIndex-1);
    
    String classNameAndTest = className + "." + testName;
    String exception = failure.thrownException().toString();
    StackTraceElement[] stackTrace = failure.thrownException().getStackTrace();
    
    
    StringBuilder sb = new StringBuilder();
    sb.append(exception);
    sb.append('\n');
    for(StackTraceElement s: stackTrace) {
      sb.append("\tat ");
      sb.append(s);
    }
    String combined = sb.toString();
    int lineNum = -1;
    if (combined.indexOf(classNameAndTest) == -1) {
      
      String trace = failure.trace();
      
      trace = trace.substring(trace.indexOf('\n')+1);
      if (trace.trim().length()>0) {
        while (trace.indexOf("junit.framework.Assert") != -1 &&
               trace.indexOf("junit.framework.Assert") < trace.indexOf("(")) {
          
          trace = trace.substring(trace.indexOf('\n') + 1);
        }
        trace = trace.substring(trace.indexOf('(')+1);
        trace = trace.substring(0, trace.indexOf(')'));
        
        
        
        if (combined.indexOf(className) == -1) {
          int dotPos = trace.lastIndexOf('.');
          if (dotPos!=-1) {
            className = trace.substring(0,dotPos);
            classNameAndTest = className + "." + testName;
          }
        }
        
        try {
          lineNum = Integer.parseInt(trace.substring(trace.indexOf(':') + 1)) - 1;
        }
        catch (NumberFormatException e) { lineNum = 0; } 
      }      
    }
    
    if (lineNum < 0) {
      lineNum = _lineNumber(combined, classNameAndTest);
    }
    

    
    String message =  (isError) ? failure.thrownException().toString(): 
      failure.thrownException().getMessage();
    boolean isFailure = (failure.thrownException() instanceof AssertionFailedError) &&
      !classNameAndTest.equals("junit.framework.TestSuite$1.warning");
    


















    
    int indexOfClass = classNames.indexOf(className);
    File file;
    if (indexOfClass != -1) file = files.get(indexOfClass);
    else file = _jmc.getFileForClassName(className);
    
    
    
    
    if (file == null) {
      return new JUnitError(new File("nofile"), 0, 0, message, !isFailure, testName, className, exception, stackTrace);
    }
    
    return new JUnitError(file, lineNum, 0, message, !isFailure, testName, className, exception, stackTrace);
  }
  
  
  private int _lineNumber(String sw, String classname) {
    
    int lineNum;
    int idxClassname = sw.indexOf(classname);
    if (idxClassname == -1) return -1;

    String theLine = sw.substring(idxClassname, sw.length());
    
    theLine = theLine.substring(theLine.indexOf(classname), theLine.length());
    theLine = theLine.substring(theLine.indexOf("(") + 1, theLine.length());
    theLine = theLine.substring(0, theLine.indexOf(")"));
    
    try {
      int i = theLine.indexOf(":") + 1;
      lineNum = Integer.parseInt(theLine.substring(i, theLine.length())) - 1;
    }
    catch (NumberFormatException e) { lineNum = 0; } 
    
    return lineNum;
  }
  
  
  private JUnitTestRunner makeRunner() {
    ClassLoader current = JUnitTestManager.class.getClassLoader();
    
    
    
    ClassLoader parent = ShadowingClassLoader.whiteList(current, "junit", "org.junit");
    return new JUnitTestRunner(_jmc, _loaderFactory.value(parent));
  }
  
}
