

package edu.rice.cs.plt.debug;

import edu.rice.cs.plt.swing.SwingUtil;
import edu.rice.cs.plt.iter.SizedIterable;
import edu.rice.cs.plt.iter.IterUtil;

public class PopupLogSink extends TextLogSink {
  
  private String _name;
  
  public PopupLogSink(String name) {
    super(40); 
    _name = name;
  }
  
  @Override protected void write(Message m, SizedIterable<String> messages) {
    Iterable<String> header = IterUtil.make("[" + formatLocation(m.caller()) + "]",
                                            "[" + formatThread(m.thread()) + "]",
                                            "[" + formatTime(m.time()) + "]");
    String text = IterUtil.multilineToString(IterUtil.compose(header, messages));
    SwingUtil.showPopup(_name, text);
  }
  
  @Override protected void writeStart(StartMessage m, SizedIterable<String> messages) { write(m, messages); }
  @Override protected void writeEnd(EndMessage m, SizedIterable<String> messages) { write(m, messages); }
  
  public void close() {}
}
