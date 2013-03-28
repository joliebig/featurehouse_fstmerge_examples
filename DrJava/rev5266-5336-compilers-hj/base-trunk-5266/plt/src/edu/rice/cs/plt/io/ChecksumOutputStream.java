

package edu.rice.cs.plt.io;

import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.util.zip.Adler32;


public class ChecksumOutputStream extends DirectOutputStream {
  private final Checksum _checksum;
  
  
  public ChecksumOutputStream(Checksum checksum) { _checksum = checksum; }
  
  
  public long getValue() { return _checksum.getValue(); }
  
  @Override public void close() {}
  @Override public void flush() {}
  @Override public void write(byte[] bbuf) { _checksum.update(bbuf, 0, bbuf.length); }
  @Override public void write(byte[] bbuf, int offset, int len) { _checksum.update(bbuf, offset, len); }
  @Override public void write(int b) { _checksum.update(b); }
  
  
  public static ChecksumOutputStream makeCRC32() {
    return new ChecksumOutputStream(new CRC32());
  }
  
  
  public static ChecksumOutputStream makeAdler32() {
    return new ChecksumOutputStream(new Adler32());
  }
  
}
