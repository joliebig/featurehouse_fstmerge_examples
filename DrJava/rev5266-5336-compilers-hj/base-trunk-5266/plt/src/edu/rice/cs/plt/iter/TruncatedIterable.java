

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class TruncatedIterable<T> extends AbstractIterable<T>
                                  implements SizedIterable<T>, Composite, Serializable {
  
  private final Iterable<? extends T> _iterable;
  protected final int _size;
  
  public TruncatedIterable(Iterable<? extends T> iterable, int size) {
    if (size < 0) { throw new IllegalArgumentException("size < 0"); }
    _iterable = iterable;
    _size = size;
  }

  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iterable) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iterable) + 1; }
    
  public TruncatedIterator<T> iterator() {
    return new TruncatedIterator<T>(_iterable.iterator(), _size);
  }
  
  public boolean isEmpty() { return (_size == 0) || IterUtil.isEmpty(_iterable); }

  
  public int size() { return IterUtil.sizeOf(_iterable, _size); }
  
  public int size(int bound) { return IterUtil.sizeOf(_iterable, _size <= bound ? _size : bound); }
  
  public boolean isInfinite() { return false; }
    
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_iterable); }
  
  public boolean isStatic() { return IterUtil.isStatic(_iterable); }
  
  
  public static <T> TruncatedIterable<T> make(Iterable<? extends T> iterable, int size) {
    return new TruncatedIterable<T>(iterable, size);
  }
  
  
  public static <T> SnapshotIterable<T> makeSnapshot(Iterable<? extends T> iterable, int size) { 
    return new SnapshotIterable<T>(new TruncatedIterable<T>(iterable, size));
  }
}
