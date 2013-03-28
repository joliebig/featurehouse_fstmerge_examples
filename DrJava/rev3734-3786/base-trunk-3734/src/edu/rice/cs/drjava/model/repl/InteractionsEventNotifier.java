

package edu.rice.cs.drjava.model.repl;

import java.io.File;

import edu.rice.cs.drjava.model.EventNotifier;



public class InteractionsEventNotifier extends EventNotifier<InteractionsListener> implements InteractionsListener {

  
  public void interactionStarted() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++)  _listeners.get(i).interactionStarted();
    }
    finally { _lock.endRead(); }
  }

  
  public void interactionEnded() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).interactionEnded();
    }
    finally { _lock.endRead(); }
  }

  
  public void interactionErrorOccurred(int offset, int length) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).interactionErrorOccurred(offset, length);
    }
    finally { _lock.endRead(); }
  }

  
  public void interpreterResetting() {
    _lock.startRead();
    try {
      int size = _listeners.size();

      for (int i = 0; i < size; i++) _listeners.get(i).interpreterResetting();
    }
    finally { _lock.endRead(); }
  }

  
  public void interpreterReady(File wd) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) _listeners.get(i).interpreterReady(wd);
    }
    finally { _lock.endRead(); }
  }

  
  public void interpreterResetFailed(final Throwable t) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++)  _listeners.get(i).interpreterResetFailed(t);
    }
    finally { _lock.endRead(); }
  }

  
  public void interpreterExited(int status) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++) { _listeners.get(i).interpreterExited(status); }
    }
    finally { _lock.endRead(); }
  }

  
  public void interpreterChanged(boolean inProgress) {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++)  _listeners.get(i).interpreterChanged(inProgress);
    }
    finally { _lock.endRead(); }
  }

  
  public void interactionIncomplete() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++)  _listeners.get(i).interactionIncomplete();
    }
    finally { _lock.endRead(); }
  }
  
  
  public void slaveJVMUsed() {
    _lock.startRead();
    try {
      int size = _listeners.size();
      for (int i = 0; i < size; i++)  _listeners.get(i).slaveJVMUsed();
    }
    finally { _lock.endRead(); }
  }
}
