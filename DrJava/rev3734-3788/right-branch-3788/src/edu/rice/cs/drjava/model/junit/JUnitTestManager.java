

package edu.rice.cs.drjava.model.junit;

import junit.framework.*;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;

import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.swing.ScrollableDialog;

import java.lang.reflect.Modifier;


public class JUnitTestManager {
  
  
  private final JUnitModelCallback _jmc;
  
  
  private JUnitTestRunner _testRunner;
  
  
  private TestSuite _suite = null;
  
  
  private List<String> _testClassNames = null;
  
  
  private List<File> _testFiles = null;
  
  
  public JUnitTestManager(JUnitModelCallback jmc) { _jmc = jmc; }

  public JUnitTestRunner getTestRunner() { return _testRunner; }
  
  
  public List<String> findTestClasses(final List<String> classNames, final List<File> files) {

    

    if (_testClassNames != null && ! _testClassNames.isEmpty()) 
      throw new IllegalStateException("Test suite is still pending!");
    
    _testRunner = new JUnitTestRunner(_jmc);
    
    _testClassNames = new ArrayList<String>();
    _testFiles = new ArrayList<File>();
    _suite = new TestSuite();

   
    
    int i = 0;
    try {
      for (i = 0; i < classNames.size(); i++) {
        String cName = classNames.get(i);
       
        try {
          if (_isTestCase(cName)) {
            
            _testClassNames.add(cName);
            _testFiles.add(files.get(i));
            _suite.addTest(_testRunner.getTest(cName));
          }
        }
        catch(LinkageError e) { 
          
          _jmc.classFileError(new ClassFileError(cName, files.get(i).getCanonicalPath(), e));
        }
      }
    }
    catch(IOException e) { throw new UnexpectedException(e); }
    
     
    return _testClassNames;
  }
    
  
  public  boolean runTestSuite() {
    
    if (_testClassNames == null || _testClassNames.isEmpty()) return false;
    


    try {
      TestResult result = _testRunner.doRun(_suite);
    
      JUnitError[] errors = new JUnitError[result.errorCount() + result.failureCount()];
      
      Enumeration failures = result.failures();
      Enumeration errEnum = result.errors();
      
      int i = 0;
      
      while (errEnum.hasMoreElements()) {
        TestFailure tErr = (TestFailure) errEnum.nextElement();
        errors[i] = _makeJUnitError(tErr, _testClassNames, true, _testFiles);
        i++;
      }
      
      while (failures.hasMoreElements()) {
        TestFailure tFail = (TestFailure) failures.nextElement();
        errors[i] = _makeJUnitError(tFail, _testClassNames, false, _testFiles);
        i++;
      }

      
      _jmc.testSuiteEnded(errors);
    }
    catch(Throwable t) { 
      JUnitError[] errors = new JUnitError[1];
      errors[0] = new JUnitError(null, -1, -1, t.getMessage(),
                                 false, "", "", StringOps.getStackTrace(t));
      _jmc.testSuiteEnded(errors);

      
    }
    finally {
      _suite = null;
      _testClassNames = null;
      _testFiles = null;
    }
    return true;
  }

  
  private boolean _isJUnitTest(Class c) {
    

    return Test.class.isAssignableFrom(c) && !Modifier.isAbstract(c.getModifiers()) && 
      !Modifier.isInterface(c.getModifiers());
  }

  
  private boolean _isTestCase(String className) {
    try { return _isJUnitTest(_testRunner.getLoader().load(className)); }
    catch (ClassNotFoundException cnfe) {
              
        return false; }
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
    String className1 = testString.substring(firstIndex, secondIndex);
    String className2 = testString.substring(0, firstIndex-1);
    if (firstIndex == secondIndex) className = className2;
    else className = className1;
    
    String classNameAndTest = className + "." + testName;
    String stackTrace = StringOps.getStackTrace(failure.thrownException());
    
    
    if (stackTrace.indexOf(className) == -1) {
      
      String trace = failure.trace();
      
      trace = trace.substring(trace.indexOf('\n')+1);
      while (trace.indexOf("junit.framework.Assert") != -1 &&
            trace.indexOf("junit.framework.Assert") < trace.indexOf("(")) {
        
        trace = trace.substring(trace.indexOf('\n') + 1);
      }
      trace = trace.substring(trace.indexOf('(')+1);
      trace = trace.substring(0, trace.indexOf(')'));
      className = trace.substring(0,trace.indexOf(':'));
      className = trace.substring(0,trace.lastIndexOf('.'));
      classNameAndTest = className + "." + testName;
    }
    
    
    
    int lineNum = _lineNumber(stackTrace, classNameAndTest);
    


    String exception =  (isError) ? failure.thrownException().toString(): 
                                    failure.thrownException().getMessage();
    boolean isFailure = (failure.thrownException() instanceof AssertionFailedError) &&
      !classNameAndTest.equals("junit.framework.TestSuite$1.warning");




















    int indexOfClass = classNames.indexOf(className);
    File file;
    if (indexOfClass != -1) file = files.get(indexOfClass);
    else file = _jmc.getFileForClassName(className);
    
    
    if (file == null) {
      return new JUnitError(new File("nofile"), 0, 0, exception, !isFailure, testName, className, stackTrace);
    }
    
    
    
    String name = file.getName();
    int adjLineNum;
    if (name.endsWith(".dj0") || name.endsWith(".dj0")) adjLineNum = lineNum - 1;
    else adjLineNum = lineNum;
    
    return new JUnitError(file, adjLineNum, 0, exception, !isFailure, testName, className, stackTrace);
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
    catch (NumberFormatException e) { throw new UnexpectedException(e); }
    
    return lineNum;
  }
}
