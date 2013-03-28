

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.Lambda;


public abstract class PendingContinuation<T> implements Continuation<T> {
  
  
  public T value() {
    Continuation<? extends T> k = this;
    while (!k.isResolved()) { k = k.step(); }
    return k.value();
  }
  
  
  public boolean isResolved() { return false; }
  
  
  public <R> Continuation<R> compose(Lambda<? super T, ? extends Continuation<? extends R>> c) {
    return new ComposedContinuation<T, R>(this, c);
  }
  
  
  public abstract Continuation<? extends T> step();
  
}
