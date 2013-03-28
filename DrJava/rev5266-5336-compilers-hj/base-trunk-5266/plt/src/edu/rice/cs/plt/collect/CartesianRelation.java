

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;
import edu.rice.cs.plt.iter.CartesianIterator;
import edu.rice.cs.plt.iter.EmptyIterator;


public class CartesianRelation<T1, T2> extends AbstractRelation<T1, T2> implements Composite, Serializable {
  
  private final PredicateSet<T1> _firstSet;
  private final PredicateSet<T2> _secondSet;
  
  public CartesianRelation(Set<? extends T1> firsts, Set<? extends T2> seconds) {
    _firstSet = new ImmutableSet<T1>(firsts);
    _secondSet = new ImmutableSet<T2>(seconds);
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_firstSet, _secondSet) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_firstSet, _secondSet) + 1; }
  
  @Override public int size(int bound) {
    
    int size1 = _firstSet.size(bound);
    if (size1 == 0) { return 0; }
    else {
      int bound2 = bound / size1;
      if (bound2 < Integer.MAX_VALUE) { bound2++; } 
      int size2 = _secondSet.size(bound2);
      
      
      int result = size1*size2;
      return (result > bound || result < 0) ? bound : result;
    }
  }
  
  @Override public boolean isEmpty() { return _firstSet.isEmpty() && _secondSet.isEmpty(); }
  public boolean isInfinite() { return _firstSet.isInfinite() || _secondSet.isInfinite(); }
  public boolean hasFixedSize() { return _firstSet.hasFixedSize() && _secondSet.hasFixedSize(); }
  public boolean isStatic() { return _firstSet.isStatic() && _secondSet.isStatic(); }
  
  public boolean contains(T1 first, T2 second) {
    return _firstSet.contains(first) && _secondSet.contains(second);
  }
  
  public boolean contains(Object o) {
    if (o instanceof Pair<?, ?>) {
      Pair<?, ?> p = (Pair<?, ?>) o;
      return _firstSet.contains(p.first()) && _secondSet.contains(p.second());
    }
    else { return false; }
  }
  
  public Iterator<Pair<T1, T2>> iterator() {
    return CartesianIterator.make(_firstSet.iterator(), _secondSet, Pair.<T1, T2>factory());
  }
  
  public PredicateSet<T1> firstSet() { return _firstSet; }

  public PredicateSet<T2> matchFirst(T1 first) {
    if (_firstSet.isStatic()) {
      return _firstSet.contains(first) ? _secondSet : CollectUtil.<T2>emptySet();
    }
    else { return new MatchSet<T1, T2>(first, _firstSet, _secondSet); }
  }

  public PredicateSet<T2> secondSet() { return _secondSet; }

  public PredicateSet<T1> matchSecond(T2 second) {
    if (_secondSet.isStatic()) {
      return _secondSet.contains(second) ? _firstSet : CollectUtil.<T1>emptySet();
    }
    else { return new MatchSet<T2, T1>(second, _secondSet, _firstSet); }
  }

  
  private static class MatchSet<K, V> extends AbstractPredicateSet<V> {
    private final K _key;
    private final PredicateSet<K> _keys;
    private final PredicateSet<V> _vals;
    
    public MatchSet(K key, PredicateSet<K> keys, PredicateSet<V> vals) {
      _key = key;
      _keys = keys;
      _vals = vals;
    }
    
    public boolean contains(Object obj) {
      return _keys.contains(_key) ? _vals.contains(obj) : false;
    }
    public Iterator<V> iterator() {
      return _keys.contains(_key) ? _vals.iterator() : EmptyIterator.<V>make();
    }
    
    @Override public boolean isEmpty() { return !_keys.contains(_key) || _vals.isEmpty(); }
    @Override public int size() { return _keys.contains(_key) ? _vals.size() : 0; }
    @Override public int size(int bound) { return _keys.contains(_key) ? _vals.size(bound) : 0; }
    public boolean isInfinite() { return _keys.contains(_key) && _vals.isInfinite(); }
    public boolean hasFixedSize() { return _keys.isStatic() && _vals.hasFixedSize(); }
    public boolean isStatic() { return _keys.isStatic() && _vals.isStatic(); }
  }
  
}
