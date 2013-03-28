

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import java.util.Iterator;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class CollapsedIterable<T> extends AbstractIterable<T> 
  implements SizedIterable<T>, OptimizedLastIterable<T>, Composite, Serializable {
  
  private final Iterable<? extends Iterable<? extends T>> _iters;
  
  public CollapsedIterable(Iterable<? extends Iterable<? extends T>> iters) { _iters = iters; }
    
  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iters) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iters) + 1; }
  
  public CollapsedIterator<T> iterator() {
    Iterator<? extends Iterator<? extends T>> i =
      new MappedIterable<Iterable<? extends T>, Iterator<? extends T>>(_iters, GetIterator.<T>make()).iterator();
    return new CollapsedIterator<T>(i);
  }
  
  public boolean isEmpty() { return size(1) == 0; }
  
  public int size() {
    int result = 0;
    for (Iterable<?> iter : _iters) {
      result += IterUtil.sizeOf(iter);
      if (result < 0) { result = Integer.MAX_VALUE; break; } 
    }
    return result;
  }
  
  public int size(int bound) {
    int result = 0;
    for (Iterable<?> iter : _iters) {
      result += IterUtil.sizeOf(iter);
      if (result >= bound) { break; }
      else if (result < 0) { result = Integer.MAX_VALUE; break; } 
    }
    return result <= bound ? result : bound;
  }
  
  public boolean isInfinite() {
    if (IterUtil.isInfinite(_iters)) { return true; }
    for (Iterable<?> iter : _iters) {
      if (IterUtil.isInfinite(iter)) { return true; }
    }
    return false;
  }
  
  public boolean hasFixedSize() {
    if (!IterUtil.hasFixedSize(_iters)) { return false; }
    for (Iterable<?> iter : _iters) {
      if (!IterUtil.hasFixedSize(iter)) { return false; }
    }
    return true;
  }
  
  public boolean isStatic() {
    if (!IterUtil.isStatic(_iters)) { return false; }
    for (Iterable<?> iter : _iters) {
      if (!IterUtil.isStatic(iter)) { return false; }
    }
    return true;
  }
  
  
  public T last() {
    Iterable<? extends T> lastNonEmpty = null;
    for (Iterable<? extends T> iter : _iters) {
      if (lastNonEmpty == null || !IterUtil.isEmpty(iter)) { lastNonEmpty = iter; }
    }
    return IterUtil.last(lastNonEmpty);
  }
  
  
  public static <T> CollapsedIterable<T> make(Iterable<? extends Iterable<? extends T>> iters) {
    return new CollapsedIterable<T>(iters);
  }
  
  private static final class GetIterator<T>
    implements Lambda<Iterable<? extends T>, Iterator<? extends T>>, Serializable {
    public static final GetIterator<Object> INSTANCE = new GetIterator<Object>();
    @SuppressWarnings("unchecked") public static <T> GetIterator<T> make() { return (GetIterator<T>) INSTANCE; }
    private GetIterator() {}
    public Iterator<? extends T> value(Iterable<? extends T> iter) { return iter.iterator(); }
  }
  
}
