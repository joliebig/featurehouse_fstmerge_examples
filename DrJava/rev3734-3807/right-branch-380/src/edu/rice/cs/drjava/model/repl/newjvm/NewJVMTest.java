

package edu.rice.cs.drjava.model.repl.newjvm;

import edu.rice.cs.drjava.DrJavaTestCase;
import edu.rice.cs.drjava.config.FileOption;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.rmi.RemoteException;


public final class NewJVMTest extends DrJavaTestCase {
  private final boolean printMessages = false;
  
  private static TestJVMExtension _jvm;
  
  
  private final static Object _testLock = new Object();
  
  public NewJVMTest(String name) { super(name); }

  protected void setUp() throws Exception {
    super.setUp();
    _jvm.resetFlags();
  }

  public static Test suite() {
    TestSuite suite = new TestSuite(NewJVMTest.class);
    TestSetup setup = new TestSetup(suite) {
      protected void setUp() throws Exception {
        _jvm = new TestJVMExtension();
      }

      protected void tearDown() throws Exception { _jvm.killInterpreter(null); }
    };

    return setup;
  }


  public void testPrintln() throws Throwable {
    if (printMessages) System.out.println("----testPrintln-----");
    synchronized(_testLock) {
      _jvm.interpret("System.err.print(\"err\");");
      _testLock.wait(); 

      assertEquals("system err buffer", "err", _jvm.errBuf);
      assertEquals("void return flag", true, _jvm.voidReturnFlag);
      _jvm.resetFlags();
    }

    synchronized(_testLock) {
      _jvm.interpret("System.err.print(\"err2\");");
      _testLock.wait(); 

      assertEquals("system err buffer", "err2", _jvm.errBuf);
      assertEquals("void return flag", true, _jvm.voidReturnFlag);
      _jvm.resetFlags();
    }

    synchronized(_testLock) {
      _jvm.interpret("System.out.print(\"out\");");
      _testLock.wait(); 

      assertEquals("system out buffer", "out", _jvm.outBuf);
      assertEquals("void return flag", true, _jvm.voidReturnFlag);
    }
  }

  public void testReturnConstant() throws Throwable {
    if (printMessages) System.out.println("----testReturnConstant-----");
    synchronized(_testLock) {
      _jvm.interpret("5");
      _testLock.wait();
      assertEquals("result", "5", _jvm.returnBuf);
    }
  }

  public void testWorksAfterRestartConstant() throws Throwable {
    if (printMessages) System.out.println("----testWorksAfterRestartConstant-----");

    
    synchronized(_testLock) {
      _jvm.interpret("5");
      _testLock.wait();
      assertEquals("result", "5", _jvm.returnBuf);
    }

    
    synchronized(_testLock) {
      _jvm.killInterpreter(FileOption.NULL_FILE);  
      _testLock.wait();
    }

    
    synchronized(_testLock) {
      _jvm.interpret("4");
      _testLock.wait();
      assertEquals("result", "4", _jvm.returnBuf);
    }
  }


  public void testThrowRuntimeException() throws Throwable {
    if (printMessages) System.out.println("----testThrowRuntimeException-----");
    synchronized(_testLock) {
      _jvm.interpret("throw new RuntimeException();");
      _testLock.wait();
      assertEquals("exception class",
                   "java.lang.RuntimeException",
                   _jvm.exceptionClassBuf);
    }
  }

  public void testToStringThrowsRuntimeException() throws Throwable {
    if (printMessages) System.out.println("----testToStringThrowsRuntimeException-----");
    synchronized(_testLock) {
      _jvm.interpret(
        "class A { public String toString() { throw new RuntimeException(); } };" +
        "new A()");
      _testLock.wait();
      assertTrue("exception should have been thrown by toString",
                 _jvm.exceptionClassBuf != null);
    }
  }

  public void testThrowNPE() throws Throwable {
    if (printMessages) System.out.println("----testThrowNPE-----");
    synchronized(_testLock) {
      _jvm.interpret("throw new NullPointerException();");

      while (_jvm.exceptionClassBuf == null) {
        _testLock.wait();
      }

      assertEquals("exception class",
                   "java.lang.NullPointerException",
                   _jvm.exceptionClassBuf);
    }
  }

  public void testStackTraceEmptyTrace() throws Throwable {
    if (printMessages) System.out.println("----testStackTraceEmptyTrace-----");
    synchronized(_testLock) {
      _jvm.interpret("null.toString()");

      while (_jvm.exceptionClassBuf == null) {
        _testLock.wait();
      }

      assertEquals("exception class",
                   "java.lang.NullPointerException",
                   _jvm.exceptionClassBuf);
      assertEquals("stack trace",
                   InterpreterJVM.EMPTY_TRACE_TEXT.trim(),
                   _jvm.exceptionTraceBuf.trim());
    }
  }


  
  public void testSwitchToNonExistantInterpreter() {
    try {
      _jvm.setActiveInterpreter("monkey");
      fail("Should have thrown an exception!");
    }
    catch (IllegalArgumentException e) {
      
    }
  }

  
  public void testSwitchActiveInterpreter() throws InterruptedException {
    synchronized(_testLock) {
      _jvm.interpret("x = 6;");
      _testLock.wait();
    }
    _jvm.addJavaInterpreter("monkey");

    
    synchronized(_testLock) {
      _jvm.interpret("x");
      _testLock.wait();
      assertEquals("result", "6", _jvm.returnBuf);
    }

    
    _jvm.setActiveInterpreter("monkey");
    synchronized(_testLock) {
      _jvm.interpret("x");
      _testLock.wait();
      assertTrue("exception was thrown",
                 !_jvm.exceptionClassBuf.equals(""));
    }

    
    synchronized(_testLock) {
      _jvm.interpret("x = 3;");
      _testLock.wait();
    }
    _jvm.setToDefaultInterpreter();

    
    synchronized(_testLock) {
      _jvm.interpret("x");
      _testLock.wait();
      assertEquals("result", "6", _jvm.returnBuf);
    }

    
    







  }

  private static class TestJVMExtension extends MainJVM {
    public String outBuf;
    public String errBuf;
    public String returnBuf;
    public String exceptionClassBuf;
    public String exceptionMsgBuf;
    public String exceptionTraceBuf;
    public String syntaxErrorMsgBuf;
    public int syntaxErrorStartRow;
    public int syntaxErrorStartCol;
    public int syntaxErrorEndRow;
    public int syntaxErrorEndCol;
    public boolean voidReturnFlag;

    private InterpretResultVisitor<Object> _testHandler;

    public TestJVMExtension() {
      super(null);
      _testHandler = new TestResultHandler();
      startInterpreterJVM();
      ensureInterpreterConnected();
    }

    protected InterpretResultVisitor<Object> getResultHandler() {
      return _testHandler;
    }

    public void resetFlags() {
      outBuf = null;
      errBuf = null;
      returnBuf = null;
      exceptionClassBuf = null;
      exceptionMsgBuf = null;
      exceptionTraceBuf = null;
      voidReturnFlag = false;
      syntaxErrorMsgBuf = null;
      syntaxErrorStartRow = 0;
      syntaxErrorStartCol = 0;
      syntaxErrorEndRow = 0;
      syntaxErrorEndCol = 0;
    }

    protected void handleSlaveQuit(int status) {
      synchronized(_testLock) {
        _testLock.notify();
        super.handleSlaveQuit(status);
      }
    }

    public void systemErrPrint(String s) throws RemoteException {
      synchronized(_testLock) {
        
        errBuf = s;

      }
    }

    public void systemOutPrint(String s) throws RemoteException {
      synchronized(_testLock) {
        
        outBuf = s;

      }
    }

    private class TestResultHandler implements InterpretResultVisitor<Object> {
      public Object forVoidResult(VoidResult that) {
        synchronized(_testLock) {
          voidReturnFlag = true;
          
          _testLock.notify();
          return null;
        }
      }
      public Object forValueResult(ValueResult that) {
        synchronized(_testLock) {
          returnBuf = that.getValueStr();
          
          _testLock.notify();
          return null;
        }
      }
      public Object forExceptionResult(ExceptionResult that) {
        synchronized(_testLock) {
          exceptionClassBuf = that.getExceptionClass();
          exceptionTraceBuf = that.getStackTrace();
          exceptionMsgBuf = that.getExceptionMessage();

          
          _testLock.notify();
          return null;
        }
      }

      public Object forSyntaxErrorResult(SyntaxErrorResult that) {
        synchronized(_testLock) {
          syntaxErrorMsgBuf = that.getErrorMessage();
          syntaxErrorStartRow = that.getStartRow();
          syntaxErrorStartCol = that.getStartCol();
          syntaxErrorEndRow = that.getEndRow();
          syntaxErrorEndCol = that.getEndCol();
          
          _testLock.notify();
          return null;
        }
      }

    }
  }
}
