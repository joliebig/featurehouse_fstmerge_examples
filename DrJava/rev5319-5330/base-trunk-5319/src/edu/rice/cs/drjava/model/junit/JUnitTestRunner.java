

package edu.rice.cs.drjava.model.junit;

import junit.runner.*;
import junit.framework.*;

import edu.rice.cs.util.UnexpectedException;


public class JUnitTestRunner extends BaseTestRunner {
  
  
  private JUnitModelCallback _jmc;

  
  private ClassLoader _loader;

  
  private TestResult _result;

  
  private int _errorCount;

  
  private int _failureCount;

  
  public JUnitTestRunner(JUnitModelCallback jmc, ClassLoader loader) {
    super();
    _jmc = jmc;
    _loader = loader;
    _result = null;
    _errorCount = 0;
    _failureCount = 0;
  }
 
  public synchronized TestResult runSuite(TestSuite suite) {
    
    _errorCount = 0;
    _failureCount = 0;

    
    _result = new TestResult();
    _result.addListener(this);
    _jmc.testSuiteStarted(suite.countTestCases());
    suite.run(_result);
    return _result;
  }
  
  public Class<?> loadPossibleTest(String className) throws ClassNotFoundException {
    return _loader.loadClass(className);
  }
  
  @Override protected Class<? extends TestCase> loadSuiteClass(String className) throws ClassNotFoundException {
    return loadPossibleTest(className).asSubclass(TestCase.class);
  }

  
  @Override public synchronized void testStarted(String testName) {
    _jmc.testStarted(testName);
  }

  
  @Override public synchronized void testEnded(String testName) {
    boolean error = false;
    boolean failure = false;
    if (_result.errorCount() > _errorCount) {
      error = true;
      _errorCount++;
    }
    if (_result.failureCount() > _failureCount) {
      failure = true;
      _failureCount++;
    }
    boolean success = ! (failure || error);
    _jmc.testEnded(testName, success, failure);
  }
  
  @Override public synchronized void testFailed(int status, Test test, Throwable t) {
    
  }
  
  @Override protected void runFailed(String message) {
    throw new UnexpectedException(message);
  }  
}
