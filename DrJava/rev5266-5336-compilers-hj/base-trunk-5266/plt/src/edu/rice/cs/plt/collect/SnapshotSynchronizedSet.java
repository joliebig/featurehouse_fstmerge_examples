

package edu.rice.cs.plt.collect;

import java.io.Serializable;
import java.util.*;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.CachedThunk;
import edu.rice.cs.plt.lambda.Thunk;


public class SnapshotSynchronizedSet<E> extends DelegatingSet<E> {
  private final CachedThunk<Iterable<E>> _copy;
  
  public SnapshotSynchronizedSet(Set<E> delegate) {
    super(Collections.synchronizedSet(delegate));
    _copy = CachedThunk.make(new Thunk<Iterable<E>>() {
      public Iterable<E> value() {
        synchronized(_delegate) {
          return IterUtil.snapshot(_delegate.iterator());
        }
      }
    });
  }

  
  public void discardSnapshot() { _copy.reset(); }
  
  
  private boolean reset(boolean changed) {
    if (changed) { _copy.reset(); }
    return changed;
  }
  
  @Override public Iterator<E> iterator() { return _copy.value().iterator(); }
  
  @Override public boolean add(E o) { return reset(_delegate.add(o)); }
  @Override public boolean addAll(Collection<? extends E> c) { return reset(_delegate.addAll(c)); }
  @Override public void clear() { _delegate.clear(); reset(true); }
  @Override public boolean remove(Object o) { return reset(_delegate.remove(o)); }
  @Override public boolean removeAll(Collection<?> c) { return reset(_delegate.removeAll(c)); }
  @Override public boolean retainAll(Collection<?> c) { return reset(_delegate.retainAll(c)); }
  
  
  public static <T> Thunk<Set<T>> factory(Thunk<? extends Set<T>> delegateFactory) {
    return new Factory<T>(delegateFactory);
  }
  
  private static final class Factory<T> implements Thunk<Set<T>>, Serializable {
    private final Thunk<? extends Set<T>> _delegateFactory;
    private Factory(Thunk<? extends Set<T>> delegateFactory) { _delegateFactory = delegateFactory; }
    public Set<T> value() { return new SnapshotSynchronizedSet<T>(_delegateFactory.value()); }
  }
  
  
  public static <T> SnapshotSynchronizedSet<T> make(Set<T> delegate) {
    return new SnapshotSynchronizedSet<T>(delegate);
  }
  
  public static <T> SnapshotSynchronizedSet<T> makeHash() {
    return new SnapshotSynchronizedSet<T>(new HashSet<T>());
  }
  
  public static <T> SnapshotSynchronizedSet<T> makeLinkedHash() {
    return new SnapshotSynchronizedSet<T>(new LinkedHashSet<T>());
  }
  
  public static <T> SnapshotSynchronizedSet<T> makeTree() {
    return new SnapshotSynchronizedSet<T>(new TreeSet<T>());
  }
  
}
