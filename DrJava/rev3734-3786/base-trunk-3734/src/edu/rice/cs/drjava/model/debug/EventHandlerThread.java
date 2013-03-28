

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.util.Log;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;
import java.util.*;


public class EventHandlerThread extends Thread {

  
  protected final JPDADebugger _debugger;

  
  protected final VirtualMachine _vm;

  
  private boolean _connected;

  
  protected final Log _log;

  
  EventHandlerThread(JPDADebugger debugger, VirtualMachine vm) {
    super("DrJava Debug Event Handler");
    _debugger = debugger;
    _vm = vm;
    _connected = true;
    _log = new Log("EventHandlerThreadLog", false);
  }

  
  protected void _log(String message) {
    _log.logTime(message);
  }

  
  protected void _log(String message, Throwable t) {
    _log.logTime(message, t);
  }

  
  public void run() {
    _debugger.notifyDebuggerStarted();

    EventQueue queue = _vm.eventQueue();
    while (_connected) {
      try {
        try {
          
          EventSet eventSet = queue.remove();
          EventIterator it = eventSet.eventIterator();
          
          while (it.hasNext()) handleEvent(it.nextEvent());
        }
        catch (InterruptedException ie) {
          
          
          _log("InterruptedException in main loop: " + ie);
        }
        catch (VMDisconnectedException de) {
          
          handleDisconnectedException();
          break;
        }
      }
      catch (Exception e) {
        
        _log("Exception in main event handler loop.", e);
        _debugger.eventHandlerError(e);
        _debugger.printMessage("An exception occurred in the event handler:\n" + e);
        _debugger.printMessage("The debugger may have become unstable as a result.");
      }
    }

    _debugger.notifyDebuggerShutdown();
  }

  
  public void handleEvent(Event e) throws DebugException {

    _log("handling event: " + e);

    if (e instanceof BreakpointEvent) _handleBreakpointEvent((BreakpointEvent) e);
    else if (e instanceof StepEvent) _handleStepEvent((StepEvent) e);
    
    
    
    else if (e instanceof ClassPrepareEvent) _handleClassPrepareEvent((ClassPrepareEvent) e);
    else if (e instanceof ThreadStartEvent) _handleThreadStartEvent((ThreadStartEvent) e);
    else if (e instanceof ThreadDeathEvent) _handleThreadDeathEvent((ThreadDeathEvent) e);
    else if (e instanceof VMDeathEvent) _handleVMDeathEvent((VMDeathEvent) e);
    else if (e instanceof VMDisconnectEvent) _handleVMDisconnectEvent((VMDisconnectEvent) e);
    else
      throw new DebugException("Unexpected event type: " + e);
  }

  
  protected boolean _isSuspendedWithFrames(ThreadReference thread) throws DebugException {
    try {
      return thread.isSuspended() && thread.frameCount() > 0;
    }
    catch (IncompatibleThreadStateException itse) {
      throw new DebugException("Could not count frames on a suspended thread: " +
                               itse);
    }
  }

  
  protected void _handleBreakpointEvent(BreakpointEvent e) throws DebugException {
    synchronized(_debugger) {
      if (_isSuspendedWithFrames(e.thread()) && _debugger.setCurrentThread(e.thread())) {

        _debugger.currThreadSuspended();

        _debugger.reachedBreakpoint((BreakpointRequest) e.request());
      }
    }
  }

  
  protected void _handleStepEvent(StepEvent e) throws DebugException {
    synchronized(_debugger) {
      if (_isSuspendedWithFrames(e.thread()) && _debugger.setCurrentThread(e.thread())) {
        _debugger.printMessage("Stepped to " +
                               e.location().declaringType().name() + "." +
                               e.location().method().name() + "(...)  [line " +
                               e.location().lineNumber() + "]");
        _debugger.currThreadSuspended();

      }
      
      _debugger.getEventRequestManager().deleteEventRequest(e.request());
    }
  }











  
  protected void _handleClassPrepareEvent(ClassPrepareEvent e) throws DebugException {
    synchronized(_debugger) {
      _debugger.getPendingRequestManager().classPrepared(e);
      
      
      e.thread().resume();
    }
  }

  
  protected void _handleThreadStartEvent(ThreadStartEvent e) {
    synchronized(_debugger) { _debugger.threadStarted(); }
  }

  
  protected void _handleThreadDeathEvent(ThreadDeathEvent e) throws DebugException {
    
    
    synchronized(_debugger) {
      ThreadReference running = _debugger.getCurrentRunningThread();
      if (e.thread().equals(running)) {
        
        EventRequestManager erm = _vm.eventRequestManager();
        List steps = erm.stepRequests();
        for (int i = 0; i < steps.size(); i++) {
          StepRequest step = (StepRequest)steps.get(i);
          if (step.thread().equals(e.thread())) {
            erm.deleteEventRequest(step);

            
            
            break;
          }
        }
        _debugger.currThreadDied();
      }
      else
        _debugger.nonCurrThreadDied();
    }

    
    e.thread().resume();
  }

  
  protected void _handleVMDeathEvent(VMDeathEvent e) throws DebugException {
    _cleanUp(e);
  }

  
  protected void _handleVMDisconnectEvent(VMDisconnectEvent e) throws DebugException {
    _cleanUp(e);
  }

  
  protected void _cleanUp(Event e) throws DebugException {
    synchronized(_debugger) {
      _connected = false;
      if (_debugger.isReady()) {
        
        
        _debugger.shutdown();
      }
    }
  }

  
  synchronized void handleDisconnectedException() throws DebugException {
    EventQueue queue = _vm.eventQueue();
    while (_connected) {
      try {
        EventSet eventSet = queue.remove();
        EventIterator iter = eventSet.eventIterator();
        while (iter.hasNext()) {
          Event event = iter.nextEvent();
          if (event instanceof VMDeathEvent) _handleVMDeathEvent((VMDeathEvent)event);
          else if (event instanceof VMDisconnectEvent)  _handleVMDisconnectEvent((VMDisconnectEvent)event);
          
        }
        eventSet.resume(); 
      }
      catch (InterruptedException ie) {
        
        _log("InterruptedException after a disconnected exception.", ie);
      }
      catch (VMDisconnectedException de) {
        
        _log("A second VMDisconnectedException.", de);
      }
    }
  }
}
