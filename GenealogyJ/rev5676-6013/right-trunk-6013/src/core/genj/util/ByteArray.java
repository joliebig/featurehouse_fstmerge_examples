
package genj.util;

import java.io.IOException;
import java.io.InputStream;


public class ByteArray {

  
  private final static int CLUSTER = 1024*4;

  
  private final static byte[] EMPTY = new byte[]{};

  
  private byte[] bits = EMPTY;
  
  private boolean isAllowInterrupts;

  
  public ByteArray(InputStream in) throws InterruptedException, IOException {
    
    this(in, Math.max(in.available(), CLUSTER), false);
  }

  
  public ByteArray(InputStream in, boolean allowInterrupts) throws InterruptedException, IOException {
    
    this(in, Math.max(in.available(), CLUSTER), allowInterrupts);
  }

  
  public ByteArray(InputStream in, int cluster, boolean allowInterrupts) throws InterruptedException, IOException {
    
    isAllowInterrupts = allowInterrupts;

    
    
    
    byte buffer[] = new byte[cluster+1];
    int len=0,total=0;

    while (true) {

      
      len = in.read(buffer,total,buffer.length-total);

      
      if (isAllowInterrupts&&Thread.currentThread().isInterrupted())
        throw new InterruptedException();

      
      if (len<0) break;

      
      total+=len;
      
      
      if (total<buffer.length)
        continue;

      
      byte tmp[] = new byte[buffer.length*2];
      System.arraycopy(buffer,0,tmp,0,buffer.length);
      buffer = tmp;

      
    }

    
    bits = new byte[total];
    System.arraycopy(buffer, 0, bits, 0, total);
    buffer = null;
       
    
  }

  
  public byte[] getBytes() {
    return bits;
  }

} 
