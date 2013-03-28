

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;


public class DelegatingList<E> extends DelegatingCollection<E> implements List<E> {
  protected final List<E> _delegate; 
  public DelegatingList(List<E> delegate) { super(delegate); _delegate = delegate; }
  
  public boolean equals(Object o) { return _delegate.equals(o); }
  public int hashCode() { return _delegate.hashCode(); }
  public E get(int index) { return _delegate.get(index); }
  public E set(int index, E element) { return _delegate.set(index, element); }
  public void add(int index, E element) { _delegate.add(index, element); }
  public E remove(int index) { return _delegate.remove(index); }
  public boolean addAll(int index, Collection<? extends E> c) { return _delegate.addAll(index, c); }
  public int indexOf(Object o) { return _delegate.indexOf(o); }
  public int lastIndexOf(Object o) { return _delegate.lastIndexOf(o); }
  public ListIterator<E> listIterator() { return _delegate.listIterator(); }
  public ListIterator<E> listIterator(int index) { return _delegate.listIterator(index); }
  public List<E> subList(int fromIndex, int toIndex) { return _delegate.subList(fromIndex, toIndex); }
}
