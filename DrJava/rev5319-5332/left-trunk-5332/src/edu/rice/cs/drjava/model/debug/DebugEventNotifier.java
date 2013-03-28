

package edu.rice.cs.drjava.model.debug;

import edu.rice.cs.drjava.model.EventNotifier;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;

import java.awt.EventQueue;


public class DebugEventNotifier extends EventNotifier<DebugListener> implements DebugListener {
  
  
  public void debuggerStarted() {
    assert EventQueue.isDispatchThread();
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
    assert EventQueue.isDispatchThread();
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
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).threadLocationUpdated(doc, lineNumber, shouldHighlight);
      }
    }
    finally { _lock.endRead(); }
  }

  
  public void regionAdded(Breakpoint bp) {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).regionAdded(bp); }
    }
    finally { _lock.endRead(); }
  }

  
  public void breakpointReached(Breakpoint bp) {
    assert EventQueue.isDispatchThread();
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

  
  public void regionChanged(Breakpoint bp) {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) {
        _listeners.get(i).regionChanged(bp);
      }
    }
    finally {
      _lock.endRead();
    }
  }
  
  
  public void watchSet(DebugWatchData w) {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).watchSet(w); }
    }
    finally { _lock.endRead(); }
  }
  
  
  public void watchRemoved(DebugWatchData w) {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).watchRemoved(w); }
    }
    finally { _lock.endRead(); }
  }

  
  public void regionRemoved(Breakpoint bp) {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).regionRemoved(bp);
    }
    finally { _lock.endRead(); }
  }

  
  public void stepRequested() {
    assert EventQueue.isDispatchThread();
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
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).currThreadResumed();
    }
    finally { _lock.endRead(); }
  }

  
  public void threadStarted() {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).threadStarted();
    }
    finally { _lock.endRead(); }
  }

  
  public void currThreadDied() {
    assert EventQueue.isDispatchThread();
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).currThreadDied();
    }
    finally { _lock.endRead(); }
  }

  
  public void nonCurrThreadDied() {
    assert EventQueue.isDispatchThread();
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
