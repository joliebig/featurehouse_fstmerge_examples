

package edu.rice.cs.plt.iter;

import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda;


public class SequenceIterable<T> implements SizedIterable<T>, Serializable {
  
  private final T _initial;
  private final Lambda<? super T, ? extends T> _successor;
  
  
  public SequenceIterable(T initial, Lambda<? super T, ? extends T> successor) {
    _initial = initial;
    _successor = successor;
  }
  
  
  public SequenceIterator<T> iterator() { return new SequenceIterator<T>(_initial, _successor); }
  
  public boolean isEmpty() { return false; }
  public int size() { return Integer.MAX_VALUE; }
  public int size(int bound) { return bound; }
  public boolean isInfinite() { return true; }
  public boolean hasFixedSize() { return true; }
  
  public boolean isStatic() { return false; }

  
  public String toString() { return IterUtil.toString(this); }
  
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (o == null || !getClass().equals(o.getClass())) { return false; }
    else {
      SequenceIterable<?> cast = (SequenceIterable<?>) o;
      return _initial.equals(cast._initial) && _successor.equals(cast._successor);
    }
  }
  
  public int hashCode() {
    return getClass().hashCode() ^ (_initial.hashCode() << 1) ^ (_successor.hashCode() << 2);
  }
  
  
  public static <T> SequenceIterable<T> make(T initial, Lambda<? super T, ? extends T> successor) {
    return new SequenceIterable<T>(initial, successor);
  }

}
