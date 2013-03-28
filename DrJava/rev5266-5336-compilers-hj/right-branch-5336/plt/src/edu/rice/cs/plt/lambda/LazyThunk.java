

package edu.rice.cs.plt.lambda;


public class LazyThunk<R> implements ResolvingThunk<R> {

  private volatile R _val;
  private volatile Thunk<? extends R> _thunk;
  
  public LazyThunk(Thunk<? extends R> value) {
    _thunk = value;
    
  }
  
  public R value() {
    if (_thunk != null) { resolve(); }
    return _val;
  }
  
  public boolean isResolved() { return _thunk == null; }
  
  private synchronized void resolve() {
    if (_thunk != null) { 
      _val = _thunk.value();
      _thunk = null;
    }
  }
  
  public static <R> LazyThunk<R> make(Thunk<? extends R> value) { return new LazyThunk<R>(value); }
  
}
