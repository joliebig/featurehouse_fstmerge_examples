

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.io.Serializable;


public class EmptyMap<K, V> implements LambdaMap<K, V>, Serializable {
  
  public static final EmptyMap<Object, Object> INSTANCE = new EmptyMap<Object, Object>();

  private EmptyMap() {}
  
  public V value(K key) { return null; }
  public V get(Object key) { return null; }
  
  public int size() { return 0; }
  public boolean isEmpty() { return true; }
  public boolean containsKey(Object o) { return false; }
  public boolean containsValue(Object o) { return false; }
  public EmptySet<K> keySet() { return EmptySet.make(); }
  public EmptySet<V> values() { return EmptySet.make(); }
  public EmptySet<Map.Entry<K, V>> entrySet() { return EmptySet.make(); }
  
  public V put(K key, V value) { throw new UnsupportedOperationException(); }
  public void putAll(Map<? extends K, ? extends V> t) { throw new UnsupportedOperationException(); }
  public V remove(Object key) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
  public String toString() { return "{}"; }
  
  public boolean equals(Object o) {
    return (this == o) || (o instanceof Map<?, ?> && ((Map<?, ?>) o).isEmpty());
  }
  
  public int hashCode() { return 0; }
  
  
  @SuppressWarnings("unchecked") public static <K, V> EmptyMap<K, V> make() {
    return (EmptyMap<K, V>) INSTANCE;
  }
  
}
