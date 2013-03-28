

package edu.rice.cs.plt.debug;

import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;
import edu.rice.cs.plt.tuple.IdentityWrapper;


public class SplitLogSink implements LogSink, Composite {
  
  private final Set<IdentityWrapper<LogSink>> _sinkSet;
  
  private volatile Iterable<LogSink> _sinks;
  
  
  public SplitLogSink(LogSink... sinks) { this(IterUtil.asIterable(sinks)); }
  
  public SplitLogSink(Iterable<? extends LogSink> sinks) {
    _sinkSet = new HashSet<IdentityWrapper<LogSink>>();
    add(sinks);
  }
  
  public void add(LogSink... toAdd) {
    for (LogSink s : toAdd) { _sinkSet.add(IdentityWrapper.make(s)); }
    refreshSinks();
  }

  public void add(Iterable<? extends LogSink> toAdd) {
    for (LogSink s : toAdd) { _sinkSet.add(IdentityWrapper.make(s)); }
    refreshSinks();
  }
  
  public void remove(LogSink... toRemove) {
    for (LogSink s : toRemove) { _sinkSet.remove(IdentityWrapper.make(s)); }
    refreshSinks();
  }
  
  public void remove(Iterable<? extends LogSink> toRemove) {
    for (LogSink s : toRemove) { _sinkSet.remove(IdentityWrapper.make(s)); }
    refreshSinks();
  }
  
  private void refreshSinks() {
    _sinks = IterUtil.snapshot(IterUtil.valuesOf(_sinkSet));
  }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_sinks) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_sinks) + 1; }
  
  public void close() throws IOException {
    IOException exception = null;
    for (LogSink s : _sinks) {
      try { s.close(); }
      catch (IOException e) { exception = e; }
    }
    if (exception != null) { throw exception; }
  }
  
  public void log(StandardMessage m) {
    for (LogSink s : _sinks) { s.log(m); }
  }
  
  public void logStart(StartMessage m) {
    for (LogSink s : _sinks) { s.logStart(m); }
  }
  
  public void logEnd(EndMessage m) {
    for (LogSink s : _sinks) { s.logEnd(m); }
  }

  public void logError(ErrorMessage m) {
    for (LogSink s : _sinks) { s.logError(m); }
  }

  public void logStack(StackMessage m) {
    for (LogSink s : _sinks) { s.logStack(m); }
  }
  
}
