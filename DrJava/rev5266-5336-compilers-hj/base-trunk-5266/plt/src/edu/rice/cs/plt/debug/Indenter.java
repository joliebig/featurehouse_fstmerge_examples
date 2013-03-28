

package edu.rice.cs.plt.debug;

import edu.rice.cs.plt.text.TextUtil;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.LazyThunk;


public class Indenter {
  
  private final String _token;
  private int _level;
  private Thunk<String> _stringFactory;
  
  
  public Indenter() { this("  "); }
  
  
  public Indenter(int spaces) { this(TextUtil.repeat(' ', spaces)); }
  
  
  public Indenter(String token) {
    _token = token;
    _level = 0;
    _stringFactory = makeThunk();
  }
  
  
  public void push() { _level++; _stringFactory = makeThunk(); }
  
  
  public void pop() { _level--; _stringFactory = makeThunk(); }
  
  
  public void atomicPush() { adjust(1); _stringFactory = makeThunk(); }
  
  
  public void atomicPop() { adjust(-1); _stringFactory = makeThunk(); }
  
  
  private synchronized void adjust(int delta) { _level += delta; }

  
  public String indentString() { return _stringFactory.value(); }
  
  private Thunk<String> makeThunk() {
    return LazyThunk.make(new Thunk<String>() {
      public String value() {
        if (_level <= 0) { return ""; }
        else { return TextUtil.repeat(_token, _level); }
      }
    });
  }
  
}
