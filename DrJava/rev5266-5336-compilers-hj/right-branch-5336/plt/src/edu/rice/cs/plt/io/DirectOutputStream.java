

package edu.rice.cs.plt.io;

import java.io.*;


public abstract class DirectOutputStream extends OutputStream {
  
  protected static final int DEFAULT_BUFFER_SIZE = 1024;

  
  @Override public void write(int b) throws IOException { write(new byte[]{ (byte) b }, 0, 1); }
  
  
  @Override public void write(byte[] bbuf) throws IOException { write(bbuf, 0, bbuf.length); }
      
  
  @Override public abstract void write(byte[] bbuf, int offset, int bytes) throws IOException;

  
  public int write(InputStream in, int bytes) throws IOException {
    return write(in, bytes, (bytes < DEFAULT_BUFFER_SIZE) ? bytes : DEFAULT_BUFFER_SIZE);
  }
  
  
  public int write(InputStream in, int bytes, int bufferSize) throws IOException {
    return write(in, bytes, new byte[bufferSize]);
  }
  
  
  public int write(InputStream in, int bytes, byte[] buffer) throws IOException {
    return IOUtil.doWriteFromInputStream(in, this, bytes, buffer);
  }
  
  
  public int writeAll(InputStream in) throws IOException {
    return writeAll(in, DEFAULT_BUFFER_SIZE);
  }
  
  
  public int writeAll(InputStream in, int bufferSize) throws IOException {
    return writeAll(in, new byte[bufferSize]);
  }

  
  public int writeAll(InputStream in, byte[] buffer) throws IOException {
    return IOUtil.doCopyInputStream(in, this, buffer);
  }  
  
}
