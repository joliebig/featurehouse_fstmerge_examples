

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.Lambda;


public class ValueContinuation<T> implements Continuation<T> {
  
  private final T _val;
  
  
  public ValueContinuation(T val) { _val = val; }
  
  
  public T value() { return _val; }
  
  
  public boolean isResolved() { return true; }
  
  
  public Continuation<T> step() { throw new IllegalStateException(); }
  
  
  public <R> Continuation<R> compose(Lambda<? super T, ? extends Continuation<? extends R>> c) {
    return new ComposedContinuation<T, R>(this, c);
  }
  
  
  public static <T> ValueContinuation<T> make(T val) { return new ValueContinuation<T>(val); }
  
}
