

package edu.rice.cs.plt.collect;

import java.util.Set;


public class DelegatingSet<T> extends DelegatingCollection<T> implements PredicateSet<T> {
  
  public DelegatingSet(Set<T> delegate) { super(delegate); }
  
  public boolean equals(Object o) { return _delegate.equals(o); }
  public int hashCode() { return _delegate.hashCode(); }
}
