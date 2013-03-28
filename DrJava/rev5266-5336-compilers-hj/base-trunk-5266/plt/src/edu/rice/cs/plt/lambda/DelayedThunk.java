

package edu.rice.cs.plt.lambda;

import java.io.Serializable;


public class DelayedThunk<R> implements Box<R>, ResolvingThunk<R>, Serializable {
  
  private R _val;
  private boolean _initialized;
  
  public DelayedThunk() {
    _initialized = false;
    
  }
  
  
  public R value() {
    if (!_initialized) { throw new IllegalStateException("DelayedThunk is not initialized"); }
    return _val;
  }
  
  
  public void set(R val) {
    if (_initialized) { throw new IllegalStateException("DelayedThunk is already initialized"); }
    _val = val;
    _initialized = true;
  }
  
  public boolean isResolved() { return _initialized; }
  
  
  public static <R> DelayedThunk<R> make() { return new DelayedThunk<R>(); }
  
}
