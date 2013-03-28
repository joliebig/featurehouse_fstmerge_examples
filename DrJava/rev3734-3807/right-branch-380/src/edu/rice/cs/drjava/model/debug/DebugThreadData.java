

package edu.rice.cs.drjava.model.debug;

import com.sun.jdi.*;


public class DebugThreadData {
  private final ThreadReference _thread;
  private final String _name;
  private final String _status;
  private final long _uniqueID;
  
  
  public DebugThreadData(ThreadReference thread) {
    _thread = thread;
    String name;
    try {
      name = _thread.name();
    }
    catch(VMDisconnectedException e) {
      name = "";
    }
    _name = name;
    String status = "(unknown)";
    try{
      switch (_thread.status()) {
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
    if ( isSuspended() && status.equals("RUNNING") ) {
      _status = "SUSPENDED";
    }
    else{
      _status = status;
    }
    _uniqueID = _thread.uniqueID();
  }
  
  
  public String getName() {
    return _name;
  }
  
  
  public String getStatus() {
    return _status;
  }
  
  public long getUniqueID() {
    return _uniqueID;
  }
  
  
  public boolean isSuspended() {
    try {
      return _thread.isSuspended();
    }
    catch (ObjectCollectedException oce) {
      return false;
    }
    catch (VMDisconnectedException vmde) {
      return false;
    }
  }
}
