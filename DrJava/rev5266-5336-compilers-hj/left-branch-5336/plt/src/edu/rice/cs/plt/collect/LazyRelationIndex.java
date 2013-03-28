

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.iter.IterUtil;


public class LazyRelationIndex<K, V> implements RelationIndex<K, V>, Serializable {
  
  
  private final Iterable<? extends Pair<K, V>> _pairs;
  private final PredicateSet<Pair<K, V>> _pairSet;
  
  public LazyRelationIndex(Iterable<? extends Pair<K, V>> pairs) {
    _pairs = pairs;
    _pairSet = new IterableSet<Pair<K, V>>(_pairs);
  }
  
  public LazyRelationIndex(Set<Pair<K, V>> pairs) {
    _pairs = pairs;
    _pairSet = CollectUtil.asPredicateSet(pairs);
  }
  
  public boolean isEmpty() { return _pairSet.isEmpty(); }
  public int size() { return _pairSet.size(); }
  public int size(int bound) { return _pairSet.size(bound); }
  public boolean isInfinite() { return _pairSet.isInfinite(); }
  public boolean hasFixedSize() { return _pairSet.hasFixedSize(); }
  public boolean isStatic() { return _pairSet.isStatic(); }
  
  
  public boolean contains(Object key, Object value) {
    return IterUtil.contains(_pairs, Pair.make(key, value));
  }
  
  public PredicateSet<K> keys() {
    return new IterableSet<K>(IterUtil.pairFirsts(_pairs));
  }
  
  public PredicateSet<V> match(K key) {
    return new IterableSet<V>(IterUtil.pairSeconds(IterUtil.filter(_pairs, new FirstMatcher<K>(key))));
  }
  
  public Iterator<Pair<K, V>> iterator() { return _pairSet.iterator(); }
  
  public void added(K key, V value) {}
  public void removed(K key, V value) {}
  public void cleared() {}

  
  private static class FirstMatcher<T> implements Predicate<Pair<? extends T, ?>>, Serializable {
    private T _val;
    public FirstMatcher(T val) { _val = val; }
    public boolean contains(Pair<? extends T, ?> pair) { return _val.equals(pair.first()); }
  }
  
}
