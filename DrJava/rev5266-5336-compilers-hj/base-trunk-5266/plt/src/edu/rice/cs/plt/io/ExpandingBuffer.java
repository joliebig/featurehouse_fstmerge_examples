

package edu.rice.cs.plt.io;

import java.io.Serializable;
import java.util.LinkedList;


public abstract class ExpandingBuffer<T> implements Serializable {
  
  protected static final int BUFFER_SIZE = 1024;
  
  private final LinkedList<T> _buffers;
  
  

  
  private long _base;
  
  
  private long _nextBuffer;

  
  private long _first;

  
  private long _last;
  
  public ExpandingBuffer() {
    _buffers = new LinkedList<T>();
    _base = 0l;
    _nextBuffer = 0l;
    _first = 0l;
    _last = 0l;
  }
  
  
  protected abstract T allocateBuffer(int size);
  
  
  public synchronized long size() {
    return _last - _first;
  }
  
  public synchronized boolean isEmpty() {
    return _first == _last;
  }
  
  
  
  protected int allocate() {
    if (_last == _nextBuffer) {
      _buffers.addLast(allocateBuffer(BUFFER_SIZE));
      _nextBuffer += BUFFER_SIZE;
      return BUFFER_SIZE;
    }
    else { return (int) (_nextBuffer - _last); }
  }
  
  
  protected int elementsInFirstBuffer() {
    long secondBuffer = _base + BUFFER_SIZE;
    return (int) (((secondBuffer > _last) ? _last : secondBuffer) - _base);
  }
  
  
  protected boolean deallocate() {
    long secondBuffer = _base + BUFFER_SIZE;
    if (_first >= secondBuffer) {
      _buffers.removeFirst();
      _base = secondBuffer;
      return true;
    }
    else { return false; }
  }
  
  
  protected T firstBuffer() { return _buffers.getFirst(); }
  
  
  protected int firstIndex() { return (int) (_first - _base); }
  
  
  protected T lastBuffer() { return _buffers.getLast(); }
  
  
  protected int lastIndex() { return (int) (_last - (_nextBuffer - BUFFER_SIZE)); }
  
  
  protected void recordWrite(long written) { _last += written; }
  
  
  protected void recordRead(long read) { _first += read; }
}
