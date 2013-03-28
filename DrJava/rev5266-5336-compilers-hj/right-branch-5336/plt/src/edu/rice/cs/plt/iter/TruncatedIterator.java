

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class TruncatedIterator<T> implements Iterator<T>, Composite {
  
  private final Iterator<? extends T> _iter;
  private int _size;
  
  public TruncatedIterator(Iterator<? extends T> iter, int size) {
    _iter = iter;
    _size = size;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_iter) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_iter) + 1; }
    
  public boolean hasNext() { return _size > 0 && _iter.hasNext(); }
  
  public T next() {
    if (_size <= 0) { throw new NoSuchElementException(); }
    _size--;
    return _iter.next();
  }
  
  public void remove() { _iter.remove(); }
  
}
