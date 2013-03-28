

package edu.rice.cs.plt.io;

import junit.framework.TestCase;
import java.io.IOException;
import java.io.FileInputStream;

public class ChecksumOutputStreamTest extends TestCase {
  
  public void testCRC32() throws IOException {
    
    ChecksumOutputStream out;
    
    out = ChecksumOutputStream.makeCRC32();
    assertEquals(0, (int) out.getValue());
    
    out = ChecksumOutputStream.makeCRC32();
    out.writeAll(new FileInputStream("testFiles/hashfile.txt"));
    assertEquals(0x29f9bb29, (int) out.getValue());
  }
  
  public void testAdler32() throws IOException {
    
    ChecksumOutputStream out;
    
    out = ChecksumOutputStream.makeAdler32();
    assertEquals(1, (int) out.getValue());
    
    out = ChecksumOutputStream.makeAdler32();
    out.writeAll(new FileInputStream("testFiles/hashfile.txt"));
    assertEquals(2054513547, (int) out.getValue());
  }
  
}
