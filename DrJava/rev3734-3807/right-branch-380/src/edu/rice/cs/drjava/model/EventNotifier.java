

package edu.rice.cs.drjava.model;

import java.util.LinkedList;
import edu.rice.cs.util.ReaderWriterLock;
import edu.rice.cs.util.swing.Utilities;


public abstract class EventNotifier<T> {

  
  protected final LinkedList<T> _listeners = new LinkedList<T>();

  
  protected final ReaderWriterLock _lock = new ReaderWriterLock();

  
  public void addListener(T listener) {

    _lock.startWrite();
    try { _listeners.add(listener); }
    finally {
      _lock.endWrite();

    }
  }

  
  public void removeListener(T listener) {

    _lock.startWrite();
    try { _listeners.remove(listener); }
    finally {
      _lock.endWrite();

    }
  }

  
  public void removeAllListeners() {

    _lock.startWrite();
    try { _listeners.clear(); }
    finally {
      _lock.endWrite();

    }
  }
}
