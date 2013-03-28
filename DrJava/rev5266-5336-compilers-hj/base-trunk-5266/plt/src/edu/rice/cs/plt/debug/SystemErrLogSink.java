

package edu.rice.cs.plt.debug;

import java.io.*;
import java.nio.charset.Charset;


public class SystemErrLogSink extends IndentedTextLogSink {
  
  private final Charset _charset;
  private volatile BufferedWriter _writer;
  private volatile PrintStream _currentStream; 
  
  
  public SystemErrLogSink() {
    super();
    _charset = Charset.defaultCharset();
    _writer = null;
    _currentStream = null;
  }
  
  
  public SystemErrLogSink(String charsetName) throws UnsupportedEncodingException {
    super();
    _charset = Charset.forName(charsetName);
    _currentStream = null;
  }
  
  
  public SystemErrLogSink(int idealLineWidth) {
    super(idealLineWidth);
    _charset = Charset.defaultCharset();
    _writer = null;
    _currentStream = null;
  }
  
  
  public SystemErrLogSink(String charsetName, int idealLineWidth) throws UnsupportedEncodingException {
    super(idealLineWidth);
    _charset = Charset.forName(charsetName);
    _writer = null;
    _currentStream = null;
  }
  
  @Override protected BufferedWriter writer(Message m) {
    
    PrintStream err = System.err;
    if (_currentStream != err) {
      
      _writer = new BufferedWriter(new OutputStreamWriter(err, _charset));
      _currentStream = err;
    }
    return _writer;
  }
  
  public void close() throws IOException { _writer.close(); }
  
}
