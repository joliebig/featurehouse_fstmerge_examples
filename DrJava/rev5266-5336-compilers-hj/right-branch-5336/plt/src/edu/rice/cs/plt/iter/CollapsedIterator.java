

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class CollapsedIterator<T> implements Iterator<T>, Composite {
  
  private Iterator<? extends T> _i;
  private Iterator<? extends T> _last; 
  private final Iterator<? extends Iterator<? extends T>> _rest;
  
  
  public CollapsedIterator(Iterator<? extends Iterator<? extends T>> iters) {
    _i = EmptyIterator.make();
    _rest = iters;
    advance();
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_rest) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_rest) + 1; }
  
  public boolean hasNext() { return _i.hasNext(); }

  public T next() {
    T result = _i.next();
    _last = _i;
    advance();
    return result;
  }
  
  public void remove() { _last.remove(); }
  
  private void advance() {
    while (!_i.hasNext() && _rest.hasNext()) { _i = _rest.next(); }
  }
  
  
  public static <T> CollapsedIterator<T> make(Iterator<? extends Iterator<? extends T>> iters) {
    return new CollapsedIterator<T>(iters);
  }
    
}
