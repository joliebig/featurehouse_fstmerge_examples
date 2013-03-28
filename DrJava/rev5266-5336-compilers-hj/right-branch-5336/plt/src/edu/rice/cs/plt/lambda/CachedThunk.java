

package edu.rice.cs.plt.lambda;

import edu.rice.cs.plt.tuple.Option;


public class CachedThunk<R> implements ResolvingThunk<R> {

  private final Thunk<? extends R> _thunk;
  private volatile Option<R> _val;
  
  public CachedThunk(Thunk<? extends R> value) {
    _thunk = value;
    _val = Option.none();
  }
  
  public R value() {
    Option<R> v = _val; 
    if (v.isNone()) { return resolve(); }
    else { return v.unwrap(); }
  }
  
  public synchronized void reset() {
    
    
    _val = Option.none();
  }
  
  public boolean isResolved() { return _val.isSome(); }
  
  
  public Option<R> cachedValue() { return _val; }
  
  private synchronized R resolve() {
    if (_val.isNone()) { 
      R result = _thunk.value();
      _val = Option.some(result);
      return result;
    }
    else { return _val.unwrap(); }
  }
  
  public static <R> CachedThunk<R> make(Thunk<? extends R> value) { return new CachedThunk<R>(value); }
  
}
