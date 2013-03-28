

package edu.rice.cs.plt.collect;

import java.util.Map.Entry;
import java.io.Serializable;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class ImmutableMapEntry<K, V> implements Entry<K, V>, Composite, Serializable {
  
  protected Entry<? extends K, ? extends V> _delegate;
  
  public ImmutableMapEntry(Entry<? extends K, ? extends V> entry) { _delegate = entry; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_delegate) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_delegate) + 1; }
  
  public K getKey() { return _delegate.getKey(); }
  public V getValue() { return _delegate.getValue(); }
  public V setValue(V value) { throw new UnsupportedOperationException(); }
  
  public String toString() { return _delegate.toString(); }
  public boolean equals(Object o) { return _delegate.equals(o); }
  public int hashCode() { return _delegate.hashCode(); }
  
  
  public static <K, V> ImmutableMapEntry<K, V> make(Entry<? extends K, ? extends V> entry) {
    return new ImmutableMapEntry<K, V>(entry);
  }
  
  @SuppressWarnings("unchecked") public static <K, V> Lambda<Entry<? extends K, ? extends V>, Entry<K, V>> factory() {
    return (Factory<K, V>) Factory.INSTANCE;
  }
  
  private static final class Factory<K, V> implements Lambda<Entry<? extends K, ? extends V>, Entry<K, V>>, Serializable {
    public static final Factory<Object, Object> INSTANCE = new Factory<Object, Object>();
    private Factory() {}
    public Entry<K, V> value(Entry<? extends K, ? extends V> arg) { return new ImmutableMapEntry<K, V>(arg); }
  }
  
}
