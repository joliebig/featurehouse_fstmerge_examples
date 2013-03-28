

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ImmutableIterator<T> extends ReadOnlyIterator<T> implements Composite {
  
  private final Iterator<? extends T> _i;
  
  public ImmutableIterator(Iterator<? extends T> i) { _i = i; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i) + 1; }
    
  public boolean hasNext() { return _i.hasNext(); }
  public T next() { return _i.next(); }
  
  
  public static <T> ImmutableIterator<T> make(Iterator<? extends T> i) {
    return new ImmutableIterator<T>(i);
  }
}
