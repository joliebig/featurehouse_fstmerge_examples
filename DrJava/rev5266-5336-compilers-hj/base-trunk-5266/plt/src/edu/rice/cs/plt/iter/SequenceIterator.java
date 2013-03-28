

package edu.rice.cs.plt.iter;

import edu.rice.cs.plt.lambda.Lambda;


public class SequenceIterator<T> extends ReadOnlyIterator<T> {
  
  private T _next;
  private final Lambda<? super T, ? extends T> _successor;
  
  public SequenceIterator(T initial, Lambda<? super T, ? extends T> successor) {
    _next = initial;
    _successor = successor;
  }
  
  public boolean hasNext() { return true; }
  
  public T next() { 
    T result = _next; 
    _next = _successor.value(_next);
    return result;
  }
  
}
