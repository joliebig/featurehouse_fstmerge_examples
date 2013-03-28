

package edu.rice.cs.plt.iter;

import java.util.NoSuchElementException;


public class SingletonIterator<T> extends ReadOnlyIterator<T> {
  
  private final T _element;
  private boolean _hasNext;
  
  public SingletonIterator(T element) { 
    _element = element;
    _hasNext = true;
  }
  
  public boolean hasNext() { return _hasNext; }
  
  public T next() {
    if (_hasNext) { _hasNext = false; return _element; }
    else { throw new NoSuchElementException(); }
  }
  
  
  public static <T> SingletonIterator<T> make(T element) { 
    return new SingletonIterator<T>(element);
  }
}
