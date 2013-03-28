

package edu.rice.cs.plt.iter;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Iterator;
import java.util.HashSet;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.OptionUnwrapException;


public class NoDuplicatesIterator<T> extends ReadOnlyIterator<T> implements Composite {
  
  private final Iterator<? extends T> _i;
  private final Set<T> _seen;
  private Option<T> _lookahead;
  
  public NoDuplicatesIterator(Iterator<? extends T> i) {
    _i = i;
    _seen = new HashSet<T>();
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
      if (!_seen.contains(next)) {
        _lookahead = Option.some(next);
        _seen.add(next);
      }
    }
  }
  
  
  
  public static <T> NoDuplicatesIterator<T> make(Iterator<? extends T> i) {
    return new NoDuplicatesIterator<T>(i);
  }
  
}
