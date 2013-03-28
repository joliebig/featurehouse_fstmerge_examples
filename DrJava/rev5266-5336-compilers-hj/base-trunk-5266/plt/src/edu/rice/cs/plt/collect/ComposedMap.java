

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Predicate;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ComposedMap<K, X, V> extends AbstractKeyBasedMap<K, V> implements Composite, Serializable {
  private final Map<? extends K, ? extends X> _map1;
  private final Map<? super X, ? extends V> _map2;
  private final PredicateSet<K> _keys;
  
  public ComposedMap(Map<? extends K, ? extends X> map1, Map<? super X, ? extends V> map2) {
    _map1 = map1;
    _map2 = map2;
    _keys = new FilteredSet<K>(_map1.keySet(), new Predicate<K>() {
      public boolean contains(K key) { return _map2.containsKey(_map1.get(key)); }
    });
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_map1, _map2) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_map1, _map2) + 1; }
  
  public V get(Object key) {
    if (_map1.containsKey(key)) {
      X middle = _map1.get(key);
      return _map2.get(middle);
    }
    else { return null; }
  }
  
  public PredicateSet<K> keySet() { return _keys; }
  
}
