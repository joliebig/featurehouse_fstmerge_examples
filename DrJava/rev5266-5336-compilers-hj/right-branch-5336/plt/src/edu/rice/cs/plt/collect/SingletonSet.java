

package edu.rice.cs.plt.collect;

import java.util.Collection;
import java.io.Serializable;
import java.util.Iterator;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.iter.SingletonIterator;


public class SingletonSet<E> extends AbstractPredicateSet<E> implements Serializable {
  
  private final E _elt;
  
  public SingletonSet(E elt) { _elt = elt; }
  
  public boolean contains(Object o) { return (_elt == null) ? (o == null) : _elt.equals(o); }
  public Iterator<E> iterator() { return new SingletonIterator<E>(_elt); }
  public boolean isInfinite() { return false; }
  public boolean hasFixedSize() { return true; }
  public boolean isStatic() { return true; }
  
  @Override public int size() { return 1; }
  @Override public int size(int bound) { return (bound < 1) ? bound : 1; }
  @Override public boolean isEmpty() { return false; }
  @Override public Object[] toArray() { return new Object[]{ _elt }; }
  
  @Override public boolean add(E o) { throw new UnsupportedOperationException(); }
  @Override public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
  @Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
  @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  @Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
  @Override public void clear() { throw new UnsupportedOperationException(); }
  
  
  public static <E> SingletonSet<E> make(E element) { 
    return new SingletonSet<E>(element);
  }
  
  @SuppressWarnings("unchecked") public static <T> Lambda<T, SingletonSet<T>> factory() {
    return (Factory<T>) Factory.INSTANCE;
  }
  
  private static final class Factory<T> implements Lambda<T, SingletonSet<T>>, Serializable {
    public static final Factory<Object> INSTANCE = new Factory<Object>();
    private Factory() {}
    public SingletonSet<T> value(T arg) { return new SingletonSet<T>(arg); }
  }
  
}
