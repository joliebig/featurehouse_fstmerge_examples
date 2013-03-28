

package edu.rice.cs.plt.debug;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.io.Closeable;
import java.io.Serializable;
import edu.rice.cs.plt.tuple.Option;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;


public interface LogSink extends Closeable {
  
  public void log(StandardMessage m);
  public void logStart(StartMessage m);
  public void logEnd(EndMessage m);
  public void logError(ErrorMessage m);
  public void logStack(StackMessage m);
  
  public abstract class Message implements Serializable {
    private final ThreadSnapshot _thread;
    private final Option<String> _text;
    
    protected Message(ThreadSnapshot thread) { _thread = thread; _text = Option.none(); }
    protected Message(ThreadSnapshot thread, String text) { _thread = thread; _text = Option.some(text); }
    protected Message(Message copy) { _thread = copy._thread; _text = copy._text; }
    
    
    public ThreadSnapshot thread() { return _thread; }
    
    public Option<String> text() { return _text; }
    
    public Date time() { return _thread.snapshotTime(); }
    
    public Option<StackTraceElement> caller() { return Option.wrap(_thread.callingLocation()); }
    
    public Iterable<StackTraceElement> stack() { return IterUtil.skipFirst(_thread.getStackTrace()); }
    
    public abstract void send(LogSink sink);
    public abstract <T> T apply(MessageVisitor<? extends T> visitor);
    
    
    public abstract Message serializable();

  
  }
  
  public abstract class ValueMessage extends Message {
    private final Iterable<Pair<String, Object>> _values;
    
    protected ValueMessage(ThreadSnapshot thread, String[] names, Object[] vals) {
      super(thread);
      _values = makeValues(names, vals);
    }
    protected ValueMessage(ThreadSnapshot thread, String message, String[] names, Object[] vals) {
      super(thread, message);
      _values  = makeValues(names, vals);
    }
    
    protected ValueMessage(ValueMessage copy) {
      super(copy);
      List<Pair<String, Object>> safeVals = new LinkedList<Pair<String, Object>>();
      for (Pair<String, Object> p : copy._values) {
        safeVals.add(Pair.make(p.first(), IOUtil.ensureSerializable(p.second())));
      }
      _values = safeVals;
    }
    
    private Iterable<Pair<String, Object>> makeValues(String[] names, Object[] vals) {
      if (names.length != vals.length) {
        throw new IllegalArgumentException("Lengths of names and values are inconsistent");
      }
      return IterUtil.zip(IterUtil.make(names), IterUtil.make(vals));
    }
    
    
    public Iterable<Pair<String, Object>> values() { return _values; }
    
  }
  
  
  public class StandardMessage extends ValueMessage {
    public StandardMessage(ThreadSnapshot thread, String[] names, Object[] vals) {
      super(thread, names, vals);
    }
    public StandardMessage(ThreadSnapshot thread, String message, String[] names, Object[] vals) {
      super(thread, message, names, vals);
    }
    protected StandardMessage(StandardMessage copy) { super(copy); }
    public void send(LogSink sink) { sink.log(this); }
    public <T> T apply(MessageVisitor<? extends T> visitor) { return visitor.forStandard(this); }
    public StandardMessage serializable() { return new StandardMessage(this); }
  }
  
  
  public class StartMessage extends ValueMessage {
    public StartMessage(ThreadSnapshot thread, String[] names, Object[] vals) {
      super(thread, names, vals);
    }
    public StartMessage(ThreadSnapshot thread, String message, String[] names, Object[] vals) {
      super(thread, message, names, vals);
    }
    protected StartMessage(StartMessage copy) { super(copy); }
    public void send(LogSink sink) { sink.logStart(this); }
    public <T> T apply(MessageVisitor<? extends T> visitor) { return visitor.forStart(this); }
    public StartMessage serializable() { return new StartMessage(this); }
  }
  
  
  public class EndMessage extends ValueMessage {
    public EndMessage(ThreadSnapshot thread, String[] names, Object[] vals) {
      super(thread, names, vals);
    }
    public EndMessage(ThreadSnapshot thread, String message, String[] names, Object[] vals) {
      super(thread, message, names, vals);
    }
    protected EndMessage(EndMessage copy) { super(copy); }
    public void send(LogSink sink) { sink.logEnd(this); }
    public <T> T apply(MessageVisitor<? extends T> visitor) { return visitor.forEnd(this); }
    public EndMessage serializable() { return new EndMessage(this); }
  }
  
  
  public class ErrorMessage extends Message {
    private final Throwable _error;
    public ErrorMessage(ThreadSnapshot thread, Throwable error) { super(thread); _error = error; }
    public ErrorMessage(ThreadSnapshot thread, String text, Throwable error) { super(thread, text); _error = error; }
    public ErrorMessage(ErrorMessage copy) { super(copy); _error = IOUtil.ensureSerializable(copy._error); }
    public Throwable error() { return _error; }
    public void send(LogSink sink) { sink.logError(this); }
    public <T> T apply(MessageVisitor<? extends T> visitor) { return visitor.forError(this); }
    public ErrorMessage serializable() { return new ErrorMessage(this); }
  }
  
  
  public class StackMessage extends Message {
    public StackMessage(ThreadSnapshot thread) { super(thread); }
    public StackMessage(ThreadSnapshot thread, String text) { super(thread, text); }
    public StackMessage(StackMessage copy) { super(copy); }
    public void send(LogSink sink) { sink.logStack(this); }
    public <T> T apply(MessageVisitor<? extends T> visitor) { return visitor.forStack(this); }
    public StackMessage serializable() { return new StackMessage(this); }
  }
  
  public interface MessageVisitor<T> {
    public T forStandard(StandardMessage m);
    public T forStart(StartMessage m);
    public T forEnd(EndMessage m);
    public T forError(ErrorMessage m);
    public T forStack(StackMessage m);
  }
  
}
