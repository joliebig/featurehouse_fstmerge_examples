

package edu.rice.cs.drjava.model.debug;

import java.io.*;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.model.*;

import edu.rice.cs.util.swing.Utilities;


public final class DebugTest extends DebugTestCase implements OptionConstants {
  
  
  public void testStartupAndShutdown() throws DebugException, InterruptedException {
    if (printMessages) System.out.println("----testStartupAndShutdown----");
    DebugTestListener debugListener = new DebugStartAndStopListener();
    _debugger.addListener(debugListener);

    
    synchronized(_notifierLock) {
      _debugger.startup();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(1);  
    debugListener.assertDebuggerShutdownCount(0);

    
    assertTrue("Debug Manager should be ready", _debugger.isReady());
    assertNotNull("EventRequestManager should not be null after startup",
                  _debugger.getEventRequestManager());
    assertNotNull("PendingRequestManager should not be null after startup",
                  _debugger.getPendingRequestManager());

    
    synchronized(_notifierLock) {
      _debugger.shutdown();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(1);
    debugListener.assertDebuggerShutdownCount(1);  

    
    synchronized(_notifierLock) {
      _debugger.startup();
      _setPendingNotifies(1);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    debugListener.assertDebuggerStartedCount(2);  
    debugListener.assertDebuggerShutdownCount(1);
    
    
    InterpretListener resetListener = new InterpretListener() {
      public void interactionStarted() {
        
        if (printMessages) System.out.println("interactionStarted called in resetListener");
        interactionStartCount++;
      }
      public void interactionEnded() {
         
        if (printMessages) System.out.println("interactionEnded called in resetListener");
        interactionEndCount++;
      }
      public void interpreterChanged(boolean inProgress) {
        
        if (printMessages) System.out.println("interpreterChanged called in resetListener");
        interpreterChangedCount++;
      }
      public void interpreterResetting() {
        
        if (printMessages) System.out.println("interpreterResetting called in resetListener");
        interpreterResettingCount++;
      }
      public void interpreterReady(File wd) {
        synchronized(_notifierLock) {
          interpreterReadyCount++;
          if (printEvents) System.out.println("interpreterReady " + interpreterReadyCount);
          _notifyLock();
        }
      }
      public void consoleReset() { consoleResetCount++; }
    };
    
    
    _model.addListener(resetListener); 
    _setPendingNotifies(2);
    
    
    interpret("2+2");
    
    _model.resetInteractions(FileOption.NULL_FILE);

    synchronized(_notifierLock) { while (_pendingNotifies > 0) _notifierLock.wait(); }
    
    _model.removeListener(resetListener);
    
    resetListener.assertInterpreterResettingCount(1);  
    resetListener.assertInterpreterReadyCount(1);  
    debugListener.assertDebuggerStartedCount(2);
    debugListener.assertDebuggerShutdownCount(2);  


    
    synchronized(_notifierLock) {
      _debugger.startup();
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


  
  public synchronized void testMultiThreadedSetCurrentThread() throws Exception {
    if (printMessages) System.out.println("----testMultiThreadedSetCurrentThread----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("Monkey.java",
                                                   MONKEY_CLASS);

    
    int index = MONKEY_CLASS.indexOf("System.out.println(\"I\'m a thread! Yeah!\");");
    _debugger.toggleBreakpoint(doc,index,11,true);
    index = MONKEY_CLASS.indexOf("System.out.println(\"James likes bananas!\");");
    _debugger.toggleBreakpoint(doc,index,17,true);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("java Monkey");
      _setPendingNotifies(6); 
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    DebugThreadData threadA = new DebugThreadData(_debugger.getCurrentThread());
    DebugThreadData threadB = new DebugThreadData(_debugger.getThreadAt(1));
    synchronized(_notifierLock) {
      _asyncDoSetCurrentThread(threadB);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    DebugThreadData thread1 = new DebugThreadData(_debugger.getThreadAt(1));
    DebugThreadData thread2 = new DebugThreadData(_debugger.getCurrentThread());

    
    assertTrue(thread1.getUniqueID() == threadA.getUniqueID());
    assertTrue(thread2.getUniqueID() == threadB.getUniqueID());

    
    _shutdownAndWaitForInteractionEnded();
    _debugger.removeListener(debugListener);
  }

  

  
  public synchronized void testMultiThreadedBreakpointsAndStep() throws Exception {
    if (printMessages) System.out.println("----testMultiThreadedBreakpointsAndStep----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("Monkey.java",
                                                   MONKEY_CLASS);

    
    int index = MONKEY_CLASS.indexOf("System.out.println(\"I\'m a thread! Yeah!\");");
    _debugger.toggleBreakpoint(doc,index,11,true);
    index = MONKEY_CLASS.indexOf("System.out.println(\"I just woke up.  I\'m a big boy now.\");");
    _debugger.toggleBreakpoint(doc,index,16,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(2);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("java Monkey");
      _setPendingNotifies(6);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    DebugThreadData thread = new DebugThreadData(_debugger.getCurrentThread());
    
    synchronized(_notifierLock) {
      _asyncResume();
      _setPendingNotifies(2);  
                            
                            
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    DebugThreadData thread2 = new DebugThreadData(_debugger.getCurrentThread());
    assertTrue("testMultiThreadedBreakPoint thread references should not be equal",
               !thread.getName().equals(thread2.getName()));

    
    debugListener.assertBreakpointReachedCount(2);  
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertCurrThreadResumedCount(1);
    _debugger.removeListener(debugListener);

    if (printMessages) {
      System.out.println("Testing stepping...");
    }
    
    StepTestListener stepTestListener = new StepTestListener();
    _debugger.addListener(stepTestListener);
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_INTO);
      _setPendingNotifies(2); 
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    stepTestListener.assertStepRequestedCount(1);
    _debugger.removeListener(stepTestListener);

    DebugThreadData thread3 = new DebugThreadData(_debugger.getCurrentThread());
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



  
  public synchronized void testBreakpoints() throws Exception {
    if (printMessages) System.out.println("----testBreakpoints----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);

   
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("----After breakpoint:\n" + getInteractionsText());

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");

    if (printMessages) System.out.println("adding another breakpoint");

    
    _debugger.toggleBreakpoint(doc, DEBUG_CLASS.indexOf("System.out.println(\"Bar Line 2\")"), 9, true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(2);

    
    synchronized(_notifierLock) {
      if (printMessages) System.out.println("resuming");
      _asyncResume();
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    if (printMessages) System.out.println("----After one resume:\n" + getInteractionsText());
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
      if ( printMessages ) System.out.println("-------- Resuming --------");
      _asyncResume();
      _setPendingNotifies(3);  
                            
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);

    if (printMessages) System.out.println("----After second resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(2);  
    debugListener.assertBreakpointReachedCount(2);
    debugListener.assertThreadLocationUpdatedCount(2);
    debugListener.assertCurrThreadSuspendedCount(2);
    assertInteractionsContains("Foo Line 3");

    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }

  
  public synchronized void testBreakpointsWithSameNamePrefix() throws Exception {
    if (printMessages) System.out.println("----testBreakpointsWithSameNamePrefix----");
    BreakpointTestListener debugListener = new BreakpointTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);

   
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("Bar Line 1"),8,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass2().baz()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("----After breakpoint:\n" + getInteractionsText());

    
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
      if ( printMessages ) System.out.println("-------- Resuming --------");
      _asyncResume();
      _setPendingNotifies(3);  
                            
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    interpretListener.assertInteractionEndCount(1);
    _model.removeListener(interpretListener);

    if (printMessages) System.out.println("----After second resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(1);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertThreadLocationUpdatedCount(1);
    debugListener.assertCurrThreadSuspendedCount(1);
    assertInteractionsContains("Bar Line 2");

    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }

  
  public void testStepInto() throws Exception {
    if (printMessages) System.out.println("----testStepInto----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) {
      System.out.println("----After breakpoint:\n" + getInteractionsText());
    }

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");

    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_INTO);
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
      _asyncStep(Debugger.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("****"+getInteractionsText());
    debugListener.assertStepRequestedCount(2);  
    debugListener.assertCurrThreadResumedCount(2); 
    debugListener.assertThreadLocationUpdatedCount(3);  
    debugListener.assertCurrThreadDiedCount(0);
    debugListener.assertCurrThreadSuspendedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    assertInteractionsContains("Bar Line 1");
    assertInteractionsDoesNotContain("Bar Line 2");

    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_OVER);
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
      _asyncStep(Debugger.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_OVER);
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
      _asyncStep(Debugger.STEP_OVER);
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

  
  public synchronized void testStepOut() throws Exception {
    if (printMessages) {
      System.out.println("----testStepOut----");
    }
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    OpenDefinitionsDocument doc = _startupDebugger("DrJavaDebugClass.java",
                                                   DEBUG_CLASS);

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS.indexOf("bar();"),4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new DrJavaDebugClass().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("----After breakpoint:\n" + getInteractionsText());

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsContains("Foo Line 1");
    assertInteractionsDoesNotContain("Bar Line 1");

    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_INTO);
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
      _asyncStep(Debugger.STEP_OUT);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("****"+getInteractionsText());
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

  
  public synchronized void testStepOverWithPackage() throws Exception {
    if (printMessages) System.out.println("----testStepOverWithPackage----");
    StepTestListener debugListener = new StepTestListener();
    _debugger.addListener(debugListener);

    
    File aDir = new File(_tempDir, "a");
    aDir.mkdir();
    File file = new File(aDir, "DrJavaDebugClassWithPackage.java");

    
    OpenDefinitionsDocument doc = _startupDebugger(file, DEBUG_CLASS_WITH_PACKAGE);

    
    _debugger.toggleBreakpoint(doc,DEBUG_CLASS_WITH_PACKAGE.indexOf("foo line 1"), 4,true);
    
    Utilities.clearEventQueue();
    debugListener.assertBreakpointSetCount(1);

    
    synchronized(_notifierLock) {
      interpretIgnoreResult("new a.DrJavaDebugClassWithPackage().foo()");
      _setPendingNotifies(3);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("----After breakpoint:\n" + getInteractionsText());

    
    debugListener.assertBreakpointReachedCount(1);  
    debugListener.assertThreadLocationUpdatedCount(1);  
    debugListener.assertCurrThreadSuspendedCount(1);  
    debugListener.assertCurrThreadResumedCount(0);
    debugListener.assertCurrThreadDiedCount(0);
    assertInteractionsDoesNotContain("foo line 1");

    
    synchronized(_notifierLock) {
      _asyncStep(Debugger.STEP_OVER);
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
      _asyncStep(Debugger.STEP_OVER);
      _setPendingNotifies(2);  
      while (_pendingNotifies > 0) _notifierLock.wait();
    }

    if (printMessages) System.out.println("****"+getInteractionsText());
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

    if (printMessages) System.out.println("----After resume:\n" + getInteractionsText());
    debugListener.assertCurrThreadResumedCount(3);  
    debugListener.assertBreakpointReachedCount(1);
    debugListener.assertThreadLocationUpdatedCount(3);
    debugListener.assertCurrThreadSuspendedCount(3);


    
    _shutdownWithoutSuspendedInteraction();
    _debugger.removeListener(debugListener);
  }

  
  public void testGetPackageDir() {
    String class1 = "edu.rice.cs.drjava.model.MyTest";
    String class2 = "MyTest";
    String sep = System.getProperty("file.separator");

    assertEquals("package dir with package",
                 "edu" + sep + "rice" + sep + "cs" + sep +
                 "drjava" + sep + "model" + sep,
                 _debugger.getPackageDir(class1));
    assertEquals("package dir without package",
                 "",
                 _debugger.getPackageDir(class2));
  }
}
  
