

package edu.rice.cs.plt.collect;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.ImmutableIterator;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class IterableCollection<E> extends AbstractCollection<E>
                                   implements SizedIterable<E>, Composite, Serializable {
  
  private final Iterable<? extends E> _iter;
  private final boolean _fixedSize;
  private int _size; 
  
  public IterableCollection(Iterable<? extends E> iter) {
    _iter = iter;
    _fixedSize = IterUtil.hasFixedSize(iter);
    _size = -1;
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_iter) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_iter) + 1; }
  
  @Override public boolean isEmpty() {
    if (_size == -1) { return IterUtil.isEmpty(_iter); }
    else { return _size == 0; }
  }
  
  public int size() {
    if (_fixedSize) {
      if (_size == -1) { _size = IterUtil.sizeOf(_iter); }
      return _size;
    }
    else { return IterUtil.sizeOf(_iter); }
  }
  
  public int size(int bound) {
    if (_fixedSize) {
      if (_size == -1) {
        int result = IterUtil.sizeOf(_iter, bound);
        if (result < bound) { _size = result; }
        return result;
      }
      else { return (_size < bound) ? _size : bound; }
    }
    else { return IterUtil.sizeOf(_iter, bound); }
  }
  
  public boolean isInfinite() { return IterUtil.isInfinite(_iter); }
  public boolean hasFixedSize() { return _fixedSize; }
  public boolean isStatic() { return _fixedSize && IterUtil.isStatic(_iter); }
  
  @Override public boolean contains(Object o) {
    return IterUtil.contains(_iter, o);
  }
  
  public Iterator<E> iterator() { return new ImmutableIterator<E>(_iter.iterator()); }
  
  @Override public boolean add(E o) { throw new UnsupportedOperationException(); }
  @Override public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
  @Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
  @Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  @Override public void clear() { throw new UnsupportedOperationException(); }
  
  public String toString() { return _iter.toString(); }
  public boolean equals(Object o) {
    if (this == o) { return true; }
    else if (!(o instanceof IterableCollection<?>)) { return false; }
    else { return _iter.equals(((IterableCollection<?>) o)._iter); }
  }
  public int hashCode() { return IterableCollection.class.hashCode() ^ _iter.hashCode(); }
}
