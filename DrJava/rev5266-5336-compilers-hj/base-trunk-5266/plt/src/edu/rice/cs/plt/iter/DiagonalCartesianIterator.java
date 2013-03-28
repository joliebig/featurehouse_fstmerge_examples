

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.LinkedList;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class DiagonalCartesianIterator<T1, T2, R> extends ReadOnlyIterator<R> implements Composite {
  
  private final Lambda2<? super T1, ? super T2, ? extends R> _combiner;
  private final Iterator<? extends T1> _left;
  private final Iterator<? extends T2> _right;
  private LinkedList<T1> _leftCache;
  private Iterator<T1> _leftCacheIter;
  private LinkedList<T2> _rightCache;
  private Iterator<T2> _rightCacheIter;
  
  public DiagonalCartesianIterator(Iterator<? extends T1> left, Iterator<? extends T2> right,
                                   Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    _combiner = combiner;
    _left = left;
    _right = right;
    _leftCache = new LinkedList<T1>();
    _rightCache = new LinkedList<T2>();
    _leftCacheIter = _leftCache.iterator();
    _rightCacheIter = _rightCache.iterator();
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_left, _right) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_left, _right) + 1; }
  
  public boolean hasNext() {
    if (_left.hasNext()) { return _right.hasNext() || !_rightCache.isEmpty(); }
    else if (_right.hasNext()) { return !_leftCache.isEmpty(); }
    else {
      return _leftCacheIter.hasNext() && _rightCacheIter.hasNext() || 
        _leftCache.size() > 1 && _rightCache.size() > 1; 
    }





  }
  
  public R next() {
    if (!_leftCacheIter.hasNext() || !_rightCacheIter.hasNext()) {
      if (_left.hasNext()) { _leftCache.addLast(_left.next()); }
      else if (!_rightCache.isEmpty()) { _rightCache.removeLast(); }
      if (_right.hasNext()) { _rightCache.addFirst(_right.next()); }
      else if (!_leftCache.isEmpty()) { _leftCache.removeFirst(); }
      _leftCacheIter = _leftCache.iterator();
      _rightCacheIter = _rightCache.iterator();
    }
    
    return _combiner.value(_leftCacheIter.next(), _rightCacheIter.next());
  }
  
  
  public static <T1, T2, R>
    DiagonalCartesianIterator<T1, T2, R> make(Iterator<? extends T1> left, Iterator<? extends T2> right,
                                              Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new DiagonalCartesianIterator<T1, T2, R>(left, right, combiner);
  }
  
}
