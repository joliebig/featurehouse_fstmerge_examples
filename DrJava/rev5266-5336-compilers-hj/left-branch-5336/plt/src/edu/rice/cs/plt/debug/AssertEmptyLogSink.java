

package edu.rice.cs.plt.debug;

import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;


public final class AssertEmptyLogSink extends TextLogSink {
  
  public static final AssertEmptyLogSink INSTANCE = new AssertEmptyLogSink();
  
  private AssertEmptyLogSink() { super(); }
  
  public void close() {}
  
  @Override protected void write(Message m, SizedIterable<String> text) {
    assert false : makeMessage(m, text);
  }
  
  @Override protected void writeStart(StartMessage m, SizedIterable<String> text) {
    assert false : makeMessage(m, text);
  }
  
  @Override protected void writeEnd(EndMessage m, SizedIterable<String> text) {
    assert false : makeMessage(m, text);
  }
  
  private String makeMessage(Message m, SizedIterable<String> messages) {
    String first = "[" + formatLocation(m.caller()) + " - " + formatThread(m.thread()) + " - " +
                   formatTime(m.time()) + "]";
    return IterUtil.multilineToString(IterUtil.compose(first, messages));
  }
  
}
