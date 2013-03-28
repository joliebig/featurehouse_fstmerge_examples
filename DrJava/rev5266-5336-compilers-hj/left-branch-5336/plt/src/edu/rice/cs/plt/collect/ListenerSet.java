

package edu.rice.cs.plt.collect;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Runnable1;


public class ListenerSet<T> extends DelegatingSet<Runnable1<? super T>> implements Runnable1<T> {

  private final Sink _sink;
  
  
  public ListenerSet() { this(new CopyOnWriteArraySet<Runnable1<? super T>>()); }
  
  
  public ListenerSet(Set<Runnable1<? super T>> delegate) {
    super(delegate);
    _sink = new Sink();
  }
  
   
  public void run(T arg) {
    RuntimeException exception = null;
    for (Runnable1<? super T> l : _delegate) {
      try { l.run(arg); }
      catch (RuntimeException e) {
        if (exception == null) { exception = e; }
      }
    }
    if (exception != null) { throw exception; }
  }
  
  
  public Sink sink() { return _sink; }
  
  
  
  public class Sink {
    
    
    public boolean add(Runnable1<? super T> listener) {
      return _delegate.add(listener);
    }
    
    
    public boolean add(Runnable listener) {
      return _delegate.add(LambdaUtil.promote(listener));
    }
    
    
    public boolean addAll(Iterable<Runnable1<? super T>> addList) {
      return CollectUtil.addAll(_delegate, addList);
    }
    
    
    public boolean remove(Runnable1<? super T> listener) {
      return _delegate.remove(listener);
    }
    
    
    public boolean removeAll(Iterable<Runnable1<? super T>> removeList) {
      return CollectUtil.removeAll(_delegate, removeList);
    }
    
  }
  
  
  public static <T> ListenerSet<T> make(Set<Runnable1<? super T>> delegate) {
    return new ListenerSet<T>(delegate);
  }
  
}
