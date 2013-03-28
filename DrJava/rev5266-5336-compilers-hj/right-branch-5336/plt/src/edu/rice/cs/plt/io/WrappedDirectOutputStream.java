

package edu.rice.cs.plt.io;

import java.io.*;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class WrappedDirectOutputStream extends DirectOutputStream implements Composite {
  private OutputStream _stream;
  
  public WrappedDirectOutputStream(OutputStream stream) { _stream = stream; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_stream) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_stream) + 1; }
  
  @Override public void close() throws IOException { _stream.close(); }
  
  @Override public void flush() throws IOException { _stream.flush(); }
  
  @Override public void write(int b) throws IOException { _stream.write(b); }
  
  @Override public void write(byte[] bbuf) throws IOException { _stream.write(bbuf); }
  
  @Override public void write(byte[] bbuf, int offset, int length) throws IOException {
    _stream.write(bbuf, offset, length);
  }
  
  
  public static DirectOutputStream makeDirect(OutputStream stream) {
    if (stream instanceof DirectOutputStream) { return (DirectOutputStream) stream; }
    else { return new WrappedDirectOutputStream(stream); }
  }

}
