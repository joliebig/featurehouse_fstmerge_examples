
package genj.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class DefaultValueMap<Key,Value> implements Map<Key, Value> {

  private Object defaultValue;
  private Map<Key,Value> delegate;
 
  public DefaultValueMap(Map<Key,Value> delegate, Value defaultValue) {
    this.delegate = delegate;
    this.defaultValue = defaultValue;
    getDefault();
  }
  
  @SuppressWarnings("unchecked")
  protected Value getDefault() {
    try {
      return (Value)defaultValue.getClass().getMethod("clone").invoke(defaultValue);
    } catch (Throwable t) {
      throw new IllegalArgumentException("default value must be cloneable", t);
    }
  }

  public void clear() {
    delegate.clear();
  }

  public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }

  public Set<java.util.Map.Entry<Key, Value>> entrySet() {
    return delegate.entrySet();
  }

  @SuppressWarnings("unchecked")
  public Value get(Object key) {
    Value val = delegate.get(key);
    if (val==null) {
      val = getDefault();
      delegate.put((Key)key, val);
    }
    return val;
  }

  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  public Set<Key> keySet() {
    return delegate.keySet();
  }

  public Value put(Key key, Value value) {
    return delegate.put(key, value);
  }

  public void putAll(Map<? extends Key, ? extends Value> m) {
    delegate.putAll(m);
  }

  public Value remove(Object key) {
    return delegate.remove(key);
  }

  public int size() {
    return delegate.size();
  }

  public Collection<Value> values() {
    return delegate.values();
  }

}
