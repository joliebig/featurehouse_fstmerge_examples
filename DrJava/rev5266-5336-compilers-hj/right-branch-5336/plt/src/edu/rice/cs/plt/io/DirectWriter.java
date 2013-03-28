

package edu.rice.cs.plt.io;

import java.io.*;


public abstract class DirectWriter extends Writer {
  
  protected static final int DEFAULT_BUFFER_SIZE = 1024;

  
  public int write(Reader r, int chars) throws IOException {
    return write(r, chars, (chars < DEFAULT_BUFFER_SIZE) ? chars : DEFAULT_BUFFER_SIZE);
  }
  
  
  public int write(Reader r, int chars, int bufferSize) throws IOException {
    return write(r, chars, new char[bufferSize]);
  }
  
  
  public int write(Reader r, int chars, char[] buffer) throws IOException {
    return IOUtil.doWriteFromReader(r, this, chars, buffer);
  }
  
  
  public int writeAll(Reader r) throws IOException { 
    return writeAll(r, DEFAULT_BUFFER_SIZE);
  }
  
  
  public int writeAll(Reader r, int bufferSize) throws IOException { 
    return writeAll(r, new char[bufferSize]);
  }

  
  public int writeAll(Reader r, char[] buffer) throws IOException {
    return IOUtil.doCopyReader(r, this, buffer);
  }
  
}
