

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.Lambda;


public abstract class ArgContinuation<T, R> extends PendingContinuation<R> {
  
  
  protected abstract Continuation<? extends T> arg();
  
  
  protected abstract Continuation<? extends R> apply(T arg);
  
  
  public Continuation<R> step() {
    return new ComposedContinuation<T, R>(arg(), new Lambda<T, Continuation<? extends R>>() {
      public Continuation<? extends R> value(T arg) { return apply(arg); }
    });
  }
  
}
