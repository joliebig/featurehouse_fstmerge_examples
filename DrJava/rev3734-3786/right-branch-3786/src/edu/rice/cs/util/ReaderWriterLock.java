

package edu.rice.cs.util;

import java.util.LinkedList;


public class ReaderWriterLock {
  
  private int _numActiveReaders = 0;
  
  private int _numActiveWriters = 0;
  
  private int _numWaitingReaders = 0;
  
  private int _numWaitingWriters = 0;
  
  
  private final LinkedList<ReaderWriterThread> _waitQueue;
  
  
  private final LinkedList<Thread> _runningThreads;
  
  
  public ReaderWriterLock() {
    _waitQueue = new LinkedList<ReaderWriterThread>();
    _runningThreads = new LinkedList<Thread>();
  }
  
  
  public synchronized void startRead() {
    
    if (!_alreadyReading()) {
      
      
      _ensureNotAlreadyRunning();
      
      
      if (_numWaitingWriters > 0 || _numActiveWriters > 0) {
        
        _numWaitingReaders++;
        Reader r = new Reader();
        r.startWaiting();
        
        
        _numWaitingReaders--;
      }
    }
    
    
    _numActiveReaders++;
    _runningThreads.add(Thread.currentThread());
  }
  
  
  public synchronized void endRead() {
    if (_numActiveReaders == 0) {
      throw new IllegalStateException("Trying to end a read with no active readers!");
    }
    
    _numActiveReaders--;
    _ensureAlreadyRunning();
    _runningThreads.remove(Thread.currentThread());
    
    
    if (_numActiveWriters > 0) {
      String msg = "A writer was active during a read!";
      throw new UnexpectedException(new Exception(msg));
    }
    
    
    if (_numActiveReaders == 0) {
      
      
      _wakeFrontGroupOfWaitQueue();
    }
  }
  
  
  
  public synchronized void startWrite() {
    
    _ensureNotAlreadyRunning();
    
    
    
    
    
    
    
    if ((_numActiveReaders > 0 || _numActiveWriters > 0) ||
        (_numWaitingReaders > 0 || _numWaitingWriters > 0)) {
      
      _numWaitingWriters++;
      
      
      
      
      
      
      Writer w = new Writer();
      w.startWaiting();

      _numWaitingWriters--;
    }
    
    
    _numActiveWriters++;
    _runningThreads.add(Thread.currentThread());
  }
  
  
  public synchronized void endWrite() {
    if (_numActiveWriters != 1) {
      throw new IllegalStateException("Trying to end a write with " +
                                      _numActiveWriters + " active writers!");
    }
    
    _numActiveWriters--;
    _ensureAlreadyRunning();
    _runningThreads.remove(Thread.currentThread());
    
    
    if ((_numActiveWriters > 0) || (_numActiveReaders > 0)) {
      String msg = "Multiple readers/writers were active during a write!";
      throw new UnexpectedException(new Exception(msg));
    }
    
    
    _wakeFrontGroupOfWaitQueue();
  }
  

  
  private boolean _alreadyReading() {
    
    
    return _numActiveReaders > 0 &&
           _runningThreads.contains(Thread.currentThread());
      
  }

  
  private void _ensureNotAlreadyRunning() {
    if (_runningThreads.contains(Thread.currentThread())) {
      throw new IllegalStateException("Same thread cannot read or write multiple " +
                                      "times!  (Would cause deadlock.)");
    }
  }
  
  
  private void _ensureAlreadyRunning() {
    if (!_runningThreads.contains(Thread.currentThread())) {
      throw new IllegalStateException("Current thread did not initiate a read or write!");
    }
  }
  
  
  private synchronized void _wakeFrontGroupOfWaitQueue() {
    if (!_waitQueue.isEmpty()) {
      
      ReaderWriterThread front = _waitQueue.getFirst();
      front.stopWaiting();  
      
      
      if (front.isReader()) {
        while (!_waitQueue.isEmpty()) {
          front = _waitQueue.getFirst();
          if (front.isReader()) {
            front.stopWaiting();  
          }
          else {
            
            break;
          }
        }
      }
    }
  }
  
  
  
  public abstract class ReaderWriterThread {
    private boolean _isWaiting = true;
    
    public abstract boolean isWriter();
    
    public abstract boolean isReader();
    
    
    public void startWaiting() {
      synchronized (ReaderWriterLock.this) {
        _isWaiting = true;
        _waitQueue.addLast(this);
        while (_isWaiting) {
          try {
            ReaderWriterLock.this.wait();
          }
          catch (InterruptedException e) {
            
          }
        }
      }
    }
    
    
    public void stopWaiting() {
      synchronized (ReaderWriterLock.this) {
        _isWaiting = false;
        _waitQueue.remove(this);  
        ReaderWriterLock.this.notifyAll();
      }
    }
  }
  
  
  public class Reader extends ReaderWriterThread {
    public boolean isReader() { return true; }
    public boolean isWriter() { return false; }
  }
  
  
  public class Writer extends ReaderWriterThread {
    public boolean isReader() { return false; }
    public boolean isWriter() { return true; }
  }
}