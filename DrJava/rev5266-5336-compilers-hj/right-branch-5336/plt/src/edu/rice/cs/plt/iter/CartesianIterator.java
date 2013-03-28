

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.rice.cs.plt.lambda.Lambda2;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class CartesianIterator<T1, T2, R> extends ReadOnlyIterator<R> implements Composite {
  
  private final Lambda2<? super T1, ? super T2, ? extends R> _combiner;
  private final Iterator<? extends T1> _left;
  private Iterator<? extends T2> _right;
  private T1 _currentLeft;
  private boolean _done;
  private final Iterable<? extends T2> _rightIterable;
  
  public CartesianIterator(Iterator<? extends T1> left, Iterable<? extends T2> right,
                           Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    _combiner = combiner;
    _left = left;
    _right = right.iterator();
    if (_left.hasNext() && _right.hasNext()) { _done = false; _currentLeft = _left.next(); }
    else { _done = true; }
    _rightIterable = right;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_left, _right) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_left, _right) + 1; }
  
  public boolean hasNext() { return !_done; }
  
  public R next() {
    if (_done) { throw new NoSuchElementException(); }
    else {
      R result = _combiner.value(_currentLeft, _right.next());
      if (!_right.hasNext()) {
        if (!_left.hasNext()) { _done = true; }
        else {
          _currentLeft = _left.next();
          _right = _rightIterable.iterator();
        }
      }
      return result;
    }
  }
  
  
  public static <T1, T2, R>
    CartesianIterator<T1, T2, R> make(Iterator<? extends T1> left, Iterable<? extends T2> right,
                                      Lambda2<? super T1, ? super T2, ? extends R> combiner) {
    return new CartesianIterator<T1, T2, R>(left, right, combiner);
  }
  
}
