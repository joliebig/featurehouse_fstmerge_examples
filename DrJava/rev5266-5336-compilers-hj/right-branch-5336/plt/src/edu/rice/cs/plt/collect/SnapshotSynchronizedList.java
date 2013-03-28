

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import edu.rice.cs.plt.lambda.CachedThunk;
import edu.rice.cs.plt.lambda.Thunk;


public class SnapshotSynchronizedList<E> extends DelegatingList<E> {
  private final Object _lock;
  private final CachedThunk<List<E>> _copy;
  
  public SnapshotSynchronizedList(List<E> delegate) { this(Collections.synchronizedList(delegate), null); }
  
   
  private SnapshotSynchronizedList(List<E> synchronizedDelegate, Object lock) {
    super(synchronizedDelegate);
    _lock = (lock == null) ? synchronizedDelegate : lock;
    _copy = CachedThunk.make(new Thunk<List<E>>() {
      public List<E> value() {
        synchronized(_lock) { return new ArrayList<E>(_delegate); }
      }
    });
  }
  
  
  public void discardSnapshot() {
    
    _copy.reset();
  }
  
  
  private boolean reset(boolean changed) {
    if (changed) { discardSnapshot(); }
    return changed;
  }
  
  @Override public Iterator<E> iterator() { return _copy.value().iterator(); }
  @Override public ListIterator<E> listIterator() { return _copy.value().listIterator(); }
  @Override public ListIterator<E> listIterator(int index) { return _copy.value().listIterator(index); }
  
  @Override public boolean add(E o) { return reset(_delegate.add(o)); }
  @Override public boolean addAll(Collection<? extends E> c) { return reset(_delegate.addAll(c)); }
  @Override public void clear() { _delegate.clear(); discardSnapshot(); }
  @Override public boolean remove(Object o) { return reset(_delegate.remove(o)); }
  @Override public boolean removeAll(Collection<?> c) { return reset(_delegate.removeAll(c)); }
  @Override public boolean retainAll(Collection<?> c) { return reset(_delegate.retainAll(c)); }
  
  @Override public void add(int index, E element) { _delegate.add(index, element); discardSnapshot(); }
  @Override public boolean addAll(int index, Collection<? extends E> c) { return reset(_delegate.addAll(index, c)); }

  @Override public E set(int index, E element) {
    E result = _delegate.set(index, element);
    discardSnapshot();
    return result;
  }
  
  @Override public E remove(int index) {
    E result = _delegate.remove(index);
    discardSnapshot();
    return result;
  }
  
  @Override public List<E> subList(final int from, int to) {
    return new SnapshotSynchronizedList<E>(_delegate.subList(from, to), _lock) {
      @Override public void discardSnapshot() {
        
        SnapshotSynchronizedList.this.discardSnapshot();
        super.discardSnapshot();
      }
    };
  }
  
  
  public static <T> Thunk<List<T>> factory(Thunk<? extends List<T>> delegateFactory) {
    return new Factory<T>(delegateFactory);
  }
  
  private static final class Factory<T> implements Thunk<List<T>>, Serializable {
    private final Thunk<? extends List<T>> _delegateFactory;
    private Factory(Thunk<? extends List<T>> delegateFactory) { _delegateFactory = delegateFactory; }
    public List<T> value() { return new SnapshotSynchronizedList<T>(_delegateFactory.value()); }
  }
  
  
}
