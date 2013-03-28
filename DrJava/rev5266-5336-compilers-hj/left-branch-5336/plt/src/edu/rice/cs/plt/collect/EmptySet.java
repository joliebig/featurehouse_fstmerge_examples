

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.io.Serializable;


public class EmptySet<E> extends EmptyCollection<E> implements PredicateSet<E>, Serializable {
  
  public static final EmptySet<Object> INSTANCE = new EmptySet<Object>();

  private EmptySet() {}
  
  public boolean equals(Object o) {
    return (this == o) || (o instanceof Set<?> && ((Set<?>) o).isEmpty());
  }
  
  public int hashCode() { return 0; }
  
  
  @SuppressWarnings("unchecked") public static <T> EmptySet<T> make() {
    return (EmptySet<T>) INSTANCE;
  }
  
}
