

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class DiagonalCartesianIterable<T1, T2, R> extends AbstractIterable<R>
                                                  implements SizedIterable<R>, OptimizedLastIterable<R>,
                                                             Composite, Serializable {
  
  private final Iterable<? extends T1> _left;
  private final Iterable<? extends T2> _right;
  private final Lambda2<? super T1, ? super T2, ? extends R> _combiner;
  
  public DiagonalCartesianIterable(Iterable<? extends T1> left, Iterable<? extends T2> right,
                                   Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    _left = left;
    _right = right;
    _combiner = combiner;
  }
  
  public DiagonalCartesianIterator<T1, T2, R> iterator() {
    return new DiagonalCartesianIterator<T1, T2, R>(_left.iterator(), _right.iterator(), _combiner);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_left, _right) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_left, _right) + 1; }
  
  public boolean isEmpty() { return IterUtil.isEmpty(_left) || IterUtil.isEmpty(_right); }

  public int size() { return size(Integer.MAX_VALUE); }
  
  public int size(int bound) {
    int size1 = IterUtil.sizeOf(_left, bound);
    if (size1 == 0) { return 0; }
    else {
      int bound2 = bound / size1;
      if (bound2 < Integer.MAX_VALUE) { bound2++; } 
      int size2 = IterUtil.sizeOf(_right, bound2);
      
      
      int result = size1*size2;
      return (result > bound || result < 0) ? bound : result;
    }
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_left) || IterUtil.isInfinite(_right); }
  
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_left) && IterUtil.hasFixedSize(_right); }
  
  
  public boolean isStatic() { return false; }
  
  public R last() { return _combiner.value(IterUtil.last(_left), IterUtil.last(_right)); }
  
  
  public static <T1, T2, R>
    DiagonalCartesianIterable<T1, T2, R> make(Iterable<? extends T1> left, Iterable<? extends T2> right,
                                              Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new DiagonalCartesianIterable<T1, T2, R>(left, right, combiner);
  }
  
  
  public static <T1, T2, R>
    SnapshotIterable<R> makeSnapshot(Iterable<? extends T1> left, Iterable<? extends T2> right,
                                     Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new SnapshotIterable<R>(new DiagonalCartesianIterable<T1, T2, R>(left, right, combiner));
  }
  
}
