

package edu.rice.cs.drjava.model.junit;

import java.io.PrintStream;

import junit.runner.*;
import junit.framework.*;
import junit.textui.TestRunner;


public class JUnitTestRunner extends TestRunner {
  
  
  private JUnitModelCallback _jmc;

  
  private PrintStream _writer;

  
  private TestSuiteLoader _classLoader;

  
  private TestResult _result;

  
  private int _errorCount;

  
  private int _failureCount;

  
  public JUnitTestRunner(JUnitModelCallback jmc) {
    super();
    _jmc = jmc;
    _classLoader = new DrJavaTestSuiteLoader(jmc);
    _writer = new PrintStream(System.out) {
      public void print(String s) { }
      public void println(String s) { }
      public void println() { }
    };

    _errorCount = 0;
    _failureCount = 0;
  }

  public synchronized TestResult doRun(Test suite) {
    
    _errorCount = 0;
    _failureCount = 0;

    
    _result = createTestResult();
    _result.addListener(this);
    _jmc.testSuiteStarted(suite.countTestCases());

    suite.run(_result);



    return _result;
  }

  
  public TestSuiteLoader getLoader() { return _classLoader; }

  
  protected PrintStream getWriter() { return _writer; }

  protected PrintStream writer() { return getWriter(); }

  
  public synchronized void startTest(Test test) { _jmc.testStarted(test.toString()); }

  
  public synchronized void endTest(Test test) {
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
    _jmc.testEnded(test.toString(), success, failure);
  }
}
