

package edu.rice.cs.drjava.model.debug.jpda;

import java.io.*;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;
import edu.rice.cs.drjava.model.debug.*;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;


public final class DebugTest extends JPDADebugTestCase implements OptionConstants {
  
  public void testStub() {  }
  
  
  public void XXXtestStartupAndShutdown() throws DebugException, InterruptedException {
    _log.log("----testStartupAndShutdown----");
    DebugTestListener debugListener = new DebugStartAndStopListener();
    _debugger.addListener(debugListener);
    
    
    synchronized(_notifierLock) {
      _debugger.startUp();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(1);  
    debugListener.assertDebuggerShutdownCount(0);
    
    
    assertTrue("Debug Manager should be ready", _debugger.isReady());
    assertNotNull("EventRequestManager should not be null after startUp", _debugger.getEventRequestManager());
    assertNotNull("PendingRequestManager should not be null after startUp", _debugger.getPendingRequestManager());
    
    
    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(1);
    debugListener.assertDebuggerShutdownCount(1);  
    
    
    synchronized(_notifierLock) {
      _debugger.startUp();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(2);  
    debugListener.assertDebuggerShutdownCount(1);
    
    
    InterpretListener resetListener = new InterpretListener() {
      public void interactionStarted() {
        
        _log.log("interactionStarted called in resetListener");
        interactionStartCount++;
      }
      public void interactionEnded() {
        
        _log.log("interactionEnded called in resetListener");
        interactionEndCount++;
      }
      public void interpreterChanged(boolean inProgress) {
        
        _log.log("interpreterChanged called in resetListener");
        interpreterChangedCount++;
      }
      public void interpreterResetting() {
        
        _log.log("interpreterResetting called in resetListener");
        interpreterResettingCount++;
      }
      public void interpreterReady(File wd) {
        synchronized(_notifierLock) {
          interpreterReadyCount++;
          _log.log("interpreterReady " + interpreterReadyCount);
          _notifyLock();
        }
      }
      public void consoleReset() { consoleResetCount++; }
    };
    
    
    _model.addListener(resetListener); 
    _setPendingNotifies(2);
    
    

    
    _model.resetInteractions(FileOps.NULL_FILE);
    
    synchronized(_notifierLock) { while (_pendingNotifies > 0) _notifierLock.wait(); }
    
    _model.removeListener(resetListener);
    
    resetListener.assertInterpreterResettingCount(1);  
    resetListener.assertInterpreterReadyCount(1);  
    debugListener.assertDebuggerStartedCount(2);
    debugListener.assertDebuggerShutdownCount(2);  
    
    
    
    synchronized(_notifierLock) {
      _debugger.startUp();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(3);  
    debugListener.assertDebuggerShutdownCount(2);
    
    
    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(3);
    debugListener.assertDebuggerShutdownCount(3);  
    
    _debugger.removeListener(debugListener);
  }
  
  
  
  public synchronized void XXXtestMultiThreadedSetCurrentThread() throws Exception {
    _log.log("----testMultiThreadedSetCurrentThread----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("Monkey.java",
                                                   MONKEY_CLASS);
    
    
    int index = MONKEY_CLASS.indexOf("System.out.println(\"I\'m a thread! Yeah!\");");
    _debugger.toggleBreakpoint(doc,index, true);
    index = MONKEY_CLASS.indexOf("System.out.println(\"James likes bananas!\");");
    _debugger.toggleBreakpoint(doc,index, true);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("java Monkey");
      _setPendingNotifies(6); 
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    DebugThreadData threadA = new JPDAThreadData(_debugger.getCurrentThread());
    DebugThreadData threadB = new JPDAThreadData(_debugger.getThreadAt(1));
    synchronized(_notifierLock) {
      _asyncDoSetCurrentThread(threadB);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    DebugThreadData thread1 = new JPDAThreadData(_debugger.getThreadAt(1));
    DebugThreadData thread2 = new JPDAThreadData(_debugger.getCurrentThread());
    
    
    assertTrue(thread1.getUniqueID() == threadA.getUniqueID());
    assertTrue(thread2.getUniqueID() == threadB.getUniqueID());
    
    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }
  
  
  
  
  public synchronized void XXXtestMultiThreadedBreakpointsAndStep() throws Exception {
    _log.log("----testMultiThreadedBreakpointsAndStep----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("Monkey.java", MONKEY_CLASS);
    
    
    int index = MONKEY_CLASS.indexOf("System.out.println(\"I\'m a thread! Yeah!\");");
    _debugger.toggleBreakpoint(doc,index,true);
    index = MONKEY_CLASS.indexOf("System.out.println(\"I just woke up.  I\'m a big boy now.\");");
    _debugger.toggleBreakpoint(doc,index,true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(2);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("java Monkey");
      _setPendingNotifies(6);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    DebugThreadData thread = new JPDAThreadData(_debugger.getCurrentThread());
    
    synchronized(_notifierLock) {
      _asyncResume();
      _setPendingNotifies(2);  
      
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    DebugThreadData thread2 = new JPDAThreadData(_debugger.getCurrentThread());
    assertTrue("testMultiThreadedBreakPoint thread references should not be equal",
               !thread.getName().equals(thread2.getName()));
    
    
    debugListener.assertBreakpointReachedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertCurrThreadResumedCount(1);
    _debugger.removeListener(debugListener);
    
    _log.log("Testing stepping...");
    
    
    StepTestListener stepTestListener = new StepTestListener();
    _debugger.addListener(stepTestListener);
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_INTO);
      _setPendingNotifies(2); 
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    stepTestListener.assertStepRequestedCount(1);
    _debugger.removeListener(stepTestListener);
    
    DebugThreadData thread3 = new JPDAThreadData(_debugger.getCurrentThread());
    assertEquals("testMultiThreadedBreakPoint thread references should be equal",
                 thread2.getName(), thread3.getName());
    
    
    _debugger.addListener(debugListener);
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _asyncResume();
      _setPendingNotifies(3);  
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);
    
    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
  
  
  
  
  public synchronized void XXXtestBreakpoints() throws Exception {
    _log.log("----testBreakpoints----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);
    
    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(1);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("----After breakpoint:\n" + getInteractionsText());
    
    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");
    
    _log.log("adding another breakpoint");
    
    
    _debugger.toggleBreakpoint(doc, DEBUG_CLASS.indexOf("System.out.println(\"Bar Line 2\")"), true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(2);
    
    
    synchronized(_notifierLock) {
      _log.log("resuming");
      _asyncResume();
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    _log.log("----After one resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(1);  
    debugListener.assertBreakpointReachedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Bar Line 1");
    assertInteractionsDoesNotContain("Bar Line 2");
    
    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _log.log("-------- Resuming --------");
      _asyncResume();
      _setPendingNotifies(3);  
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);
    
    _log.log("----After second resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(2);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertThreadLocationUpdatedCount(2);
    debugListener.assertCurrThreadSuspendedCount(2);
    assertInteractionsContains("Foo Line 3");
    
    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
  
  
  public synchronized void XXXtestBreakpointsWithSameNamePrefix() throws Exception {
    _log.log("----testBreakpointsWithSameNamePrefix----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);
    
    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("Bar Line 1"),true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(1);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass2().baz()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("----After breakpoint:\n" + getInteractionsText());
    
    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Baz Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");
    
    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _log.log("-------- Resuming --------");
      _asyncResume();
      _setPendingNotifies(3);  
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);
    
    _log.log("----After second resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(1);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertThreadLocationUpdatedCount(1);
    debugListener.assertCurrThreadSuspendedCount(1);
    assertInteractionsContains("Bar Line 2");
    
    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
  
  
  public void XXXtestStepInto() throws Exception {
    _log.log("----testStepInto----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);
    
    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(1);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("----After breakpoint:\n" + getInteractionsText());
    
    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_INTO);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("Bar Line 1");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("****"+getInteractionsText());
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Bar Line 1");
    assertInteractionsDoesNotContain("Bar Line 2");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(3);  
    debugListener.assertCurrThreadResumedCount(3); 
    debugListener.assertThreadLocationUpdatedCount(4);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(4);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Bar Line 2");
    assertInteractionsDoesNotContain("Foo Line 3");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(5);  
    debugListener.assertCurrThreadResumedCount(5); 
    debugListener.assertThreadLocationUpdatedCount(6);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(6);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Foo Line 3");
    
    
    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(3);  
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);
    
    debugListener.assertStepRequestedCount(6);  
    debugListener.assertCurrThreadDiedCount(1);
    
    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
  
  
  public synchronized void XXXtestStepOut() throws Exception {
    _log.log("----testStepOut----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);
    
    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);
    
    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(1);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("----After breakpoint:\n" + getInteractionsText());
    
    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_INTO);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("Bar Line 1");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OUT);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("****"+getInteractionsText());
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Bar Line 2");
    assertInteractionsDoesNotContain("Foo Line 3");
    
    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }
  
  
  public synchronized void XXXtestStepOverWithPackage() throws Exception {
    _log.log("----testStepOverWithPackage----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);
    
    
    File aDir = new File(_tempDir, "a");
    aDir.mkdir();
    File file = new File(aDir, "DrJavaDebugClassWithPackage.java");
    
    
    OpenDefinitionsDocument doc = _startupDebugger(file, DEBUG_CLASS_WITH_PACKAGE);
    
    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS_WITH_PACKAGE.indexOf("foo line 1"), true);
    
    Utilities.clearEventQueue();
    debugListener.assertRegionAddedCount(1);
    
    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new a.DrJavaDebugClassWithPackage().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("----After breakpoint:\n" + getInteractionsText());
    
    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("foo line 1");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertStepRequestedCount(1);  
    debugListener.assertCurrThreadResumedCount(1); 
    debugListener.assertThreadLocationUpdatedCount(2);  
    debugListener.assertCurrThreadSuspendedCount(2);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("foo line 1");
    assertInteractionsDoesNotContain("foo line 2");
    
    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.StepType.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    
    _log.log("****"+getInteractionsText());
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("foo line 2");
    
    
    InterpretListener interpretListener = new InterpretListener();
    _model.addListener(interpretListener);
    synchronized(_notifierLock) {
      _asyncResume();
      _setPendingNotifies(3);  
      
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);
    
    _log.log("----After resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertThreadLocationUpdatedCount(3);
    debugListener.assertCurrThreadSuspendedCount(3);
    
    
    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }
  
  
  public void XXXtestGetPackageDir() {
    String class1 = "edu.rice.cs.drjava.model.MyTest";
    String class2 = "MyTest";
    String sep = System.getProperty("file.separator");
    
    assertEquals("package dir with package",
                 "edu" + sep + "rice" + sep + "cs" + sep +
                 "drjava" + sep + "model" + sep,
                 JPDADebugger.getPackageDir(class1));
    assertEquals("package dir without package",
                 "",
                 JPDADebugger.getPackageDir(class2));
  }
}

