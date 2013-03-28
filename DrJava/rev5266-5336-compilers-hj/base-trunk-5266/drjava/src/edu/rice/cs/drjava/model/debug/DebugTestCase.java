

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.*;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import java.io.*;


public abstract class DebugTestCase extends GlobalModelTestCase {
  



  
  
  
  protected volatile int _pendingNotifies = 0;
  protected final Object _notifierLock = new Object();
  
  protected volatile Debugger _debugger;
  
  protected static final String DEBUG_CLASS =
     "class DrJavaDebugClass {\n" +
     "  public void foo() {\n" +
     "    System.out.println(\"Foo Line 1\");\n" +
     "    bar();\n" +
     "    System.out.println(\"Foo Line 3\");\n" +
     "  }\n" +
     "  public void bar() {\n" +
     "    System.out.println(\"Bar Line 1\");\n" +
     "    System.out.println(\"Bar Line 2\");\n" +
     "  }\n" +
     "}\n" +
     "class DrJavaDebugClass2 {\n" +
     "  public void baz() {\n" +
     "    System.out.println(\"Baz Line 1\");\n" +
     "    new DrJavaDebugClass().bar();\n" +
     "  }\n" +
     "}";
  
  protected static final String DEBUG_CLASS_WITH_PACKAGE =
     "package a;\n" +
     "public class DrJavaDebugClassWithPackage {\n" +
     "  public void foo() {\n" +
     "    System.out.println(\"foo line 1\");\n" +
     "    System.out.println(\"foo line 2\");\n" +
     "  }\n" +
     "}";
  
  protected static final String SUSPEND_CLASS =
    "class Suspender {\n" +
    "  public static void main(String[] args) {\n" +
    "    Thread t1 = new Thread(){\n" +
    "      public void run(){\n" +
    "        int a = 1;\n" +
    "        while(true);\n" +
    "      }\n" +
    "    };\n" +
    "    t1.start();\n" +
    "  }\n" +
    "}";
  
  protected static final String MONKEY_CLASS =
        "class Monkey {\n" +
        "  public static void main(String[] args) {\n" +
        "\n" +
        "    Thread t = new Thread(){\n" +
        "      public void run(){\n" +
        "       try{\n" +
        "         Thread.sleep(1000);\n" +
        "       }\n" +
        "       catch(InterruptedException e){\n" +
        "      }\n" +
        "      System.out.println(\"I\'m a thread! Yeah!\");\n" +
        "      }\n" +
        "    };\n" +
        "    try{\n" +
        "      t.start();\n" +
        "      System.out.println(\"I just woke up.  I\'m a big boy now.\");\n" +
        "      System.out.println(\"James likes bananas!\");\n" +
        "      System.out.println(\"Yes they do.\");\n" +
        "    }catch(Exception e){\n" +
        "      e.printStackTrace();\n" +
        "    }\n" +
        "  }\n" +
        "}\n";
  
  protected static final String MONKEY_WITH_INNER_CLASS =
        "class Monkey {\n" +
        "  static int foo = 6; \n" +
        "  class MonkeyInner { \n" +
        "    int innerFoo = 8;\n" +
        "    class MonkeyInnerInner { \n" +
        "      int innerInnerFoo = 10;\n" +
        "      public void innerMethod() { \n" +
        "        int innerMethodFoo;\n" +
        "        String nullString = null;\n" +
       "        innerMethodFoo = 12;\n" +
       "        foo++;\n" +
       "        innerFoo++;\n" +
       "        innerInnerFoo++;\n" +
       "        innerMethodFoo++;\n" +
       "        staticMethod();\n" +
       "        System.out.println(\"innerMethodFoo: \" + innerMethodFoo);\n" +
       "      }\n" +
       "    }\n" +
       "  }\n" +
       "  public void bar() {\n" +
       "    final MonkeyInner.MonkeyInnerInner mi = \n" +
       "      new MonkeyInner().new MonkeyInnerInner();\n" +
       "    mi.innerMethod();\n" +
       "    final int localVar = 99;\n" +
       "    new Thread() {\n" +
       "      public void run() {\n" +
       "        final int localVar = mi.innerInnerFoo;\n" +
       "        new Thread() {\n" +
       "          public void run() {\n" +
       "            new Thread() {\n" +
       "              public void run() {\n" +
       "                System.out.println(\"localVar = \" + localVar);\n" +
       "              }\n" +
       "            }.run();\n" +
       "          }\n" +
       "        }.run();\n" +
       "      }\n" +
       "    }.run();\n" +
       "  }\n" +
       "  public static void staticMethod() {\n" +
       "    int z = 3;\n" +
       "  }\n" +
       "}\n";
  
  protected static final String INNER_CLASS_WITH_LOCAL_VARS =
     "class InnerClassWithLocalVariables {\n" +
     "  public static void main(final String[] args) {\n" +
     "    final int numArgs = args.length;\n" +
     "    final int inlined = 0;\n" +
     "    new Runnable() {\n" +
     "      public void run() {\n" +
     "        System.out.println(\"numArgs: \" + numArgs);\n" +
     "        System.out.println(\"inlined: \" + inlined);\n" +
     "        System.out.println(\"args.length: \" + args.length);\n" +
     "      }\n" +
     "    }.run();\n" +
     "  }\n" +
     "}\n";
  
  protected static final String CLASS_WITH_STATIC_FIELD =
        "public class DrJavaDebugStaticField {\n" +
        "  public static int x = 0;\n" +
        "  public void bar() {\n" +
        "    System.out.println(\"x == \" + x);\n" +
        "    x++;\n" +
        "  }\n" +
        "  public static void main(String[] nu) {\n" +
        "    new Thread(\"stuff\") {\n" +
        "      public void run() {\n" +
        "        new DrJavaDebugStaticField().bar();\n" +
        "      }\n" +
        "    }.start();\n" +
        "    new DrJavaDebugStaticField().bar();\n" +
        "  }\n" +
        "}";
  
  protected static final String MONKEY_STATIC_STUFF =
     "class MonkeyStaticStuff {\n" +
     "  static int foo = 6;\n" +
     "  static class MonkeyInner {\n" +
     "    static int innerFoo = 8;\n" +
     "    static public class MonkeyTwoDeep {\n" +
     "      static int twoDeepFoo = 13;\n" +
     "      static class MonkeyThreeDeep {\n" +
     "        public static int threeDeepFoo = 18;\n" +
     "        public static void threeDeepMethod() {\n" +
    "          System.out.println(MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.MonkeyThreeDeep.threeDeepFoo);\n" +
    "          System.out.println(MonkeyTwoDeep.twoDeepFoo);\n" +
    "          System.out.println(MonkeyStaticStuff.foo);\n" +
    "          System.out.println(MonkeyStaticStuff.MonkeyInner.innerFoo);\n" +
    "          System.out.println(MonkeyInner.MonkeyTwoDeep.twoDeepFoo);\n" +
    "          System.out.println(innerFoo);\n" +
    "        }\n" +
    "      }\n" +
    "      static int getNegativeTwo() { return -2; }\n" +
    "    }\n" +
    "  }\n" +
    "}";
  
  protected static final String THREAD_DEATH_CLASS =
     "class Jones {\n" +
     "  public static void threadShouldDie() {\n" +
     "    Thread cooper = new Thread() {\n" +
     "      public void run() {\n" +
     "        System.out.println(\"This thread should die.\");\n" +
     "      }\n" +
     "    };\n" +
     "    cooper.start();\n" +
     "    while(cooper.isAlive()) {}\n" +
     "    System.out.println(\"Thread died.\");\n" +
     "  }\n" +
     "}";
  
  
  public void setUp() throws Exception {
    super.setUp();
    _log.log("Setting up (DebugTestCase)" + this);
    _debugger = _model.getDebugger();
    assertNotNull("Debug Manager should not be null", _debugger);
  }
  
  
  public void tearDown() throws Exception {
    _log.log("Tearing down (DebugTestCase)" + this);
    _debugger = null;
    super.tearDown();
  }
  
  
  protected void _setPendingNotifies(int n) throws InterruptedException {
    synchronized(_notifierLock) {
      _log.log("Waiting for " + n + " notifications ...");
      _pendingNotifies = n;
    }
  }
  
  
  protected void _notifyLock() {
    synchronized(_notifierLock) {
      _pendingNotifies--;
      _log.log("notified; count = " + _pendingNotifies);     
      if (_pendingNotifies == 0) {
        _log.log("Notify count reached 0 -- notifying!");
        _notifierLock.notifyAll();  
      }
      if (_pendingNotifies < 0) fail("Notified too many times");
    }
  }
  
  
  protected OpenDefinitionsDocument _startupDebugger(String fileName, String classText) throws Exception {
    
    File file = IOUtil.attemptCanonicalFile(new File(_tempDir, fileName));
    return _startupDebugger(file, classText);
  }
  
  
  protected OpenDefinitionsDocument _startupDebugger(File file, String classText) throws Exception {
    
    _log.log("Compiling " + file);
    OpenDefinitionsDocument doc = doCompile(classText, file);
    _log.log("Staring debugger in " + this);
    
    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _debugger.startUp();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    _log.log("Finished starting debugger in " + this);
    return doc;
  }
  
  
  protected void _shutdownWithoutSuspendedInteraction() throws Exception {
    _log.log("Shutting down debugger in " + this + " without waiting");
    _model.getBreakpointManager().clearRegions();
    
    
    _log.log("Shutting down...");
    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _debugger.shutdown();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    _log.log("Shut down.");
    _log.log("Completed debugger shutdown for " + this);
  }
  
  
  protected void _shutdownAndWaitForInteractionEnded() throws Exception {
    _log.log("Shutting down debugger in " + this + " with waiting");
    _model.getBreakpointManager().clearRegions();
    
    
    _log.log("Shutting down...");
    InterpretListener interpretListener = new InterpretListener() {
      public void interpreterChanged(boolean inProgress) {
        
        interpreterChangedCount++;
      }
    };
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _debugger.shutdown();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    interpretListener.assertInterpreterChangedCount(1);  
    _model.removeListener(interpretListener);
    
    _log.log("Shut down.");
    _log.log("Completed debugger shutdown for " + this);
  }
  
  
  protected void _doSetCurrentThread(final DebugThreadData t) throws DebugException {
    Utilities.invokeLater(new Runnable() { 
      public void run() { 
        try {_debugger.setCurrentThread(t); }
        catch(DebugException e) { throw new UnexpectedException(e); }
      } 
    });
  }
  
  
  protected void _asyncStep(final Debugger.StepType type) {
    new Thread("asyncStep Thread") {
      public void run() {
        try { _debugger.step(type); }
        catch(DebugException dbe) {
          dbe.printStackTrace();
          listenerFail("Debugger couldn't be resumed!\n" + dbe);
        }
      }
    }.start();
  }
  
  
  protected void _asyncResume() {
    new Thread("asyncResume Thread") {
      public void run() {
        try { _debugger.resume(); }
        catch(DebugException dbe) {
          dbe.printStackTrace();
          listenerFail("Debugger couldn't be resumed!\n" + dbe);
        }
      }
    }.start();
  }
  
  
  protected void _asyncDoSetCurrentThread(final DebugThreadData th) {
    new Thread("asyncDoSetCurrentThread Thread") {
      public void run() {
        try { _doSetCurrentThread(th); }
        catch (DebugException dbe) {
          dbe.printStackTrace();
          listenerFail("Couldn't set current thread in _asyncDoSetCurrentThread\n" + dbe);
        }
      }
    }.start();
  }
  
  
  protected class DebugTestListener implements DebugListener {
    protected volatile int debuggerStartedCount = 0;
    protected volatile int debuggerShutdownCount = 0;
    protected volatile int threadLocationUpdatedCount = 0;
    protected volatile int breakpointReachedCount = 0;
    protected volatile int regionAddedCount = 0;
    protected volatile int regionChangedCount = 0;
    protected volatile int regionRemovedCount = 0;
    protected volatile int watchSetCount = 0;
    protected volatile int watchRemovedCount = 0;
    protected volatile int stepRequestedCount = 0;
    protected volatile int currThreadSuspendedCount = 0;
    protected volatile int currThreadResumedCount = 0;
    protected volatile int threadStartedCount = 0;
    protected volatile int currThreadDiedCount = 0;
    protected volatile int currThreadSetCount = 0;
    protected volatile int nonCurrThreadDiedCount = 0;
    
    public DebugTestListener() { }
    
    public void assertDebuggerStartedCount(int i) {
      assertEquals("number of times debuggerStarted fired", i, debuggerStartedCount);
    }
    
    public void assertDebuggerShutdownCount(int i) {
      assertEquals("number of times debuggerShutdown fired", i, debuggerShutdownCount);
    }
    
    public void assertThreadLocationUpdatedCount(int i) {
      assertEquals("number of times threadLocationUpdated fired", i, threadLocationUpdatedCount);
    }
    
    public void assertBreakpointReachedCount(int i) {
      assertEquals("number of times breakpointReached fired", i, breakpointReachedCount);
    }
    
    public void assertRegionAddedCount(int i) {
      assertEquals("number of times regionAdded fired", i, regionAddedCount);
    }
    
    public void assertRegionChangedCount(int i) {
      assertEquals("number of times regionChanged fired", i, regionChangedCount);
    }
    
    public void assertRegionRemovedCount(int i) {
      assertEquals("number of times regionRemoved fired", i, regionRemovedCount);
    }
    
    public void assertWatchSetCount(int i) {
      assertEquals("number of times watchSet fired", i, watchSetCount);
    }
    
    public void assertWatchRemovedCount(int i) {
      assertEquals("number of times watchRemoved fired", i, watchRemovedCount);
    }
    
    public void assertStepRequestedCount(int i) {
      assertEquals("number of times stepRequested fired", i, stepRequestedCount);
    }
    
    public void assertStepFinishedCount(int i) {
      assertEquals("number of times stepRequested fired", i, stepRequestedCount);
    }
    
    public void assertCurrThreadSuspendedCount(int i) {
      assertEquals("number of times currThreadSuspended fired", i, currThreadSuspendedCount);
    }
    
    public void assertCurrThreadResumedCount(int i) {
      assertEquals("number of times currThreadResumed fired", i, currThreadResumedCount);
    }
    
    public void assertCurrThreadSetCount(int i) {
      assertEquals("number of times currThreadSet fired", i, currThreadSetCount);
    }
    
    public void assertThreadStartedCount(int i) {
      assertEquals("number of times threadStarted fired", i,threadStartedCount);
    }
    
    public void assertCurrThreadDiedCount(int i) {
      assertEquals("number of times currThreadDied fired", i, currThreadDiedCount);
    }
    
    public void assertNonCurrThreadDiedCount(int i) {
      assertEquals("number of times nonCurrThreadDied fired", i, nonCurrThreadDiedCount);
    }
    
    
    public void debuggerStarted() { fail("debuggerStarted fired unexpectedly"); }
    
    public void debuggerShutdown() { fail("debuggerShutdown fired unexpectedly"); }
    
    public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) {
      fail("threadLocationUpdated fired unexpectedly");
    }
    
    public void breakpointReached(Breakpoint bp) { fail("breakpointReached fired unexpectedly"); }
    
    public void regionAdded(Breakpoint bp) { fail("regionAdded fired unexpectedly"); }
    
    public void regionChanged(Breakpoint bp) { fail("regionChanged fired unexpectedly"); }
    
    public void regionRemoved(Breakpoint bp) { fail("regionRemoved fired unexpectedly"); }
    
    public void watchSet(DebugWatchData w) { fail("watchSet fired unexpectedly"); }
    
    public void watchRemoved(DebugWatchData w) { fail("watchRemoved fired unexpectedly"); }
    
    public void stepRequested() { fail("stepRequested fired unexpectedly"); }
    
    public void currThreadSuspended() { fail("currThreadSuspended fired unexpectedly"); }
    
    public void currThreadResumed() { fail("currThreadResumed fired unexpectedly"); }
    
    public void currThreadSet(DebugThreadData dtd) { fail("currThreadSet fired unexpectedly"); }
    
    
    public void threadStarted() { threadStartedCount++; }
    
    public void currThreadDied() { fail("currThreadDied fired unexpectedly"); }
    
    
    public void nonCurrThreadDied() { nonCurrThreadDiedCount++; }
  }
  
  
  protected class DebugStartAndStopListener extends DebugTestListener {
    
    public DebugStartAndStopListener() { }
    
    public void debuggerStarted() {
      
      synchronized(_notifierLock) {
        debuggerStartedCount++;
        _log.log("debuggerStarted " + debuggerStartedCount);
        _notifyLock();
      }
    }
    public void debuggerShutdown() {
      
      synchronized(_notifierLock) {
        debuggerShutdownCount++;
        _log.log("debuggerShutdown " + debuggerShutdownCount);
        _notifyLock();
      }
    }
  }
  
  
  protected class BreakpointTestListener extends DebugStartAndStopListener {
    
    public BreakpointTestListener() { }
    
    public void breakpointReached(Breakpoint bp) {
      
      synchronized(_notifierLock) {
        breakpointReachedCount++;
        _log.log("breakpointReached " + breakpointReachedCount);
        _notifyLock();
      }
    }
    public void regionAdded(Breakpoint bp) {
      
      regionAddedCount++;
    }
    public void regionRemoved(Breakpoint bp) {
      
      regionRemovedCount++;
      _log.log("regionRemoved " + regionRemovedCount);
    }
    
    public void currThreadSuspended() {
      
      synchronized(_notifierLock) {
        currThreadSuspendedCount++;
        _log.log("threadSuspended " + currThreadSuspendedCount);
        _notifyLock();
      }
    }
    public void currThreadResumed() {
      
      currThreadResumedCount++;
      _log.log("threadResumed " + currThreadResumedCount);
    }
    public void currThreadSet(DebugThreadData dtd) {
      
      currThreadSetCount++;
      _log.log("threadSet " + currThreadSetCount);
    }
    public void currThreadDied() {
      
      synchronized(_notifierLock) {
        currThreadDiedCount++;
        _log.log("currThreadDied " + currThreadDiedCount);
        _notifyLock();
      }
    }
    public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) {
      
      synchronized(_notifierLock) {
        threadLocationUpdatedCount++;
        _log.log("threadUpdated " + threadLocationUpdatedCount);
        _notifyLock();
      }
    }
    public void watchSet(DebugWatchData w) {
      
      watchSetCount++;
      _log.log("watchSet " + watchSetCount);
    }
    public void watchRemoved(DebugWatchData w) {
      
      watchRemovedCount++;
      _log.log("watchRemoved " + watchRemovedCount);
    }
  }
  
  
  protected class StepTestListener extends BreakpointTestListener {
    
    public StepTestListener() { }
    
    public void stepRequested() {
      
      stepRequestedCount++;
      _log.log("stepRequested " + stepRequestedCount);
    }
  }
  
  
  protected class InterpretListener extends TestListener {
    
    public InterpretListener() { }
    
    public void interactionStarted() {
      synchronized(_notifierLock) {
        interactionStartCount++;
        _log.log("interactionStarted " + interactionStartCount);
        _notifyLock();
      }
    }
    public void interactionEnded() {
      synchronized(_notifierLock) {
        interactionEndCount++;
        _log.log("interactionEnded " + interactionEndCount);
        _notifyLock();
      }
    }
    
    public void interpreterChanged(boolean inProgress) {
      synchronized(_notifierLock) {
        interpreterChangedCount++;
        _log.log("interpreterChanged " + interpreterChangedCount);
        _notifyLock();
      }
    }
  }
}