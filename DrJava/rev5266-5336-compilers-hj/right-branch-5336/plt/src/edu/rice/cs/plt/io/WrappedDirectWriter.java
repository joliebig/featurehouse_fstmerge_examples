

package edu.rice.cs.plt.io;

import java.io.*;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class WrappedDirectWriter extends DirectWriter implements Composite {
  private Writer _writer;
  
  public WrappedDirectWriter(Writer writer) { _writer = writer; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_writer) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_writer) + 1; }
  
  @Override public void close() throws IOException { _writer.close(); }
  
  @Override public void flush() throws IOException { _writer.flush(); }
  
  
  
  
  




  
  




  
  @Override public void write(int c) throws IOException { _writer.write(c); }
  
  @Override public void write(char[] cbuf) throws IOException { _writer.write(cbuf); }
  
  @Override public void write(char[] cbuf, int offset, int length) throws IOException {
    _writer.write(cbuf, offset, length);
  }
  
  @Override public void write(String s) throws IOException { _writer.write(s); }
  
  @Override public void write(String s, int offset, int length) throws IOException {
    _writer.write(s, offset, length);
  }

  
  public static DirectWriter makeDirect(Writer writer) {
    if (writer instanceof DirectWriter) { return (DirectWriter) writer; }
    else { return new WrappedDirectWriter(writer); }
  }

}
