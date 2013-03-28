

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class SkipFirstIterable<T> extends AbstractIterable<T>
                                  implements SizedIterable<T>, OptimizedLastIterable<T>,
                                             Composite, Serializable {
  
  private final Iterable<T> _iterable;
  
  public SkipFirstIterable(Iterable<T> iterable) { _iterable = iterable; }

  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iterable) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iterable) + 1; }
    
  public Iterator<T> iterator() {
    Iterator<T> result = _iterable.iterator();
    if (result.hasNext()) { result.next(); }
    return result;
  }
  
  public boolean isEmpty() { return IterUtil.sizeOf(_iterable, 2) < 2; }
  
  public int size() {
    
    
    
    
    
    int nestedSize = IterUtil.sizeOf(_iterable);
    if (nestedSize == 0) { return 0; }
    else if (nestedSize == Integer.MAX_VALUE) { return Integer.MAX_VALUE; }
    else { return nestedSize - 1; }
  }
  
  public int size(int bound) {
    if (bound == Integer.MAX_VALUE) { return size(); }
    else {
      int nestedSize = IterUtil.sizeOf(_iterable, bound + 1);
      return (nestedSize == 0) ? 0 : nestedSize - 1;
    }
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_iterable); }
  
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_iterable); }
  
  public boolean isStatic() { return IterUtil.isStatic(_iterable); }
  
  public T last() {
    
    IterUtil.first(this);
    return IterUtil.last(_iterable);
  }
  
  
  public static <T> SkipFirstIterable<T> make(Iterable<T> iterable) {
    return new SkipFirstIterable<T>(iterable);
  }
  
  
  public static <T> SnapshotIterable<T> makeSnapshot(Iterable<T> iterable) { 
    return new SnapshotIterable<T>(new SkipFirstIterable<T>(iterable));
  }
}
