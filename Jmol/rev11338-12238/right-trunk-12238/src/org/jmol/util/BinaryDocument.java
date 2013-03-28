
package org.jmol.util;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;





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
    if (os != null) {
      try {
        os.flush();
        os.close();
      } catch (IOException e) {
        
      }
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
    return ioReadByte();
  }

  private byte ioReadByte() throws Exception {
    byte b = stream.readByte();
    if (os != null)
      os.write(b);
    return b;
  }

  public int readByteArray(byte[] b) throws IOException {
    int n = ioRead(b);
    nBytes += n;
    return n;
  }

  private int ioRead(byte[] b) throws IOException {
    int n = stream.read(b);
    if (n > 0 && os != null)
      os.write(b, 0, n);
    return n;
  }

  public void readByteArray(byte[] b, int off, int len) throws Exception {
    nBytes += ioRead(b, off, len);
  }

  private long ioRead(byte[] b, int off, int len) throws Exception {
    int n = stream.read(b, off, len);
    if (n > 0 && os != null)
      os.write(b, off, n);
    return n;
  }

  public String readString(int nChar) throws Exception {
    byte[] temp = new byte[nChar];
    readByteArray(temp);
    StringBuffer s = new StringBuffer();
    for (int j = 0; j < nChar; j++)
      s.append((char) temp[j]);
    return s.toString();
  }
  
  public short readShort() throws Exception {
    nBytes += 2;
    return (isBigEndian ? ioReadShort()
        : (short) ((((int) ioReadByte()) & 0xff) 
                 | (((int) ioReadByte()) & 0xff) << 8));
  }

  private short ioReadShort() throws Exception {
    short b = stream.readShort();
    if (os != null)
      os.write(b);
    return b;
  }


  public int readInt() throws Exception {
    nBytes += 4;
    return (isBigEndian ? ioReadInt() : readLEInt());
  }
  
  private int ioReadInt() throws Exception {
    int i = stream.readInt();
    if (os != null)
      writeInt(i);
    return i;
  }

  private void writeInt(int i) throws Exception {
    os.write((byte) ((i >> 24) & 0xFF));
    os.write((byte) ((i >> 16) & 0xFF));
    os.write((byte) ((i >> 8) & 0xFF));
    os.write((byte) (i & 0xFF));
  }

  public int readUnsignedShort() throws Exception {
    nBytes += 2;
    int a = (((int) ioReadByte()) & 0xff);
    int b = (((int) ioReadByte()) & 0xff);
    return (isBigEndian ? (a << 8) + b : (b << 8) + a);
  }
  
  public long readLong() throws Exception {
    nBytes += 8;
    return (isBigEndian ? ioReadLong()
       : ((((long) ioReadByte()) & 0xff)
        | (((long) ioReadByte()) & 0xff) << 8
        | (((long) ioReadByte()) & 0xff) << 16
        | (((long) ioReadByte()) & 0xff) << 24
        | (((long) ioReadByte()) & 0xff) << 32
        | (((long) ioReadByte()) & 0xff) << 40
        | (((long) ioReadByte()) & 0xff) << 48 
        | (((long) ioReadByte()) & 0xff) << 54));
  }

  private long ioReadLong() throws Exception {
    long b = stream.readLong();
    if (os != null)
      writeLong(b);
    return b;
  }

  private void writeLong(long b) throws Exception {
    writeInt((int)((b >> 32) & 0xFFFFFFFFl));
    writeInt((int)(b & 0xFFFFFFFFl));
  }

  public float readFloat() throws Exception {
    nBytes += 4;
    return (isBigEndian ? ioReadFloat() 
        : Float.intBitsToFloat(readLEInt()));
  }
  
  private float ioReadFloat() throws Exception {
    float f = stream.readFloat();
    if (os != null)
      os.write(Float.floatToIntBits(f));
    return f;
  }

  public double readDouble() throws Exception {
    nBytes += 8;
    return (isBigEndian ? ioReadDouble() : Double.longBitsToDouble(readLELong()));  
  }
    
  private double ioReadDouble() throws Exception {
    double d = stream.readDouble();
    if (os != null)
      writeLong(Double.doubleToRawLongBits(d));
    return d;
  }

  private int readLEInt() throws Exception {
    return ((((int) ioReadByte()) & 0xff)
          | (((int) ioReadByte()) & 0xff) << 8
          | (((int) ioReadByte()) & 0xff) << 16 
          | (((int) ioReadByte()) & 0xff) << 24);
  }

  private long readLELong() throws Exception {
    return ((((long) ioReadByte()) & 0xff)
          | (((long) ioReadByte()) & 0xff) << 8
          | (((long) ioReadByte()) & 0xff) << 16 
          | (((long) ioReadByte()) & 0xff) << 24
          | (((long) ioReadByte()) & 0xff) << 32
          | (((long) ioReadByte()) & 0xff) << 40
          | (((long) ioReadByte()) & 0xff) << 48
          | (((long) ioReadByte()) & 0xff) << 56);
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

  OutputStream os;
  public void setOutputStream(OutputStream os) {
    this.os = os;
  }


}
