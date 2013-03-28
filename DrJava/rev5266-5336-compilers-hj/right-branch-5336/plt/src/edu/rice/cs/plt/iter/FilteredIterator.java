

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.OptionUnwrapException;


public class FilteredIterator<T> extends ReadOnlyIterator<T> implements Composite {
  
  private final Predicate<? super T> _p;
  private final Iterator<? extends T> _i;
  private Option<T> _lookahead;
  
  public FilteredIterator(Iterator<? extends T> i, Predicate<? super T> p) {
    _p = p;
    _i = i;
    advanceLookahead();
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_i) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_i) + 1; }
  
  public boolean hasNext() { return _lookahead.isSome(); }
  
  public T next() {
    try {
      T result = _lookahead.unwrap();
      advanceLookahead();
      return result;
    }
    catch (OptionUnwrapException e) { throw new NoSuchElementException(); }
  }
  
  
  private void advanceLookahead() {
    _lookahead = Option.none();
    while (_i.hasNext() && _lookahead.isNone()) {
      T next = _i.next();
      if (_p.contains(next)) { _lookahead = Option.some(next); }
    }
  }
  
  
  public static <T> FilteredIterator<T> make(Iterator<? extends T> i, Predicate<? super T> p) {
    return new FilteredIterator<T>(i, p);
  }
  
}
