

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.*;

import java.io.*;


public abstract class DebugTestCase extends GlobalModelTestCase {

  protected final boolean printEvents = false;
  protected final boolean printMessages = false;
  protected PrintStream printStream = System.out; 
  

  protected int _pendingNotifies = 0;
  protected Object _notifierLock = new Object();

  protected JPDADebugger _debugger;

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
    _debugger = (JPDADebugger) _model.getDebugger();
    assertNotNull("Debug Manager should not be null", _debugger);
  }

  
  public void tearDown() throws Exception {
    _debugger = null;
    super.tearDown();
  }

  
  protected void _setPendingNotifies(int n) throws InterruptedException {
    synchronized(_notifierLock) {
      if (printMessages) printStream.println("waiting for " + n + " notifications...");
      _pendingNotifies = n;
    }
  }

  
  protected void _notifyLock() {
    synchronized(_notifierLock) {
      _pendingNotifies--;
      if (printMessages) printStream.println("notified, count = "+_pendingNotifies);     
      if (_pendingNotifies == 0) {
        if (printMessages) printStream.println("Notify count reached 0 -- notifying!");
        _notifierLock.notifyAll();  
      }
      if (_pendingNotifies < 0) fail("Notified too many times");
    }
  }

  
  protected OpenDefinitionsDocument _startupDebugger(String fileName, String classText) throws Exception {
    
    File file = new File(_tempDir, fileName);
    return _startupDebugger(file, classText);
  }

  
  protected OpenDefinitionsDocument _startupDebugger(File file, String classText) throws Exception {
    
    OpenDefinitionsDocument doc = doCompile(classText, file);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _debugger.startup();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    return doc;
  }

  
  protected void _shutdownWithoutSuspendedInteraction() throws Exception {
    _debugger.removeAllBreakpoints();

    
    if (printMessages) printStream.println("Shutting down...");
    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _debugger.shutdown();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    if (printMessages) printStream.println("Shut down.");
  }

  
  protected void _shutdownAndWaitForInteractionEnded() throws Exception {
    _debugger.removeAllBreakpoints();

    
    if (printMessages) printStream.println("Shutting down...");
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

    if (printMessages) printStream.println("Shut down.");
  }

  
  protected void _doSetCurrentThread(final DebugThreadData t) throws DebugException {
    _debugger.setCurrentThread(t);
  }

  
  protected void _asyncStep(final int whatKind) {
    new Thread("asyncStep Thread") {
      public void run() {
        try { _debugger.step(whatKind); }
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
    protected int debuggerStartedCount = 0;
    protected int debuggerShutdownCount = 0;
    protected int threadLocationUpdatedCount = 0;
    protected int breakpointSetCount = 0;
    protected int breakpointReachedCount = 0;
    protected int breakpointChangedCount = 0;
    protected int breakpointRemovedCount = 0;
    protected int watchSetCount = 0;
    protected int watchRemovedCount = 0;
    protected int stepRequestedCount = 0;
    protected int currThreadSuspendedCount = 0;
    protected int currThreadResumedCount = 0;
    protected int threadStartedCount = 0;
    protected int currThreadDiedCount = 0;
    protected int currThreadSetCount = 0;
    protected int nonCurrThreadDiedCount = 0;

    public void assertDebuggerStartedCount(int i) {
      assertEquals("number of times debuggerStarted fired", i, debuggerStartedCount);
    }

    public void assertDebuggerShutdownCount(int i) {
      assertEquals("number of times debuggerShutdown fired", i, debuggerShutdownCount);
    }

    public void assertThreadLocationUpdatedCount(int i) {
      assertEquals("number of times threadLocationUpdated fired", i, threadLocationUpdatedCount);
    }

    public void assertBreakpointSetCount(int i) {
      assertEquals("number of times breakpointSet fired", i, breakpointSetCount);
    }

    public void assertBreakpointReachedCount(int i) {
      assertEquals("number of times breakpointReached fired", i, breakpointReachedCount);
    }

    public void assertBreakpointChangedCount(int i) {
      assertEquals("number of times breakpointChanged fired", i, breakpointChangedCount);
    }

    public void assertBreakpointRemovedCount(int i) {
      assertEquals("number of times breakpointRemoved fired", i, breakpointRemovedCount);
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

    public void breakpointSet(Breakpoint bp) { fail("breakpointSet fired unexpectedly"); }

    public void breakpointReached(Breakpoint bp) { fail("breakpointReached fired unexpectedly"); }

    public void breakpointChanged(Breakpoint bp) { fail("breakpointChanged fired unexpectedly"); }

    public void breakpointRemoved(Breakpoint bp) { fail("breakpointRemoved fired unexpectedly"); }

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
    public void debuggerStarted() {
      
      synchronized(_notifierLock) {
        debuggerStartedCount++;
        if (printEvents) printStream.println("debuggerStarted " + debuggerStartedCount);
        _notifyLock();
      }
    }
    public void debuggerShutdown() {
      
      synchronized(_notifierLock) {
        debuggerShutdownCount++;
        if (printEvents) printStream.println("debuggerShutdown " + debuggerShutdownCount);
        _notifyLock();
      }
    }
  }

  
  protected class BreakpointTestListener extends DebugStartAndStopListener {
    public BreakpointTestListener() { }
    public void breakpointSet(Breakpoint bp) {
      
      breakpointSetCount++;
    }
    public void breakpointReached(Breakpoint bp) {
      
      synchronized(_notifierLock) {
        breakpointReachedCount++;
        if (printEvents) printStream.println("breakpointReached " + breakpointReachedCount);
        _notifyLock();
      }
    }
    public void breakpointRemoved(Breakpoint bp) {
      
      breakpointRemovedCount++;
      if (printEvents) printStream.println("breakpointRemoved " + breakpointRemovedCount);
    }

    public void currThreadSuspended() {
      
      synchronized(_notifierLock) {
        currThreadSuspendedCount++;
        if (printEvents) printStream.println("threadSuspended " + currThreadSuspendedCount);
        _notifyLock();
      }
    }
    public void currThreadResumed() {
      
      currThreadResumedCount++;
      if (printEvents) printStream.println("threadResumed " + currThreadResumedCount);
    }
    public void currThreadSet(DebugThreadData dtd) {
      
      currThreadSetCount++;
      if (printEvents) printStream.println("threadSet " + currThreadSetCount);
    }
    public void currThreadDied() {
      
      synchronized(_notifierLock) {
        currThreadDiedCount++;
        if (printEvents) printStream.println("currThreadDied " + currThreadDiedCount);
        _notifyLock();
      }
    }
    public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) {
      
      synchronized(_notifierLock) {
        threadLocationUpdatedCount++;
        if (printEvents) printStream.println("threadUpdated " + threadLocationUpdatedCount);
        _notifyLock();
      }
    }
    public void watchSet(DebugWatchData w) {
      
      watchSetCount++;
      if (printEvents) printStream.println("watchSet " + watchSetCount);
    }
    public void watchRemoved(DebugWatchData w) {
      
      watchRemovedCount++;
      if (printEvents) printStream.println("watchRemoved " + watchRemovedCount);
    }
  }

  
  protected class StepTestListener extends BreakpointTestListener {
    public void stepRequested() {
      
      stepRequestedCount++;
      if (printEvents) printStream.println("stepRequested " + stepRequestedCount);
    }
  }

  
  protected class InterpretListener extends TestListener {
    public void interactionStarted() {
      synchronized(_notifierLock) {
        interactionStartCount++;
        if (printEvents) printStream.println("interactionStarted " + interactionStartCount);
        _notifyLock();
      }
    }
    public void interactionEnded() {
      synchronized(_notifierLock) {
        interactionEndCount++;
        if (printEvents) printStream.println("interactionEnded " + interactionEndCount);
        _notifyLock();
      }
    }

    public void interpreterChanged(boolean inProgress) {
      synchronized(_notifierLock) {
        interpreterChangedCount++;
        if (printEvents) printStream.println("interpreterChanged " + interpreterChangedCount);
        _notifyLock();
      }
    }
  }
}