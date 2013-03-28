

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.util.Iterator;
import edu.rice.cs.plt.iter.EmptyIterator;
import edu.rice.cs.plt.iter.SizedIterable;


public abstract class EmptyCollection<T> implements Collection<T>, SizedIterable<T> {

  @Override public abstract boolean equals(Object o);
  @Override public abstract int hashCode();

  public int size() { return 0; }
  public int size(int bound) { return 0; }
  public boolean isEmpty() { return true; }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  public boolean contains(Object o) { return false; }
  public boolean containsAll(Collection<?> c) { return c.isEmpty(); }
  
  public Iterator<T> iterator() { return EmptyIterator.make(); }
  
  public Object[] toArray() { return new Object[0]; }
  
  public <S> S[] toArray(S[] a) {
    if (a.length > 0) { a[0] = null; }
    return a;
  }
  
  
  public String toString() { return "[]"; }

  public boolean add(T o) { throw new UnsupportedOperationException(); }
  public boolean addAll(Collection<? extends T> c) { throw new UnsupportedOperationException(); }
  public boolean remove(Object o) { throw new UnsupportedOperationException(); }
  public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  public void clear() { throw new UnsupportedOperationException(); }
  
}
