

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.EventNotifier;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public class DebugEventNotifier extends EventNotifier<DebugListener> implements DebugListener {
  
  
  public void debuggerStarted() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).debuggerStarted();
      }
    }
    finally { _lock.endRead(); }
  }

  
  public void debuggerShutdown() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).debuggerShutdown();
      }
    }
    finally { _lock.endRead(); }
  }

  
  public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber,  boolean shouldHighlight) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).threadLocationUpdated(doc, lineNumber, shouldHighlight);
      }
    }
    finally { _lock.endRead(); }
  }

  
  public void breakpointSet(Breakpoint bp) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).breakpointSet(bp); }
    }
    finally { _lock.endRead(); }
  }

  
  public void breakpointReached(Breakpoint bp) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).breakpointReached(bp);
      }
    }
    finally {
      _lock.endRead();
    }
  }

  
  public void breakpointChanged(Breakpoint bp) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).breakpointChanged(bp);
      }
    }
    finally {
      _lock.endRead();
    }
  }
  
  
  public void watchSet(DebugWatchData w) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).watchSet(w); }
    }
    finally { _lock.endRead(); }
  }
  
  
  public void watchRemoved(DebugWatchData w) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).watchRemoved(w); }
    }
    finally { _lock.endRead(); }
  }

  
  public void breakpointRemoved(Breakpoint bp) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).breakpointRemoved(bp);
    }
    finally { _lock.endRead(); }
  }

  
  public void stepRequested() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).stepRequested();
    }
    finally { _lock.endRead(); }
  }

  
  public void currThreadSuspended() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).currThreadSuspended();
    }
    finally { _lock.endRead(); }
  }

  
  public void currThreadResumed() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).currThreadResumed();
    }
    finally { _lock.endRead(); }
  }

  
  public void threadStarted() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).threadStarted();
    }
    finally { _lock.endRead(); }
  }

  
  public void currThreadDied() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).currThreadDied();
    }
    finally { _lock.endRead(); }
  }

  
  public void nonCurrThreadDied() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).nonCurrThreadDied();
    }
    finally { _lock.endRead(); }
  }

  
  public void currThreadSet(DebugThreadData thread) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).currThreadSet(thread);
      }
    }
    finally { _lock.endRead(); }
  }
}
