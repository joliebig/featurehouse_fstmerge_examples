

package edu.rice.cs.drjava.model.debug.jpda;

import edu.rice.cs.util.Log;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.io.*;
import javax.swing.SwingUtilities;  

import edu.rice.cs.drjava.model.debug.DebugException;
import edu.rice.cs.util.UnexpectedException;


public class EventHandlerThread extends Thread {
  
  
  private final JPDADebugger _debugger;
  
  
  private final VirtualMachine _vm;
  
  
  private volatile boolean _connected;
  
  
  private static final Log _log = new Log("GlobalModel.txt", false);
  
  
  EventHandlerThread(JPDADebugger debugger, VirtualMachine vm) {
    super("DrJava Debug Event Handler");
    _debugger = debugger;
    _vm = vm;
    _connected = true;
  }
  
  
  private void _log(String message) { _log.log(message); }
  
  
  private void _log(String message, Throwable t) { _log.log(message, t); }
  
  
  public void run() {
    _log.log("Debugger starting");
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(baos, true));
        _debugger.printMessage("Stack trace: "+baos.toString());
      }
    }
    
    _debugger.notifyDebuggerShutdown();
  }
  
  
  private void handleEvent(Event e) throws DebugException {

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
  
  
  private boolean _isSuspendedWithFrames(ThreadReference thread) throws DebugException {
    
    try { return thread.isSuspended() && thread.frameCount() > 0; }
    catch (IncompatibleThreadStateException itse) {
      throw new DebugException("Could not count frames on a suspended thread: " + itse);
    }
  }
  
  
  private void _handleBreakpointEvent(final BreakpointEvent e)  {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {

        try {
          if (_isSuspendedWithFrames(e.thread()) && _debugger.setCurrentThread(e.thread())) {

            _debugger.currThreadSuspended((BreakpointRequest) e.request());

            _debugger.reachedBreakpoint((BreakpointRequest) e.request());

          }
        }
        catch(DebugException e) { throw new UnexpectedException(e); }
      }
    });
  }
  
  
  private void _handleStepEvent(final StepEvent e)  {
    
    
    
    
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          _debugger.preloadDocument(e.location());
          

          if (_isSuspendedWithFrames(e.thread()) && _debugger.setCurrentThread(e.thread())) {
            _debugger.printMessage("Stepped to " + e.location().declaringType().name() + "." + e.location().method().name()
                                     + "(...)  [line " + e.location().lineNumber() + "]");
            _debugger.currThreadSuspended();

          }
          
          _debugger.getEventRequestManager().deleteEventRequest(e.request());

        }
        catch(DebugException e) { throw new UnexpectedException(e); }
      }
    });
  }
  









  
  
  private void _handleClassPrepareEvent(final ClassPrepareEvent e)  {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          _debugger.getPendingRequestManager().classPrepared(e);
          
          
          e.thread().resume();
        }
        catch(DebugException e) { throw new UnexpectedException(e); }

      }
    });
  }
  
  
  private void _handleThreadStartEvent(ThreadStartEvent e) { 

      _debugger.threadStarted(); 

  }
  
  
  private void _handleThreadDeathEvent(final ThreadDeathEvent e)  {
    
    

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try {
          ThreadReference running = _debugger.getCurrentRunningThread();
          if (e.thread().equals(running)) {
            
            EventRequestManager erm = _vm.eventRequestManager();
            for (StepRequest step : erm.stepRequests()) {
              if (step.thread().equals(e.thread())) {
                erm.deleteEventRequest(step);
                
                
                
                break;
              }
            }
            _debugger.currThreadDied();
          }
          else _debugger.nonCurrThreadDied();

          
          
          e.thread().resume();
        }
        catch(DebugException e) { throw new UnexpectedException(e); }
      }   
    });
  }
                          
  
  
  private void _handleVMDeathEvent(VMDeathEvent e) throws DebugException { _cleanUp(e); }
  
  
  private void _handleVMDisconnectEvent(VMDisconnectEvent e) throws DebugException { _cleanUp(e); }
  
  
  private void _cleanUp(Event e) throws DebugException {

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        _connected = false;
        if (_debugger.isReady()) {
          
          
          _debugger.shutdown();
        }

      }
    });
  }
  
  
  private void handleDisconnectedException() throws DebugException {
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
