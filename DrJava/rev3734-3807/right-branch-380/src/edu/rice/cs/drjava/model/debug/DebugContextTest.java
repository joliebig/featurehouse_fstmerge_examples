

package edu.rice.cs.drjava.model.debug;

import java.io.*;
import java.util.Vector;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.util.swing.Utilities;


public final class DebugContextTest extends DebugTestCase {
  
  
  public void testDebugSourcepath() throws Exception {
    if (printMessages) printStream.println("----testDebugSourcePath----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java", DEBUG_CLASS);
    Vector<File> path = new Vector<File>();
    path.addElement(_tempDir);  

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),4,true);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    debugListener.assertThreadLocationUpdatedCount(1);  

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_INTO);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(2);  

    
    _model.closeFile(doc);
    Utilities.clearEventQueue();
    debugListener.assertBreakpointRemovedCount(1);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(2);  

    synchronized(_debugger) {
      
      DrJava.getConfig().setSetting(OptionConstants.DEBUG_SOURCEPATH, path);
    }

    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    debugListener.assertStepRequestedCount(3);  
    debugListener.assertThreadLocationUpdatedCount(3);  

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  
  public synchronized void testBreakpointsAndStepsInNonPublicClasses() throws Exception {
    if (printMessages) printStream.println("----testBreakpointsAndStepsInNonPublicClasses----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java", DEBUG_CLASS);

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("Baz Line 1"), 14,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("new DrJavaDebugClass2().baz()");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) printStream.println("----After breakpoint:\n" + getInteractionsText());

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("Baz Line 1");

    if (printMessages) printStream.println("adding another breakpoint");

    
    _debugger.toggleBreakpoint(doc, DEBUG_CLASS.indexOf("System.out.println(\"Bar Line 2\")"), 9,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(2);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) printStream.println("****"+getInteractionsText());
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2); 
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Baz Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");

    
    synchronized(_notifierLock) {
      if (printMessages) printStream.println("resuming");
      _setPendingNotifies(3);  
      _asyncResume();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    if (printMessages) printStream.println("----After one resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(2);  
    debugListener.assertBreakpointReachedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Bar Line 1");
    assertInteractionsDoesNotContain("Bar Line 2");

    if ( printMessages ) printStream.println("-------- Adding interpret listener --------");
    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      if ( printMessages ) printStream.println("-------- resuming --------");
      _setPendingNotifies(3);  
      _asyncResume();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);

    if (printMessages) printStream.println("----After second resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(3);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertThreadLocationUpdatedCount(3);
    debugListener.assertCurrThreadSuspendedCount(3);
    assertInteractionsContains("Bar Line 2");

    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }

  
  public synchronized void testStepIntoOverBreakpoint() throws Exception {
    if (printMessages) {
      printStream.println("----testStepIntoOverBreakpoint----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java", DEBUG_CLASS);

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("Foo Line 1"), 3,true);
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();\n"), 4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(2);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) printStream.println("----After breakpoint:\n" + getInteractionsText());

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("Foo Line 1");

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) {
      printStream.println("****"+getInteractionsText());
    }
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);

    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      _asyncResume();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);

    if (printMessages) {
      printStream.println("----After resume:\n" + getInteractionsText());
    }
    debugListener.assertCurrThreadResumedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertThreadLocationUpdatedCount(3);
    debugListener.assertCurrThreadSuspendedCount(3);

    
    _model.closeFile(doc);
    Utilities.clearEventQueue();
    debugListener.assertBreakpointRemovedCount(2);  

    
    if (printMessages) printStream.println("Shutting down...");

    synchronized(_notifierLock) {
      _setPendingNotifies(1);  
      _debugger.shutdown();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    debugListener.assertDebuggerShutdownCount(1);  
    if (printMessages) printStream.println("Shut down.");
    _debugger.removeListener(debugListener);
  }

  
  public void testStaticFieldsConsistent() throws Exception {
    if (printMessages) printStream.println("----testStaticFieldsConsistent----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugStaticField.java",
                                                   CLASS_WITH_STATIC_FIELD);

    
    _debugger.toggleBreakpoint(doc,CLASS_WITH_STATIC_FIELD.indexOf("System.out.println"), 4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(6);  
      interpretIgnoreResult("java DrJavaDebugStaticField");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    
    DebugThreadData threadA = new DebugThreadData(_debugger.getCurrentThread());
    DebugThreadData threadB = new DebugThreadData(_debugger.getThreadAt(1));

     if (printMessages) {
      printStream.println("----After breakpoint:\n" + getInteractionsText());
    }

    
    debugListener.assertBreakpointReachedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertEquals("x has correct value at start", "0", interpret("DrJavaDebugStaticField.x"));
    assertEquals("assigning x succeeds", "5", interpret("DrJavaDebugStaticField.x = 5"));
    assertEquals("assignment reflected in this", "5", interpret("this.x"));

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("x == 5");
    assertEquals("x retains correct value after step", "5", interpret("DrJavaDebugStaticField.x"));
    assertEquals("this has correct value for x after step", "5", interpret("this.x"));

    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    if (printMessages) {
      printStream.println("****"+getInteractionsText());
    }
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(4);  
    debugListener.assertCurrThreadSuspendedCount(4);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertCurrThreadDiedCount(0);
    assertEquals("x has correct value after increment", "6", interpret("DrJavaDebugStaticField.x"));
    assertEquals("this has correct value for x after increment", "6", interpret("this.x"));

    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncDoSetCurrentThread(threadB);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpret("");
    assertInteractionsContains("The current thread has changed.");
    assertEquals("x has correct value in other thread", "6", interpret("DrJavaDebugStaticField.x"));
    assertEquals("this has correct value for x in other thread", "6", interpret("this.x"));

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  
  public void testNonStaticWatches() throws Exception {
    if (printMessages) {
      printStream.println("----testNonStaticWatches----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("Monkey.java",
                                                   MONKEY_WITH_INNER_CLASS);

    
    _debugger.toggleBreakpoint(doc,MONKEY_WITH_INNER_CLASS.indexOf("innerMethodFoo = 12;"), 10, true);
    _debugger.toggleBreakpoint(doc,MONKEY_WITH_INNER_CLASS.indexOf("System.out.println(\"localVar = \" + localVar);"), 32, true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(2);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("new Monkey().bar()");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    _debugger.addWatch("foo");
    _debugger.addWatch("innerFoo");
    _debugger.addWatch("innerInnerFoo");
    _debugger.addWatch("innerMethodFoo");
    _debugger.addWatch("asdf");
    _debugger.addWatch("nullString");
    _debugger.addWatch("localVar");
    Utilities.clearEventQueue();
    debugListener.assertWatchSetCount(7);

    if (printMessages) {
      printStream.println("first step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    Vector<DebugWatchData> watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "foo", watches.get(0).getName());
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch name incorrect", "innerInnerFoo", watches.get(2).getName());
    assertEquals("watch name incorrect", "innerMethodFoo", watches.get(3).getName());
    assertEquals("watch name incorrect", "asdf", watches.get(4).getName());
    assertEquals("watch name incorrect", "nullString", watches.get(5).getName());
    assertEquals("watch value incorrect", "6", watches.get(0).getValue());
    assertEquals("watch value incorrect", "8", watches.get(1).getValue());
    assertEquals("watch value incorrect", "10", watches.get(2).getValue());
    assertEquals("watch value incorrect", "12", watches.get(3).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(4).getValue());
    assertEquals("watch value incorrect", "null", watches.get(5).getValue());
    assertEquals("watch type incorrect", "java.lang.String", watches.get(5).getType());

    interpret("innerFoo = 0");
    watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch value incorrect", "0", watches.get(1).getValue());

    interpret("innerFoo = 8");
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch value incorrect", "8", watches.get(1).getValue());

    if (printMessages) {
      printStream.println("second step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    if (printMessages) {
      printStream.println("third step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(3);  
    debugListener.assertCurrThreadResumedCount(3); 
    debugListener.assertThreadLocationUpdatedCount(4);  
    debugListener.assertCurrThreadSuspendedCount(4);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    if (printMessages) {
      printStream.println("fourth step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(4);  
    debugListener.assertCurrThreadResumedCount(4); 
    debugListener.assertThreadLocationUpdatedCount(5);  
    debugListener.assertCurrThreadSuspendedCount(5);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    if (printMessages) {
      printStream.println("fifth step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_OVER);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(5);  
    debugListener.assertCurrThreadResumedCount(5); 
    debugListener.assertThreadLocationUpdatedCount(6);  
    debugListener.assertCurrThreadSuspendedCount(6);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "foo", watches.get(0).getName());
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch name incorrect", "innerInnerFoo", watches.get(2).getName());
    assertEquals("watch name incorrect", "innerMethodFoo", watches.get(3).getName());
    assertEquals("watch name incorrect", "asdf", watches.get(4).getName());
    assertEquals("watch name incorrect", "nullString", watches.get(5).getName());
    assertEquals("watch value incorrect", "7", watches.get(0).getValue());
    assertEquals("watch value incorrect", "9", watches.get(1).getValue());
    assertEquals("watch value incorrect", "11", watches.get(2).getValue());
    assertEquals("watch value incorrect", "13", watches.get(3).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(4).getValue());
    assertEquals("watch value incorrect", "null", watches.get(5).getValue());
    assertEquals("watch type incorrect", "java.lang.String", watches.get(5).getType());

    if (printMessages) {
      printStream.println("sixth step");
    }
    
    synchronized(_notifierLock) {
      _setPendingNotifies(2);  
      _asyncStep(Debugger.STEP_INTO);
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(6);  
    debugListener.assertCurrThreadResumedCount(6); 
    debugListener.assertThreadLocationUpdatedCount(7);  
    debugListener.assertCurrThreadSuspendedCount(7);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);

    
    watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "foo", watches.get(0).getName());
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch name incorrect", "innerInnerFoo", watches.get(2).getName());
    assertEquals("watch name incorrect", "innerMethodFoo", watches.get(3).getName());
    assertEquals("watch name incorrect", "asdf", watches.get(4).getName());
    assertEquals("watch name incorrect", "nullString", watches.get(5).getName());
    assertEquals("watch value incorrect", "7", watches.get(0).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(1).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(2).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(3).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(4).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(5).getValue());
    assertEquals("watch type incorrect", DebugWatchData.NO_TYPE, watches.get(5).getType());

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      _asyncResume();
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(6);  
    debugListener.assertCurrThreadResumedCount(7); 
    debugListener.assertThreadLocationUpdatedCount(8);  
    debugListener.assertCurrThreadSuspendedCount(8);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertCurrThreadDiedCount(0);

    
    watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "localVar", watches.get(6).getName());
    assertEquals("watch value incorrect", "11", watches.get(6).getValue());

    
    _model.closeFile(doc);
    Utilities.clearEventQueue();
    debugListener.assertBreakpointRemovedCount(2);  

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  
  public void testStaticWatches() throws Exception {
    if (printMessages) {
      printStream.println("----testStaticWatches----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("MonkeyStaticStuff.java",
                                                   MONKEY_STATIC_STUFF);

    
    int index = MONKEY_STATIC_STUFF.indexOf("System.out.println(MonkeyInner.MonkeyTwoDeep.twoDeepFoo);");
    _debugger.toggleBreakpoint(doc, index, 14, true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("MonkeyStaticStuff.MonkeyInner.MonkeyTwoDeep.MonkeyThreeDeep.threeDeepMethod();");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    _debugger.addWatch("foo");
    _debugger.addWatch("innerFoo");
    _debugger.addWatch("twoDeepFoo");
    _debugger.addWatch("threeDeepFoo");
    _debugger.addWatch("asdf");
    Utilities.clearEventQueue();
    debugListener.assertWatchSetCount(5);
    
    Vector<DebugWatchData> watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "foo", watches.get(0).getName());
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch name incorrect", "twoDeepFoo", watches.get(2).getName());
    assertEquals("watch name incorrect", "threeDeepFoo", watches.get(3).getName());
    assertEquals("watch name incorrect", "asdf", watches.get(4).getName());
    assertEquals("watch value incorrect", "6", watches.get(0).getValue());
    assertEquals("watch value incorrect", "8", watches.get(1).getValue());
    assertEquals("watch value incorrect", "13", watches.get(2).getValue());
    assertEquals("watch value incorrect", "18", watches.get(3).getValue());
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(4).getValue());

    interpret("innerFoo = 0");
    watches = _debugger.getWatches();
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch value incorrect", "0", watches.get(1).getValue());

    interpret("innerFoo = 8");
    assertEquals("watch name incorrect", "innerFoo", watches.get(1).getName());
    assertEquals("watch value incorrect", "8", watches.get(1).getValue());

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  
  public void testWatchLocalVarsFromInnerClass() throws Exception {
    if (printMessages) {
      printStream.println("----testWatchLocalVarsFromInnerClass----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("InnerClassWithLocalVariables.java",
                                                   INNER_CLASS_WITH_LOCAL_VARS);

    
    int index = INNER_CLASS_WITH_LOCAL_VARS.indexOf("numArgs:");
    _debugger.toggleBreakpoint(doc, index, 7, true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      _setPendingNotifies(3);  
      interpretIgnoreResult("java InnerClassWithLocalVariables arg");
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    _debugger.addWatch("numArgs");
    _debugger.addWatch("args");
    _debugger.addWatch("inlined");
    Utilities.clearEventQueue();
    debugListener.assertWatchSetCount(3);

    
    Vector<DebugWatchData> watches = _debugger.getWatches();
    assertEquals("numArgs watch value incorrect",
                 "1", watches.get(0).getValue());
    String argsWatch = watches.get(1).getValue();
    assertTrue("args watch value incorrect",
               argsWatch.indexOf("java.lang.String") != -1);

    
    assertEquals("watch value incorrect", DebugWatchData.NO_VALUE, watches.get(2).getValue());

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  
  public void testThreadShouldDie() throws Exception {
    if (printMessages) {
      printStream.println("----testThreadShouldDie----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaThreadDeathTest.java",
                                                   THREAD_DEATH_CLASS);

    
    
    interpret("Jones.threadShouldDie()");

    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
}
