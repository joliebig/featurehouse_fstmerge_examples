

package edu.rice.cs.plt.collect;

import java.util.Collection;
import edu.rice.cs.plt.tuple.Pair;


public class ImmutableRelation<T1, T2> extends DelegatingRelation<T1, T2> {
  
  private ImmutableRelation<T2, T1> _inverse; 
  
  public ImmutableRelation(Relation<T1, T2> relation) { super(relation); _inverse = null; }
  
  private ImmutableRelation(Relation<T1, T2> relation, ImmutableRelation<T2, T1> inverse) {
    super(relation);
    _inverse = inverse;
  }
  
  public boolean add(Pair<T1, T2> o) { throw new UnsupportedOperationException(); }
  public boolean remove(Object o) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends Pair<T1, T2>> c) { throw new UnsupportedOperationException(); }
  public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  public boolean contains(T1 first, T2 second) {
    return _delegate.contains(first, second);
  }
  public boolean add(T1 first, T2 second) { throw new UnsupportedOperationException(); }
  public boolean remove(T1 first, T2 second) { throw new UnsupportedOperationException(); }
  
  public Relation<T2, T1> inverse() {
    if (_inverse == null) {
      _inverse = new ImmutableRelation<T2, T1>(_delegate.inverse(), this);
    }
    return _inverse;
  }
  
  public PredicateSet<T1> firstSet() {
    return new ImmutableSet<T1>(_delegate.firstSet());
  }
  public boolean containsFirst(T1 first) {
    return _delegate.containsFirst(first);
  }
  public PredicateSet<T2> matchFirst(T1 first) {
    return new ImmutableSet<T2>(_delegate.matchFirst(first));
  }
  public PredicateSet<T2> excludeFirsts() {
    return new ImmutableSet<T2>(_delegate.excludeFirsts());
  }

  public PredicateSet<T2> secondSet() {
    return new ImmutableSet<T2>(_delegate.secondSet());
  }
  public boolean containsSecond(T2 second) {
    return _delegate.containsSecond(second);
  }
  public PredicateSet<T1> matchSecond(T2 second) {
    return new ImmutableSet<T1>(_delegate.matchSecond(second));
  }
  public PredicateSet<T1> excludeSeconds() {
    return new ImmutableSet<T1>(_delegate.excludeSeconds());
  }
  
  
  public static <T1, T2> ImmutableRelation<T1, T2> make(Relation<T1, T2> relation) {
    return new ImmutableRelation<T1, T2>(relation);
  }
  
}
