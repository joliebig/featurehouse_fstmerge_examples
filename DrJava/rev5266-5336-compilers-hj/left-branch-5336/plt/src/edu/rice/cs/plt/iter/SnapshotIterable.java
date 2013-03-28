

package edu.rice.cs.plt.iter;

import java.util.Iterator;
import java.util.ArrayList;
import java.io.Serializable;


public class SnapshotIterable<T> extends AbstractIterable<T>
                                 implements SizedIterable<T>, OptimizedLastIterable<T>, Serializable {
  
  private final ArrayList<T> _values;
  
  public SnapshotIterable(Iterable<? extends T> iterable) {
    _values = new ArrayList<T>(0); 
    for (T e : iterable) { _values.add(e); }
  }
  
  public SnapshotIterable(Iterator<? extends T> iterator) {
    _values = new ArrayList<T>(0); 
    while (iterator.hasNext()) { _values.add(iterator.next()); }
  }
    
  public Iterator<T> iterator() { return new ImmutableIterator<T>(_values.iterator()); }
  public boolean isEmpty() { return _values.isEmpty(); }
  public int size() { return _values.size(); }
  public int size(int bound) { int result = _values.size(); return result < bound ? result : bound; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  public T last() { return _values.get(_values.size()-1); }
  
  
  public static <T> SnapshotIterable<T> make(Iterable<? extends T> iterable) {
    return new SnapshotIterable<T>(iterable);
  }
  
  
  public static <T> SnapshotIterable<T> make(Iterator<? extends T> iterator) {
    return new SnapshotIterable<T>(iterator);
  }
}
