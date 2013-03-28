

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class FilteredIterable<T> extends AbstractIterable<T>
                                 implements Iterable<T>, Composite, Serializable {
  
  private Iterable<? extends T> _iterable;
  private Predicate<? super T> _predicate;
  
  public FilteredIterable(Iterable<? extends T> iterable, Predicate<? super T> predicate) {
    _iterable = iterable;
    _predicate = predicate;
  }
  
  public FilteredIterator<T> iterator() { 
    return new FilteredIterator<T>(_iterable.iterator(), _predicate);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _iterable) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _iterable) + 1; }
  
  
  public static <T> FilteredIterable<T> make(Iterable<? extends T> iterable, 
                                             Predicate<? super T> predicate) {
    return new FilteredIterable<T>(iterable, predicate);
  }
  
  
  public static <T> SnapshotIterable<T> makeSnapshot(Iterable<? extends T> iterable,
                                                     Predicate<? super T> predicate) {
    return new SnapshotIterable<T>(new FilteredIterable<T>(iterable, predicate));
  }
}
