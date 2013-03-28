

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.io.Serializable;


public class SingletonMap<K, V> extends AbstractKeyBasedMap<K, V> implements Serializable {
  
  private final K _key;
  private final V _value;
  
  public SingletonMap(K key, V value) { _key = key; _value = value; }

  public V get(Object obj) {
    if ((_key == null) ? (obj == null) : _key.equals(obj)) { return _value; }
    else { return null; }
  }
  
  public PredicateSet<K> keySet() { return new SingletonSet<K>(_key); }
  
  @Override public int size() { return 1; }
  @Override public boolean isEmpty() { return false; }
  
  @Override public boolean containsKey(Object obj) {
    return (_key == null) ? (obj == null) : _key.equals(obj);
  }
  
  @Override public boolean containsValue(Object obj) {
    return (_value == null) ? (obj == null) : _value.equals(obj);
  }
  
  @Override public Collection<V> values() { return new SingletonSet<V>(_value); }
  
  
  public static <K, V> SingletonMap<K, V> make(K key, V value) {
    return new SingletonMap<K, V>(key, value);
  }
  
}
