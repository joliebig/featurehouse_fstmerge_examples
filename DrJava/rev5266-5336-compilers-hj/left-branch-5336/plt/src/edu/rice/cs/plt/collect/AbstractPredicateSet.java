

package edu.rice.cs.plt.collect;

import java.util.AbstractSet;


public abstract class AbstractPredicateSet<T> extends AbstractSet<T> implements PredicateSet<T> {
  
  
  @Override public abstract boolean contains(Object o);
  
  
  @Override public boolean isEmpty() { return size(1) == 0; }
  
  
  public int size() { return size(Integer.MAX_VALUE); }
  
  
  public int size(int bound) {
    int result = 0;
    
    
    for (T elt : this) { result++; if (result == bound) break; }
    return result;
  }
  
}
