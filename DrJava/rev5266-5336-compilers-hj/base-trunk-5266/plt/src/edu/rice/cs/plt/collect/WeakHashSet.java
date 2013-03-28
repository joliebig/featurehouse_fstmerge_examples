

package edu.rice.cs.plt.collect;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.WeakHashMap;


public class WeakHashSet<T> extends AbstractSet<T> {
  
  private static final Object NOT_NULL = new Object();
  
  
  private WeakHashMap<T, Object> _items;
  
  
  public WeakHashSet() {
    _items = new WeakHashMap<T, Object>();
  }
  
  
  public int size() {
    return _items.size();
  }
  
  
  public boolean add(T item) {
    
    return _items.put(item, NOT_NULL) == null;
  }
  
  
  public void clear() {
    _items.clear();
  }
  
  
  public boolean contains(Object o) {
    return _items.containsKey(o);
  }
  
  
  public boolean remove(Object o) {
    
    return _items.remove(o) != null;
  }

  
  public Iterator<T> iterator() {
    return _items.keySet().iterator();
  }
}
