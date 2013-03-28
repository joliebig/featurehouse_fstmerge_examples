

package edu.rice.cs.drjava.model.debug.jpda;

import com.sun.jdi.*;
import edu.rice.cs.drjava.model.debug.DebugThreadData;


public class JPDAThreadData extends DebugThreadData {
  private final ThreadReference _thread;
  
  
  public JPDAThreadData(ThreadReference thread) {
    super(threadName(thread), threadStatus(thread), thread.uniqueID());
    _thread = thread;
  }
  
  private static String threadName(ThreadReference thread) {
    try { return thread.name(); }
    catch (VMDisconnectedException e) { return ""; }
  }
  
  private static String threadStatus(ThreadReference thread) {
    String status = "(unknown)";
    try {
      switch (thread.status()) {
        case ThreadReference.THREAD_STATUS_MONITOR: 
          status = "MONITOR"; break;
        case ThreadReference.THREAD_STATUS_NOT_STARTED:
          status = "NOT STARTED"; break;
        case ThreadReference.THREAD_STATUS_RUNNING:
          status = "RUNNING"; break;
        case ThreadReference.THREAD_STATUS_SLEEPING:
          status = "SLEEPING"; break;
        case ThreadReference.THREAD_STATUS_UNKNOWN:
          status = "UNKNOWN"; break;
        case ThreadReference.THREAD_STATUS_WAIT:
          status = "WAIT"; break;
        case ThreadReference.THREAD_STATUS_ZOMBIE:
          status = "ZOMBIE"; break;
      }
    }
    catch (VMDisconnectedException e) {
      
    }
    if ( safeIsSuspended(thread) && status.equals("RUNNING") ) {
      status = "SUSPENDED";
    }
    return status;
  }
  
  
  public boolean isSuspended() { return safeIsSuspended(_thread); }
  
  
  private static boolean safeIsSuspended(ThreadReference t) {
    try { return t.isSuspended(); }
    catch (ObjectCollectedException e) { return false; }
    catch (VMDisconnectedException e) { return false; }
  }
  
}
