

package edu.rice.cs.plt.io;

import java.io.*;


public abstract class DirectReader extends Reader {
  
  protected static final int DEFAULT_BUFFER_SIZE = 1024;

  
  public int read(Writer w, int chars) throws IOException {
    return read(w, chars, (chars < DEFAULT_BUFFER_SIZE) ? chars : DEFAULT_BUFFER_SIZE);
  }
  
  
  public int read(Writer w, int chars, int bufferSize) throws IOException {
    return read(w, chars, new char[bufferSize]);
  }
  
  
  public int read(Writer w, int chars, char[] buffer) throws IOException {
    return IOUtil.doWriteFromReader(this, w, chars, buffer);
  }
  
  
  public int readAll(Writer w) throws IOException { return readAll(w, DEFAULT_BUFFER_SIZE); }
  
  
  public int readAll(Writer w, int bufferSize) throws IOException { return readAll(w, new char[bufferSize]); }

  
  public int readAll(Writer w, char[] buffer) throws IOException {
    return IOUtil.doCopyReader(this, w, buffer);
  }  
  
}
