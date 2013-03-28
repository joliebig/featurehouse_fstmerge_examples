

package edu.rice.cs.plt.debug;

import java.io.*;


public class OutputStreamLogSink extends IndentedTextLogSink {
  
  private final BufferedWriter _writer;
    
  public OutputStreamLogSink(OutputStream out) {
    super();
    _writer = new BufferedWriter(new OutputStreamWriter(out));
  }
  
  public OutputStreamLogSink(OutputStream out, String charset) throws UnsupportedEncodingException {
    super();
    _writer = new BufferedWriter(new OutputStreamWriter(out, charset));
  }
  
  public OutputStreamLogSink(OutputStream out, int idealLineWidth) {
    super(idealLineWidth);
    _writer = new BufferedWriter(new OutputStreamWriter(out));
  }

  public OutputStreamLogSink(OutputStream out, String charset, int idealLineWidth)
      throws UnsupportedEncodingException {
    super(idealLineWidth);
    _writer = new BufferedWriter(new OutputStreamWriter(out, charset));
  }
  
  public void close() throws IOException { _writer.close(); }
  
  @Override protected BufferedWriter writer(Message m) {
    return _writer;
  }

}
