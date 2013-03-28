

package edu.rice.cs.plt.debug;

import java.io.Serializable;
import java.util.Date;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.iter.SizedIterable;


public class ThreadSnapshot implements Serializable {
  
  
  private static final int GET_STACK_TRACE_DEPTH;
  static {
    StackTraceElement[] s = Thread.currentThread().getStackTrace();
    int depth = 0;
    String name = ThreadSnapshot.class.getName();
    while (depth < s.length) {
      if (name.equals(s[depth].getClassName())) { break; }
      depth++;
    }
    GET_STACK_TRACE_DEPTH = depth;
  }
  
  private final String _name;
  private final long _id;
  private final boolean _daemon;
  private final int _priority;
  private final String _group;
  
  private final Date _time;
  
  private final SizedIterable<StackTraceElement> _stack;
  private final StackTraceElement _running;
  private final StackTraceElement _calling;
  
  private final Thread.State _state;
  private final boolean _alive;
  private final boolean _interrupted;
  
  public ThreadSnapshot() {
    this(Thread.currentThread(), true);
  }
  
  public ThreadSnapshot(Thread t) {
    this(t, t == Thread.currentThread());
  }
  
  
  private ThreadSnapshot(Thread t, boolean filterStack) {
    _name = t.getName();
    _id = t.getId();
    _daemon = t.isDaemon();
    _priority = t.getPriority();
    ThreadGroup g = t.getThreadGroup();
    _group = (g == null) ? null : g.getName();
    
    _time = new Date();
    
    StackTraceElement[] s = t.getStackTrace();
    if (filterStack) {
      int offset = GET_STACK_TRACE_DEPTH + 2; 
      if (s.length > offset) { 
        _stack = IterUtil.arraySegment(s, offset);
        _running = s[offset];
        _calling = (s.length > offset+1) ? s[offset+1] : null;
      }
      else {
        _stack = IterUtil.empty();
        _running = null;
        _calling = null;
      }
    }
    else {
      _stack = IterUtil.asIterable(s);
      _running = (s.length >= 1) ? s[0] : null;
      _calling = (s.length >= 2) ? s[1] : null;
    }
    
    _state = t.getState();
    _alive = t.isAlive();
    _interrupted = t.isInterrupted();
  }
  
  
  public String getName() { return _name; }
  
  public long getId() { return _id; }
  
  public boolean isDaemon() { return _daemon; }
  
  public int getPriority() { return _priority; }
  
  public String getThreadGroup() { return _group; }

  
  public Date snapshotTime() { return _time; }

  
  public SizedIterable<StackTraceElement> getStackTrace() { return _stack; }
  
  public StackTraceElement runningLocation() { return _running; }
  
  public StackTraceElement callingLocation() { return _calling; }
  
  
  public Thread.State getState() { return _state; }  
  
  public boolean isAlive() { return _alive; }
  
  public boolean isInterrupted() { return _interrupted; }

  
  public String toString() {
    return "Thread[" + _name + "," + _priority + "," + (_group == null ? "" : _group) + "]";
  }
  
}
