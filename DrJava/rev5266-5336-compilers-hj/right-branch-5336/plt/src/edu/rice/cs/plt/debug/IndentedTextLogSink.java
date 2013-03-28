

package edu.rice.cs.plt.debug;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.WeakHashMap;

import edu.rice.cs.plt.collect.TotalMap;
import edu.rice.cs.plt.concurrent.LockMap;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.lambda.WrappedException;


public abstract class IndentedTextLogSink extends TextLogSink {

  private static final String HANGING_INDENT = "    ";
  
  private static final Lambda<Long, Indenter> MAKE_INDENTER = new Lambda<Long, Indenter>() {
    public Indenter value(Long l) { return new Indenter(); }
  };
  
  private final TotalMap<Long, Indenter> _indenters;
  private final LockMap<BufferedWriter> _locks;
  private final WeakHashMap<BufferedWriter, Long> _lastThreads; 
  
  protected IndentedTextLogSink() {
    super();
    _indenters = new TotalMap<Long, Indenter>(MAKE_INDENTER, true);
    _locks = new LockMap<BufferedWriter>(5);
    _lastThreads = new WeakHashMap<BufferedWriter, Long>(5);
  }
  
  protected IndentedTextLogSink(int idealLineWidth) {
    super(idealLineWidth);
    _indenters = new TotalMap<Long, Indenter>(MAKE_INDENTER, true);
    _locks = new LockMap<BufferedWriter>(5);
    _lastThreads = new WeakHashMap<BufferedWriter, Long>(5);
  }

  
  protected abstract BufferedWriter writer(Message m);
   
  protected void write(Message m, SizedIterable<String> text) {
    BufferedWriter w = writer(m);
    doWrite(w, m, text);
  }
  
  protected void writeStart(StartMessage m, SizedIterable<String> text) {
    write(m, text);
    synchronized(_indenters) { _indenters.get(m.thread().getId()).push(); }
  }

  protected void writeEnd(EndMessage m, SizedIterable<String> text) {
    synchronized(_indenters) { _indenters.get(m.thread().getId()).pop(); }
    write(m, text);
  }
      
  private void doWrite(BufferedWriter w, Message m, SizedIterable<String> text) {
    Long threadId = m.thread().getId();
    String indentString;
    synchronized(_indenters) { indentString = _indenters.get(threadId).indentString(); }
    Runnable unlock = _locks.lock(w);
    try {
      if (_lastThreads.containsKey(w)) {
        Long prevId = _lastThreads.get(w);
        if (!prevId.equals(threadId)) { w.newLine(); _lastThreads.put(w, threadId); }
      }
      else { _lastThreads.put(w, threadId); }
      w.write(indentString);
      w.write("[" + formatLocation(m.caller()) + " - " + formatThread(m.thread()) +" - " +
              formatTime(m.time()) + "]");
      w.newLine();
      for (String s : text) {
        w.write(indentString);
        w.write(HANGING_INDENT);
        w.write(s);
        w.newLine();
      }
      w.flush();
    }
    catch (IOException e) {
      
      
      throw new WrappedException(e);
    }
    finally { unlock.run(); }
  }
  
}
