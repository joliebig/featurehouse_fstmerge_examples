

package edu.rice.cs.plt.io;

import java.io.*;


public abstract class DirectInputStream extends InputStream {
  
  protected static final int DEFAULT_BUFFER_SIZE = 1024;

  
  @Override public int read() throws IOException {
    byte[] bbuf = new byte[1];
    int readResult = read(bbuf, 0, 1);
    if (readResult == -1) { return readResult; }
    else if (readResult == 1) { return bbuf[0]; }
    else { throw new IOException("Unexpected read result: " + readResult); }
  }
  
  
  @Override public int read(byte[] bbuf) throws IOException { return read(bbuf, 0, bbuf.length); }
  
  
  @Override public abstract int read(byte[] bbuf, int offset, int bytes) throws IOException;
      
  
  public int read(OutputStream out, int bytes) throws IOException {
    return read(out, bytes, (bytes < DEFAULT_BUFFER_SIZE) ? bytes : DEFAULT_BUFFER_SIZE);
  }
  
  
  public int read(OutputStream out, int bytes, int bufferSize) throws IOException {
    return read(out, bytes, new byte[bufferSize]);
  }
  
  
  public int read(OutputStream out, int bytes, byte[] buffer) throws IOException {
    return IOUtil.doWriteFromInputStream(this, out, bytes, buffer);
  }
  
  
  public int readAll(OutputStream out) throws IOException { return readAll(out, DEFAULT_BUFFER_SIZE); }
  
  
  public int readAll(OutputStream out, int bufferSize) throws IOException {
    return readAll(out, new byte[bufferSize]);
  }

  
  public int readAll(OutputStream out, byte[] buffer) throws IOException {
    return IOUtil.doCopyInputStream(this, out, buffer);
  }  
  
}
