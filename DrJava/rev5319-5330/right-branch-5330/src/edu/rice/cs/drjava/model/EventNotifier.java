

package edu.rice.cs.drjava.model;

import java.util.LinkedList;
import edu.rice.cs.util.ReaderWriterLock;


public abstract class EventNotifier<T> {
  
  protected final LinkedList<T> _listeners = new LinkedList<T>();
  
  
  protected final ReaderWriterLock _lock = new ReaderWriterLock();
  
  
  public void addListener(T listener) {
    _lock.startWrite();
    try { _listeners.add(listener); }
    finally { _lock.endWrite(); }
  }
  
  
  public void removeListener(final T listener) {
    try {
      _lock.startWrite();
      try { _listeners.remove(listener); }
      finally { _lock.endWrite(); }
    }
    catch(ReaderWriterLock.DeadlockException e) {
      
      
      new Thread(new Runnable() {
        public void run() {
          _lock.startWrite();
          try { _listeners.remove(listener); }
          finally { _lock.endWrite(); }
        }
      }, "Pending Listener Removal").start();



    }
  }
  
  
  public void removeAllListeners() {
    try { 
      _lock.startWrite();
      try { _listeners.clear(); }
      finally { _lock.endWrite(); }
    }
    catch(ReaderWriterLock.DeadlockException e) {
      
      
      new Thread(new Runnable() {
        public void run() {
          _lock.startWrite();
          try { _listeners.clear(); }
          finally { _lock.endWrite(); }
        }
      }, "Pending Listener Removal").start();
    }
  }
}
