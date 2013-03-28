

package edu.rice.cs.plt.collect;

import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.MappedIterator;
import edu.rice.cs.plt.lambda.Lambda;


public abstract class AbstractKeyBasedMap<K, V> implements LambdaMap<K, V> {
  
  public abstract V get(Object key);
  public abstract PredicateSet<K> keySet();
  
  
  public V value(K key) { return get(key); }
  
  public int size() { return keySet().size(); }
  
  public boolean isEmpty() { return keySet().isEmpty(); }
  
  public boolean containsKey(Object key) { return keySet().contains(key); }
  
  
  public boolean containsValue(Object val) {
    return IterUtil.contains(IterUtil.map(keySet(), this), val);
  }
  
  
  public Collection<V> values() {
    return new IterableCollection<V>(IterUtil.map(keySet(), this));
  }
  
  
  public Set<Entry<K, V>> entrySet() {
    return new EntrySet();
  }
  
  
  public V put(K key, V val) { throw new UnsupportedOperationException(); }
  
  public V remove(Object key) { throw new UnsupportedOperationException(); }
  
  public void clear() { throw new UnsupportedOperationException(); }
  
  
  public void putAll(Map<? extends K, ? extends V> elts) {
    for (Entry<? extends K, ? extends V> entry : elts.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }
  
  public String toString() { return IterUtil.toString(entrySet(), "{", ", ", "}"); }
  
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (!(o instanceof Map<?, ?>)) { return false; }
    else { return entrySet().equals(((Map<?, ?>) o).entrySet()); }
  }
  
  public int hashCode() { return entrySet().hashCode(); }
    
  
  
  protected class EntrySet extends AbstractPredicateSet<Entry<K, V>> {
    
    
    @Override public boolean contains(Object o) {
      if (o instanceof Entry<?, ?>) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        Object key = entry.getKey();
        if (containsKey(key)) {
          Object val = entry.getValue();
          Object mapVal = get(key);
          return (val == null) ? (mapVal == null) : val.equals(mapVal);
        }
      }
      return false;
    }
    
    
    public Iterator<Entry<K, V>> iterator() {
      return MappedIterator.make(keySet().iterator(), new Lambda<K, Entry<K, V>>() {
        public Entry<K, V> value(K key) {
          return mapEntryForKey(AbstractKeyBasedMap.this, key);
        }
      });
    }
    
    
    public boolean isInfinite() { return keySet().isInfinite(); }
    
    public boolean hasFixedSize() { return keySet().hasFixedSize(); }
    
    public boolean isStatic() { return false; }

    
    @Override public boolean isEmpty() { return AbstractKeyBasedMap.this.isEmpty(); }
    
    @Override public int size() { return AbstractKeyBasedMap.this.size(); }
    
    @Override public int size(int bound) { return keySet().size(bound); }
    
    
    @Override public boolean add(Entry<K, V> entry) {
      boolean present = contains(entry);
      AbstractKeyBasedMap.this.put(entry.getKey(), entry.getValue());
      return !present;
    }
    
    
    @Override public boolean remove(Object o) {
      if (o instanceof Entry<?, ?>) {
        Entry<?, ?> entry = (Entry<?, ?>) o;
        boolean present = containsKey(entry.getKey());
        AbstractKeyBasedMap.this.remove(entry.getKey());
        return present;
      }
      else { return false; }
    }
    
    
    @Override public void clear() {
      AbstractKeyBasedMap.this.clear();
    }
    
  }
  
  
  protected static <K, V> Entry<K, V> mapEntryForKey(final Map<K, V> map, final K key) {
    return new Entry<K, V>() {
      public K getKey() { return key; }
      public V getValue() { return map.get(key); }
      public V setValue(V value) { return map.put(key, value); }
      public boolean equals(Object o) {
        if (this == o) { return true; }
        else if (!(o instanceof Entry<?, ?>)) { return false; }
        else {
          Entry<?, ?> cast = (Entry<?, ?>) o;
          if (key == null ? cast.getKey() == null : key.equals(cast.getKey())) {
            V val = map.get(key);
            return val == null ? cast.getValue() == null : val.equals(cast.getValue());
          }
          else { return false; }
        }
      }
      public int hashCode() {
        V val = map.get(key);
        return (key == null ? 0 : key.hashCode()) ^ (val == null ? 0 : val.hashCode());
      }
    };
  }
  
}
