

package edu.rice.cs.plt.iter;

import java.io.Serializable;


public class SingletonIterable<T> extends AbstractIterable<T>
                                  implements SizedIterable<T>, OptimizedLastIterable<T>, Serializable {
  
  private final T _element;
  
  public SingletonIterable(T element) { _element = element; }
  public SingletonIterator<T> iterator() { return new SingletonIterator<T>(_element); }
  public boolean isEmpty() { return false; }
  public int size() { return 1; }
  public int size(int bound) { return 1 <= bound ? 1 : bound; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  public T last() { return _element; }
  
  
  public static <T> SingletonIterable<T> make(T element) { 
    return new SingletonIterable<T>(element);
  }
  
}
