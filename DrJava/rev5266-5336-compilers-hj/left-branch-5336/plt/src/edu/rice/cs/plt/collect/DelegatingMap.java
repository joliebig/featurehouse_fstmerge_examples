

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class DelegatingMap<K, V> implements LambdaMap<K, V>, Composite, Serializable {
  
  protected Map<K, V> _delegate;
  
  public DelegatingMap(Map<K, V> delegate) { _delegate = delegate; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_delegate) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_delegate) + 1; }
  
  public int size() { return _delegate.size(); }
  public boolean isEmpty() { return _delegate.isEmpty(); }
  
  public V get(Object key) { return _delegate.get(key); }
  public V value(K key) { return _delegate.get(key); }
  
  public boolean containsKey(Object o) { return _delegate.containsKey(o); }
  public PredicateSet<K> keySet() { return CollectUtil.asPredicateSet(_delegate.keySet()); }
  
  public boolean containsValue(Object o) { return _delegate.containsValue(o); }
  public Collection<V> values() { return _delegate.values(); }
  
  public Set<Map.Entry<K, V>> entrySet() { return _delegate.entrySet(); }

  public V put(K key, V value) { return _delegate.put(key, value); }
  public void putAll(Map<? extends K, ? extends V> t) { _delegate.putAll(t); }
  public V remove(Object key) { return _delegate.remove(key); }
  public void clear() { _delegate.clear(); }
  
  public String toString() { return _delegate.toString(); }
  public boolean equals(Object o) { return _delegate.equals(o); }
  public int hashCode() { return _delegate.hashCode(); }
}
