

package edu.rice.cs.plt.debug;

import java.io.*;
import java.util.Date;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.lambda.LazyThunk;
import edu.rice.cs.plt.lambda.Thunk;
import edu.rice.cs.plt.lambda.WrappedException;
import edu.rice.cs.plt.text.TextUtil;


public class FileLogSink extends IndentedTextLogSink {
  
  private final Thunk<BufferedWriter> _writer;
  private volatile boolean _active;
  
  public FileLogSink(String filename) { this(new File(filename), null, true); }
  
  public FileLogSink(File f) { this(f, null, true); }
  
  public FileLogSink(File f, int idealLineWidth) { this(f, null, idealLineWidth, true); }
  
  public FileLogSink(File f, boolean closeOnExit) { this(f, null, closeOnExit); }
  
  public FileLogSink(File f, int idealLineWidth, boolean closeOnExit) {
    this(f, null, idealLineWidth, closeOnExit);
  }
  
  public FileLogSink(File f, String charset) { this(f, charset, true); }

  public FileLogSink(File f, String charset, boolean closeOnExit) {
    super();
    _writer = initWriter(f, charset);
    _active = false;
    if (closeOnExit) { IOUtil.closeOnExit(this); }
  }
  
  public FileLogSink(File f, String charset, int idealLineWidth) {
    this(f, charset, idealLineWidth, true);
  }
  
  public FileLogSink(File f, String charset, int idealLineWidth, boolean closeOnExit) {
    super(idealLineWidth);
    _writer = initWriter(f, charset);
    _active = false;
    if (closeOnExit) { IOUtil.closeOnExit(this); }
  }
  
  
  private Thunk<BufferedWriter> initWriter(final File f, final String charset) {
    return LazyThunk.make(new Thunk<BufferedWriter>() {
      public BufferedWriter value() {
        try {
          OutputStream out = new FileOutputStream(f, true);
          try {
            Writer w = (charset == null) ? new OutputStreamWriter(out) : new OutputStreamWriter(out, charset);
            BufferedWriter result = new BufferedWriter(w);
            IOUtil.closeOnExit(result);
            String stars = TextUtil.repeat('*', 40);
            result.write(stars);
            result.newLine();
            result.write("Opened log file " + formatTime(new Date()));
            result.newLine();
            result.write(stars);
            result.newLine();
            result.newLine();
            result.flush();
            _active = true;
            return result;
          }
          finally { if (!_active) { out.close(); } }
        }
        catch (IOException e) { throw new WrappedException(e); }
      }
    });
  }
  
  @Override protected BufferedWriter writer(Message m) { return _writer.value(); }
  
  
  public void close() throws IOException {
    if (_active) { _writer.value().close(); _active = false; }
  }
    
}
