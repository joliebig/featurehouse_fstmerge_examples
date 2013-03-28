

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ComposedIterator<T> implements Iterator<T>, Composite {
  
  private Iterator<? extends T> _i;
  private Iterator<? extends T> _rest;
  
  
  public ComposedIterator(Iterator<? extends T> i1, Iterator<? extends T> i2) {
    _i = i1;
    _rest = i2;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i, _rest) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i, _rest) + 1; }
  
  public boolean hasNext() { return _i.hasNext() || (_rest != null && _rest.hasNext()); }
  
  public T next() {
    if (_rest != null && !_i.hasNext()) { _i = _rest; _rest = null; }
    return _i.next();
  }
  
  public void remove() { _i.remove(); }
  
  
  public static <T> ComposedIterator<T> make(Iterator<? extends T> i1, Iterator<? extends T> i2) {
    return new ComposedIterator<T>(i1, i2);
  }
    
}
