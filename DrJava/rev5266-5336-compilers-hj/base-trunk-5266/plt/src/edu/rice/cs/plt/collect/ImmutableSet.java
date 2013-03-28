

package edu.rice.cs.plt.collect;

import java.util.Set;


public class ImmutableSet<T> extends ImmutableCollection<T> implements PredicateSet<T> {
  
  public ImmutableSet(Set<? extends T> set) { super(set); }
  
  public boolean equals(Object o) { return _delegate.equals(o); }
  public int hashCode() { return _delegate.hashCode(); }
  
  
  public static <T> ImmutableSet<T> make(Set<? extends T> set) {
    return new ImmutableSet<T>(set);
  }
  
}
