

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import edu.rice.cs.plt.lambda.Runnable1;


public class MutableSingletonIterator<T> implements Iterator<T> {
  
  private final T _element;
  private final Runnable1<? super T> _removeListener;
  private boolean _hasNext;
  private boolean _removed;
  
  public MutableSingletonIterator(T element, Runnable1<? super T> removeListener) { 
    _element = element;
    _removeListener = removeListener;
    _hasNext = true;
    _removed = false;
  }
  
  public boolean hasNext() { return _hasNext; }
  
  public T next() {
    if (_hasNext) { _hasNext = false; return _element; }
    else { throw new NoSuchElementException(); }
  }
  
  public void remove() {
    if (_hasNext || _removed) { throw new IllegalStateException(); }
    else { _removeListener.run(_element); }
  }
  
  
  public static <T> MutableSingletonIterator<T> make(T element, Runnable1<? super T> removeListener) { 
    return new MutableSingletonIterator<T>(element, removeListener);
  }
}
