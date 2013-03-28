

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class MappedIterable<S, T> extends AbstractIterable<T>
                                  implements SizedIterable<T>, OptimizedLastIterable<T>,
                                             Composite, Serializable {
  
  private final Iterable<? extends S> _source;
  private final Lambda<? super S, ? extends T> _map;
  
  public MappedIterable(Iterable<? extends S> source, Lambda<? super S, ? extends T> map) {
    _source = source;
    _map = map;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight((Object) _source) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize((Object) _source) + 1; }
  
  public MappedIterator<S, T> iterator() { 
    return new MappedIterator<S, T>(_source.iterator(), _map);
  }
  
  public boolean isEmpty() { return IterUtil.isEmpty(_source); }
  public int size() { return IterUtil.sizeOf(_source); }
  public int size(int bound) { return IterUtil.sizeOf(_source, bound); }
  public boolean isInfinite() { return IterUtil.isInfinite(_source); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_source); }
  
  public boolean isStatic() { return false; }
  
  public T last() { return _map.value(IterUtil.last(_source)); }
  
  
  public static <S, T> MappedIterable<S, T> make(Iterable<? extends S> source, 
                                                 Lambda<? super S, ? extends T> map) {
    return new MappedIterable<S, T>(source, map);
  }
  
  
  public static <S, T> SnapshotIterable<T> 
    makeSnapshot(Iterable<? extends S> source, Lambda<? super S, ? extends T> map) {
    return new SnapshotIterable<T>(new MappedIterable<S, T>(source, map));
  }
  
}
