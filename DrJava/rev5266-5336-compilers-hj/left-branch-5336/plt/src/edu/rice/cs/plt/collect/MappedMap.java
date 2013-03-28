

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.Collection;
import java.io.Serializable;
import edu.rice.cs.plt.iter.MappedIterable;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class MappedMap<K, X, V> extends AbstractKeyBasedMap<K, V> implements Composite, Serializable {
  private final Map<K, ? extends X> _map;
  private final Lambda<? super X, ? extends V> _lambda;
  
  public MappedMap(Map<K, ? extends X> map, Lambda<? super X, ? extends V> lambda) {
    _map = map;
    _lambda = lambda;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_map, _lambda) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_map, _lambda) + 1; }
  
  public V get(Object key) {
    if (_map.containsKey(key)) { return _lambda.value(_map.get(key)); }
    else { return null; }
  }
  
  public PredicateSet<K> keySet() {
    return CollectUtil.asPredicateSet(_map.keySet());
  }
  
  @Override public V remove(Object key) {
    if (_map.containsKey(key)) {
      X resultX = _map.remove(key);
      return _lambda.value(resultX);
    }
    else { return null; }
  }
  
  @Override public void clear() { _map.clear(); }
  
  @Override public int size() { return _map.size(); }
  @Override public boolean isEmpty() { return _map.isEmpty(); }
  
  @Override public boolean containsKey(Object o) { return _map.containsKey(o); }

  @Override public Collection<V> values() {
    return new IterableCollection<V>(new MappedIterable<X, V>(_map.values(), _lambda));
  }
  
}
