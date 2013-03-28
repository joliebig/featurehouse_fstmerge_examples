

package edu.rice.cs.plt.io;

import java.io.*;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class WrappedDirectReader extends DirectReader implements Composite {
  private Reader _reader;
  
  public WrappedDirectReader(Reader reader) { _reader = reader; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_reader) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_reader) + 1; }
  
  @Override public void close() throws IOException { _reader.close(); }
  
  @Override public void mark(int readAheadLimit) throws IOException { _reader.mark(readAheadLimit); }
  
  @Override public boolean markSupported() { return _reader.markSupported(); }
  
  @Override public int read() throws IOException { return _reader.read(); }
  
  @Override public int read(char[] cbuf) throws IOException { return _reader.read(cbuf); }
  
  @Override public int read(char[] cbuf, int offset, int length) throws IOException {
    return _reader.read(cbuf, offset, length);
  }
  
  
  
  
  @Override public boolean ready() throws IOException { return _reader.ready(); }
  
  @Override public void reset() throws IOException { _reader.reset(); }
  
  @Override public long skip(long n) throws IOException { return _reader.skip(n); }

  
  public static DirectReader makeDirect(Reader reader) {
    if (reader instanceof DirectReader) { return (DirectReader) reader; }
    else { return new WrappedDirectReader(reader); }
  }

}
