

package edu.rice.cs.plt.iter;

import java.io.Serializable;


public class EmptyIterable<T> extends AbstractIterable<T> implements SizedIterable<T>, Serializable {
  
  public static final EmptyIterable<Object> INSTANCE = new EmptyIterable<Object>();

  private EmptyIterable() {}
  
  @SuppressWarnings("unchecked")
  public EmptyIterator<T> iterator() { return (EmptyIterator<T>) EmptyIterator.INSTANCE; }
  
  public boolean isEmpty() { return true; }
  public int size() { return 0; }
  public int size(int bound) { return 0; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  
  
  @SuppressWarnings("unchecked")
  public static <T> EmptyIterable<T> make() { return (EmptyIterable<T>) INSTANCE; }
}
