

package edu.rice.cs.drjava.model.repl.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;

import edu.rice.cs.plt.concurrent.CompletionMonitor;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public final class NewJVMTest extends DrJavaTestCase {
  private static final Log _log  = new Log("MasterSlave.txt", false);
  
  private static volatile TestJVMExtension _jvm;
  
  public NewJVMTest(String name) { super(name); }

  public static Test suite() {
    TestSuite suite = new TestSuite(NewJVMTest.class);
    TestSetup setup = new TestSetup(suite) {
      protected void setUp() throws Exception { 
        super.setUp();
        _jvm = new TestJVMExtension(); 
      }
      protected void tearDown() throws Exception { _jvm.dispose(); }
    };

    return setup;
  }


  public void testPrintln() throws Throwable {
    debug.logStart();
    _log.log("NewJVMTest.testPrintln executing");
    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("System.err.print(\"err\");"));
    assertEquals("system err buffer", "err", _jvm.errBuf());
    assertEquals("void return flag", true, _jvm.voidReturnFlag());

    _jvm.resetFlags();
    assertTrue(_jvm.interpret("System.err.print(\"err2\");"));
    assertEquals("system err buffer", "err2", _jvm.errBuf());
    assertEquals("void return flag", true, _jvm.voidReturnFlag());

    _jvm.resetFlags();
    assertTrue(_jvm.interpret("System.out.print(\"out\");"));
    assertEquals("system out buffer", "out", _jvm.outBuf());
    assertEquals("void return flag", true, _jvm.voidReturnFlag());
    
    debug.logEnd();
  }

  public void testReturnConstant() throws Throwable {
    debug.logStart();
   _log.log("NewJVMTest.testReturnConstant executing");

   _jvm.resetFlags();
   assertTrue(_jvm.interpret("5"));
   assertEquals("result", "5", _jvm.returnBuf());

   debug.logEnd();
  }

  public void testWorksAfterRestartConstant() throws Throwable {
    debug.logStart();
    _log.log("NewJVMTest.testWorksAfterRestartConstant executing");

    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("5"));
    assertEquals("result", "5", _jvm.returnBuf());
    
    
    _jvm.restartInterpreterJVM(true);

    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("4"));
    assertEquals("result", "4", _jvm.returnBuf());
    
    debug.logEnd();
  }


  public void testThrowRuntimeException() throws Throwable {
    debug.logStart();
    _log.log("NewJVMTest.testThrowRuntimeException executing");
    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("throw new RuntimeException();"));
    assertTrue("exception message", _jvm.exceptionMsgBuf().startsWith("java.lang.RuntimeException"));

    debug.logEnd();
  }

  public void testToStringThrowsRuntimeException() throws Throwable {
    debug.logStart();
    _log.log("NewJVMTest.testToStringThrowsRuntimeException executing");
    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("class A { public String toString() { throw new RuntimeException(); } };" +
                              "new A()"));
    assertTrue("exception should have been thrown by toString",
               _jvm.exceptionMsgBuf() != null);
    
    debug.logEnd();
  }

  
  public void testSwitchToNonExistantInterpreter() {
    debug.logStart();
    try {
      _jvm.setActiveInterpreter("thisisabadname");

      fail("Should have thrown an exception!");
    }
    catch (IllegalArgumentException e) {
      
    }
    debug.logEnd();
  }

  
  public void testSwitchActiveInterpreter() throws InterruptedException {
    debug.logStart();
    
    assertTrue(_jvm.interpret("int x = 6;"));
    _jvm.addInterpreter("monkey");

    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("x"));
    assertEquals("result", "6", _jvm.returnBuf());

    
    _jvm.setActiveInterpreter("monkey");
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("x"));
    assertNotNull("exception was thrown", _jvm.exceptionMsgBuf());

    
    assertTrue(_jvm.interpret("int x = 3;"));
    _jvm.setToDefaultInterpreter();

    
    _jvm.resetFlags();
    assertTrue(_jvm.interpret("x"));
    assertEquals("result", "6", _jvm.returnBuf());

    
    




    debug.logEnd();
  }

  private static class TestJVMExtension extends MainJVM {
    private static final int WAIT_TIMEOUT = 30000; 
    
    private final CompletionMonitor _done;
    private volatile String _outBuf;
    private volatile String _errBuf;
    private volatile String _returnBuf;
    private volatile String _exceptionMsgBuf;
    private volatile boolean _voidReturnFlag;

    private volatile InterpretResult.Visitor<Void> _testHandler;

    public TestJVMExtension() throws RemoteException {
      super(IOUtil.WORKING_DIRECTORY);
      _done = new CompletionMonitor();
      _testHandler = new TestResultHandler();
      startInterpreterJVM();
      resetFlags();
    }

    @Override protected InterpretResult.Visitor<Void> resultHandler() {
      return _testHandler;
    }

    public void resetFlags() {
      _done.reset();
      _outBuf = "";
      _errBuf = "";
      _returnBuf = null;
      _exceptionMsgBuf = null;
      _voidReturnFlag = false;
    }
    
    public String outBuf() {
      assertTrue(_done.attemptEnsureSignaled(WAIT_TIMEOUT));
      return _outBuf;
    }

    public String errBuf() {
      assertTrue(_done.attemptEnsureSignaled(WAIT_TIMEOUT));
      return _errBuf;
    }

    public String returnBuf() {
      try {
      assertTrue(_done.attemptEnsureSignaled(WAIT_TIMEOUT));
      return _returnBuf;
      }
      finally { debug.logValue("_returnBuf", _returnBuf); }
    }

    public String exceptionMsgBuf() {
      assertTrue(_done.attemptEnsureSignaled(WAIT_TIMEOUT));
      return _exceptionMsgBuf;
    }

    public boolean voidReturnFlag() {
      assertTrue(_done.attemptEnsureSignaled(WAIT_TIMEOUT));
      return _voidReturnFlag;
    }

    public void systemErrPrint(String s) { _errBuf += s; }
    public void systemOutPrint(String s) { _outBuf += s; }

    private class TestResultHandler implements InterpretResult.Visitor<Void> {
      public Void forNoValue() {
        debug.log();
        _voidReturnFlag = true;
        _done.signal();
        _log.log("NewJVMTest: void returned by interpretResult callback");
        return null;
      }
      public Void forStringValue(String s) { handleValueResult('"' + s + '"'); return null; }
      public Void forCharValue(Character c) { handleValueResult("'" + c + "'"); return null; }
      public Void forNumberValue(Number n) { handleValueResult(n.toString()); return null; }
      public Void forBooleanValue(Boolean b) { handleValueResult(b.toString()); return null; }
      public Void forObjectValue(String objString, String objTypeString) { handleValueResult(objString); return null; }
      
      private void handleValueResult(String s) {
        debug.log();
        _returnBuf = s;
        _done.signal();
        _log.log("NewJVMTest: " + _returnBuf + " returned by interpretResult callback");
      }
      
      public Void forEvalException(String message, StackTraceElement[] stackTrace) {
        debug.log();
        StringBuilder sb = new StringBuilder(message);
        for(StackTraceElement ste: stackTrace) {
          sb.append("\n\tat ");
          sb.append(ste);
        }
        _exceptionMsgBuf = sb.toString().trim();
        _done.signal();
        return null;
      }
      
      public Void forException(String message) {
        debug.log();
        _exceptionMsgBuf = message;
        _done.signal();
        return null;
      }
      
      public Void forUnexpectedException(Throwable t) {
        debug.log();
        throw new UnexpectedException(t);
      }

      public Void forBusy() {
        debug.log();
        throw new UnexpectedException("MainJVM.interpret called when interpreter was busy!");
      }
    }
  }
}
