

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.Lambda;


public abstract class BinaryArgContinuation<T1, T2, R> extends PendingContinuation<R> {
  
  
  protected abstract Continuation<? extends T1> arg1();
  
  
  protected abstract Continuation<? extends T2> arg2();
  
  
  protected abstract Continuation<? extends R> apply(T1 arg1, T2 arg2);
  
  
  public Continuation<R> step() {
    return new ComposedContinuation<T1, R>(arg1(), new Lambda<T1, Continuation<? extends R>>() {
      public Continuation<? extends R> value(final T1 arg1) {
        return new ComposedContinuation<T2, R>(arg2(), new Lambda<T2, Continuation<? extends R>>() {
          public Continuation<? extends R> value(T2 arg2) { return apply(arg1, arg2); }
        });
      }
    });
  }
  
}
