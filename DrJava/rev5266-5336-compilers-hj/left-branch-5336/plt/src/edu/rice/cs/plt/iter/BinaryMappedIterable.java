

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;



public class BinaryMappedIterable<T1, T2, R> extends AbstractIterable<R> 
                                             implements SizedIterable<R>, OptimizedLastIterable<R>,
                                                        Composite, Serializable {
  
  private final Iterable<? extends T1> _source1;
  private final Iterable<? extends T2> _source2;
  private final Lambda2<? super T1, ? super T2, ? extends R> _map;
  
  public BinaryMappedIterable(Iterable<? extends T1> source1, Iterable<? extends T2> source2,
                              Lambda2<? super T1, ? super T2, ? extends R> map) {
    _source1 = source1;
    _source2 = source2;
    _map = map;
  }
  
  public BinaryMappedIterator<T1, T2, R> iterator() { 
    return new BinaryMappedIterator<T1, T2, R>(_source1.iterator(), _source2.iterator(), _map);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_source1, _source2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_source1, _source2) + 1; }
  
  public boolean isEmpty() { return IterUtil.isEmpty(_source1); }
  public int size() { return IterUtil.sizeOf(_source1); }
  public int size(int bound) { return IterUtil.sizeOf(_source1, bound); }
  public boolean isInfinite() { return IterUtil.isInfinite(_source1); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_source1); }
  
  public boolean isStatic() { return false; }
  
  public R last() { return _map.value(IterUtil.last(_source1), IterUtil.last(_source2)); }
  
  
  public static <T1, T2, R> BinaryMappedIterable<T1, T2, R> 
    make(Iterable<? extends T1> source1, Iterable<? extends T2> source2, 
         Lambda2<? super T1, ? super T2, ? extends R> map) {
    return new BinaryMappedIterable<T1, T2, R>(source1, source2, map);
  }
  
  
  public static <T1, T2, R> SnapshotIterable<R> 
    makeSnapshot(Iterable<? extends T1> source1, Iterable<? extends T2> source2, 
                 Lambda2<? super T1, ? super T2, ? extends R> map) {
    return new SnapshotIterable<R>(new BinaryMappedIterable<T1, T2, R>(source1, source2, map));
  }
  
}
