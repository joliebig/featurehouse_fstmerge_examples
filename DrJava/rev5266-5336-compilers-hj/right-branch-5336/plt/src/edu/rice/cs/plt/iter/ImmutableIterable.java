

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ImmutableIterable<T> extends AbstractIterable<T>
                                  implements SizedIterable<T>, OptimizedLastIterable<T>, Composite, Serializable {
  
  private final Iterable<? extends T> _iterable;
  
  public ImmutableIterable(Iterable<? extends T> iterable) { _iterable = iterable; }
  public ImmutableIterator<T> iterator() { return new ImmutableIterator<T>(_iterable.iterator()); }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iterable) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iterable) + 1; }
  
  public boolean isEmpty() { return IterUtil.isEmpty(_iterable); }
  public int size() { return IterUtil.sizeOf(_iterable); }
  public int size(int bound) { return IterUtil.sizeOf(_iterable, bound); }
  public boolean isInfinite() { return IterUtil.isInfinite(_iterable); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_iterable); }
  public boolean isStatic() { return IterUtil.isStatic(_iterable); }
  
  public T last() { return IterUtil.last(_iterable); }
  
  
  public static <T> ImmutableIterable<T> make(Iterable<? extends T> iterable) {
    return new ImmutableIterable<T>(iterable);
  }
  
}
