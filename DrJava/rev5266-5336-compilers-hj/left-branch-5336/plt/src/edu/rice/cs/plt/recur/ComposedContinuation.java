

package edu.rice.cs.plt.recur;

import edu.rice.cs.plt.lambda.Lambda;


public class ComposedContinuation<T, R> extends PendingContinuation<R> {
  
  private final Continuation<? extends T> _first;
  private final Lambda<? super T, ? extends Continuation<? extends R>> _rest;
  
  public ComposedContinuation(Continuation<? extends T> first,
                              Lambda<? super T, ? extends Continuation<? extends R>> rest) {
    _first = first;
    _rest = rest;
  }
  
  
  public Continuation<? extends R> step() {
    if (_first.isResolved()) { return _rest.value(_first.value()); }
    else { return _first.step().compose(_rest); }
  }
  
  
  public <S> Continuation<S> compose(final Lambda<? super R, ? extends Continuation<? extends S>> c) {
    return new ComposedContinuation<T, S>(_first, new Lambda<T, Continuation<? extends S>>() {
      public Continuation<? extends S> value(T arg) {
        return _rest.value(arg).compose(c);
      }
    });
  }
  
}
