

package edu.rice.cs.plt.debug;

import java.io.*;
import java.nio.charset.Charset;


public class SystemOutLogSink extends IndentedTextLogSink {
  
  private final Charset _charset;
  private volatile BufferedWriter _writer;
  private volatile PrintStream _currentStream; 
  
  
  public SystemOutLogSink() {
    super();
    _charset = Charset.defaultCharset();
    _writer = null;
    _currentStream = null;
  }
  
  
  public SystemOutLogSink(String charsetName) throws UnsupportedEncodingException {
    super();
    _charset = Charset.forName(charsetName);
    _currentStream = null;
  }
  
  
  public SystemOutLogSink(int idealLineWidth) {
    super(idealLineWidth);
    _charset = Charset.defaultCharset();
    _writer = null;
    _currentStream = null;
  }
  
  
  public SystemOutLogSink(String charsetName, int idealLineWidth) throws UnsupportedEncodingException {
    super(idealLineWidth);
    _charset = Charset.forName(charsetName);
    _writer = null;
    _currentStream = null;
  }
  
  @Override protected BufferedWriter writer(Message m) {
    
    PrintStream out = System.out;
    if (_currentStream != out) {
      
      _writer = new BufferedWriter(new OutputStreamWriter(out, _charset));
      _currentStream = out;
    }
    return _writer;
  }
  
  public void close() throws IOException { _writer.close(); }
  
}
