

package edu.rice.cs.plt.iter;

import edu.rice.cs.plt.lambda.Lambda;
import java.util.NoSuchElementException;


public class FiniteSequenceIterator<T> extends SequenceIterator<T> {
  
  private int _size;
  
  public FiniteSequenceIterator(T initial, Lambda<? super T, ? extends T> successor, int size) {
    super(initial, successor);
    _size = size;
  }
  
  public boolean hasNext() { return _size > 0; }
  
  public T next() {
    if (_size <= 0) { throw new NoSuchElementException(); }
    _size--;
    return super.next();
  }
  
}
