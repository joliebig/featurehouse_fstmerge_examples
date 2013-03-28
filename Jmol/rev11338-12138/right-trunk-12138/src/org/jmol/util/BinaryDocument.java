
package org.jmol.util;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;





public class BinaryDocument {

  public BinaryDocument() {  
  }
  

  
  protected DataInputStream stream;
  protected boolean isRandom = false;
  protected boolean isBigEndian = true;

  public void close() {
    try {
      stream.close();
    } catch (IOException e) {
      
    }
  }
  
  public void setStream(BufferedInputStream bis, boolean isBigEndian) {
    if (bis == null)
      return;
    stream = new DataInputStream(bis);
    this.isBigEndian = isBigEndian;
  }
  
  public void setStream(DataInputStream stream) {
    this.stream = stream;
  }
  
  public void setRandom(boolean TF) {
    isRandom = TF;
    
  }
  
  public byte readByte() throws Exception {
    nBytes++;
    return stream.readByte();
  }

  public int readByteArray(byte[] b) throws IOException {
    int n = stream.read(b);
    nBytes += n;
    return n;
  }

  public void readByteArray(byte[] b, int off, int len) throws Exception {
    nBytes += stream.read(b, off, len);
  }

  public short readShort() throws Exception {
    nBytes += 2;
    return (isBigEndian ? stream.readShort()
        : (short) ((((int) stream.readByte()) & 0xff) 
                 | (((int) stream.readByte()) & 0xff) << 8));
  }

  public int readInt() throws Exception {
    nBytes += 4;
    return (isBigEndian ? stream.readInt() : readLEInt());
  }
  
  public int readUnsignedShort() throws Exception {
    nBytes += 2;
    int a = (((int) stream.readByte()) & 0xff);
    int b = (((int) stream.readByte()) & 0xff);
    return (isBigEndian ? (a << 8) + b : (b << 8) + a);
  }
  
  public long readLong() throws Exception {
    nBytes += 8;
    return (isBigEndian ? stream.readLong()
       : ((((long) stream.readByte()) & 0xff)
        | (((long) stream.readByte()) & 0xff) << 8
        | (((long) stream.readByte()) & 0xff) << 16
        | (((long) stream.readByte()) & 0xff) << 24
        | (((long) stream.readByte()) & 0xff) << 32
        | (((long) stream.readByte()) & 0xff) << 40
        | (((long) stream.readByte()) & 0xff) << 48 
        | (((long) stream.readByte()) & 0xff) << 54));
  }

  public float readFloat() throws Exception {
    nBytes += 4;
    return (isBigEndian ? stream.readFloat() 
        : Float.intBitsToFloat(readLEInt()));
  }
  
  public double readDouble() throws Exception {
    nBytes += 8;
    return (isBigEndian ? stream.readDouble() : Double.longBitsToDouble(readLELong()));  
  }
  
  
  private int readLEInt() throws Exception {
    return ((((int) stream.readByte()) & 0xff)
          | (((int) stream.readByte()) & 0xff) << 8
          | (((int) stream.readByte()) & 0xff) << 16 
          | (((int) stream.readByte()) & 0xff) << 24);
  }

  private long readLELong() throws Exception {
    return ((((long) stream.readByte()) & 0xff)
          | (((long) stream.readByte()) & 0xff) << 8
          | (((long) stream.readByte()) & 0xff) << 16 
          | (((long) stream.readByte()) & 0xff) << 24
          | (((long) stream.readByte()) & 0xff) << 32
          | (((long) stream.readByte()) & 0xff) << 40
          | (((long) stream.readByte()) & 0xff) << 48
          | (((long) stream.readByte()) & 0xff) << 56);
  }

  public void seek(long offset) {
    
    try {
      if (offset == nBytes)
        return;
      if (offset < nBytes) {
        stream.reset();
        nBytes = 0;
      } else {
        offset -= nBytes;
      }
      stream.skipBytes((int)offset);
      nBytes += offset;
    } catch (Exception e) {
      Logger.error(null, e);
    }
  }

  long nBytes;
  
  public long getPosition() {
    return nBytes;
  }


}
