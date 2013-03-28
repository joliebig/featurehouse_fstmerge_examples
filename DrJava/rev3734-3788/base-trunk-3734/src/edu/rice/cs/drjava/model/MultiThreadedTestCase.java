

package edu.rice.cs.drjava.model;

import edu.rice.cs.drjava.DrJavaTestCase;
import junit.framework.AssertionFailedError;


public abstract class MultiThreadedTestCase extends DrJavaTestCase {
  public MultiThreadedTestCase() { super(); }
  public MultiThreadedTestCase(String name) { super(name); }  
  
  
  protected static boolean _testFailed = false;

  
  public void setUp() throws Exception {
    super.setUp();
    _testFailed = false;
    ExceptionHandler.ONLY.reset();
    Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler.ONLY);
  }

  
  public void tearDown() throws Exception {
    ExceptionHandler.ONLY.rethrow();
    if ( _testFailed ) fail("test failed in another thread");
    super.tearDown();
  }

  
  protected static void listenerFail(String s) {
    System.out.println("TEST FAILED: " + s);
    new AssertionFailedError(s).printStackTrace(System.out);
    _testFailed = true;
    fail(s);
  }
  
  
  public static void join(Thread t) {
    try {
      t.join();
    }
    catch(InterruptedException e) {
      throw new edu.rice.cs.util.UnexpectedException(e, "Thread.join was unexpectedly interrupted.");
    }
  }
  
  
  public static void wait(Object o) {
    try {
      o.wait();
    }
    catch(InterruptedException e) {
      e.printStackTrace();
      throw new edu.rice.cs.util.UnexpectedException(e, "Thread.wait was unexpectedly interrupted.");
    }
  }
  
  
  private static class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
    
    
    private Throwable _e = null;
    
    
    private java.lang.Thread _t = null;
    
    
    private java.lang.Thread _mainThread = java.lang.Thread.currentThread();
    
    
    public void uncaughtException(java.lang.Thread t, Throwable e) {
      _t = t;
      _e = e;
      if (_mainThread != null) {
        System.out.println("Uncaught Exception in spawned thread within a MultiThreadedTestCase:\n" + e);
        _mainThread.interrupt();
      }
    }
    
    
    public void reset() {
      _t = null;
      _e = null;
    }
    
    
    public void rethrow() {
      if (exceptionOccurred()) {
        if (_e instanceof Error) {
          throw (Error)_e;
        }
        if (_e instanceof RuntimeException) {
          throw (RuntimeException)_e;
        }
        else {
          
          throw new AssertionFailedError("Exception in thread "+_t+": "+_e);
        }
      }            
    }
    
    
    public boolean exceptionOccurred() {
      return (_e != null);
    }
    
    public Throwable getException() {
      return _e;
    }
    
    public java.lang.Thread getThread() {
      return _t;
    }
    
    
    public void setMainThread(java.lang.Thread mainThread) {
      _mainThread = mainThread;
    }
    
    
    private ExceptionHandler() {      
    }
    
    
    public static final ExceptionHandler ONLY = new ExceptionHandler();
  }
}
