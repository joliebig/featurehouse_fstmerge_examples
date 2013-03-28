

package edu.rice.cs.plt.collect;

import java.util.*;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.ImmutableIterator;
import edu.rice.cs.plt.iter.IterUtil;


public class ExternallySortedMultiMap<K, V, C extends Comparable<? super C>> {
  
  
  private final Map<K, ExternallySortedSet<V, C>> _map;

  
  private int _size;
  
  
  public ExternallySortedMultiMap() {
    _map = new HashMap<K, ExternallySortedSet<V, C>>();
    _size = 0;
  }
  
  
  public int size() { return _size; }
  
  
  public int size(int bound) { return _size <= bound ? _size : bound; }
  
  
  public boolean isEmpty() { return _size == 0; }
  
  
  public boolean containsKey(K key) { return _map.containsKey(key); }
  
  
  public boolean containsValue(V value) {
    for (ExternallySortedSet<V, C> set : _map.values()) {
      if (set.contains(value)) { return true; }
    }
    return false;
  }
  
  
  public boolean contains(K key, V value) {
    ExternallySortedSet<V, C> set = _map.get(key);
    return (set != null) && (set.contains(value));
  }
  
  
  public Iterable<V> get(final K key) {
    return new Iterable<V>() {
      public Iterator<V> iterator() {
        ExternallySortedSet<V, C> set = _map.get(key);
        if (set == null) { return EmptyIterator.make(); }
        else { return ImmutableIterator.make(set.iterator()); }
      }
    };
  }
  
  
  public boolean put(K key, V value, C orderBy) {
    ExternallySortedSet<V, C> set = _map.get(key);
    if (set == null) { set = new ExternallySortedSet<V, C>(); _map.put(key, set); }
    if (set.add(value, orderBy)) { _size++; return true; }
    else { return false; }
  }
  
  
  public boolean remove(K key, V value) {
    ExternallySortedSet<V, C> set = _map.get(key);
    if (set == null) { return false; }
    else {
      if (set.remove(value)) {
        _size--;
        if (set.isEmpty()) { _map.remove(key); }
      }
      else {
        return false;
      }
    }
    return true;
  }
  
  
  public boolean removeKey(K key) {
    ExternallySortedSet<V, C> set = _map.get(key);
    if (set == null) { return false; }
    else { _map.remove(key); _size -= set.size(); return true; }
  }
  
  
  public boolean putAll(ExternallySortedMultiMap<? extends K, ? extends V, ? extends C> map) {
    boolean result = false;
    for (Map.Entry<? extends K, ? extends ExternallySortedSet<? extends V, ? extends C>> e : 
           map._map.entrySet()) {
      ExternallySortedSet<V, C> set = _map.get(e.getKey());
      if (set == null) { set = new ExternallySortedSet<V, C>(); _map.put(e.getKey(), set); }
      _size -= set.size();
      
      
      
      
      @SuppressWarnings("unchecked") ExternallySortedSet s = e.getValue();
      @SuppressWarnings("unchecked") boolean newResult = set.addAll(s);
      result = result | newResult;
      
      _size += set.size();
    }
    return result;
  }
  
  
  public void clear() { _map.clear(); _size = 0; }
  
  
  public Iterable<K> keys() {
    return IterUtil.immutable(_map.keySet());
  }
  
  
  public Iterable<V> values() { return IterUtil.immutable(IterUtil.collapse(_map.values())); }
  
}
