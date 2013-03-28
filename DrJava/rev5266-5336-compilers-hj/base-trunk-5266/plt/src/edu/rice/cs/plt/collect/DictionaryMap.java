

package edu.rice.cs.plt.collect;

import java.util.Dictionary;
import java.util.Iterator;
import java.io.Serializable;
import edu.rice.cs.plt.iter.IterUtil;


public class DictionaryMap<K, V> extends AbstractKeyBasedMap<K, V> implements Serializable {
  
  private final Dictionary<K, V> _d;
  
  public DictionaryMap(Dictionary<K, V> d) {
    _d = d;
  }
  
  public V get(Object key) { return _d.get(key); }
  
  public PredicateSet<K> keySet() {
    return new AbstractPredicateSet<K>() {
      public boolean contains(Object o) { return _d.get(o) != null; }
      public Iterator<K> iterator() { return IterUtil.asIterator(_d.keys()); }
      public boolean isInfinite() { return false; }
      public boolean hasFixedSize() { return false; }
      public boolean isStatic() { return false; }
      @Override public int size() { return _d.size(); }
      @Override public int size(int b) { int s = _d.size(); return (s < b) ? s : b; }
      @Override public boolean isEmpty() { return _d.isEmpty(); }
      @Override public boolean remove(Object o) { return _d.remove(o) != null; }
    };
  }
  
  @Override public V value(K key) { return _d.get(key); }
  @Override public int size() { return _d.size(); }
  @Override public boolean isEmpty() { return _d.isEmpty(); }
  @Override public boolean containsKey(Object key) { return _d.get(key) != null; }

  @Override public V put(K key, V value) { return _d.put(key, value); }
  @Override public V remove(Object key) { return _d.remove(key); }
  @Override public void clear() { keySet().clear(); }
}
