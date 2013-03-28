

package edu.rice.cs.plt.io;

import java.io.OutputStream;
import java.io.IOException;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.object.Composite;
import edu.rice.cs.plt.object.ObjectUtil;


public class OutputStreamSplitter extends DirectOutputStream implements Composite {
  
  private final Iterable<? extends OutputStream> _streams;
  
  public OutputStreamSplitter(OutputStream... streams) { _streams = IterUtil.asIterable(streams); }
  
  public OutputStreamSplitter(Iterable<? extends OutputStream> streams) { _streams = streams; }
  
  public int compositeHeight() { return ObjectUtil.compositeHeight(_streams) + 1; }
  public int compositeSize() { return ObjectUtil.compositeSize(_streams) + 1; }
  
  @Override public void close() throws IOException {
    for (OutputStream s : _streams) { s.close(); }
  }
  
  @Override public void flush() throws IOException {
    for (OutputStream s : _streams) { s.flush(); }
  }
  
  @Override public void write(byte[] bytes) throws IOException {
    for (OutputStream s : _streams) { s.write(bytes); }
  }
  
  @Override public void write(byte[] bytes, int off, int len) throws IOException {
    for (OutputStream s : _streams) { s.write(bytes, off, len); }
  }
  
  @Override public void write(int b) throws IOException {
    for (OutputStream s : _streams) { s.write(b); }
  }
  
}
