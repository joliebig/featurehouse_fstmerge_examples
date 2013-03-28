

package edu.rice.cs.plt.io;

import java.io.*;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class WrappedDirectInputStream extends DirectInputStream implements Composite {
  private InputStream _stream;
  
  public WrappedDirectInputStream(InputStream stream) { _stream = stream; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_stream) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_stream) + 1; }
  
  @Override public int available() throws IOException { return _stream.available(); }
  
  @Override public void close() throws IOException { _stream.close(); }
  
  @Override public void mark(int readAheadLimit) { _stream.mark(readAheadLimit); }
  
  @Override public boolean markSupported() { return _stream.markSupported(); }
  
  @Override public int read() throws IOException { return _stream.read(); }
  
  @Override public int read(byte[] bbuf) throws IOException { return _stream.read(bbuf); }
  
  @Override public int read(byte[] bbuf, int offset, int length) throws IOException {
    return _stream.read(bbuf, offset, length);
  }
  
  @Override public void reset() throws IOException { _stream.reset(); }
  
  @Override public long skip(long n) throws IOException { return _stream.skip(n); }
  
  
  public static DirectInputStream makeDirect(InputStream stream) {
    if (stream instanceof DirectInputStream) { return (DirectInputStream) stream; }
    else { return new WrappedDirectInputStream(stream); }
  }

}
