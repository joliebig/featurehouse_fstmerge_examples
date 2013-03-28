

package edu.rice.cs.plt.collect;

import edu.rice.cs.plt.tuple.Pair;


public class DelegatingRelation<T1, T2> extends DelegatingSet<Pair<T1, T2>> implements Relation<T1, T2> {
  protected final Relation<T1, T2> _delegate; 
  public DelegatingRelation(Relation<T1, T2> delegate) { super(delegate); _delegate = delegate; }
  
  public int size(int bound) { return _delegate.size(bound); }
  public boolean isInfinite() { return _delegate.isInfinite(); }
  public boolean hasFixedSize() { return _delegate.hasFixedSize(); }
  public boolean isStatic() { return _delegate.isStatic(); }

  public boolean contains(T1 first, T2 second) { return _delegate.contains(first, second); }
  public boolean add(T1 first, T2 second) { return _delegate.add(first, second); }
  public boolean remove(T1 first, T2 second) { return _delegate.remove(first, second); }
  public Relation<T2, T1> inverse() { return _delegate.inverse(); }
  public PredicateSet<T1> firstSet() { return _delegate.firstSet(); }
  public boolean containsFirst(T1 first) { return _delegate.containsFirst(first); }
  public PredicateSet<T2> matchFirst(T1 first) { return _delegate.matchFirst(first); }
  public PredicateSet<T2> excludeFirsts() { return _delegate.excludeFirsts(); }
  public PredicateSet<T2> secondSet() { return _delegate.secondSet(); }
  public boolean containsSecond(T2 second) { return _delegate.containsSecond(second); }
  public PredicateSet<T1> matchSecond(T2 second) { return _delegate.matchSecond(second); }
  public PredicateSet<T1> excludeSeconds() { return _delegate.excludeSeconds(); }
  
}
