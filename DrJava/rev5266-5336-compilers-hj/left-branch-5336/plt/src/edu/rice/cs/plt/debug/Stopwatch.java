

package edu.rice.cs.plt.debug;

import java.util.List;
import java.util.LinkedList;
import edu.rice.cs.plt.iter.IterUtil;


public class Stopwatch {
  private final List<Long> _splits;
  private boolean _running;
  private long _start;
  
  
  public Stopwatch() {
    _running = false;
    _splits = new LinkedList<Long>();
  }
  
  
  public Stopwatch(boolean startImmediately) {
    _running = false;
    _splits = new LinkedList<Long>();
    if (startImmediately) start();
  }
  
  
  public void start() {
    if (_running) { throw new IllegalStateException("Already running"); }
    _start = System.currentTimeMillis();
    _running = true;
  }
  
  
  public long split() {
    if (!_running) { throw new IllegalStateException("Not running"); }
    long result = System.currentTimeMillis() - _start;
    _splits.add(result);
    return result;
  }
  
  
  public long stop() {
    long result = split();
    _running = false;
    return result;
  }

  
  public Iterable<Long> splits() { return IterUtil.immutable(_splits); }
}
