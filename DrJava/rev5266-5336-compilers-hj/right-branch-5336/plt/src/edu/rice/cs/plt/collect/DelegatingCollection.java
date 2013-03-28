

package edu.rice.cs.plt.collect;

import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractCollection;
import java.io.Serializable;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class DelegatingCollection<T> extends AbstractCollection<T> implements SizedIterable<T>, Composite, Serializable {
  
  protected Collection<T> _delegate;
  
  public DelegatingCollection(Collection<T> delegate) { _delegate = delegate; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_delegate) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_delegate) + 1; }
  
  public boolean isEmpty() { return _delegate.isEmpty(); }
  public int size() { return _delegate.size(); }
  public int size(int bound) { return IterUtil.sizeOf(_delegate, bound); }
  public boolean isInfinite() { return IterUtil.isInfinite(_delegate); }
  public boolean hasFixedSize() { return IterUtil.hasFixedSize(_delegate); }
  public boolean isStatic() { return IterUtil.isStatic(_delegate); }
  
  public boolean contains(Object o) { return _delegate.contains(o); }
  public boolean containsAll(Collection<?> c) { return _delegate.containsAll(c); }
  public Iterator<T> iterator() { return _delegate.iterator(); }
  public Object[] toArray() { return _delegate.toArray(); }
  public <S> S[] toArray(S[] a) { return _delegate.toArray(a); }
  
  public boolean add(T o) { return _delegate.add(o); }
  public boolean addAll(Collection<? extends T> c) { return _delegate.addAll(c); }
  public boolean remove(Object o) { return _delegate.remove(o); }
  public boolean retainAll(Collection<?> c) { return _delegate.retainAll(c); }
  public boolean removeAll(Collection<?> c) { return _delegate.removeAll(c); }
  public void clear() { _delegate.clear(); }
  
  public String toString() { return _delegate.toString(); }
  
  protected boolean abstractCollectionIsEmpty() { return super.isEmpty(); }
  protected boolean abstractCollectionContains(Object o) { return super.contains(o); }
  protected Object[] abstractCollectionToArray() { return super.toArray(); }
  protected <S> S[] abstractCollectionToArray(S[] a) { return super.toArray(a); }
  protected boolean abstractCollectionRemove(T o) { return super.remove(o); }
  protected boolean abstractCollectionContainsAll(Collection<?> c) { return super.containsAll(c); }
  protected boolean abstractCollectionAddAll(Collection<? extends T> c) { return super.addAll(c); }
  protected boolean abstractCollectionRetainAll(Collection<?> c) { return super.retainAll(c); }
  protected boolean abstractCollectionRemoveAll(Collection<?> c) { return super.removeAll(c); }
  protected void abstractCollectionClear() { super.clear(); }
  
}
