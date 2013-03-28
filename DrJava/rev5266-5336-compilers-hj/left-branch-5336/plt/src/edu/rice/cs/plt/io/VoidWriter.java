

package edu.rice.cs.plt.io;

import java.io.Writer;


public class VoidWriter extends DirectWriter {
  public static final VoidWriter INSTANCE = new VoidWriter();
  protected VoidWriter() {} 
  @Override public Writer append(char c) { return this; }
  @Override public Writer append(CharSequence s)  { return this; }
  @Override public Writer append(CharSequence s, int start, int end)  { return this; }
  @Override public void close() {}
  @Override public void flush() {}
  @Override public void write(char[] cbuf) {}
  @Override public void write(char[] cbuf, int offset, int len) {}
  @Override public void write(int c) {}
  @Override public void write(String s) {}
  @Override public void write(String s, int offset, int len) {}
}
