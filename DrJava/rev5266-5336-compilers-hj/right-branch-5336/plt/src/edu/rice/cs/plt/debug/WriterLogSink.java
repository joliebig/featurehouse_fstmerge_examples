

package edu.rice.cs.plt.debug;

import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import edu.rice.cs.plt.io.IOUtil;


public class WriterLogSink extends IndentedTextLogSink {
  
  private final BufferedWriter _w;
  
  public WriterLogSink(Writer w) {
    super();
    _w = IOUtil.asBuffered(w);
  }
  
  public WriterLogSink(Writer w, int idealLineWidth) {
    super(idealLineWidth);
    _w = IOUtil.asBuffered(w);
  }
  
  @Override protected BufferedWriter writer(Message m) { return _w; }
  
  public void close() throws IOException { _w.close(); }
  
}
