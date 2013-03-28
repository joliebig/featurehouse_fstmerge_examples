

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class SkipLastIterator<T> extends ReadOnlyIterator<T> implements Composite {
  
  private final Iterator<? extends T> _i;
  private T _lookahead;
  
  public SkipLastIterator(Iterator<? extends T> i) {
    _i = i;
    if (_i.hasNext()) { _lookahead = _i.next(); }
    else { _lookahead = null; }
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i) + 1; }
    
  public boolean hasNext() { return _i.hasNext(); }
  
  public T next() {
    T result = _lookahead;
    _lookahead = _i.next();
    return result;
  }
  
  
  
  public static <T> SkipLastIterator<T> make(Iterator<? extends T> i) {
    return new SkipLastIterator<T>(i);
  }
  
}
