

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.io.Serializable;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class UnionMap<K, V> extends AbstractKeyBasedMap<K, V> implements Composite, Serializable {
  private final Map<? extends K, ? extends V> _parent;
  private final Map<? extends K, ? extends V> _child;
  private final PredicateSet<K> _keys;
  
  public UnionMap(Map<? extends K, ? extends V> parent, Map<? extends K, ? extends V> child) {
    _parent = parent;
    _child = child;
    _keys = new UnionSet<K>(parent.keySet(), child.keySet());
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_parent, _child) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_parent, _child) + 1; }
  
  public V get(Object key) {
    if (_child.containsKey(key)) { return _child.get(key); }
    else { return _parent.get(key); }
  }
  
  public PredicateSet<K> keySet() { return _keys; }
  
}
