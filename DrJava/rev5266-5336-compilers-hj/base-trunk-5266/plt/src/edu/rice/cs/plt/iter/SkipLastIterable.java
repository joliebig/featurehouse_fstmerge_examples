

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class SkipLastIterable<T> extends AbstractIterable<T>
                                 implements SizedIterable<T>, Composite, Serializable {
  
  private final Iterable<? extends T> _iterable;
  
  public SkipLastIterable(Iterable<? extends T> iterable) { _iterable = iterable; }

  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iterable) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iterable) + 1; }
    
  public SkipLastIterator<T> iterator() { return new SkipLastIterator<T>(_iterable.iterator()); }
  
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
  
  
  public static <T> SkipLastIterable<T> make(Iterable<? extends T> iterable) {
    return new SkipLastIterable<T>(iterable);
  }
  
  
  public static <T> SnapshotIterable<T> makeSnapshot(Iterable<? extends T> iterable) { 
    return new SnapshotIterable<T>(new SkipLastIterable<T>(iterable));
  }
}
