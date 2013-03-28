

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.util.AbstractCollection;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.tuple.Option;


public class HashMultiset<T> extends AbstractCollection<T> implements Multiset<T>, Serializable {
  
  private HashMap<T, Integer> _counts;
  private int _size;
  
  
  public HashMultiset() { _counts = new HashMap<T, Integer>(); }
  
  
  public HashMultiset(Collection<? extends T> coll) {
    this();
    addAll(coll);
  }
  
  public int size() { return _size; }
  public int size(int bound) { return _size < bound ? _size : bound; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return false; }
  public boolean isStatic() { return false; }
  @Override public boolean isEmpty() { return _size == 0; }
  
  @Override public boolean contains(Object obj) { return _counts.containsKey(obj); }
  
  @Override public boolean containsAll(Collection<?> c) {
    for (Object obj : asMultiset(c).asSet()) { if (!contains(obj)) { return false; } }
    return true;
  }
  
  public boolean isSupersetOf(Multiset<?> m) {
    for (Object elt : m.asSet()) {
      if (m.count(elt) > count(elt)) { return false; }
    }
    return true;
  }
  
  public int count(Object value) { 
    if (_counts.containsKey(value)) { return _counts.get(value); }
    else { return 0; }
  }
  
  
  public PredicateSet<T> asSet() { return CollectUtil.asPredicateSet(_counts.keySet()); }
  
  public Iterator<T> iterator() {
    final Iterator<Map.Entry<T, Integer>> entries = _counts.entrySet().iterator();
    return new Iterator<T>() {
      private Map.Entry<T, Integer> _current = null;
      private int _currentCount = 0;
      private boolean _removed = false;

      public boolean hasNext() { return _currentCount > 0 || entries.hasNext(); }
      
      public T next() {
        if (_currentCount == 0) {
          _current = entries.next();
          _currentCount = _current.getValue();
        }
        _removed = false;
        _currentCount--;
        return _current.getKey();
      }
      
      public void remove() {
        if (_current == null || _removed) { throw new IllegalStateException(); }
        else {
          _removed = true;
          _size--;
          int oldCount = _current.getValue();
          if (oldCount == 1) { entries.remove(); }
          else { _current.setValue(oldCount - 1); }
        }
      }
      
    };
  }
  
  @Override public boolean add(T val) {
    _counts.put(val, count(val) + 1);
    _size++;
    return true;
  }
  
  public boolean add(T val, int instances) { 
    _counts.put(val, count(val) + instances);
    _size += instances;
    return true;
  }
  
  @Override public boolean addAll(Collection<? extends T> coll) {
    boolean result = false;
    Multiset<? extends T> collMultiset = asMultiset(coll);
    for (T entry : collMultiset.asSet()) {
      result |= add(entry, collMultiset.count(entry));
    }
    return result;
  }
  
  @Override public boolean remove(Object obj) {
    Option<T> cast = CollectUtil.castIfContains(this, obj);
    if (cast.isSome()) { doRemove(cast.unwrap(), 1); return true; }
    else { return false; }
  }
  
  public boolean remove(Object obj, int instances) {
    Option<T> cast = CollectUtil.castIfContains(this, obj);
    if (cast.isSome()) { doRemove(cast.unwrap(), instances); return true; }
    else { return false; }
  }
  
  public boolean removeAllInstances(Object obj) {
    Option<T> cast = CollectUtil.castIfContains(this, obj);
    if (cast.isSome()) { doRemove(cast.unwrap(), count(obj)); return true; }
    else { return false; }
  }
  
  private void doRemove(T key, int instances) {
    int newCount = count(key) - instances;
    if (newCount <= 0) {
      int actualInstances = _counts.remove(key);
      _size -= actualInstances;
    }
    else {
      _counts.put(key, newCount);
      _size -= instances;
    }
  }
  
  @Override public boolean removeAll(Collection<?> coll) {
    boolean result = false;
    Multiset<?> collMultiset = asMultiset(coll);
    for (Object obj : collMultiset.asSet()) { result |= remove(obj, collMultiset.count(obj)); }
    return result;
  }
  
  @Override public boolean retainAll(Collection<?> coll) {
    boolean result = false;
    Multiset<?> collMultiset = asMultiset(coll);
    Iterator<Map.Entry<T, Integer>> iter = _counts.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<T, Integer> entry = iter.next();
      if (collMultiset.contains(entry.getKey())) {
        int newCount = collMultiset.count(entry.getKey());
        if (entry.getValue() > newCount) {
          _size -= (entry.getValue() - newCount);
          entry.setValue(newCount);
          result = true;
        }
      }
      else { _size -= entry.getValue(); iter.remove(); result = true; }
    }
    return result;
  }
  
  @Override public void clear() { _counts.clear(); }
  
  public String toString() { return IterUtil.toString(this); }
  
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    else if (!(obj instanceof Multiset<?>)) { return false; }
    else {
      Multiset<?> cast = (Multiset<?>) obj;
      if (_size == cast.size()) {
        for (Object elt : cast.asSet()) {
          if (count(elt) != cast.count(elt)) { return false; }
        }
        return true;
      }
      else { return false; }
    }
  }
  
  public int hashCode() {
    int result = 0;
    for (T elt : asSet()) { result += (elt == null ? 1 : elt.hashCode()) * count(elt); }
    return result;
  }
  
  
  private <S> Multiset<S> asMultiset(Collection<S> coll) {
    if (coll instanceof Multiset<?>) { return (Multiset<S>) coll; }
    else { return new HashMultiset<S>(coll); }
  }
  
}
